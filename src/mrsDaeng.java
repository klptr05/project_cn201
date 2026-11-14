import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;
import java.util.Random;

public class mrsDaeng extends Villager {
    private static final String[] CHAT1 = {
        "Lovely day for farming, isn't it?",
        "Your grandpa grew the best turnips in the valley.",
        "Strawberries keep fruiting once they start, you know.",
        "Don't stay up past midnight - you'll pass out!",
    };

    public mrsDaeng(String name, int col, int row) {
        super(name, col, row);
    }

    @Override
    public String talk(Farmer farmer) {
        if (request == null) return name + ": " + CHAT1[chats++ % CHAT1.length];

        String crop = request.getCropName();
        if (farmer.getBasket().remove(crop, request.getAmount())) {
            farmer.earn(request.getReward());
            friendship = Math.min(MAX_FRIENDSHIP, friendship + 1);
            requestsDone++;
            String thanks = name + ": Wonderful, thank you! Here's " + request.getReward() + "G." + "[Friendship +1]";
            request = null;
            return thanks;
        }
        return name + ": Could you bring me " + request.getAmount() + " x " + crop + "? I'll pay "
                + request.getReward() + "G. (" + request.getDaysLeft() + " days left)";
    }

    @Override
    public void draw(Graphics2D g, int frames) {
        int x = col * Farm.TILE;
        int y = row * Farm.TILE;
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(x + 7, y + 26, 18, 6);
        g.setColor(new Color(215, 105, 145));           // dress
        g.fillPolygon(new int[] {x + 11, x + 21, x + 25, x + 7}, new int[] {y + 14, y + 14, y + 29, y + 29}, 4);
        g.setColor(new Color(250, 210, 170));           // face
        g.fillOval(x + 9, y + 3, 14, 14);
        g.setColor(new Color(40, 30, 30));
        g.fillRect(x + 12, y + 10, 2, 2);
        g.fillRect(x + 18, y + 10, 2, 2);
        g.setColor(new Color(175, 170, 175));           // grey hair and bun
        g.fillArc(x + 8, y + 1, 16, 12, 0, 180);
        g.fillOval(x + 12, y - 4, 8, 7);

        if (request != null) {   // a bobbing "!" bubble while she wants something
            int bob = (frames / 15) % 2;
            g.setColor(Color.WHITE);
            g.fillOval(x + 20, y - 18 - bob, 14, 16);
            g.setColor(new Color(220, 50, 50));
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
            g.drawString("!", x + 24, y - 5 - bob);
        }
    }
}
