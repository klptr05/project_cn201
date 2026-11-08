import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Random;

/** A neighbour who stands on the farm, chats, and now and then asks for crops at double the price. */
public class Villager {
    public static final int MAX_FRIENDSHIP = 5;
    private static final int FIRST_REQUEST_DAY = 2;
    private static final String[] CHATTER = {
        "Lovely day for farming, isn't it?",
        "Your grandpa grew the best turnips in the valley.",
        "Strawberries keep fruiting once they start, you know.",
        "Don't stay up past midnight - you'll pass out!",
    };

    private final String name;
    private final int col, row;
    private Request request;
    private int friendship = 0;
    private int requestsDone = 0;
    private int chats = 0;

    public Villager(String name, int col, int row) {
        this.name = name;
        this.col = col;
        this.row = row;
    }

    /** @return what the villager says. Hands over the reward if the farmer brought what was asked for. */
    public String talk(Farmer farmer) {
        if (request == null) return name + ": " + CHATTER[chats++ % CHATTER.length];

        String crop = request.getCropName();
        if (farmer.getBasket().remove(crop, request.getAmount())) {
            farmer.earn(request.getReward());
            friendship = Math.min(MAX_FRIENDSHIP, friendship + 1);
            requestsDone++;
            String thanks = name + ": Wonderful, thank you! Here's " + request.getReward() + "G.";
            request = null;
            return thanks;
        }
        return name + ": Could you bring me " + request.getAmount() + " x " + crop + "? I'll pay "
                + request.getReward() + "G. (" + request.getDaysLeft() + " days left)";
    }

    /** @return news for the end-of-day summary, or null if nothing happened */
    public String newDay(int day, Random rng) {
        String news = null;
        if (request != null && !request.passDay()) {
            news = name + " gave up waiting for the " + request.getCropName() + ".";
            friendship = Math.max(0, friendship - 1);
            request = null;
        }
        if (request == null && (day == FIRST_REQUEST_DAY || rng.nextInt(3) == 0)) {
            request = Request.random(day, rng);
            String asked = name + " has a request for you!";
            news = news == null ? asked : news + " " + asked;
        }
        return news;
    }

    public String getName() { return name; }
    public int getCol() { return col; }
    public int getRow() { return row; }
    public Request getRequest() { return request; }
    public int getFriendship() { return friendship; }
    public int getRequestsDone() { return requestsDone; }

    public void setRequest(Request request) { this.request = request; }

    public void restore(int friendship, int requestsDone) {
        if (friendship < 0 || friendship > MAX_FRIENDSHIP || requestsDone < 0) {
            throw new IllegalArgumentException("bad villager state: " + friendship + " " + requestsDone);
        }
        this.friendship = friendship;
        this.requestsDone = requestsDone;
    }

    public void draw(Graphics2D g, int frames) {
        int x = col * Farm.TILE, y = row * Farm.TILE;
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
