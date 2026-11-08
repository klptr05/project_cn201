import java.awt.Color;
import java.awt.Graphics2D;

public class Potato extends Crop {
    @Override public String getName() { return "Potato"; }
    @Override public int getDaysToMature() { return 5; }
    @Override public int getSeedPrice() { return 50; }
    @Override public int getSellPrice() { return 140; }

    @Override
    protected void drawProduce(Graphics2D g, int cx, int cy) {
        g.setColor(new Color(185, 140, 85));
        g.fillOval(cx - 10, cy - 3, 11, 8);
        g.fillOval(cx - 1, cy - 1, 11, 8);
        g.setColor(new Color(120, 85, 50));
        g.fillOval(cx - 6, cy, 2, 2);
        g.fillOval(cx + 4, cy + 2, 2, 2);
    }
}
