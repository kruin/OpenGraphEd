package operation.extenders;

import java.util.Vector;
import graphStructure.*;

public class ChanNodeEx extends NodeExtender
{
  protected ChanNodeEx parent;
  protected int subTreeSize;
  protected int gridX, gridY;
  protected int boundX, boundY;
  protected int boundWidth, boundHeight;
  
  public ChanNodeEx()
  {
    super();
    parent = null;
  }
  
  public void setParent(ChanNodeEx parent) { this.parent = parent; }
  
  public ChanNodeEx getParent() { return parent; }
  
  public void setSubTreeSize(int subTreeSize) { this.subTreeSize = subTreeSize; }
  
  public int getSubTreeSize() { return subTreeSize; }
  
  public Vector getChildren()
  {
    Vector edges = incidentEdges();
    Vector children = new Vector(edges.size());
    ChanNodeEx node;
    for ( int i=0; i<edges.size(); i++ )
    {
      node = (ChanNodeEx)((ChanEdgeEx)edges.elementAt(i)).otherEndFrom(this);
      if ( node != parent )
      {
        children.addElement(node);
      }
    }
    return children;
  }
  
  public ChanNodeEx getLeftChild()
  {
    if ( (parent == null && (refNode.getNumEdges() > 2 ||
                             refNode.getNumEdges() < 1)) ||
         (parent != null && (refNode.getNumEdges() > 3 ||
                             refNode.getNumEdges() < 2)) )
    {
      return null;
    }
    return (ChanNodeEx)getChildren().elementAt(0);
  }
  
  public ChanNodeEx getRightChild()
  {
    if ( (parent == null && refNode.getNumEdges() != 2) ||
         (parent != null && refNode.getNumEdges() != 3) )
    {
      return null;
    }
    return (ChanNodeEx)getChildren().elementAt(1);
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
  
  public String toString()
  {
    return refNode.getLocation() + " " + gridX + " " + gridY + " " + boundX + " " + boundWidth + " " + boundY + " " + boundHeight + " " + subTreeSize;
  }
}
