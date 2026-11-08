public class Hoe implements Tool {
    @Override public String getLabel() { return "Hoe"; }
    @Override public String getActionName() { return "Till"; }
    @Override public int getEnergyCost() { return 4; }

    @Override
    public void use(Tile tile) throws FarmException {
        tile.till();
    }
}
