package operation.extenders;

import graphStructure.*;

public class PQNodeEx extends NodeExtender
{
  protected int stNumber;
  protected boolean isOld;
  
  public void setStNumber(int stNumber) { this.stNumber = stNumber; }
  
  public int getStNumber() { return stNumber; }
  
  public void setIsOld(boolean isOld) { this.isOld = isOld; }
  
  public boolean isOld() { return isOld; }
}