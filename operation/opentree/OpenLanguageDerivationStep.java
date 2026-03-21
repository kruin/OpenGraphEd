package operation.opentree;

import java.util.ArrayList;
import java.util.List;

public class OpenLanguageDerivationStep {

    private final int stepIndex;
    private final int targetNodeId;

    private final List<OpenLanguageNode> introducedNodes = new ArrayList<>();
    private final List<PlacementAction> placementActions = new ArrayList<>();

    public OpenLanguageDerivationStep(int stepIndex, int targetNodeId) {
        this.stepIndex = stepIndex;
        this.targetNodeId = targetNodeId;
    }

    public void addIntroducedNode(OpenLanguageNode node) {
        introducedNodes.add(node);
    }

    public void addPlacementAction(PlacementAction action) {
        placementActions.add(action);
    }

    public int getStepIndex() { return stepIndex; }
    public int getTargetNodeId() { return targetNodeId; }

    public List<OpenLanguageNode> getIntroducedNodes() {
        return introducedNodes;
    }

    public List<PlacementAction> getPlacementActions() {
        return placementActions;
    }
}
