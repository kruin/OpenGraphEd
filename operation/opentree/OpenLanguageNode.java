package operation.opentree;

public class OpenLanguageNode {

    private final int id;
    private final String label;

    private Integer parentId;
    private Integer leftChildId;
    private Integer rightChildId;

    private NodeStructureState structureState;
    private NodePlacementState placementState;

    private int col = -1;
    private int row = -1;

    private int createdAtStep = -1;
    private int expandedAtStep = -1;

    private String lexicalValue;
    private String categoryValue;
    private String syntacticReflection;

    public OpenLanguageNode(int id, String label) {
        this.id = id;
        this.label = label;
        this.structureState = NodeStructureState.DECLARED;
        this.placementState = NodePlacementState.UNPLACED;
    }

    public void setParent(int parentId) {
        this.parentId = parentId;
    }

    public void setChildren(int leftId, int rightId) {
        this.leftChildId = leftId;
        this.rightChildId = rightId;
        this.structureState = NodeStructureState.EXPANDED;
    }

    public boolean isLeaf() {
        return leftChildId == null && rightChildId == null;
    }

    public void place(int col, int row) {
        this.col = col;
        this.row = row;
        this.placementState = NodePlacementState.PLACED;
    }

    public void deferPlacement() {
        this.placementState = NodePlacementState.PENDING;
    }

    public boolean isPlaced() {
        return placementState == NodePlacementState.PLACED;
    }

    public void setLexicalValue(String value) {
        this.lexicalValue = value;
    }

    public void setCategoryValue(String value) {
        this.categoryValue = value;
    }

    public void setSyntacticReflection(String value) {
        this.syntacticReflection = value;
    }

    public int getId() { return id; }
    public String getLabel() { return label; }

    public Integer getParentId() { return parentId; }
    public Integer getLeftChildId() { return leftChildId; }
    public Integer getRightChildId() { return rightChildId; }

    public int getCol() { return col; }
    public int getRow() { return row; }

    public NodeStructureState getStructureState() { return structureState; }
    public NodePlacementState getPlacementState() { return placementState; }

    public String getLexicalValue() { return lexicalValue; }
    public String getCategoryValue() { return categoryValue; }
    public String getSyntacticReflection() { return syntacticReflection; }

    public int getCreatedAtStep() { return createdAtStep; }
    public int getExpandedAtStep() { return expandedAtStep; }

    public void setCreatedAtStep(int step) {
        this.createdAtStep = step;
    }

    public void setExpandedAtStep(int step) {
        this.expandedAtStep = step;
    }
}
