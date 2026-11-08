
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.security.MessageDigest;

public class FarmGoldenOutputTest {

    public static void main(String[] args) throws Exception {

        Farm farm = new Farm();

        int width = Farm.COLS * Farm.TILE;
        int height = Farm.ROWS * Farm.TILE;

        BufferedImage image = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = image.createGraphics();

        try {
            farm.draw(g);
        } finally {
            g.dispose();
        }

        System.out.println("Farm image: " + width + "x" + height);
        System.out.println("SHA-256: " + sha256(image));
    }

    private static String sha256(BufferedImage image)
            throws Exception {

        MessageDigest digest =
                MessageDigest.getInstance("SHA-256");

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                int argb = image.getRGB(x, y);

                digest.update((byte) (argb >>> 24));
                digest.update((byte) (argb >>> 16));
                digest.update((byte) (argb >>> 8));
                digest.update((byte) argb);
            }
        }

        StringBuilder result = new StringBuilder();

        for (byte b : digest.digest()) {
            result.append(String.format("%02x", b & 0xff));
        }

        return result.toString();
    }
}
