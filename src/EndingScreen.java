import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

/** Day 30 is over: was grandpa's debt paid? */
public class EndingScreen implements Screen {
    private final GamePanel game;
    private final FarmScreen farm;

    public EndingScreen(GamePanel game, FarmScreen farm) {
        this.game = game;
        this.farm = farm;
    }

    @Override
    public void keyPressed(int code) {
        if (code == KeyEvent.VK_ENTER) game.showTitle();
    }

    @Override
    public void draw(Graphics2D g) {
        farm.draw(g);
        Ui.dim(g, 215);
        int gold = farm.getFarmer().getGold();
        int goal = FarmScreen.GOAL_GOLD;
        Ui.centered(g, "DAY " + FarmScreen.LAST_DAY + " IS OVER", 38, 100, Color.WHITE);
        Ui.centered(g, "Grandpa's debt   " + goal + "G", 18, 155, new Color(200, 200, 200));
        Ui.centered(g, "Your gold        " + gold + "G", 18, 180, Ui.HIGHLIGHT);
        String verdict;
        if (gold >= 2 * goal) verdict = "A legendary farmer! The whole valley knows your name.";
        else if (gold >= goal) verdict = "Debt paid - the farm is yours!";
        else if (gold >= goal / 2) verdict = "So close... but the bank takes the farm.";
        else verdict = "The bank takes the farm. Grandpa would be sad.";
        Ui.centered(g, verdict, 18, 225, gold >= goal ? new Color(130, 230, 110) : new Color(255, 110, 100));
        Ui.centered(g, "Earned this month  " + farm.getFarmer().getTotalEarned() + "G", 15, 275, Color.WHITE);

        int y = 305;
        for (Villager v : farm.getFarm().getVillagers()) {
            Ui.centered(g, v.getName() + " - Requests: " + v.getRequestsDone()
                    + " | Friendship: " + v.getFriendship() + "/" + Villager.MAX_FRIENDSHIP, 14, y, Color.WHITE);
            y += 24;
        }
        for (Animal a : farm.getFarm().getAnimals()) {
            Ui.centered(g, a.getName() + " - Friendship: " + a.getFriendship() + "/" + Animal.MAX_FRIENDSHIP,
                    14, y, Color.WHITE);
            y += 24;
        }

        if ((game.getFrames() / 30) % 2 == 0) Ui.centered(g, "press ENTER", 18, 460, Color.WHITE);
    }
}
