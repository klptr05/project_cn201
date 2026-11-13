
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Random;

public abstract class Animal {
    public static final int MAX_FRIENDSHIP = 5;

    protected final String name;
    protected final int col, row;
    protected int friendship = 0;
    protected int chats = 0;

    public Animal(String name, int col, int row) {
        this.name = name;
        this.col = col;
        this.row = row;
    }

    public abstract String talk(Animal meengern);
    public abstract void draw(Graphics2D g, int frames);

}
