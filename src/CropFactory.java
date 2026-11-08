/** The one place that turns a crop's name into a crop object (Factory pattern). */
public class CropFactory {
    public static final String[] NAMES = {"Turnip", "Potato", "Strawberry"};

    public static Crop create(String name) {
        switch (name) {
            case "Turnip": return new Turnip();
            case "Potato": return new Potato();
            case "Strawberry": return new Strawberry();
            default: throw new IllegalArgumentException("unknown crop: " + name);
        }
    }
}
