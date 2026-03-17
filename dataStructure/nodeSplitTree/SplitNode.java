package dataStructure.nodeSplitTree;

import java.awt.Point;
import graphStructure.Node;

public abstract class SplitNode
{
  protected int splitCount;
  protected Node splitNode;

  public Node getSplitNode() { return splitNode; }

  public abstract Node nodeAt(Point aPoint, int radius);

  public abstract void printNode();
}