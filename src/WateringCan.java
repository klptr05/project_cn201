public class WateringCan implements Tool {
    private static final int CAPACITY = 20;
    private int water = CAPACITY;

    @Override public String getLabel() { return "Can " + water + "/" + CAPACITY; }
    @Override public String getActionName() { return "Water"; }
    @Override public int getEnergyCost() { return 2; }

    @Override
    public void use(Tile tile) throws FarmException {
        if (water == 0) throw new FarmException("The can is empty - refill it at the pond");
        tile.water();
        water--;
    }

    public void refill() { water = CAPACITY; }
    public int getWater() { return water; }

    public void setWater(int amount) {
        water = Math.max(0, Math.min(CAPACITY, amount));
    }
}
