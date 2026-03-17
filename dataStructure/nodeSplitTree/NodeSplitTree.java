package dataStructure.nodeSplitTree;

import java.util.Vector;
import java.awt.Point;
import graphStructure.Node;
import graphStructure.Location;

public class NodeSplitTree
{
  public static int count;
  private SplitNode root;
  private int radius;
  private int splitDepth;

  public NodeSplitTree(Vector nodes)
  {
    this(nodes, 3, Node.RADIUS);
  }

  public NodeSplitTree(Vector nodes, int splitDepth, int radius)
  {
    this.splitDepth = splitDepth;
    this.radius = radius;
    if ( splitDepth > 0 )
    {
      root = new InternalSplitNode(splitDepth, 0, nodes);
    }
    else
    {
      root = new ExternalSplitNode(0, nodes);
    }
  }

  public Node nodeAt(Point point)
  {
    return root.nodeAt(point, radius);
  }

  public Node nodeAt(Location location)
  {
    return root.nodeAt(new Point(location.intX(), location.intY()), radius);
  }

  public void printTree()
  {
    System.out.println("NodeSplitTree:");
    count = 0;
    root.printNode();
    System.out.println("\nnum nodes: " + count + "\n\n");
  }
}