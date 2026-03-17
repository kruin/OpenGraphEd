package operation.extenders;

import graphStructure.*;

public class DFSNodeEx extends NodeExtender
{
  protected int number;
  protected int lowNumber;
  protected DFSNodeEx parent;
  
  public void setNumber(int number) { this.number = number; }
  
  public int getNumber() { return number; }
  
  public void setLowNumber(int lowNumber) { this.lowNumber = lowNumber; }
  
  public int getLowNumber() { return lowNumber; }
  
  public void setParent(DFSNodeEx parent) { this.parent = parent; }
  
  public DFSNodeEx getParent() {  return parent; }
}