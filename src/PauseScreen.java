import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

/** ESC on the farm. Time stands still while it is open. */
public class PauseScreen implements Screen {
    private final GamePanel game;
    private final FarmScreen farm;
    private final Menu menu = new Menu();

    public PauseScreen(GamePanel game, FarmScreen farm) {
        this.game = game;
        this.farm = farm;
        menu.add("Resume", () -> game.setScreen(farm))
            .add("How to play", () -> game.setScreen(new HelpScreen(game, this)))
            .add("Quit to title", () -> game.setScreen(new ConfirmScreen(game, this,
                    "Quit to the title?", "Everything since you last slept will be lost.",
                    "Quit", "Keep playing", true, game::showTitle)));
    }

    @Override
    public void keyPressed(int code) {
        if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_P) game.setScreen(farm);
        else menu.keyPressed(code);
    }

    @Override
    public void draw(Graphics2D g) {
        farm.draw(g);
        Ui.dim(g, 170);
        Ui.centered(g, "PAUSED", 40, 170, Color.WHITE);
        menu.draw(g, 240, Color.WHITE);
        Ui.centered(g, "The farm saves itself each night when you sleep.", 13, 400, new Color(200, 200, 200));
    }
}
