import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Walks tile by tile. A tap turns on the spot; holding the key walks. */
public class Farmer {
    public static final int MAX_ENERGY = 100;
    private static final int STEP_TICKS = 8;   // ticks to slide from one tile to the next
    private static final int TURN_TICKS = 5;   // hold this long after turning before walking

    private int col, row;
    private Direction facing = Direction.DOWN;
    private int stepTicks = 0;
    private int turnTicks = 0;
    private int energy = MAX_ENERGY;
    private int gold;
    private int totalEarned = 0;
    private final Bag<String> basket = new Bag<>();
    private final Hoe hoe = new Hoe();
    private final WateringCan can = new WateringCan();
    private final List<SeedBag> seedBags = new ArrayList<>();
    private int selectedSeed = 0;

    public Farmer(int col, int row, int gold) {
        this.col = col;
        this.row = row;
        this.gold = gold;
        for (String name : CropFactory.NAMES) seedBags.add(new SeedBag(name));
    }

    /** @param wanted the direction key being held, or null */
    public void update(Farm farm, Direction wanted) {
        if (stepTicks > 0) {
            stepTicks--;
            return;
        }
        if (wanted == null) return;
        if (wanted != facing) {
            facing = wanted;
            turnTicks = TURN_TICKS;
            return;
        }
        if (turnTicks > 0) {
            turnTicks--;
            return;
        }
        if (farm.isWalkable(col + facing.dx, row + facing.dy)) {
            col += facing.dx;
            row += facing.dy;
            stepTicks = STEP_TICKS;
        }
    }

    /**
     * The tool that makes sense for this tile, so the player never has to switch by hand:
     * grass gets the hoe, empty soil gets the chosen seeds, a dry plant gets the can.
     * @return null if there is nothing a tool can do here
     */
    public Tool toolFor(Tile tile) {
        switch (tile.getType()) {
            case GRASS:
                return hoe;
            case SOIL:
                if (!tile.hasCrop()) return getSelectedSeedBag();
                if (!tile.isWatered() && !tile.getCrop().isMature()) return can;
                return null;
            default:
                return null;
        }
    }

    public void use(Tool tool, Tile tile) throws FarmException {
        if (energy < tool.getEnergyCost()) throw new FarmException("Too tired - go to bed in the house");
        tool.use(tile);
        energy -= tool.getEnergyCost();   // only reached if use() didn't throw
    }

    public void spendGold(int amount) throws FarmException {
        if (amount > gold) throw new FarmException("Not enough gold (need " + amount + "G)");
        gold -= amount;
    }

    /** Money from shipping and requests; counted towards the end-of-month evaluation. */
    public void earn(int amount) {
        gold += amount;
        totalEarned += amount;
    }

    public void wakeUp(int col, int row, boolean passedOut) {
        this.col = col;
        this.row = row;
        facing = Direction.DOWN;
        stepTicks = 0;
        energy = passedOut ? MAX_ENERGY / 2 : MAX_ENERGY;
    }

    public void selectSeed(int index) {
        if (index >= 0 && index < seedBags.size()) selectedSeed = index;
    }

    public void cycleSeed(int delta) {
        selectedSeed = (selectedSeed + delta + seedBags.size()) % seedBags.size();
    }

    public SeedBag getSeedBag(String cropName) {
        for (SeedBag bag : seedBags) {
            if (bag.getCropName().equals(cropName)) return bag;
        }
        throw new IllegalArgumentException("no seed bag for " + cropName);
    }

    public int facingCol() { return col + facing.dx; }
    public int facingRow() { return row + facing.dy; }
    public Direction getFacing() { return facing; }
    public SeedBag getSelectedSeedBag() { return seedBags.get(selectedSeed); }
    public int getSelectedSeedIndex() { return selectedSeed; }
    public List<SeedBag> getSeedBags() { return Collections.unmodifiableList(seedBags); }
    public WateringCan getCan() { return can; }
    public Bag<String> getBasket() { return basket; }
    public int getEnergy() { return energy; }
    public int getGold() { return gold; }
    public int getTotalEarned() { return totalEarned; }
    public int getCol() { return col; }
    public int getRow() { return row; }

    public void setGold(int gold) {
        if (gold < 0) throw new IllegalArgumentException("gold can't be negative: " + gold);
        this.gold = gold;
    }

    public void setTotalEarned(int amount) {
        if (amount < 0) throw new IllegalArgumentException("earnings can't be negative: " + amount);
        totalEarned = amount;
    }

    public void draw(Graphics2D g) {
        // while sliding, draw part of the way back towards the tile we came from
        double back = (double) stepTicks / STEP_TICKS;
        int x = (int) ((col - facing.dx * back) * Farm.TILE);
        int y = (int) ((row - facing.dy * back) * Farm.TILE);
        int bob = stepTicks > 0 && (stepTicks / 2) % 2 == 0 ? 1 : 0;

        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(x + 7, y + 26, 18, 6);
        g.setColor(new Color(70, 50, 40));
        g.fillRect(x + 11, y + 25 - bob, 4, 5);
        g.fillRect(x + 17, y + 25 + bob - 1, 4, 5);
        g.setColor(new Color(60, 100, 190));            // overalls
        g.fillRoundRect(x + 9, y + 14, 14, 13, 4, 4);
        g.setColor(new Color(250, 210, 170));           // face
        g.fillOval(x + 9, y + 3, 14, 14);
        g.setColor(new Color(40, 30, 30));
        switch (facing) {
            case DOWN:  g.fillRect(x + 12, y + 10, 2, 2); g.fillRect(x + 18, y + 10, 2, 2); break;
            case LEFT:  g.fillRect(x + 11, y + 10, 2, 2); break;
            case RIGHT: g.fillRect(x + 19, y + 10, 2, 2); break;
            case UP:    break;
        }
        g.setColor(new Color(235, 200, 90));            // straw hat
        g.fillOval(x + 4, y + 4, 24, 6);
        g.fillOval(x + 10, y - 1, 12, 9);
        g.setColor(new Color(200, 60, 50));
        g.fillRect(x + 10, y + 4, 12, 2);
    }
}
