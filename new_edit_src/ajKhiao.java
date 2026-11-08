package new_edit_src;

public class ajKhiao extends Villager {
        private static final String[] CHAT1 = {
        "Hello! You're the new one around here, aren't you?",
        "My name is Khiao, but you can call me AJ Khiao.",
        "Feel free to come and listen to my Dhamma teachings whenever you have some free time!"};
        private static final String[] CHAT2 = {"Hey, we meet again! Do you enjoy listening to Dhamma teachings?"};
        private int countforFirsttalk = 0;

    public ajKhiao(String name, int col, int row) {
        super(name, col, row);
    }

    public String talk(Farmer farmer) {
        if (request == null) {
            if (countforFirsttalk == 0) { //เช็คว่าคุยไปรอบแรกหรือยัง (ถ้ายังวนลูป CHAT1 ก่อน)
                countforFirsttalk++;
                return name + ": " + CHAT1[chats++ % CHAT1.length];
            }
            String chat2loop = name + ": " + CHAT2[0];
            if (aChoice == yes) { // ถามว่าชอบธรรมมะไหม เขียนคร่าว ๆ ก่อน ยังไม่ถูกไวยากรณ์ เดี๋ยวมาแก้
                friendship = Math.min(MAX_FRIENDSHIP, friendship + 2);
                return name + ": " + "That's wonderful! I'm quite an expert in Buddhist teachings,"+ 
                                    "you know. It's great to see young people taking an interest in Buddhism!";
            } 
            else {
                return name + ": " + "Oh, I see! Well, if you ever get the chance, why not give it a try?";
            }
        }

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

    public void draw() {
    }

}