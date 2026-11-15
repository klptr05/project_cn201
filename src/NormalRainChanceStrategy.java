import java.util.Random;

public class NormalRainChanceStrategy implements RainChanceStrategy {

    @Override
    public boolean shouldRain(Random rng) {
        return rng.nextInt(100) < 25;
    }
}