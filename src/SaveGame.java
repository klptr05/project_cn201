import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class SaveGame {
    private static final String HEADER = "little-farm save 1";
    private final Path file;

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
            for (String line : Files.readAllLines(file)) {
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
            out.println("day " + clock.getDay());
            out.println("weather " + clock.getWeather().name());
            out.println("gold " + farmer.getGold());
            out.println("earned " + farmer.getTotalEarned());
            out.println("can " + farmer.getCan().getWater());
            for (String name : CropFactory.NAMES) {
                out.println("seeds " + name + " " + farmer.getSeedBag(name).getSeeds());
            }
            for (Map.Entry<String, Integer> entry : farmer.getBasket().asMap().entrySet()) {
                out.println("basket " + entry.getKey() + " " + entry.getValue());
            }

            for (Villager v : farm.getVillagers()) {
                String id = v.getClass().getSimpleName();
                out.println("villager " + id + " " + v.getFriendship() + " " + v.getRequestsDone());
                Request request = v.getRequest();
                if (request != null) {
                    out.println("request " + id + " " + request.getCropName() + " " + request.getAmount() + " "
                            + request.getReward() + " " + request.getDaysLeft());
                }
                if (v instanceof ajKhiao) {
                    ajKhiao k = (ajKhiao) v;
                    out.println("khiao_state " + k.isAskedQuestion() + " " + k.isQuestCompleted() + " " + farmer.getChoice());
                }
            }

            for (Animal a : farm.getAnimals()) {
                String id = a.getClass().getSimpleName();
                out.println("animal " + id + " " + a.getFriendship());
            }

            for (int r = 0; r < Farm.ROWS; r++) {
                for (int c = 0; c < Farm.COLS; c++) {
                    Tile tile = farm.tileAt(c, r);
                    if (tile.getType() != TileType.SOIL) continue;
                    Crop crop = tile.getCrop();
                    out.println("soil " + c + " " + r + " " + (crop == null ? "- 0" : crop.getName() + " " + crop.getDaysGrown())
                            + (tile.isWatered() ? " wet" : " dry"));
                }
            }
        }
    }

    public void loadInto(Farm farm, Farmer farmer, GameClock clock) throws IOException {
        List<String> lines = Files.readAllLines(file);
        if (lines.isEmpty() || !lines.get(0).equals(HEADER)) throw new IOException("not a Little Farm save file");
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).trim().split(" ");
            try {
                switch (parts[0]) {
                    case "day":     clock.setDay(Integer.parseInt(parts[1])); break;
                    case "weather": clock.setWeather(Weather.valueOf(parts[1])); break;
                    case "gold":    farmer.setGold(Integer.parseInt(parts[1])); break;
                    case "earned":  farmer.setTotalEarned(Integer.parseInt(parts[1])); break;
                    case "villager":
                        if (parts.length == 3 && !farm.getVillagers().isEmpty()) {
                            farm.getVillagers().get(0).restore(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                        } else {
                            Villager v = findVillager(farm, parts[1]);
                            if (v != null) v.restore(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                        }
                        break;
                    case "khiao_state":
                        Villager kv = findVillager(farm, "ajKhiao");
                        if (kv instanceof ajKhiao) {
                            ((ajKhiao) kv).restoreState(Boolean.parseBoolean(parts[1]), Boolean.parseBoolean(parts[2]));
                        }
                        if (parts.length > 3) farmer.setChoice(Boolean.parseBoolean(parts[3]));
                        break;
                    case "request":
                        if (parts.length == 5 && !farm.getVillagers().isEmpty()) {
                            farm.getVillagers().get(0).setRequest(new Request(parts[1], Integer.parseInt(parts[2]),
                                    Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
                        } else {
                            Villager v = findVillager(farm, parts[1]);
                            if (v != null) {
                                v.setRequest(new Request(parts[2], Integer.parseInt(parts[3]),
                                        Integer.parseInt(parts[4]), Integer.parseInt(parts[5])));
                            }
                        }
                        break;
                    case "animal":
                        Animal a = findAnimal(farm, parts[1]);
                        if (a != null) a.restore(Integer.parseInt(parts[2]));
                        break;
                    case "can":    farmer.getCan().setWater(Integer.parseInt(parts[1])); break;
                    case "seeds":  farmer.getSeedBag(parts[1]).setSeeds(Integer.parseInt(parts[2])); break;
                    case "basket": farmer.getBasket().add(parts[1], Integer.parseInt(parts[2])); break;
                    case "soil":   loadSoil(farm, parts); break;
                    case "":       break;
                    default:       throw new IOException("line " + (i + 1) + ": unknown entry '" + parts[0] + "'");
                }
            } catch (IllegalArgumentException | IndexOutOfBoundsException | FarmException e) {
                throw new IOException("line " + (i + 1) + " is damaged: " + lines.get(i), e);
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