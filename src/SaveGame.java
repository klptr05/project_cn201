import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SaveGame {
    private static final String HEADER = "little-farm save 1";
    private final Path file;

    private static String hashLine(String line) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(line.getBytes(StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();
            for (byte b : hash) {
                result.append(String.format("%02x", b & 0xff));
            }
            return result.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void writeCheckedLine(PrintWriter out, String line) {
        out.println(line + "\t" + hashLine(line));
    }

    public SaveGame(String fileName) {
        this.file = Paths.get(fileName);
    }

    public boolean exists() {
        return Files.exists(file);
    }

    public String describe() {
        if (!exists()) return null;
        String day = "?", gold = "?";
        try {
            for (String rawLine : Files.readAllLines(file)) {
                int separator = rawLine.lastIndexOf('\t');

                String line = separator == -1
                        ? rawLine
                        : rawLine.substring(0, separator);

                if (line.startsWith("day ")) day = line.substring(4);
                else if (line.startsWith("gold ")) gold = line.substring(5);
            }
        } catch (IOException e) {
            return "unreadable save";
        }
        return "Day " + day + ", " + gold + "G";
    }

    public void delete() throws IOException {
        Files.deleteIfExists(file);
    }

        public void save(Farm farm, Farmer farmer, GameClock clock) throws IOException {
            try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file))) {
                out.println(HEADER);
                writeCheckedLine(out, "day " + clock.getDay());
                writeCheckedLine(out, "weather " + clock.getWeather().name());
                writeCheckedLine(out, "gold " + farmer.getGold());
                writeCheckedLine(out, "earned " + farmer.getTotalEarned());
                writeCheckedLine(out, "can " + farmer.getCan().getWater());
                for (String name : CropFactory.NAMES) {
                    writeCheckedLine(out, "seeds " + name + " " + farmer.getSeedBag(name).getSeeds());
                }
                for (Map.Entry<String, Integer> entry : farmer.getBasket().asMap().entrySet()) {
                    writeCheckedLine(out, "basket " + entry.getKey() + " " + entry.getValue());
                }
                for (Villager v : farm.getVillagers()) {
                    String id = v.getClass().getSimpleName();
                    writeCheckedLine(out, "villager " + id + " " + v.getFriendship() + " " + v.getRequestsDone());
                    Request request = v.getRequest();
                    if (request != null) {
                        writeCheckedLine(out, "request " + id + " " + request.getCropName() + " "
                                + request.getAmount() + " " + request.getReward() + " " + request.getDaysLeft());
                    }
                    if (v instanceof ajKhiao) {
                        ajKhiao k = (ajKhiao) v;
                        writeCheckedLine(out, "khiao_state " + k.isAskedQuestion() + " "
                                + k.isQuestCompleted() + " " + farmer.getChoice());
                    }
                }
                for (Animal a : farm.getAnimals()) {
                    String id = a.getClass().getSimpleName();
                    writeCheckedLine(out, "animal " + id + " " + a.getFriendship());
                }
                for (int y = 0; y < Farm.ROWS; y++) {
                    for (int x = 0; x < Farm.COLS; x++) {
                        Tile tile = farm.tileAt(x, y);

                        if (tile != null && tile.getType() == TileType.SOIL) {
                            Crop crop = tile.getCrop();

                            String cropName = crop == null ? "-" : crop.getName();
                            int daysGrown = crop == null ? 0 : crop.getDaysGrown();
                            String wet = tile.isWatered() ? "wet" : "dry";

                            writeCheckedLine(out, "soil " + x + " " + y + " "
                                    + cropName + " " + daysGrown + " " + wet);
                        }
                    }
                }
            }
        }

        public void loadInto(Farm farm, Farmer farmer, GameClock clock) throws IOException {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            if (lines.isEmpty()) {
                throw new IOException("line 1 is damaged: <empty file> (missing save header)");
            }
            if (!lines.get(0).equals(HEADER)) {
                throw new IOException("line 1 is damaged: " + lines.get(0)
                        + " (expected '" + HEADER + "')");
            }
            for (int i = 1; i < lines.size(); i++) {
                String rawLine = lines.get(i);
                int separator = rawLine.lastIndexOf('\t');

                if (separator == -1) {
                    throw new IOException(
                            "Cheating detected!\n"
                            + "Your save file has been damaged.\n"
                            + "Please start a new game. Line " + (i + 1)+ " is modified.");
                }

                String data = rawLine.substring(0, separator);
                String savedHash = rawLine.substring(separator + 1);

                if (!hashLine(data).equals(savedHash)) {
                    throw new IOException(
                            "Cheating detected!\n"
                            + "Your save file has been modified.\n"
                            + "Please start a new game. Line " + (i + 1)+ " is modified.");
                }
            }

            for (int i = 1; i < lines.size(); i++) {
                String rawLine = lines.get(i);
                String data = rawLine.substring(0, rawLine.lastIndexOf('\t'));
                String[] parts = data.trim().split("\\s+");
                try {
                    switch (parts[0]) {
                        case "day":
                            clock.setDay(Integer.parseInt(parts[1]));
                            break;
                        case "weather":
                            clock.setWeather(Weather.valueOf(parts[1]));
                            break;
                        case "gold":
                            farmer.setGold(Integer.parseInt(parts[1]));
                            break;
                        case "earned":
                            farmer.setTotalEarned(Integer.parseInt(parts[1]));
                            break;
                        case "villager":
                            if (parts.length == 3 && !farm.getVillagers().isEmpty()) {
                                farm.getVillagers().get(0).restore(
                                        Integer.parseInt(parts[1]),
                                        Integer.parseInt(parts[2]));
                            } else {
                                Villager v = findVillager(farm, parts[1]);
                                if (v != null) {
                                    v.restore(Integer.parseInt(parts[2]),
                                            Integer.parseInt(parts[3]));
                                }
                            }
                            break;
                        case "khiao_state":
                            Villager kv = findVillager(farm, "ajKhiao");
                            if (kv instanceof ajKhiao) {
                                ((ajKhiao) kv).restoreState(
                                        Boolean.parseBoolean(parts[1]),
                                        Boolean.parseBoolean(parts[2]));
                            }
                            if (parts.length > 3) {
                                farmer.setChoice(Boolean.parseBoolean(parts[3]));
                            }
                            break;
                        case "request":
                            if (parts.length == 5 && !farm.getVillagers().isEmpty()) {
                                farm.getVillagers().get(0).setRequest(
                                        new Request(parts[1],
                                                Integer.parseInt(parts[2]),
                                                Integer.parseInt(parts[3]),
                                                Integer.parseInt(parts[4])));
                            } else {
                                Villager v = findVillager(farm, parts[1]);
                                if (v != null) {
                                    v.setRequest(new Request(parts[2],
                                            Integer.parseInt(parts[3]),
                                            Integer.parseInt(parts[4]),
                                            Integer.parseInt(parts[5])));
                                }
                            }
                            break;
                        case "animal":
                            Animal a = findAnimal(farm, parts[1]);
                            if (a != null) {
                                a.restore(Integer.parseInt(parts[2]));
                            }
                            break;
                        case "can":
                            farmer.getCan().setWater(Integer.parseInt(parts[1]));
                            break;
                        case "seeds":
                            farmer.getSeedBag(parts[1]).setSeeds(Integer.parseInt(parts[2]));
                            break;
                        case "basket":
                            farmer.getBasket().add(parts[1], Integer.parseInt(parts[2]));
                            break;
                        case "soil":
                            loadSoil(farm, parts);
                            break;
                        case "":
                            break;
                        default:
                            throw new IOException("line " + (i + 1)
                                    + ": unknown entry '" + parts[0] + "'");
                    }
                } catch (IllegalArgumentException | IndexOutOfBoundsException | FarmException e) {
                    throw new IOException(
                            "line " + (i + 1) + " is damaged: " + lines.get(i), e);
                }
            }
            for (Villager v : farm.getVillagers()) {
                v.setCurrentDay(clock.getDay());
            }
        }
    private Villager findVillager(Farm farm, String id) {
        for (Villager v : farm.getVillagers()) {
            if (v.getClass().getSimpleName().equals(id)) return v;
        }
        return null;
    }
    private Animal findAnimal(Farm farm, String id) {
        for (Animal a : farm.getAnimals()) {
            if (a.getClass().getSimpleName().equals(id)) return a;
        }
        return null;
    }
    private void loadSoil(Farm farm, String[] parts) throws FarmException {
        Tile tile = farm.tileAt(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        if (tile == null) throw new FarmException("soil is off the map");
        if (tile.getType() != TileType.SOIL) tile.till();
        if (parts.length > 5 && parts[5].equals("wet")) tile.soak();
        if (parts[3].equals("-")) return;
        Crop crop = CropFactory.create(parts[3]);
        crop.setDaysGrown(Integer.parseInt(parts[4]));
        tile.plant(crop);
    }
}