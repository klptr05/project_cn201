import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;
import java.util.Random;

public class ajKhiao extends Villager {
        private static final String[] CHAT1 = {
        "Hello! You're the new one around here, aren't you?",
        "My name is Khiao, but you can call me AJ Khiao.",
        "Feel free to come and listen to my Dhamma teachings whenever you have some free time!"};
        private static final String[] CHAT2 = {"Hey, we meet again! Do you enjoy listening to Dhamma teachings?"};
        private static final String[] CHAT3 = {
        "Today, I'm going to try playing the guitar to make sharing Dhamma teachings more interesting. Come and watch sometime!",
        "I used to think life was boring, too, until I became interested in Buddhism. It has brought me so much peace of mind.",
        "Farming is fun, isn't it? It's perfectly normal to have tiring days sometimes. Don't give up!"};
        private int countforFirsttalk = 0;
        private boolean askedQuestion = false;
        private boolean hasOfferedRequest = false;

    public ajKhiao(String name, int col, int row) {
        super(name, col, row);
    }

    @Override
    public String talk(Farmer farmer) {
        if (request != null) { //ถ้ามีเควส เข้าลูปนี้
            if (!hasOfferedRequest) { //เช็คว่าเคยเปิดเควสหรือยัง (ยังไม่เคยเปิดเควส)
                hasOfferedRequest = true; //เปิดเควสครั้งแรก
                return name + ": I need some potato for my wife. Could you help me find some? I'll give you 100 Baht in return!";
            }
            //โค้ดส่วนทำเควส
            String crop = request.getCropName();
            if (farmer.getBasket().remove(crop, request.getAmount())) {
                farmer.earn(request.getReward());
                friendship = Math.min(MAX_FRIENDSHIP, friendship + 3);
                requestsDone++;
                request = null;
                hasOfferedRequest = false;
                return name + ": " + "thanks!";
            }
            // เคยเปิดเควสแล้ว วนคำขอไปเรื่อย ๆ
            return name + ": I need some potato for my wife. Could you bring me 3 potatoes? I'll give you 100 Baht in return!";
        }
        if (request == null) { //ถ้าไม่มีเควส เข้าลูปนี้
            if (countforFirsttalk < CHAT1.length) { //คุยรอบแรก (วันแรก)
                return name + ": " + CHAT1[countforFirsttalk++];
            }
            if (!askedQuestion) { //คุยรอบสอง (วันที่สอง)
                askedQuestion = true;
                if (farmer.getChoice()) { // ถามว่าชอบธรรมมะไหม
                    friendship = Math.min(MAX_FRIENDSHIP, friendship + 2);
                    return name + ": " + CHAT2[0] + "That's wonderful! I'm quite an expert in Buddhist teachings,"+ 
                                        "you know. It's great to see young people taking an interest in Buddhism!";
                } 
                else {
                    return name + ": " + CHAT2[2] + "Oh, I see! Well, if you ever get the chance, why not give it a try?";
                }
            }
            //ทำเควสเสร็จ สุ่มประโยคบทสนทนามา 1
            int randomIndex = (int) (Math.random() * CHAT3.length);
            return name + ": " + CHAT3[randomIndex];
        }
    }
}

    public void draw(Graphics2D g, int frames) { //เทสเป็นวัตถุกลม ๆ ก่อน
        int x = col * Farm.TILE;
        int y = row * Farm.TILE;

        g.setColor(Color.WHITE);
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
