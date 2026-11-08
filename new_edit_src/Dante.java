package new_edit_src;

public class Dante extends Villager {
    private static final String[] CHATTER = {
        "Hey, I'm Dante. I'm about the same age as you, so you don't have to be so formal with me."
    };

    public Dante(String name, int col, int row) {
        super(name, col, row);
    }

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

        public void draw() {

    }

}