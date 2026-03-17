package operation.extenders;

import java.util.Vector;
import graphStructure.*;
import dataStructure.binaryHeap.*;

public class KruinNodeEx extends NodeExtender implements HeapObject
{
  protected KruinNodeEx parent;
  protected int subTreeSize;
  protected int gridX, gridY;
  protected int boundX, boundY;
  protected int boundWidth, boundHeight;
  
  protected int subTreeWidth, subTreeHeight;
  protected boolean subTreeDone ;
   private double cost;
  private boolean isDone;
  private KruinEdgeEx traceBackEdge;
  private HeapNode heapNode;
  
  public KruinNodeEx()
  {
    super();
    parent = null;
     cost = 0;
    isDone = false;
    traceBackEdge = null;
    heapNode = null;
  }
  public double getCost() { return cost; }
  
  public void setCost(double c) { cost = c; }
  
  public boolean isDone() { return isDone; }
  
  public void setIsDone(boolean d) { isDone = d; }
  
  public KruinEdgeEx getTraceBackEdge() { return traceBackEdge; }
  
  public void setTraceBackEdge(KruinEdgeEx t) { traceBackEdge = t; }
  
  public boolean isUsed() { return traceBackEdge != null; }
  
  public HeapNode getHeapNode() { return heapNode; }
  
  public void setHeapNode(HeapNode hn) { heapNode = hn; }

  public void setParent(KruinNodeEx parent) { this.parent = parent; }
  
  public KruinNodeEx getParent() { return parent; }
  public Vector getChildren()
  {
    Vector edges = incidentEdges();
    Vector children = new Vector(edges.size());
    KruinNodeEx node;
    for ( int i=0; i<edges.size(); i++ )
    {
      node = (KruinNodeEx)((KruinEdgeEx)edges.elementAt(i)).otherEndFrom(this);
      if ( node != parent )
      {
        children.addElement(node);
      }
    }
    return children;
  }
  
  public KruinNodeEx getLeftChild()
  {
    if ( (parent == null && (refNode.getNumEdges() > 2 ||
                             refNode.getNumEdges() < 1)) ||
         (parent != null && (refNode.getNumEdges() > 3 ||
                             refNode.getNumEdges() < 2)) )
    {
      return null;
    }
    return (KruinNodeEx)getChildren().elementAt(0);
  }
  
  public KruinNodeEx getRightChild()
  {
    if ( (parent == null && refNode.getNumEdges() != 2) ||
         (parent != null && refNode.getNumEdges() != 3) )
    {
      return null;
    }
    return (KruinNodeEx)getChildren().elementAt(1);
  }
  
  
  public int getGridX() { return gridX; }
  public void setGridX(int gridX) { this.gridX = gridX; }
  public int getGridY() { return gridY; }
  public void setGridY(int gridY) { this.gridY = gridY; }
  public void shiftX(int shiftX) { gridX+= shiftX; }
  public void shiftY(int shiftY) { gridY+= shiftY; }
  public int getBoundX() { return boundX; }
  public void setBoundX(int boundX) { this.boundX = boundX; }
  public int getBoundY() { return boundY; }
  public void setBoundY(int boundY) { this.boundY = boundY; }
  public int getBoundWidth() { return boundWidth; }
  public void setBoundWidth(int boundWidth) { this.boundWidth = boundWidth; }
  public int getBoundHeight() { return boundHeight; }
  public void setBoundHeight(int boundHeight) { this.boundHeight = boundHeight; } 
  
  public int  getSubTreeSize() { return subTreeSize; }
  public void setSubTreeSize (int subTreeSize) {this.subTreeSize = subTreeSize;}
  public boolean getSubTreeDone() { return subTreeDone; }
  public void setSubTreeDone(boolean subTreeDone) {this.subTreeDone = true;}
 
  


  
  
  public String toString()
  {
    return refNode.getLocation() + "X:" + gridX + "Y:" + gridY + " " + subTreeSize;
  }
}
