import java.awt.Color;
import java.awt.Graphics2D;
import java.util.EnumSet;
import java.util.Set;

/** The first-day to-do list in the corner of the screen. Each step ticks itself off as the player does it. */
public class Checklist {
    public enum Step {
        PLANT("Plant the turnip seeds"),
        WATER("Water them"),
        TALK("Say hello to Mrs. Daeng"),
        SLEEP("Go to bed in the house");

        private final String text;

        Step(String text) {
            this.text = text;
        }
    }

    private final Set<Step> done = EnumSet.noneOf(Step.class);

    public void complete(Step step) { done.add(step); }
    public boolean isDone(Step step) { return done.contains(step); }

    public void draw(Graphics2D g, int x, int y) {
        int width = 250, height = 28 + Step.values().length * 20;
        g.setColor(new Color(250, 240, 215, 225));
        g.fillRoundRect(x, y, width, height, 12, 12);
        g.setColor(Ui.WOOD);
        g.drawRoundRect(x, y, width, height, 12, 12);
        g.setFont(Ui.font(13));
        g.drawString("Your first day", x + 10, y + 18);

        g.setFont(Ui.font(12));
        int line = y + 38;
        for (Step step : Step.values()) {
            boolean ticked = done.contains(step);
            g.setColor(Ui.WOOD);
            g.drawRect(x + 10, line - 10, 11, 11);
            if (ticked) {
                g.setColor(new Color(40, 150, 60));
                g.drawLine(x + 12, line - 4, x + 15, line);
                g.drawLine(x + 15, line, x + 21, line - 11);
            }
            g.setColor(ticked ? new Color(150, 140, 125) : Ui.INK);
            g.drawString(step.text, x + 30, line);
            line += 20;
        }
    }
}
