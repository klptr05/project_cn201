import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

/** The wooden bar under the farm: day, weather, gold, energy, can, seeds, basket and the current request. */
public class Hud {
    private Hud() {}

    public static void draw(Graphics2D g, Farmer farmer, GameClock clock, Villager villager) {
        int top = Ui.MAP_HEIGHT;
        g.setColor(Ui.WOOD);
        g.fillRect(0, top, Ui.WIDTH, Ui.HUD_HEIGHT);

        g.setFont(Ui.font(15));
        g.setColor(Color.WHITE);
        int day = Math.min(clock.getDay(), FarmScreen.LAST_DAY);
        g.drawString("Day " + day + "/" + FarmScreen.LAST_DAY + "  " + clock.timeText(), 10, top + 20);
        clock.getWeather().drawIcon(g, 168, top + 5);
        g.setColor(Ui.HIGHLIGHT);
        g.drawString(farmer.getGold() + "/" + FarmScreen.GOAL_GOLD + "G", 198, top + 20);

        g.setColor(Color.WHITE);
        g.drawString("Energy", 340, top + 20);
        g.setColor(new Color(40, 30, 20));
        g.fillRect(410, top + 9, 150, 13);
        g.setColor(farmer.getEnergy() < 25 ? new Color(230, 80, 60) : new Color(110, 210, 90));
        g.fillRect(410, top + 9, 150 * farmer.getEnergy() / Farmer.MAX_ENERGY, 13);

        g.setFont(Ui.font(12));
        slot(g, 8, top + 30, farmer.getCan().getLabel(), false);
        List<SeedBag> bags = farmer.getSeedBags();
        for (int i = 0; i < bags.size(); i++) {
            slot(g, 134 + i * 126, top + 30, (i + 1) + " " + bags.get(i).getLabel(), i == farmer.getSelectedSeedIndex());
        }
        g.setColor(new Color(200, 185, 160));
        g.drawString("ESC menu", 548, top + 45);

        g.setColor(new Color(230, 220, 200));
        g.drawString("Basket: " + farmer.getBasket(), 10, top + 71);
        Request request = villager.getRequest();
        if (request != null) {
            String wants = "Wanted: " + request.getAmount() + " " + request.getCropName()
                    + " (" + request.getDaysLeft() + "d) " + request.getReward() + "G";
            g.setColor(new Color(255, 170, 190));
            g.drawString(wants, Ui.WIDTH - 10 - g.getFontMetrics().stringWidth(wants), top + 71);
        }
    }

    private static void slot(Graphics2D g, int x, int y, String label, boolean chosen) {
        g.setColor(chosen ? Ui.HIGHLIGHT : new Color(100, 78, 55));
        g.fillRoundRect(x, y, 120, 22, 6, 6);
        g.setColor(chosen ? Color.BLACK : Color.WHITE);
        g.drawString(label, x + 6, y + 15);
    }
}
