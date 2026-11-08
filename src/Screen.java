import java.awt.Graphics2D;

/**
 * One page of the game: the title, the farm itself, the shop, a menu...
 * GamePanel shows exactly one at a time and hands it every key press (State pattern).
 */
public interface Screen {
    void keyPressed(int keyCode);

    /** Called 60 times a second. Most menus have nothing to do here, so the game waits while they are open. */
    default void update() {}

    void draw(Graphics2D g);
}
