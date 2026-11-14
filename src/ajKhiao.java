import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Random;

public class ajKhiao extends Villager {
    private static final String[] CHAT1 = {
        "Hello! You're the new one around here, aren't you?",
        "My name is Khiao, but you can call me AJ Khiao.",
        "Feel free to come and listen to my Dhamma teachings whenever you have some free time!"
    };
    private static final String[] CHAT2 = {
        "Hey, we meet again! Do you enjoy listening to Dhamma teachings?"
    };
    private static final String[] CHAT3 = {
        "Today, I'm going to try playing the guitar to make sharing Dhamma teachings more interesting. Come and watch sometime!",
        "I used to think life was boring, too, until I became interested in Buddhism. It has brought me so much peace of mind.",
        "Farming is fun, isn't it? It's perfectly normal to have tiring days sometimes. Don't give up!"
    };

    private int chat1Index = 0;
    private boolean askedQuestion = false;
    private boolean questCompleted = false;

    public ajKhiao(String name, int col, int row) {
        super(name, col, row);
    }

    public boolean shouldAskQuestion() {
        return currentDay == 2 && !askedQuestion;
    }

    public String getQuestionText() {
        return CHAT2[0];
    }

    public String answerQuestion(Farmer farmer, boolean yes) {
        askedQuestion = true;
        farmer.setChoice(yes);
        if (yes) {
            friendship = Math.min(MAX_FRIENDSHIP, friendship + 1);
            return name + ": That's wonderful! I'm quite an expert in Buddhist teachings, "
                    + "you know. It's great to see young people taking an interest in Buddhism! [Friendship +1]";
        } else {
            friendship = Math.max(0, friendship - 1);
            return name + ": Oh, I see! Well, if you ever get the chance, why not give it a try? [Friendship -1]";
        }
    }

    @Override
    public String talk(Farmer farmer) {
        if (currentDay == 1) {
            return name + ": " + CHAT1[chat1Index++ % CHAT1.length];
        }
        if (!questCompleted && request != null) {
            String crop = request.getCropName();
            if (farmer.getBasket().remove(crop, request.getAmount())) {
                int reward = request.getReward();
                farmer.earn(reward);
                friendship = Math.min(MAX_FRIENDSHIP, friendship + 2);
                requestsDone++;
                questCompleted = true;
                request = null;
                return name + ": Thank you for the " + crop + "! Here is " + reward + "G. [Friendship +2]";
            }
            return name + ": I need some " + crop + " for my wife. Could you bring me "
                    + request.getAmount() + " " + crop + "? I'll give you " + request.getReward() + "G in return!";
        }
        return randomChat3();
    }

    private String randomChat3() {
        int randomIndex = (int) (Math.random() * CHAT3.length);
        return name + ": " + CHAT3[randomIndex];
    }

    @Override
    public String newDay(int day, Random rng) {
        this.currentDay = day;
        if (day >= FIRST_REQUEST_DAY && !questCompleted && request == null) {
            request = Request.random(day, rng);
            return name + " has a request for you!";
        }
        return null;
    }

    public boolean isAskedQuestion() { return askedQuestion; }
    public boolean isQuestCompleted() { return questCompleted; }

    public void restoreState(boolean askedQuestion, boolean questCompleted) {
        this.askedQuestion = askedQuestion;
        this.questCompleted = questCompleted;
    }

    @Override
    public void draw(Graphics2D g, int frames) {
        int x = col * Farm.TILE;
        int y = row * Farm.TILE;
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(x + 7, y + 26, 18, 6);
        g.setColor(new Color(60, 140, 75));           // outfit
        g.fillPolygon(new int[] {x + 11, x + 21, x + 25, x + 7}, new int[] {y + 14, y + 14, y + 29, y + 29}, 4);
        g.setColor(new Color(250, 210, 170));          // face
        g.fillOval(x + 9, y + 3, 14, 14);
        g.setColor(new Color(40, 30, 30));
        g.fillRect(x + 12, y + 10, 2, 2);
        g.fillRect(x + 18, y + 10, 2, 2);
        g.setColor(new Color(175, 170, 175));          // long grey hair
        g.fillRect(x + 7, y + 1, 18, 6);
        g.fillRect(x + 7, y + 7, 4, 20);
        g.fillRect(x + 21, y + 7, 4, 20);

        boolean hasAlert = (currentDay == 2 && !askedQuestion) || (currentDay >= FIRST_REQUEST_DAY && !questCompleted);
        if (hasAlert) {
            int bob = (frames / 15) % 2;
            g.setColor(Color.WHITE);
            g.fillOval(x + 20, y - 18 - bob, 14, 16);
            g.setColor(new Color(220, 50, 50));
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
            g.drawString("!", x + 24, y - 5 - bob);
        }
    }
}