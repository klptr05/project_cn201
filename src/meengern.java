import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Random;

public class meengern extends Animal {
    // สเตตัสความชอบยังไม่แน่ใจว่าจะปรับปรุงยังไง สมมติผู้เล่นเกรียนให้ปลาจนหัวใจเป็นพัน ยังไม่ได้กำหนด exception
    private static final String[] CHAT1 = {"Meowwwwwww! (Human, my name is Mee Ngern. You'd better obey me!)",
                                            "Meow, meowwwwwww! (Hey! Rub my belly, will you?!)",
                                            "Mraowww! (This feels so nice!)",
                                            "Myaaah! (Don't touch me! I'm in a bad mood today.)"};
    private int countforFirstTalk = 0;

    public meengern(String name, int col, int row) {
        super(name, col, row);
    }

    public String talk(Farmer farmer) {
        if (farmer.getBasket().remove("strawberry", 3)) {
            friendship = Math.min(MAX_FRIENDSHIP, friendship + 3);
            return name + ": " +  "Purr (Mee Ngern ate fish happily) [Friendship +3]";
        }
        if (countforFirstTalk < 1) {
            countforFirstTalk++;
            return name + ": Meowwwwwww! (Human, my name is Mee Ngern. You'd better obey me!)";
        }
        else {
            int randomIndex = (int) (Math.random() * CHAT1.length);
            return name + ": " + CHAT1[randomIndex];
        }
    }

    public void draw(Graphics2D g, int frames) {
        int x = col * Farm.TILE;
        int y = row * Farm.TILE;

        g.setColor(Color.ORANGE);
        g.fillOval(x + 4, y + 4, 24, 24);

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