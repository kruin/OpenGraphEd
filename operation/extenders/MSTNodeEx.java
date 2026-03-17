package operation.extenders;

import graphStructure.*;
import dataStructure.binaryHeap.*;

public class MSTNodeEx extends NodeExtender implements HeapObject
{
  private HeapNode heapNode;
  private MSTEdgeEx linkEdge;
  private double cost;
  private boolean marked;
  
  public MSTNodeEx()
  {
    super();
    heapNode = null;
    linkEdge = null;
    cost = 0;
    marked = false;
  }
  
  public HeapNode getHeapNode() { return heapNode; }
  
  public void setHeapNode(HeapNode hn) { heapNode = hn; }
  
  public MSTEdgeEx getLinkEdge() { return linkEdge; }
  
  public void setLinkEdge(MSTEdgeEx le) { linkEdge = le; }
  
  public double getCost() { return cost; }
  
  public void setCost(double c) { cost = c; }
  
  public boolean isMarked() { return marked; }
  
  public void setMarked(boolean m) { marked = m; }
}