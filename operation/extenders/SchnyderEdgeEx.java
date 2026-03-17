package operation.extenders;

import graphStructure.*;

public class SchnyderEdgeEx extends EdgeExtender
{
  protected int normalLabel;
  
  public void setNormalLabel(int label) { normalLabel = label; }
  
  public int getNormalLabel() { return normalLabel; }
  
  public SchnyderNodeEx getNormalLabelSourceNode()
  {
    if ( ((SchnyderNodeEx)getStartNode()).getRXParent(normalLabel) == getEndNode() )
    {
      return (SchnyderNodeEx)getStartNode();
    }
    else if ( ((SchnyderNodeEx)getEndNode()).getRXParent(normalLabel) == getStartNode() )
    {
      return (SchnyderNodeEx)getEndNode();
    }
    else
    {
      return null;
    }
  }
}