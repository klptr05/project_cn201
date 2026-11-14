import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Random;

public class meengern extends Animal {
    private static final String[] CHAT1 = {
        "Meowwwwwww! (Human, my name is Mee Ngern. You'd better obey me! I'd love to if you bring me 3 strawberries!)",
        "Meow, meowwwwwww! (Hey! Rub my belly, will you?!)",
        "Mraowww! (This feels so nice!)",
        "Myaaah! (Don't touch me! I'm in a bad mood today.)"
    };
    private int countforFirstTalk = 0;

    public meengern(String name, int col, int row) {
        super(name, col, row);
    }

    @Override
    public String talk(Farmer farmer) {
        if (farmer.getBasket().remove("Strawberry", 3)) {
            friendship = Math.min(MAX_FRIENDSHIP, friendship + 1);
            return name + ": Purr~ (Mee Ngern ate 3 Strawberries happily) [Friendship +1]";
        }
        if (countforFirstTalk < 1) {
            countforFirstTalk++;
            return name + ": " + CHAT1[0];
        }
        int randomIndex = (int) (Math.random() * CHAT1.length);
        return name + ": " + CHAT1[randomIndex];
    }

    @Override
    public void draw(Graphics2D g, int frames) {
        int x = col * Farm.TILE;
        int y = row * Farm.TILE;
        g.setColor(new Color(220, 130, 45));
        g.fillOval(x + 2, y + 18, 9, 5);           // Tail
        g.setColor(new Color(240, 155, 55));
        g.fillOval(x + 8, y + 16, 17, 14);          // Body
        g.fillOval(x + 8, y + 26, 7, 4);
        g.fillOval(x + 19, y + 26, 7, 4);           // Feet
        g.setColor(new Color(230, 140, 45));
        g.fillPolygon(new int[]{x + 7, x + 8, x + 14}, new int[]{y + 10, y + 1, y + 8}, 3);
        g.fillPolygon(new int[]{x + 18, x + 24, x + 25}, new int[]{y + 8, y + 1, y + 10}, 3); // Ears
        g.setColor(new Color(255, 175, 75));
        g.fillOval(x + 6, y + 5, 20, 17);           // Face
        g.setColor(Color.BLACK);
        g.fillOval(x + 11, y + 11, 3, 3);
        g.fillOval(x + 19, y + 11, 3, 3);           // Eyes
        g.setColor(new Color(230, 100, 120));
        g.fillOval(x + 15, y + 15, 3, 2);           // Nose
        g.setColor(Color.BLACK);
        g.drawLine(x + 16, y + 17, x + 16, y + 19);
        g.drawLine(x + 16, y + 19, x + 14, y + 20);
        g.drawLine(x + 16, y + 19, x + 18, y + 20); // Mouth
        g.drawLine(x + 5, y + 16, x + 11, y + 17);
        g.drawLine(x + 21, y + 17, x + 27, y + 16); // Whiskers
    }
}