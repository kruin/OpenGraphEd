package graphStructure;

import java.util.Vector;
import java.awt.Graphics2D;
import java.awt.Color;

public interface NodeInterface
{
  public Vector incidentEdges();
  public Vector incidentOutgoingEdges();
  public EdgeIterator incidentEdgesIterator();
  public EdgeInterface incidentEdgeWith(NodeInterface aNode);
  public boolean hasNoIncidentEdges();
  public boolean hasEdge(EdgeInterface edge);
  public void draw(Graphics2D g2, boolean drawSelected,
                   boolean showCoord, boolean showLabel);
  public NodeInterface getCopy();
  public NodeInterface getMasterCopy();
  public void setCopy(NodeInterface aCopy);
  public void addIncidentEdgeNoCheck(EdgeInterface edge);
  public boolean addIncidentEdge(EdgeInterface edge);
  public void deleteIncidentEdge(EdgeInterface edge);
  public void resetIncidentEdges();
  public void addEdgeBetween( EdgeInterface edge, EdgeInterface prev,
                              EdgeInterface next );
  public Location getLocation();
  public void setLocation(Location aLocation);
  public int getX();
  public int getY();
  public int getIndex();
  public void setIndex(int index);
  public void setColor(Color aColor);
  public Color getColor();
  public void setLabel(String text);
  public void appendLabel(String newLabel);
  public String getLabel();
  public void setDrawX(boolean draw);
  public Node getNode();
  public boolean getDrawX();
}