package graphStructure;

import java.awt.Color;

public interface EdgeInterface
{
  public NodeInterface otherEndFrom(NodeInterface aNode);

  public boolean isBetween(NodeInterface firstNode, NodeInterface secondNode);

  public NodeInterface getStartNode();

  public NodeInterface getEndNode();

  public boolean isGenerated();

  public EdgeInterface getCopy();
  
  public EdgeInterface getMasterCopy();

  public void setCopy(EdgeInterface aCopy);

  public EdgeInterface getPreviousInOrderFrom( NodeInterface sourceNode );

  public EdgeInterface getNextInOrderFrom( NodeInterface sourceNode );

  public void setNextInOrderFrom( NodeInterface sourceNode, EdgeInterface nextEdge );

  public void setPreviousInOrderFrom( NodeInterface sourceNode, EdgeInterface prevEdge );

  public int getHigherIndex();

  public int getLowerIndex();

  public void setColor(Color aColor);

  public Color getColor();

  public Node getDirectedSourceNode();

  public void setDirectedFrom(NodeInterface directedSourceNode);

  public double getStraightLength();

  public double getLength();

  public void makeStraight();

  public void update();

  public Edge getEdge();
}