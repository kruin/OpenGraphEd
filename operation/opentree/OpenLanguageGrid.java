package operation.opentree;

import java.util.HashMap;
import java.util.Map;

public class OpenLanguageGrid {

    private final Map<String, Integer> cellToNode = new HashMap<>();

    private String key(int col, int row) {
        return col + "," + row;
    }

    public boolean isFree(int col, int row) {
        return !cellToNode.containsKey(key(col, row));
    }

    public void occupy(int col, int row, int nodeId) {
        String k = key(col, row);
        if (cellToNode.containsKey(k)) {
            throw new IllegalStateException("Cell already occupied: " + k);
        }
        cellToNode.put(k, nodeId);
    }

    public Integer getNodeAt(int col, int row) {
        return cellToNode.get(key(col, row));
    }
}
