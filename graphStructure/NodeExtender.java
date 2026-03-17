package graphStructure;

import java.util.Vector;
import java.awt.Graphics2D;
import java.awt.Color;

public abstract class NodeExtender implements NodeInterface
{
  protected Node refNode;

  public NodeExtender() { }

  public NodeExtender( NodeExtender node )
  {
    refNode = new Node(node.getRef());
    refNode.setExtender(this);
  }

  public void setRef(Node refNode) { this.refNode = refNode; }

  public Node getRef() { return refNode; }

  public Node getNode() { return refNode; }

  public int getX() { return refNode.getX(); }

  public int getY() { return refNode.getY(); }

  public Vector incidentEdges()
  {
    Vector incidentEdges = refNode.incidentEdges();
    Vector toReturn = new Vector(incidentEdges.size());
    for ( int i=0; i<incidentEdges.size(); i++ )
    {
      toReturn.addElement(((Edge)incidentEdges.elementAt(i)).getExtender());
    }
    return toReturn;
  }

  public Vector incidentOutgoingEdges()
  {
    Vector incidentEdges = refNode.incidentOutgoingEdges();
    Vector toReturn = new Vector(incidentEdges.size());
    for ( int i=0; i<incidentEdges.size(); i++ )
    {
      toReturn.addElement(((Edge)incidentEdges.elementAt(i)).getExtender());
    }
    return toReturn;
  }

  public EdgeIterator incidentEdgesIterator()
  {
    return refNode.incidentEdgesIterator();
  }
  
  public EdgeInterface incidentEdgeWith(NodeInterface aNode)
  {
    return ((Edge)refNode.incidentEdgeWith(((NodeExtender)aNode).getRef())).getExtender();
  }

  public boolean hasNoIncidentEdges()
  {
    return refNode.hasNoIncidentEdges();
  }

  public boolean hasEdge(EdgeInterface edge)
  {
    return refNode.hasEdge(((EdgeExtender)edge).getRef());
  }

  public void draw(Graphics2D g2, boolean drawSelected,
                   boolean showCoord, boolean showLabel)
  {
    refNode.draw(g2, drawSelected, showCoord, showLabel);
  }

  public NodeInterface getCopy()
  {
    if ( refNode.getCopy() == null )
    {
      return null;
    }
    else
    {
      return ((Node)refNode.getCopy()).getExtender();
    }
  }
  
  public NodeInterface getMasterCopy()
  {
    if ( refNode.getMasterCopy() == null )
    {
      return null;
    }
    else
    {
      return ((Node)refNode.getMasterCopy()).getExtender();
    }
  }

  public void setCopy(NodeInterface aCopy)
  {
    if ( aCopy == null )
    {
      refNode.setCopy(null);
    }
    else
    {
      refNode.setCopy(((NodeExtender)aCopy).getRef());
    }
  }

  public void addIncidentEdgeNoCheck(EdgeInterface edge)
  {
    refNode.addIncidentEdgeNoCheck( ((EdgeExtender)edge).getRef() );
  }

  public boolean addIncidentEdge(EdgeInterface edge)
  {
    return refNode.addIncidentEdge( ((EdgeExtender)edge).getRef() );
  }

  public void deleteIncidentEdge(EdgeInterface edge)
  {
    refNode.deleteIncidentEdge( ((EdgeExtender)edge).getRef() );
  }

  public void resetIncidentEdges()
  {
    refNode.resetIncidentEdges();
  }

  public void addEdgeBetween( EdgeInterface edge, EdgeInterface prev,
                              EdgeInterface next )
  {
    refNode.addEdgeBetween( ((EdgeExtender)edge).getRef(),
                            ((EdgeExtender)prev).getRef(),
                            ((EdgeExtender)next).getRef() );
  }

  public Location getLocation()
  {
    return refNode.getLocation();
  }

  public void setLocation(Location aLocation)
  {
    refNode.setLocation(aLocation);
  }

  public int getIndex()
  {
    return refNode.getIndex();
  }

  public void setIndex(int index)
  {
    refNode.setIndex(index);
  }

  public void setColor(Color aColor)
  {
    refNode.setColor(aColor);
  }

  public Color getColor()
  {
    return refNode.getColor();
  }

  public void setLabel(String text)
  {
    refNode.setLabel(text);
  }

  public void appendLabel(String newLabel)
  {
    refNode.appendLabel(newLabel);
  }

  public String getLabel()
  {
    return refNode.getLabel();
  }

  public void setDrawX(boolean draw)
  {
    refNode.setDrawX(draw);
  }

  public boolean getDrawX()
  {
    return refNode.getDrawX();
  }

  public boolean equals(Object o)
  {
    if ( o instanceof NodeExtender )
    {
      return refNode.equals( ((NodeExtender)o).refNode );
    }
    else
    {
      return false;
    }
  }

  public String toString()
  {
    return getClass().getName() + " - " + refNode;
  }

  public static Vector toNode(Vector nodeExVector)
  {
    Vector nodeVector = new Vector(nodeExVector.size());
    for ( int i=0; i<nodeExVector.size(); i++ )
    {
      nodeVector.addElement( ((NodeExtender)nodeExVector.elementAt(i)).getRef() );
    }
    return nodeVector;
  }
}