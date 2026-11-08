import java.awt.Color;
import java.awt.Graphics2D;

/** Slow to start, but after the first picking it fruits again every two days. */
public class Strawberry extends Crop {
    private static final int REGROW_DAYS = 2;

    @Override public String getName() { return "Strawberry"; }
    @Override public int getDaysToMature() { return 6; }
    @Override public int getSeedPrice() { return 100; }
    @Override public int getSellPrice() { return 60; }

    @Override
    public String getNote() {
        return "After the first harvest it fruits again every " + REGROW_DAYS + " days.";
    }

    @Override
    public boolean pick() {
        setDaysGrown(getDaysToMature() - REGROW_DAYS);
        return true;
    }

    @Override
    protected void drawProduce(Graphics2D g, int cx, int cy) {
        int[][] berries = {{-8, -6}, {3, -8}, {-2, -1}};
        for (int[] b : berries) {
            g.setColor(new Color(225, 40, 60));
            g.fillOval(cx + b[0], cy + b[1], 7, 8);
            g.setColor(new Color(255, 230, 120));
            g.fillRect(cx + b[0] + 2, cy + b[1] + 3, 1, 1);
            g.fillRect(cx + b[0] + 4, cy + b[1] + 5, 1, 1);
        }
    }
}
