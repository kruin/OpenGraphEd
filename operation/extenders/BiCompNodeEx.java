package operation.extenders;

public class BiCompNodeEx extends DFSNodeEx
{
  protected int subGraphNumber;
  protected boolean isOld;
  
  public void setSubGraphNumber(int subGraphNumber) { this.subGraphNumber = subGraphNumber; }
  
  public int getSubGraphNumber() { return subGraphNumber; }
  
  public void setIsOld(boolean isOld) { this.isOld = isOld; }
  
  public boolean isOld() { return isOld; }
}