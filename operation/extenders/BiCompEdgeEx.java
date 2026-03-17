package operation.extenders;

import graphStructure.*;

public class BiCompEdgeEx extends DFSEdgeEx
{
  protected boolean wasAdded;
  protected int subGraphNumber;
  
  public BiCompEdgeEx() { super(); }
  
  public BiCompEdgeEx(NodeExtender start, NodeExtender end)
  {
    super(start, end);
  }
  
  public BiCompEdgeEx(BiCompEdgeEx edgeEx, NodeExtender start, NodeExtender end)
  {
    super(edgeEx, start, end);
  }
  
  public void setWasAdded(boolean isAdded) { this.wasAdded = isAdded; }
  
  public boolean wasAdded() { return wasAdded; }
  
  public void setSubGraphNumber(int subGraphNumber) { this.subGraphNumber = subGraphNumber; }
  
  public int getSubGraphNumber() { return subGraphNumber; }
}