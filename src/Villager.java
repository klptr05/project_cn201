import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Random;

/** A neighbour who stands on the farm, chats, and now and then asks for crops at double the price. */
public abstract class Villager {
    public static final int MAX_FRIENDSHIP = 5;
    public static final int FIRST_REQUEST_DAY = 2;

    protected final String name;
    protected final int col, row;
    protected Request request;
    protected int friendship = 0;
    protected int requestsDone = 0;
    protected int chats = 0;

    public Villager(String name, int col, int row) {
        this.name = name;
        this.col = col;
        this.row = row;
    }
    
    /** @return what the villager says. Hands over the reward if the farmer brought what was asked for. */
    public abstract String talk(Farmer farmer);
    public abstract void draw(Graphics2D g, int frames);

    /** @return news for the end-of-day summary, or null if nothing happened */
    public String newDay(int day, Random rng) { //อันนี้คือเหี้ยไรกุงง
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

}