package new_edit_src;

public abstract class Animals implements drawable, talk {
    public static final int MAX_FRIENDSHIP = 5;

    protected final String name;
    protected final int col, row;
    protected int friendship = 0;
    protected int chats = 0;

    public Animals(String name, int col, int row) {
        this.name = name;
        this.col = col;
        this.row = row;
    }
}
