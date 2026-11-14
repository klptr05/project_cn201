import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.List;

/** The end-of-day report: what was shipped, the gold counting up, and tomorrow's weather. */
public class SummaryScreen implements Screen {
    private static final int COUNT_UP_TICKS = 60;   // also stops a held SPACE from skipping the report

    private final GamePanel game;
    private final FarmScreen farm;
    private final List<String> lines;
    private final int goldBefore, goldAfter;
    private int ticks = 0;

    public SummaryScreen(GamePanel game, FarmScreen farm, List<String> lines, int goldBefore, int goldAfter) {
        this.game = game;
        this.farm = farm;
        this.lines = lines;
        this.goldBefore = goldBefore;
        this.goldAfter = goldAfter;
    }

    @Override
    public void update() {
        ticks++;
    }

    @Override
    public void keyPressed(int code) {
        if (ticks < COUNT_UP_TICKS) return;
        if (code != KeyEvent.VK_SPACE && code != KeyEvent.VK_ENTER) return;
        if (farm.isMonthOver()) game.setScreen(new EndingScreen(game, farm));
        else farm.beginDay();
    }

    @Override
    public void draw(Graphics2D g) {
        farm.draw(g);
        Ui.dim(g, 200);
        int width = 480, height = 350, left = (Ui.WIDTH - width) / 2, top = 80;
        Ui.paper(g, left, top, width, height);

        GameClock clock = farm.getClock();
        Ui.centered(g, "End of Day " + (clock.getDay() - 1), 24, top + 40, Ui.WOOD);

        g.setFont(Ui.font(13));
        g.setColor(Ui.INK);
        FontMetrics fm = g.getFontMetrics();
        int maxWidth = width - 48;
        int y = top + 78;
        for (String line : lines) {
            StringBuilder current = new StringBuilder();
            for (String word : line.split(" ")) {
                String test = current.length() == 0 ? word : current + " " + word;
                if (fm.stringWidth(test) <= maxWidth) {
                    current = new StringBuilder(test);
                } else {
                    g.drawString(current.toString(), left + 24, y);
                    y += 20;
                    current = new StringBuilder(word);
                }
            }
            if (current.length() > 0) {
                g.drawString(current.toString(), left + 24, y);
                y += 20;
            }
        }

        double progress = Math.min(1.0, ticks / (double) COUNT_UP_TICKS);
        int shown = goldBefore + (int) Math.round((goldAfter - goldBefore) * progress);
        Ui.centered(g, "Gold  " + shown + "G", 26, top + 220, new Color(190, 140, 20));

        if (!farm.isMonthOver()) {
            g.setFont(Ui.font(15));
            g.setColor(Ui.INK);
            String weather = "Tomorrow: " + clock.getWeather().getLabel();
            int textX = (Ui.WIDTH - g.getFontMetrics().stringWidth(weather)) / 2;
            g.drawString(weather, textX, top + 262);
            clock.getWeather().drawIcon(g, textX - 28, top + 246);

            int daysLeft = FarmScreen.LAST_DAY - clock.getDay() + 1;
            int stillNeeded = Math.max(0, FarmScreen.GOAL_GOLD - goldAfter);
            Ui.centered(g, daysLeft + " days left, " + stillNeeded + "G still needed", 15, top + 288, Ui.INK);
        } else {
            Ui.centered(g, "That was the last day of the month...", 15, top + 275, Ui.INK);
        }

        if (ticks >= COUNT_UP_TICKS && (game.getFrames() / 30) % 2 == 0) {
            Ui.centered(g, "press SPACE", 14, top + height - 18, Ui.WOOD);
        }
    }
}