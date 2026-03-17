package operation.extenders;

import graphStructure.*;
import dataStructure.binaryHeap.*;

public class SPNodeEx extends NodeExtender implements HeapObject
{
  private double cost;
  private boolean isDone;
  private SPEdgeEx traceBackEdge;
  private HeapNode heapNode;
  
  public SPNodeEx()
  {
    super();
    cost = 0;
    isDone = false;
    traceBackEdge = null;
    heapNode = null;
  }
    
  public double getCost() { return cost; }
  
  public void setCost(double c) { cost = c; }
  
  public boolean isDone() { return isDone; }
  
  public void setIsDone(boolean d) { isDone = d; }
  
  public SPEdgeEx getTraceBackEdge() { return traceBackEdge; }
  
  public void setTraceBackEdge(SPEdgeEx t) { traceBackEdge = t; }
  
  public boolean isUsed() { return traceBackEdge != null; }
  
  public HeapNode getHeapNode() { return heapNode; }
  
  public void setHeapNode(HeapNode hn) { heapNode = hn; }
}