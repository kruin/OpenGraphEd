package operation.extenders;

import graphStructure.*;

public class DFSEdgeEx extends EdgeExtender
{
  protected boolean isBackEdge;
  protected boolean isUsed;
  
  public DFSEdgeEx() { super(); }
  
  public DFSEdgeEx( NodeExtender start, NodeExtender end)
  {
    super(start, end);
  }
  
  public DFSEdgeEx( DFSEdgeEx edgeEx, NodeExtender start, NodeExtender end)
  {
    super(edgeEx, null, start, end);
  }
  
  public void setIsBackEdge(boolean isBackEdge) { this.isBackEdge = isBackEdge; }
  
  public boolean isBackEdge() { return isBackEdge; }
  
  public void setIsUsed(boolean isUsed) { this.isUsed = isUsed; }
  
  public boolean isUsed() { return isUsed; }
}