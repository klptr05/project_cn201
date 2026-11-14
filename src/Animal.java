
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Random;

public abstract class Animal {
    public static final int MAX_FRIENDSHIP = 5;
    protected final String name;
    protected final int col, row;
    protected int friendship = 3;
    protected int chats = 0;

    public Animal(String name, int col, int row) {
        this.name = name;
        this.col = col;
        this.row = row;
    }

    public abstract String talk(Farmer farmer);
    public abstract void draw(Graphics2D g, int frames);

    public String getName() { return name; }
    public int getCol() { return col; }
    public int getRow() { return row; }
    public int getFriendship() { return friendship; }

    public void restore(int friendship) {
        if (friendship < 0 || friendship > MAX_FRIENDSHIP) {
            throw new IllegalArgumentException("bad animal state: " + friendship);
        }
        this.friendship = friendship;
    }
}