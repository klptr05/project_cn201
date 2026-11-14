import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/** Yes / No question on top of the farm, controlled with UP/DOWN and ENTER/SPACE like Menu. */
public class ChoiceScreen implements Screen {
    private final GamePanel game;
    private final Screen previous;
    private final String speaker;
    private final String question;
    private final Menu menu = new Menu();

    public ChoiceScreen(GamePanel game, Screen previous, String speaker, String question, Consumer<Boolean> onAnswer) {
        this.game = game;
        this.previous = previous;
        this.speaker = speaker;
        this.question = question;
        menu.add("Yes", () -> {
            game.setScreen(previous);
            onAnswer.accept(true);
        });
        menu.add("No", () -> {
            game.setScreen(previous);
            onAnswer.accept(false);
        });
    }

    @Override
    public void keyPressed(int code) {
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W
                || code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S
                || code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
            menu.keyPressed(code);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        previous.draw(g);
        Ui.dim(g, 120);
        int width = Ui.WIDTH - 180, height = 250, left = (Ui.WIDTH - width) / 2, top = 135;
        Ui.paper(g, left, top, width, height);

        Ui.centered(g, speaker, 15, top + 34, Ui.WARNING);

        g.setFont(Ui.font(14));
        int maxWidth = width - 48;
        int y = top + 68;
        StringBuilder line = new StringBuilder();
        for (String word : question.split(" ")) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (g.getFontMetrics().stringWidth(test) <= maxWidth) {
                line = new StringBuilder(test);
            } else {
                Ui.centered(g, line.toString(), 14, y, Ui.INK);
                y += 22;
                line = new StringBuilder(word);
            }
        }
        if (line.length() > 0) Ui.centered(g, line.toString(), 14, y, Ui.INK);

        menu.draw(g, top + 135, Ui.INK);
        Ui.centered(g, "UP/DOWN choose    ENTER / SPACE select", 12, top + height - 16, Ui.WOOD);
    }
}