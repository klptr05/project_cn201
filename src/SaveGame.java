import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Saves the farm as plain text you can open and read, e.g.
 *   day 4
 *   weather RAINY
 *   gold 380
 *   seeds Turnip 2
 *   request Turnip 3 360 4     (crop amount reward daysLeft)
 *   soil 5 6 Turnip 1 wet      (col row crop daysGrown wet/dry; "-" for an empty plot)
 */
public class SaveGame {
    private static final String HEADER = "little-farm save 1";
    private final Path file;

    public SaveGame(String fileName) {
        this.file = Paths.get(fileName);
    }

    public boolean exists() {
        return Files.exists(file);
    }

    /** A one-line description for the title menu, e.g. "Day 5, 780G", or null if there is no save. */
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
            for (String name : CropFactory.NAMES) out.println("seeds " + name + " " + farmer.getSeedBag(name).getSeeds());
            for (Map.Entry<String, Integer> entry : farmer.getBasket().asMap().entrySet()) {
                out.println("basket " + entry.getKey() + " " + entry.getValue());
            }
            Villager villager = farm.getVillager();
            out.println("villager " + villager.getFriendship() + " " + villager.getRequestsDone());
            Request request = villager.getRequest();
            if (request != null) {
                out.println("request " + request.getCropName() + " " + request.getAmount() + " "
                        + request.getReward() + " " + request.getDaysLeft());
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

    /** Fills a brand-new farm, farmer and clock from the file. */
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
                        farm.getVillager().restore(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                        break;
                    case "request":
                        farm.getVillager().setRequest(new Request(parts[1], Integer.parseInt(parts[2]),
                                Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
                        break;
                    case "can":    farmer.getCan().setWater(Integer.parseInt(parts[1])); break;
                    case "seeds":  farmer.getSeedBag(parts[1]).setSeeds(Integer.parseInt(parts[2])); break;
                    case "basket": farmer.getBasket().add(parts[1], Integer.parseInt(parts[2])); break;
                    case "soil":   loadSoil(farm, parts); break;
                    case "":       break;   // blank line
                    default:       throw new IOException("line " + (i + 1) + ": unknown entry '" + parts[0] + "'");
                }
            } catch (IllegalArgumentException | IndexOutOfBoundsException | FarmException e) {
                // NumberFormatException is an IllegalArgumentException, so a bad number lands here too
                throw new IOException("line " + (i + 1) + " is damaged: " + lines.get(i), e);
            }
        }
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
