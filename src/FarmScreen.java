import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** The farm itself: walking, farming, talking, and ending each day. */
public class FarmScreen implements Screen {
    public static final int GOAL_GOLD = 5000;   // grandpa's debt
    public static final int LAST_DAY = 30;
    private static final int DOOR_COL = 2, DOOR_ROW = 4;   // where the farmer wakes up
    private static final int STARTING_GOLD = 300;
    private static final int STARTING_SEEDS = 5;
    private static final int LATE = 22 * 60;    // time of the "go to bed" warning, in minutes
    private static final int FADE_TICKS = 70;
    private static final int MESSAGE_TICKS = 220;

    private final GamePanel game;
    private final Random rng = new Random();
    private final Farm farm = new Farm();
    private final Farmer farmer = new Farmer(DOOR_COL, DOOR_ROW, STARTING_GOLD);
    private final GameClock clock = new GameClock();
    private final Bag<String> shippedToday = new Bag<>();   // paid out when the farmer sleeps
    private final List<FloatingText> floatingTexts = new ArrayList<>();
    private Checklist checklist;                           // only on the first day of a new game
    private int fadeTicks, messageTicks;
    private boolean warnedLate;
    private String message = "";

    private FarmScreen(GamePanel game) {
        this.game = game;
        farmer.getSeedBag("Turnip").addSeeds(STARTING_SEEDS);
    }

    public static FarmScreen newGame(GamePanel game) {
        FarmScreen screen = new FarmScreen(game);
        screen.checklist = new Checklist();
        screen.say("Walk to the brown soil and press SPACE - the label shows what it will do");
        screen.beginDay();
        return screen;
    }

    public static FarmScreen load(GamePanel game) {
        FarmScreen screen = new FarmScreen(game);
        try {
            game.getSaveGame().loadInto(screen.farm, screen.farmer, screen.clock);
            screen.say("Welcome back!");
        } catch (IOException e) {
            screen = newGame(game);
            screen.say("Couldn't read the save (" + e.getMessage() + ") - started a new farm");
        }
        screen.beginDay();
        return screen;
    }

    public void beginDay() {
        fadeTicks = FADE_TICKS;
        warnedLate = false;
        game.setScreen(this);
    }

    public Farm getFarm() { return farm; }
    public Farmer getFarmer() { return farmer; }
    public GameClock getClock() { return clock; }
    public boolean isMonthOver() { return clock.getDay() > LAST_DAY; }

    public void say(String text) {
        message = text;
        messageTicks = MESSAGE_TICKS;
    }

    /** A note such as "-50G" rising from the tile the farmer is facing. */
    public void floatAtFacing(String text, Color color) {
        floatAt(farmer.facingCol(), farmer.facingRow(), text, color);
    }

    private void floatAt(int col, int row, String text, Color color) {
        floatingTexts.add(new FloatingText(text, col * Farm.TILE + Farm.TILE / 2.0, row * Farm.TILE, color));
    }

    // ---- keys -----------------------------------------------------------------

    @Override
    public void keyPressed(int code) {
        if (fadeTicks > 0) return;
        if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_P) game.setScreen(new PauseScreen(game, this));
        else if (code == KeyEvent.VK_SPACE) doAction();
        else if (code == KeyEvent.VK_Q) farmer.cycleSeed(-1);
        else if (code == KeyEvent.VK_E) farmer.cycleSeed(1);
        else if (code >= KeyEvent.VK_1 && code <= KeyEvent.VK_9) farmer.selectSeed(code - KeyEvent.VK_1);
    }

    /** What SPACE does depends only on what the farmer is facing; hint() describes the same choice. */
    private void doAction() {
        int col = farmer.facingCol(), row = farmer.facingRow();
        Villager villager = farm.villagerAt(col, row);
        if (villager != null) {
            talkTo(villager);
            return;
        }
        Tile tile = farm.tileAt(col, row);
        if (tile == null) return;
        try {
            switch (tile.getType()) {
                case HOUSE:
                    askToSleep();
                    return;
                case SHOP:
                    game.setScreen(new ShopScreen(game, this));
                    return;
                case BIN:
                    ship();
                    return;
                case POND:
                    farmer.getCan().refill();
                    say("Watering can refilled");
                    return;
                default:
                    break;
            }
            if (tile.hasCrop() && tile.getCrop().isMature()) {
                String name = tile.harvest();
                farmer.getBasket().add(name, 1);
                floatAt(col, row, "+1 " + name, Color.WHITE);
                say("Picked a " + name.toLowerCase() + " - ship it in the bin, or save it for Mrs. Daeng");
                return;
            }
            Tool tool = farmer.toolFor(tile);
            if (tool == null) throw new FarmException(tile.hasCrop() ? "Already watered - it grows tonight" : "Nothing to do here");
            farmer.use(tool, tile);
            if (checklist != null && tool instanceof SeedBag) checklist.complete(Checklist.Step.PLANT);
            if (checklist != null && tool instanceof WateringCan) checklist.complete(Checklist.Step.WATER);
        } catch (FarmException e) {
            say(e.getMessage());
        }
    }

    /** The label shown next to the highlighted tile, or null if SPACE would do nothing there. */
    String hint() {
        int col = farmer.facingCol(), row = farmer.facingRow();
        if (farm.villagerAt(col, row) != null) return "Talk";
        Tile tile = farm.tileAt(col, row);
        if (tile == null) return null;
        switch (tile.getType()) {
            case HOUSE: return "Sleep";
            case SHOP:  return "Shop";
            case BIN:   return "Ship basket";
            case POND:  return "Refill can";
            default:    break;
        }
        if (tile.hasCrop() && tile.getCrop().isMature()) return "Harvest";
        Tool tool = farmer.toolFor(tile);
        return tool == null ? null : tool.getActionName();
    }

    private void talkTo(Villager villager) {
        int before = farmer.getGold();
        say(villager.talk(farmer));
        int reward = farmer.getGold() - before;
        if (reward > 0) floatAt(villager.getCol(), villager.getRow(), "+" + reward + "G", Ui.HIGHLIGHT);
        if (checklist != null) checklist.complete(Checklist.Step.TALK);
    }

    private void ship() throws FarmException {
        Bag<String> basket = farmer.getBasket();
        if (basket.isEmpty()) throw new FarmException("Nothing to ship - harvest something first");
        int value = 0;
        for (Map.Entry<String, Integer> entry : basket.asMap().entrySet()) {
            value += CropFactory.create(entry.getKey()).getSellPrice() * entry.getValue();
            shippedToday.add(entry.getKey(), entry.getValue());
        }
        basket.clear();
        floatAtFacing("+" + value + "G", Ui.HIGHLIGHT);
        say("Shipped! The money arrives while you sleep");
    }

    private void askToSleep() {
        int thirsty = farm.countThirstyCrops();
        String detail = thirsty == 0 ? "Watered crops grow one day overnight."
                : thirsty + (thirsty == 1 ? " crop hasn't" : " crops haven't") + " been watered yet!";
        // with thirsty crops the safe answer, "Not yet", is picked to begin with
        game.setScreen(new ConfirmScreen(game, this, "Go to bed and end the day?", detail,
                "Sleep", "Not yet", thirsty > 0, () -> sleep(false)));
    }

    /** Ends the day: pays for what was shipped, grows the crops, saves, and shows the summary. */
    private void sleep(boolean passedOut) {
        List<String> lines = new ArrayList<>();
        if (passedOut) lines.add("You passed out at midnight! Half energy today.");
        int earned = 0;
        for (Map.Entry<String, Integer> entry : shippedToday.asMap().entrySet()) {
            int value = CropFactory.create(entry.getKey()).getSellPrice() * entry.getValue();
            lines.add(String.format("Shipped %-10s x%-3d %6dG", entry.getKey(), entry.getValue(), value));
            earned += value;
        }
        if (shippedToday.isEmpty()) lines.add("Nothing was shipped today.");
        shippedToday.clear();

        int goldBefore = farmer.getGold();
        farmer.earn(earned);
        clock.nextDay(rng);
        farm.newDay(clock.getWeather());
        String news = farm.getVillager().newDay(clock.getDay(), rng);
        if (news != null) lines.add(news);
        farmer.wakeUp(DOOR_COL, DOOR_ROW, passedOut);
        floatingTexts.clear();
        messageTicks = 0;
        checklist = null;   // the first day is over

        try {
            if (isMonthOver()) game.getSaveGame().delete();   // the story is over
            else game.getSaveGame().save(farm, farmer, clock);
        } catch (IOException e) {
            lines.add("Couldn't save: " + e.getMessage());
        }
        game.setScreen(new SummaryScreen(game, this, lines, goldBefore, farmer.getGold()));
    }

    // ---- update ---------------------------------------------------------------

    @Override
    public void update() {
        if (fadeTicks > 0) {
            fadeTicks--;
            return;
        }
        if (messageTicks > 0) messageTicks--;
        for (FloatingText text : floatingTexts) text.update();
        floatingTexts.removeIf(FloatingText::isDone);

        clock.tick();
        if (clock.isMidnight()) {
            sleep(true);
            return;
        }
        if (!warnedLate && clock.getMinutes() >= LATE) {
            warnedLate = true;
            say("It's getting late - go to bed before midnight!");
        }
        farmer.update(farm, heldDirection());
    }

    private Direction heldDirection() {
        if (game.isHeld(KeyEvent.VK_UP, KeyEvent.VK_W)) return Direction.UP;
        if (game.isHeld(KeyEvent.VK_DOWN, KeyEvent.VK_S)) return Direction.DOWN;
        if (game.isHeld(KeyEvent.VK_LEFT, KeyEvent.VK_A)) return Direction.LEFT;
        if (game.isHeld(KeyEvent.VK_RIGHT, KeyEvent.VK_D)) return Direction.RIGHT;
        return null;
    }

    // ---- drawing --------------------------------------------------------------

    @Override
    public void draw(Graphics2D g) {
        farm.draw(g);
        int col = farmer.facingCol(), row = farmer.facingRow();
        g.setColor(new Color(255, 255, 255, 110));   // the tile SPACE will act on
        g.drawRect(col * Farm.TILE, row * Farm.TILE, Farm.TILE - 1, Farm.TILE - 1);
        drawPeople(g);
        if (clock.getWeather() == Weather.RAINY) drawRain(g);
        g.setColor(new Color(15, 15, 50, clock.darkness()));
        g.fillRect(0, 0, Ui.WIDTH, Ui.MAP_HEIGHT);
        for (FloatingText text : floatingTexts) text.draw(g);
        drawHint(g, col, row);

        Hud.draw(g, farmer, clock, farm.getVillager());
        if (checklist != null) checklist.draw(g, 195, 36);
        if (messageTicks > 0) drawMessage(g);
        if (fadeTicks > 0) drawDayStart(g);
    }

    /** Whoever stands lower on the screen is drawn last, so they appear in front. */
    private void drawPeople(Graphics2D g) {
        Villager villager = farm.getVillager();
        boolean villagerBehind = villager.getRow() <= farmer.getRow();
        if (villagerBehind) villager.draw(g, game.getFrames());
        farmer.draw(g);
        if (!villagerBehind) villager.draw(g, game.getFrames());
    }

    /** A small "SPACE  Water" tag beside the highlighted tile. */
    private void drawHint(Graphics2D g, int col, int row) {
        String hint = hint();
        if (hint == null) return;
        String text = "SPACE  " + hint;
        g.setFont(Ui.font(12));
        int width = g.getFontMetrics().stringWidth(text) + 12;
        int x = col * Farm.TILE + Farm.TILE / 2 - width / 2;
        // above the tile, unless the farmer is standing there (facing down) or it would leave the screen
        int y = farmer.getFacing() == Direction.DOWN || row == 0 ? (row + 1) * Farm.TILE + 2 : row * Farm.TILE - 20;
        x = Math.max(2, Math.min(Ui.WIDTH - width - 2, x));
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(x, y, width, 18, 8, 8);
        g.setColor(Ui.HIGHLIGHT);
        g.drawString(text, x + 6, y + 13);
    }

    private void drawRain(Graphics2D g) {
        int frames = game.getFrames();
        g.setColor(new Color(40, 60, 100, 50));
        g.fillRect(0, 0, Ui.WIDTH, Ui.MAP_HEIGHT);
        g.setColor(new Color(190, 210, 255, 150));
        for (int i = 0; i < 140; i++) {   // scattered by index, falling with time: no per-drop state needed
            int x = (i * 53 + frames * 2) % (Ui.WIDTH + 20) - 10;
            int y = (i * 97 + frames * 9) % Ui.MAP_HEIGHT;
            g.drawLine(x, y, x - 3, y + 10);
        }
    }

    private void drawMessage(Graphics2D g) {
        g.setFont(Ui.font(13));
        int width = g.getFontMetrics().stringWidth(message) + 24;
        int x = (Ui.WIDTH - width) / 2, y = Ui.MAP_HEIGHT - 44;
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRoundRect(x, y, width, 28, 10, 10);
        g.setColor(Color.WHITE);
        g.drawString(message, x + 12, y + 19);
    }

    private void drawDayStart(Graphics2D g) {
        int alpha = Math.min(255, fadeTicks * 255 * 2 / FADE_TICKS);   // stays black for the first half
        Ui.dim(g, alpha);
        Ui.centered(g, "Day " + clock.getDay() + " of " + LAST_DAY, 40, Ui.MAP_HEIGHT / 2 - 10, new Color(255, 255, 255, alpha));
        Ui.centered(g, clock.getWeather().getMorningText(), 18, Ui.MAP_HEIGHT / 2 + 24, new Color(255, 230, 120, alpha));
    }
}
