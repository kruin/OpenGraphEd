package graphStructure;

import java.util.Vector;
import java.awt.Color;

public abstract class EdgeExtender implements EdgeInterface
{
  protected Edge refEdge;

  public EdgeExtender() { }

  public EdgeExtender(NodeExtender start, NodeExtender end)
  {
    refEdge = new Edge( start.getRef(), end.getRef() );
    refEdge.setExtender(this);
  }

  public EdgeExtender(EdgeExtender anEdgeEx, NodeExtender dNode, NodeExtender start, NodeExtender end)
  {
    refEdge = new Edge( anEdgeEx.getRef(), dNode.getRef(), start.getRef(), end.getRef() );
    refEdge.setExtender(this);
  }

  public void setRef(Edge refEdge) { this.refEdge = refEdge; }

  public Edge getRef() { return refEdge; }

  public Edge getEdge() { return refEdge; }

  public NodeInterface otherEndFrom(NodeInterface aNode)
  {
    return((Node)refEdge.otherEndFrom( ((NodeExtender)aNode).getRef() ) ).getExtender();
  }

  public boolean isBetween(NodeInterface firstNode, NodeInterface secondNode)
  {
    return refEdge.isBetween( ((NodeExtender)firstNode).getRef(), ((NodeExtender)secondNode).getRef() );
  }

  public NodeInterface getStartNode()
  {
    return ((Node)refEdge.getStartNode()).getExtender();
  }

  public NodeInterface getEndNode()
  {
    return ((Node)refEdge.getEndNode()).getExtender();
  }

  public boolean isGenerated()
  {
    return refEdge.isGenerated();
  }

  public void setIsGenerated(boolean isGenerated)
  {
    refEdge.setIsGenerated(isGenerated);
  }

  public EdgeInterface getCopy()
  {
    if ( refEdge.getCopy() == null )
    {
      return null;
    }
    else
    {
      return ((Edge)refEdge.getCopy()).getExtender();
    }
  }

  public EdgeInterface getMasterCopy()
  {
    if ( refEdge.getMasterCopy() == null )
    {
      return null;
    }
    else
    {
      return ((Edge)refEdge.getMasterCopy()).getExtender();
    }
  }
  
  public void setCopy(EdgeInterface aCopy)
  {
    if ( aCopy == null )
    {
      refEdge.setCopy(null);
    }
    else
    {
      refEdge.setCopy(((EdgeExtender)aCopy).getRef());
    }
  }

  public EdgeInterface getPreviousInOrderFrom( NodeInterface sourceNode )
  {
    return ((Edge)refEdge.getPreviousInOrderFrom( ((NodeExtender)sourceNode).getRef() )).getExtender();
  }

  public EdgeInterface getNextInOrderFrom( NodeInterface sourceNode )
  {
    return ((Edge)refEdge.getNextInOrderFrom( ((NodeExtender)sourceNode).getRef() )).getExtender();
  }

  public void setNextInOrderFrom( NodeInterface sourceNode, EdgeInterface nextEdge )
  {
    refEdge.setNextInOrderFrom( ((NodeExtender)sourceNode).getRef(),
                                ((EdgeExtender)nextEdge).getRef() );
  }

  public void setPreviousInOrderFrom( NodeInterface sourceNode, EdgeInterface prevEdge )
  {
    refEdge.setPreviousInOrderFrom( ((NodeExtender)sourceNode).getRef(),
                                    ((EdgeExtender)prevEdge).getRef() );
  }

  public int getHigherIndex()
  {
    return refEdge.getHigherIndex();
  }

  public int getLowerIndex()
  {
    return refEdge.getLowerIndex();
  }

  public void setColor(Color aColor)
  {
    refEdge.setColor(aColor);
  }

  public Color getColor()
  {
    return refEdge.getColor();
  }

  public Node getDirectedSourceNode()
  {
    return refEdge.getDirectedSourceNode();
  }

  public void setDirectedFrom(NodeInterface directedSourceNode)
  {
    if ( directedSourceNode == null )
    {
      refEdge.setDirectedFrom(null);
    }
    else
    {
      refEdge.setDirectedFrom(((NodeExtender)directedSourceNode).getRef());
    }
  }

  public boolean equals(Object o)
  {
    if ( o instanceof EdgeExtender )
    {
      return refEdge.equals( ((EdgeExtender)o).refEdge );
    }
    else
    {
      return false;
    }
  }

  public String toString()
  {
    return getClass().getName() + " - " + refEdge;
  }

  public static Vector toEdge(Vector edgeExVector)
  {
    Vector edgeVector = new Vector(edgeExVector.size());
    for ( int i=0; i<edgeExVector.size(); i++ )
    {
      edgeVector.addElement( ((EdgeExtender)edgeExVector.elementAt(i)).getRef() );
    }
    return edgeVector;
  }

  public double getStraightLength()
  {
    return refEdge.getStraightLength();
  }

  public double getLength()
  {
    return refEdge.getLength();
  }

  public void makeStraight()
  {
    refEdge.makeStraight();
  }

  public void update()
  {
    refEdge.update();
  }
}