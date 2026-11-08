import java.awt.Color;
import java.awt.Graphics2D;

public class TitleScreen implements Screen {
    private final GamePanel game;
    private final Farm backdrop = new Farm();   // an empty farm behind the menu
    private final Menu menu = new Menu();

    public TitleScreen(GamePanel game) {
        this.game = game;
        String saved = game.getSaveGame().describe();
        if (saved != null) {
            menu.add("Continue  (" + saved + ")", () -> FarmScreen.load(game));
            menu.add("New farm", () -> game.setScreen(new ConfirmScreen(game, this,
                    "Start a new farm?", "Your saved farm will be erased when you next sleep.",
                    "Start over", "Keep my farm", true, () -> FarmScreen.newGame(game))));
        } else {
            menu.add("New farm", () -> FarmScreen.newGame(game));
        }
        menu.add("How to play", () -> game.setScreen(new HelpScreen(game, this)));
        menu.add("Quit", game::quit);
    }

    @Override
    public void keyPressed(int code) {
        menu.keyPressed(code);
    }

    @Override
    public void draw(Graphics2D g) {
        backdrop.draw(g);
        backdrop.getVillager().draw(g, game.getFrames());
        g.setColor(Ui.WOOD);
        g.fillRect(0, Ui.MAP_HEIGHT, Ui.WIDTH, Ui.HUD_HEIGHT);
        Ui.dim(g, 150);

        Ui.centered(g, "LITTLE FARM", 48, 130, Ui.HIGHLIGHT);
        Ui.centered(g, "Grandpa left you his farm - and a " + FarmScreen.GOAL_GOLD + "G debt.", 15, 180, Color.WHITE);
        Ui.centered(g, "Pay it off by the end of day " + FarmScreen.LAST_DAY + ".", 15, 202, Color.WHITE);
        menu.draw(g, 270, Color.WHITE);
        Ui.centered(g, "UP/DOWN choose    ENTER select", 13, 520, new Color(190, 190, 190));
    }
}
