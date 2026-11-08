import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Hosts whichever Screen is showing. A Swing Timer calls tick() then repaint() about 60 times a second,
 * and every key press is passed on to the current screen.
 */
public class GamePanel extends JPanel implements ActionListener {
    private static final double START_SCALE = 1.25;   // the window opens at 800x700; drag it to resize

    private final Timer timer = new Timer(16, this);
    private final Set<Integer> keysDown = new HashSet<>();
    private final SaveGame saveGame = new SaveGame("littlefarm_save.txt");
    private Screen screen;
    private int frames;

    public GamePanel() {
        setPreferredSize(new Dimension((int) (Ui.WIDTH * START_SCALE), (int) (Ui.HEIGHT * START_SCALE)));
        setBackground(Color.BLACK);   // the bars beside the game when the window's shape doesn't match
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) { pressKey(e.getKeyCode()); }

            @Override
            public void keyReleased(KeyEvent e) { releaseKey(e.getKeyCode()); }
        });
        screen = new TitleScreen(this);
    }

    public void start() {
        timer.start();
    }

    void pressKey(int code) {
        if (!keysDown.add(code)) return;   // ignore the OS's auto-repeat while a key is held
        screen.keyPressed(code);
    }

    void releaseKey(int code) {
        keysDown.remove(code);
    }

    public void setScreen(Screen next) { screen = next; }
    public void showTitle() { screen = new TitleScreen(this); }
    public SaveGame getSaveGame() { return saveGame; }
    public int getFrames() { return frames; }

    public boolean isHeld(int key, int otherKey) {
        return keysDown.contains(key) || keysDown.contains(otherKey);
    }

    public void quit() {
        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        tick();
        repaint();
    }

    void tick() {
        frames++;
        screen.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // the game always draws at 640x560; here it is scaled up to fill the window, keeping its shape
        double scale = Math.min(getWidth() / (double) Ui.WIDTH, getHeight() / (double) Ui.HEIGHT);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate((getWidth() - Ui.WIDTH * scale) / 2, (getHeight() - Ui.HEIGHT * scale) / 2);
        g2.scale(scale, scale);
        render(g2);
        g2.dispose();
    }

    void render(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        screen.draw(g);
    }
}
