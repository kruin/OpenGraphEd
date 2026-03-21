package operation.opentree;

public class PlacementAction {

    private final PlacementActionType type;
    private final int nodeId;
    private final int col;
    private final int row;

    public PlacementAction(PlacementActionType type, int nodeId, int col, int row) {
        this.type = type;
        this.nodeId = nodeId;
        this.col = col;
        this.row = row;
    }

    public static PlacementAction place(int nodeId, int col, int row) {
        return new PlacementAction(PlacementActionType.PLACE, nodeId, col, row);
    }

    public static PlacementAction defer(int nodeId) {
        return new PlacementAction(PlacementActionType.DEFER, nodeId, -1, -1);
    }

    public PlacementActionType getType() { return type; }
    public int getNodeId() { return nodeId; }
    public int getCol() { return col; }
    public int getRow() { return row; }
}
