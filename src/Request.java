import java.util.Random;

/** "Bring me 3 turnips within 6 days and I'll pay double." */
public class Request {
    private final String cropName;
    private final int amount;
    private final int reward;
    private int daysLeft;

    public Request(String cropName, int amount, int reward, int daysLeft) {
        CropFactory.create(cropName);   // throws IllegalArgumentException if there is no such crop
        if (amount < 1 || reward < 0 || daysLeft < 1) {
            throw new IllegalArgumentException("bad request: " + amount + " " + cropName + " for " + reward + "G in " + daysLeft + " days");
        }
        this.cropName = cropName;
        this.amount = amount;
        this.reward = reward;
        this.daysLeft = daysLeft;
    }

    /** Early on she only asks for turnips; later crops join in as the month goes on. */
    public static Request random(int day, Random rng) {
        int known = day < 8 ? 1 : day < 15 ? 2 : CropFactory.NAMES.length;
        String name = CropFactory.NAMES[rng.nextInt(known)];
        Crop crop = CropFactory.create(name);
        int amount = 2 + rng.nextInt(3);
        return new Request(name, amount, crop.getSellPrice() * amount * 2, crop.getDaysToMature() + 3);
    }

    /** @return false once time has run out */
    public boolean passDay() {
        daysLeft--;
        return daysLeft > 0;
    }

    public String getCropName() { return cropName; }
    public int getAmount() { return amount; }
    public int getReward() { return reward; }
    public int getDaysLeft() { return daysLeft; }
}
