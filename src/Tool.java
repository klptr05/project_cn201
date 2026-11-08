/** Something the farmer can hold and use on the tile in front of them. */
public interface Tool {
    /** Short text for the toolbar, e.g. "Can 12/20". */
    String getLabel();

    /** What pressing SPACE will do with this tool, for the hint tag, e.g. "Water". */
    String getActionName();

    int getEnergyCost();

    /** Does the job, or throws to say why it can't be done (and then costs no energy). */
    void use(Tile tile) throws FarmException;
}
