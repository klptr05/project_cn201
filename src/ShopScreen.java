import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

/** The seed shop: every crop's numbers side by side, so the player can see what is worth growing. */
public class ShopScreen implements Screen {
    private static final int MAX_QUANTITY = 99;

    private final GamePanel game;
    private final FarmScreen farm;
    private final Crop[] crops = new Crop[CropFactory.NAMES.length];   // one sample of each, to read prices from
    private int row = 0;
    private int quantity = 1;
    private String status = "";

    public ShopScreen(GamePanel game, FarmScreen farm) {
        this.game = game;
        this.farm = farm;
        for (int i = 0; i < crops.length; i++) crops[i] = CropFactory.create(CropFactory.NAMES[i]);
    }

    @Override
    public void keyPressed(int code) {
        switch (code) {
            case KeyEvent.VK_UP:    case KeyEvent.VK_W: row = (row - 1 + crops.length) % crops.length; break;
            case KeyEvent.VK_DOWN:  case KeyEvent.VK_S: row = (row + 1) % crops.length; break;
            case KeyEvent.VK_LEFT:  case KeyEvent.VK_A: quantity = Math.max(1, quantity - 1); break;
            case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: quantity = Math.min(MAX_QUANTITY, quantity + 1); break;
            case KeyEvent.VK_ENTER: case KeyEvent.VK_SPACE: buy(); break;
            case KeyEvent.VK_ESCAPE: game.setScreen(farm); break;
            default: break;
        }
    }

    private void buy() {
        Crop crop = crops[row];
        int total = crop.getSeedPrice() * quantity;
        Farmer farmer = farm.getFarmer();
        try {
            farmer.spendGold(total);
            farmer.getSeedBag(crop.getName()).addSeeds(quantity);
            farmer.selectSeed(row);   // you'll want to plant what you just bought
            farm.floatAtFacing("-" + total + "G", new Color(255, 140, 120));
            status = "Bought " + quantity + " " + crop.getName() + " seeds. They're ready in your hand.";
        } catch (FarmException e) {
            status = e.getMessage();
        }
    }

    @Override
    public void draw(Graphics2D g) {
        farm.draw(g);
        Ui.dim(g, 150);
        int left = 40, top = 50, width = Ui.WIDTH - 80;
        Ui.paper(g, left, top, width, 400);
        Ui.centered(g, "SEED SHOP", 24, top + 38, Ui.WOOD);
        g.setFont(Ui.font(14));
        g.setColor(new Color(170, 120, 20));
        String gold = "Your gold " + farm.getFarmer().getGold() + "G";
        g.drawString(gold, left + width - 20 - g.getFontMetrics().stringWidth(gold), top + 38);

        g.setFont(Ui.font(13));
        g.setColor(Ui.WOOD);
        g.drawString(String.format("%-11s %6s %8s %7s %8s", "Crop", "Seed", "Grows", "Sells", "Profit"), left + 30, top + 80);
        for (int i = 0; i < crops.length; i++) {
            Crop crop = crops[i];
            int y = top + 110 + i * 30;
            if (i == row) {
                g.setColor(Ui.HIGHLIGHT);
                g.fillRoundRect(left + 18, y - 19, width - 36, 27, 8, 8);
            }
            g.setColor(Ui.INK);
            g.drawString(String.format("%-11s %5dG %3d days %6dG %+7dG", crop.getName(), crop.getSeedPrice(),
                    crop.getDaysToMature(), crop.getSellPrice(), crop.getSellPrice() - crop.getSeedPrice()), left + 30, y);
        }

        Crop chosen = crops[row];
        g.setColor(Ui.WOOD);
        g.drawString(chosen.getName() + ": " + chosen.getNote(), left + 30, top + 220);
        Ui.centered(g, "Quantity  < " + quantity + " >    Total " + chosen.getSeedPrice() * quantity + "G", 17, top + 268, Ui.INK);
        Ui.centered(g, status, 13, top + 305, status.startsWith("Bought") ? new Color(40, 130, 50) : Ui.WARNING);
        Ui.centered(g, "UP/DOWN crop   LEFT/RIGHT amount   ENTER buy   ESC leave", 12, top + 380, Ui.WOOD);
    }
}
