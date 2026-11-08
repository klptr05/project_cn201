import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

/** Each kind of weather carries its own odds and effects, so adding a STORM means adding one constant. */
public enum Weather {
    SUNNY("Sunny", "A fine sunny day", 75, false),
    RAINY("Rain", "Rain today - no need to water", 25, true);

    private final String label;
    private final String morningText;
    private final int chance;            // percent; the chances add up to 100
    private final boolean watersCrops;

    Weather(String label, String morningText, int chance, boolean watersCrops) {
        this.label = label;
        this.morningText = morningText;
        this.chance = chance;
        this.watersCrops = watersCrops;
    }

    public String getLabel() { return label; }
    public String getMorningText() { return morningText; }
    public boolean watersCrops() { return watersCrops; }

    public static Weather forecast(Random rng) {
        int roll = rng.nextInt(100);
        for (Weather weather : values()) {
            if (roll < weather.chance) return weather;
            roll -= weather.chance;
        }
        return SUNNY;
    }

    /** A small icon about 20 pixels across, with (x, y) its top-left corner. */
    public void drawIcon(Graphics2D g, int x, int y) {
        switch (this) {
            case SUNNY:
                g.setColor(new Color(255, 210, 60));
                g.fillOval(x + 4, y + 4, 12, 12);
                for (int i = 0; i < 8; i++) {
                    double a = i * Math.PI / 4;
                    g.drawLine(x + 10 + (int) (8 * Math.cos(a)), y + 10 + (int) (8 * Math.sin(a)),
                               x + 10 + (int) (10 * Math.cos(a)), y + 10 + (int) (10 * Math.sin(a)));
                }
                break;
            case RAINY:
                g.setColor(new Color(190, 200, 215));
                g.fillOval(x, y + 4, 12, 10);
                g.fillOval(x + 6, y + 1, 14, 13);
                g.setColor(new Color(110, 170, 255));
                for (int i = 0; i < 3; i++) g.drawLine(x + 4 + i * 6, y + 16, x + 2 + i * 6, y + 20);
                break;
        }
    }
}
