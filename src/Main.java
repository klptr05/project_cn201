import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        String strategyName = args.length == 0 ? "normal" : args[0];
        final RainChanceStrategy rainStrategy;

        try {
            rainStrategy = RainChanceStrategyFactory.create(strategyName);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.err.println(
                "Usage: java -cp out Main [normal|fifty]"
            );
            return;
        }

        SwingUtilities.invokeLater(() -> {
            GamePanel panel = new GamePanel(rainStrategy);
            JFrame frame = new JFrame("Little Farm - CN201");
            frame.add(panel);
            frame.pack();
            frame.setResizable(true);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            panel.requestFocusInWindow();
            panel.start();
        });
    }
}
