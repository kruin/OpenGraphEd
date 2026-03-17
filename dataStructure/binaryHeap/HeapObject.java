package dataStructure.binaryHeap;

public interface HeapObject
{
  public HeapNode getHeapNode();
  public void setHeapNode(HeapNode hn);
  public double getCost();
  public void setCost(double c);
}