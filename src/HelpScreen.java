import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

/** How to play. Any of ESC, ENTER or SPACE goes back to the screen it was opened from. */
public class HelpScreen implements Screen {
    private static final String[] LINES = {
        "#THE GOAL",
        "Earn " + FarmScreen.GOAL_GOLD + "G by the end of day " + FarmScreen.LAST_DAY + ".",
        "",
        "#EACH DAY",
        "1  Plant seeds on soil, then water them",
        "2  Sleep: watered crops grow one day",
        "3  Harvest ripe crops (no water drop on them)",
        "4  Ship them in the bin - paid next morning",
        "",
        "#KEYS",
        "Arrows / WASD   walk (a tap only turns)",
        "SPACE           does what the label says",
        "1-3 or Q/E      choose which seeds to plant",
        "ESC             pause menu",
        "",
        "#TIPS",
        "Rain waters every plot for you.",
        "Mrs. Daeng pays double for what she asks for.",
        "Refill the can at the pond. Be in bed by midnight.",
    };

    private final GamePanel game;
    private final Screen previous;

    public HelpScreen(GamePanel game, Screen previous) {
        this.game = game;
        this.previous = previous;
    }

    @Override
    public void keyPressed(int code) {
        if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
            game.setScreen(previous);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        previous.draw(g);
        Ui.dim(g, 150);
        Ui.paper(g, 60, 24, Ui.WIDTH - 120, Ui.HEIGHT - 48);
        Ui.centered(g, "How to play", 24, 62, Ui.WOOD);
        int y = 96;
        for (String line : LINES) {
            boolean heading = line.startsWith("#");   // "#" marks a heading
            g.setFont(Ui.font(heading ? 14 : 13));
            g.setColor(heading ? Ui.WARNING : Ui.INK);
            g.drawString(heading ? line.substring(1) : line, 90, y);
            y += line.isEmpty() ? 8 : 21;
        }
        Ui.centered(g, "press ESC to go back", 13, Ui.HEIGHT - 40, Ui.WOOD);
    }
}
