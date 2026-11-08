/** One bag per crop type. Planting takes a seed out; the shop puts seeds in. */
public class SeedBag implements Tool {
    private final String cropName;
    private int seeds = 0;

    public SeedBag(String cropName) {
        this.cropName = cropName;
    }

    @Override public String getLabel() { return cropName + " x" + seeds; }
    @Override public String getActionName() { return "Plant " + cropName; }
    @Override public int getEnergyCost() { return 1; }

    @Override
    public void use(Tile tile) throws FarmException {
        if (seeds == 0) throw new FarmException("No " + cropName + " seeds left - buy some at the shop");
        tile.plant(CropFactory.create(cropName));
        seeds--;
    }

    public String getCropName() { return cropName; }
    public int getSeeds() { return seeds; }
    public void addSeeds(int amount) { seeds += amount; }
    public void setSeeds(int amount) { seeds = Math.max(0, amount); }
}
