/** Something the farmer tried that the farm does not allow. The message is shown to the player. */
public class FarmException extends Exception {
    public FarmException(String message) {
        super(message);
    }
}
