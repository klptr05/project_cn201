/** One square of the farm. It guards its own rules: you cannot plant on grass or water a fence. */
public class Tile {
    private TileType type;
    private boolean watered;
    private Crop crop;

    public Tile(TileType type) {
        this.type = type;
    }

    public TileType getType() { return type; }
    public boolean isWatered() { return watered; }
    public Crop getCrop() { return crop; }
    public boolean hasCrop() { return crop != null; }

    public void till() throws FarmException {
        if (type == TileType.SOIL) throw new FarmException("Already tilled");
        if (type != TileType.GRASS) throw new FarmException("The hoe can't dig here");
        type = TileType.SOIL;
    }

    public void water() throws FarmException {
        if (type != TileType.SOIL) throw new FarmException("Only tilled soil needs water");
        if (watered) throw new FarmException("Already watered today");
        watered = true;
    }

    /** Rain (or a loaded save) wets tilled soil without any of water()'s complaints. */
    public void soak() {
        if (type == TileType.SOIL) watered = true;
    }

    public void plant(Crop newCrop) throws FarmException {
        if (type != TileType.SOIL) throw new FarmException("Till the ground with the hoe first");
        if (crop != null) throw new FarmException("Something is already growing here");
        crop = newCrop;
    }

    /** @return the name of what was picked */
    public String harvest() throws FarmException {
        if (crop == null || !crop.isMature()) throw new FarmException("Nothing ready to pick");
        String name = crop.getName();
        if (!crop.pick()) crop = null;
        return name;
    }

    /** Overnight: a crop grows only if it was watered today. Then the soil dries out. */
    public void newDay() {
        if (crop != null && watered) crop.grow();
        watered = false;
    }
}
