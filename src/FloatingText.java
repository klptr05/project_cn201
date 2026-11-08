import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/** A short note such as "+60G" that drifts up from where something happened and fades out. */
public class FloatingText {
    private static final int LIFE = 70;   // ticks
    private static final Font FONT = new Font(Font.MONOSPACED, Font.BOLD, 14);

    private final String text;
    private final Color color;
    private final double x;
    private double y;
    private int age = 0;

    public FloatingText(String text, double x, double y, Color color) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void update() {
        y -= 0.6;
        age++;
    }

    public boolean isDone() { return age >= LIFE; }

    public void draw(Graphics2D g) {
        int alpha = 255 * (LIFE - age) / LIFE;
        g.setFont(FONT);
        int left = (int) x - g.getFontMetrics().stringWidth(text) / 2;
        g.setColor(new Color(0, 0, 0, alpha));
        g.drawString(text, left + 1, (int) y + 1);   // a shadow so it reads on any background
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        g.drawString(text, left, (int) y);
    }
}
