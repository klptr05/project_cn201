import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

/** "Are you sure?" on top of whatever screen asked. ESC always means no. */
public class ConfirmScreen implements Screen {
    private final GamePanel game;
    private final Screen previous;
    private final String question, detail;
    private final Menu menu = new Menu();

    /**
     * @param noFirst start with the "no" answer highlighted, for choices that are hard to undo
     * @param onYes runs after going back to `previous`, so it may move on to another screen itself
     */
    public ConfirmScreen(GamePanel game, Screen previous, String question, String detail,
                         String yesLabel, String noLabel, boolean noFirst, Runnable onYes) {
        this.game = game;
        this.previous = previous;
        this.question = question;
        this.detail = detail;
        menu.add(yesLabel, () -> {
            game.setScreen(previous);
            onYes.run();
        });
        menu.add(noLabel, () -> game.setScreen(previous));
        if (noFirst) menu.select(1);
    }

    @Override
    public void keyPressed(int code) {
        if (code == KeyEvent.VK_ESCAPE) game.setScreen(previous);
        else menu.keyPressed(code);
    }

    @Override
    public void draw(Graphics2D g) {
        previous.draw(g);
        Ui.dim(g, 120);
        Ui.paper(g, 110, 150, Ui.WIDTH - 220, 220);
        Ui.centered(g, question, 18, 192, Ui.INK);
        Ui.centered(g, detail, 13, 220, detail.endsWith("!") ? Ui.WARNING : Ui.WOOD);
        menu.draw(g, 280, Ui.INK);
    }
}
