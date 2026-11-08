import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/** A list of choices: up/down to move, ENTER or SPACE to choose. Each choice carries the code it runs. */
public class Menu {
    private static final int ROW_HEIGHT = 34;

    private final List<String> labels = new ArrayList<>();
    private final List<Runnable> actions = new ArrayList<>();
    private int selected = 0;

    /** Returns this menu, so items can be chained: new Menu().add(...).add(...) */
    public Menu add(String label, Runnable action) {
        labels.add(label);
        actions.add(action);
        return this;
    }

    public void select(int index) {
        if (index >= 0 && index < labels.size()) selected = index;
    }

    public void keyPressed(int code) {
        int count = labels.size();
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) selected = (selected - 1 + count) % count;
        else if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) selected = (selected + 1) % count;
        else if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) actions.get(selected).run();
    }

    /** Draws the items centred across the screen, the first one at `top`. */
    public void draw(Graphics2D g, int top, Color textColor) {
        g.setFont(Ui.font(18));
        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);
            int width = g.getFontMetrics().stringWidth(label);
            int x = (Ui.WIDTH - width) / 2, y = top + i * ROW_HEIGHT;
            if (i == selected) {
                g.setColor(Ui.HIGHLIGHT);
                g.fillRoundRect(x - 18, y - 21, width + 36, 30, 10, 10);
                g.setColor(Ui.INK);
            } else {
                g.setColor(textColor);
            }
            g.drawString(label, x, y);
        }
    }
}
