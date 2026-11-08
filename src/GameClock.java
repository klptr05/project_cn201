import java.util.Random;

/** Days run from 06:00 to midnight; the clock moves on ten minutes every two real seconds. */
public class GameClock {
    private static final int DAY_START = 6 * 60;    // in minutes after midnight
    private static final int DAY_END = 24 * 60;
    private static final int DUSK = 18 * 60;
    private static final int TICKS_PER_STEP = 120;
    private static final int MINUTES_PER_STEP = 10;

    private int day = 1;
    private int minutes = DAY_START;
    private int ticks = 0;
    private Weather weather = Weather.SUNNY;   // day 1 is always fine

    public void tick() {
        if (++ticks >= TICKS_PER_STEP) {
            ticks = 0;
            minutes += MINUTES_PER_STEP;
        }
    }

    public boolean isMidnight() { return minutes >= DAY_END; }

    public void nextDay(Random rng) {
        day++;
        minutes = DAY_START;
        ticks = 0;
        weather = Weather.forecast(rng);
    }

    public int getDay() { return day; }
    public int getMinutes() { return minutes; }
    public Weather getWeather() { return weather; }
    public void setWeather(Weather weather) { this.weather = weather; }

    public void setDay(int day) {
        if (day < 1) throw new IllegalArgumentException("day must be at least 1, not " + day);
        this.day = day;
    }

    /** How dark the evening is: 0 until 18:00, rising to 160 by 22:00. */
    public int darkness() {
        if (minutes < DUSK) return 0;
        return Math.min(160, (minutes - DUSK) * 160 / (4 * 60));
    }

    public String timeText() {
        return String.format("%02d:%02d", minutes / 60, minutes % 60);
    }
}
