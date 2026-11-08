package new_edit_src;

public class Vivienne extends Villager {
    private static final String[] CHATTER = {
        "Um... You're the new farmer, right? Hmm... You're better-looking than I expected.",
        "I'm Vivienne. Feel free to say hello whenever we meet."
    };

    public Vivienne(String name, int col, int row) {
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