package operation.extenders;

public class STEdgeEx extends DFSEdgeEx
{
  protected boolean isOld;
  
  public void setIsOld(boolean isOld) { this.isOld = isOld; }
  
  public boolean isOld() { return isOld; }
}