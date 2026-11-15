import java.util.Random;

public class FiftyRainChanceStrategy implements RainChanceStrategy {

    @Override
    public boolean shouldRain(Random rng) {
        return rng.nextInt(100) < 50;
    }
}