package operation.extenders;

import graphStructure.*;

public class SchnyderNodeEx extends NodeExtender
{
  protected int canonicalNumber;
  protected SchnyderNodeEx r1Parent;
  protected SchnyderNodeEx r2Parent;
  protected SchnyderNodeEx r3Parent;
  protected int px[];
  protected int tx[];
  protected int rx[];
  protected int temp[];
  
  public SchnyderNodeEx()
  {
    super();
    px = new int[3];
    tx = new int[3];
    rx = new int[3];
    temp = new int[6];
  }
  
  public void setCanonicalNumber(int canon) { canonicalNumber = canon; }
  
  public int getCanonicalNumber() { return canonicalNumber; }
  
  public void setR1Parent(SchnyderNodeEx r1) { r1Parent = r1; }
  
  public SchnyderNodeEx getR1Parent() { return r1Parent; }
  
  public void setR2Parent(SchnyderNodeEx r2) { r2Parent = r2; }
  
  public SchnyderNodeEx getR2Parent() { return r2Parent; }
  
  public void setR3Parent(SchnyderNodeEx r3) { r3Parent = r3; }
  
  public SchnyderNodeEx getR3Parent() { return r3Parent; }
  
  public SchnyderNodeEx getRXParent(int x)
  {
    if ( x == 1 )
    {
      return getR1Parent();
    }
    else if ( x == 2 )
    {
      return getR2Parent();
    }
    else if ( x == 3 )
    {
      return getR3Parent();
    }
    else
    {
      return null;
    }
  }
  
  public void setPX(int treeNumber, int aPX) {  px[treeNumber-1] = aPX; }
  
  public int getPX(int treeNumber) { return px[treeNumber-1]; }
  
  public void setTX(int treeNumber, int aTX) {  tx[treeNumber-1] = aTX; }
  
  public int getTX(int treeNumber) { return tx[treeNumber-1]; }
  
  public void setRX(int treeNumber, int aRX) {  rx[treeNumber-1] = aRX; }
  
  public int getRX(int treeNumber) { return rx[treeNumber-1]; }
  
  public void setTemp(int treeNumber, int aTemp) {  temp[treeNumber-1] = aTemp; }
  
  public int getTemp(int treeNumber) { return temp[treeNumber-1]; }
}