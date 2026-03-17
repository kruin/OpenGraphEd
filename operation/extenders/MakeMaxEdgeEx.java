package operation.extenders;

import graphStructure.*;

public class MakeMaxEdgeEx extends EdgeExtender
{
  protected boolean isOld;
  
  public MakeMaxEdgeEx() { super(); }
  
  public MakeMaxEdgeEx(NodeExtender start, NodeExtender end)
  {
    super(start, end);
  }
  
  public MakeMaxEdgeEx(MakeMaxEdgeEx edgeEx, NodeExtender start, NodeExtender end)
  {
    super(edgeEx, null, start, end);
  }
  
  public void setIsOld(boolean isOld) { this.isOld = isOld; }
  
  public boolean isOld() { return isOld; }
}