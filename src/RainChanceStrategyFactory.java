public class RainChanceStrategyFactory {

    public static RainChanceStrategy create(String name) {

        switch (name.toLowerCase()) {
            case "normal":
                return new NormalRainChanceStrategy();

            case "fifty":
                return new FiftyRainChanceStrategy();

            default:
                throw new IllegalArgumentException(
                    "Unknown rain strategy: " + name
                );
        }
    }
}