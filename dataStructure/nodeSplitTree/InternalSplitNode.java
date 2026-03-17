package dataStructure.nodeSplitTree;

import java.util.Vector;
import java.awt.Point;
import graphStructure.Graph;
import graphStructure.Node;

public class InternalSplitNode extends SplitNode
{
  private SplitNode leftChild;
  private SplitNode rightChild;

  public InternalSplitNode(int splitDepth, int splitCount, Vector nodes)
  {
    Vector lesser = new Vector(), greater = new Vector();
    this.splitCount = splitCount;

    if ( splitCount % 2 == 0 )
    {
      splitNode = Graph.partitionAroundMedianX(nodes, lesser, greater);
    }
    else
    {
      splitNode = Graph.partitionAroundMedianY(nodes, lesser, greater);
    }
    if ( lesser.size() == 0 )
    {
      leftChild = rightChild = null;
    }
    else if ( lesser.size() == 1 )
    {
      leftChild = new InternalSplitNode(splitDepth, splitCount+1, lesser);
      if ( greater.size() == 1 )
      {
        rightChild = new InternalSplitNode(splitDepth, splitCount+1, greater);
      }
      else
      {
        rightChild = null;
      }
    }
    else if ( lesser.size() == 2 )
    {
      leftChild = new InternalSplitNode(splitDepth, splitCount+1, lesser);
      rightChild = new InternalSplitNode(splitDepth, splitCount+1, greater);
    }
    else if ( splitCount < splitDepth )
    {
      leftChild = new InternalSplitNode(splitDepth, splitCount+1, lesser);
      rightChild = new InternalSplitNode(splitDepth, splitCount+1, greater);
    }
    else
    {
      leftChild = new ExternalSplitNode(splitCount, lesser);
      rightChild = new ExternalSplitNode(splitCount, greater);
    }
  }

  public Node nodeAt(Point aPoint, int radius)
  {
    if ( splitNode.contains(aPoint, radius) )
    {
      return splitNode;
    }
    if ( splitCount % 2 == 0 )
    {
      if ( aPoint.x < splitNode.getX() )
      {
        if ( leftChild == null )
        {
          return null;
        }
        return leftChild.nodeAt(aPoint, radius);
      }
      else if ( aPoint.x > splitNode.getX() )
      {
        if ( rightChild == null )
        {
          return null;
        }
        return rightChild.nodeAt(aPoint, radius);
      }
      else
      {
        Node ret = null;
        if ( leftChild != null )
        {
          ret = leftChild.nodeAt(aPoint, radius);
        }
        if ( ret == null && rightChild != null )
        {
          ret = rightChild.nodeAt(aPoint, radius);
        }
        return ret;
      }
    }
    else
    {
      if ( aPoint.y < splitNode.getY() )
      {
        if ( leftChild == null )
        {
          return null;
        }
        return leftChild.nodeAt(aPoint, radius);
      }
      else if ( aPoint.y > splitNode.getY() )
      {
        if ( rightChild == null )
        {
          return null;
        }
        return rightChild.nodeAt(aPoint, radius);
      }
      else
      {
        Node ret = null;
        if ( leftChild != null )
        {
          ret = leftChild.nodeAt(aPoint, radius);
        }
        if ( ret == null && rightChild != null )
        {
          ret = rightChild.nodeAt(aPoint, radius);
        }
        return ret;
      }
    }
  }

  public void printNode()
  {
    NodeSplitTree.count++;
    System.out.println(this + " L: " + leftChild + " R: " + rightChild);
    if ( leftChild != null )
    {
      leftChild.printNode();
    }
    if ( rightChild != null )
    {
      rightChild.printNode();
    }
  }

  public String toString()
  {
    if ( splitNode == null )
    {
      return "null";
    }
    return splitNode.toString();
  }
}