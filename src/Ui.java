import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/** Sizes, colours and drawing helpers shared by every screen. */
public final class Ui {
    public static final int WIDTH = Farm.COLS * Farm.TILE;        // 640
    public static final int MAP_HEIGHT = Farm.ROWS * Farm.TILE;   // 480
    public static final int HUD_HEIGHT = 80;
    public static final int HEIGHT = MAP_HEIGHT + HUD_HEIGHT;     // 560

    public static final Color WOOD = new Color(70, 52, 36);
    public static final Color PAPER = new Color(250, 240, 215);
    public static final Color INK = new Color(60, 45, 35);
    public static final Color HIGHLIGHT = new Color(255, 230, 120);
    public static final Color WARNING = new Color(200, 60, 40);

    private Ui() {}   // only static helpers here

    public static Font font(int size) {
        return new Font(Font.MONOSPACED, Font.BOLD, size);
    }

    public static void centered(Graphics2D g, String text, int size, int y, Color color) {
        g.setFont(font(size));
        g.setColor(color);
        g.drawString(text, (WIDTH - g.getFontMetrics().stringWidth(text)) / 2, y);
    }

    /** Darkens everything drawn so far, so a menu on top stands out. */
    public static void dim(Graphics2D g, int alpha) {
        g.setColor(new Color(0, 0, 0, alpha));
        g.fillRect(0, 0, WIDTH, HEIGHT);
    }

    /** The parchment box that dialogs sit in. */
    public static void paper(Graphics2D g, int x, int y, int width, int height) {
        g.setColor(PAPER);
        g.fillRoundRect(x, y, width, height, 16, 16);
        g.setColor(WOOD);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(x, y, width, height, 16, 16);
        g.setStroke(new BasicStroke(1));
    }
}
