import java.util.Random;

public interface RainChanceStrategy {
    boolean shouldRain(Random rng);
}