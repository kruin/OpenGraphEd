package dataStructure.nodeSplitTree;

import java.util.Vector;
import java.awt.Point;
import graphStructure.Graph;
import graphStructure.Node;

public class ExternalSplitNode extends SplitNode
{
  private Vector leftChildren;
  private Vector rightChildren;

  public ExternalSplitNode( int splitCount, Vector nodes )
  {
    this.splitCount = splitCount;
    leftChildren = new Vector();
    rightChildren = new Vector();
    if ( splitCount % 2 == 0 )
    {
      splitNode = Graph.partitionAroundMedianX(nodes, leftChildren, rightChildren);
    }
    else
    {
      splitNode = Graph.partitionAroundMedianY(nodes, leftChildren, rightChildren);
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
        for ( int i=0; i<leftChildren.size(); i++ )
        {
          if ( ((Node)leftChildren.elementAt(i)).contains(aPoint, radius) )
          {
            return (Node)leftChildren.elementAt(i);
          }
        }
      }
      else if ( aPoint.x > splitNode.getX() )
      {
        for ( int i=0; i<rightChildren.size(); i++ )
        {
          if ( ((Node)rightChildren.elementAt(i)).contains(aPoint, radius) )
          {
            return (Node)rightChildren.elementAt(i);
          }
        }
      }
      else
      {
        for ( int i=0; i<leftChildren.size(); i++ )
        {
          if ( ((Node)leftChildren.elementAt(i)).contains(aPoint, radius) )
          {
            return (Node)leftChildren.elementAt(i);
          }
        }
        for ( int i=0; i<rightChildren.size(); i++ )
        {
          if ( ((Node)rightChildren.elementAt(i)).contains(aPoint, radius) )
          {
            return (Node)rightChildren.elementAt(i);
          }
        }
      }
    }
    else
    {
      if ( aPoint.y < splitNode.getY() )
      {
        for ( int i=0; i<leftChildren.size(); i++ )
        {
          if ( ((Node)leftChildren.elementAt(i)).contains(aPoint, radius) )
          {
            return (Node)leftChildren.elementAt(i);
          }
        }
      }
      else if ( aPoint.y > splitNode.getY() )
      {
        for ( int i=0; i<rightChildren.size(); i++ )
        {
          if ( ((Node)rightChildren.elementAt(i)).contains(aPoint, radius) )
          {
            return (Node)rightChildren.elementAt(i);
          }
        }
      }
      else
      {
        for ( int i=0; i<leftChildren.size(); i++ )
        {
          if ( ((Node)leftChildren.elementAt(i)).contains(aPoint, radius) )
          {
            return (Node)leftChildren.elementAt(i);
          }
        }
        for ( int i=0; i<rightChildren.size(); i++ )
        {
          if ( ((Node)rightChildren.elementAt(i)).contains(aPoint, radius) )
          {
            return (Node)rightChildren.elementAt(i);
          }
        }
      }
    }
    return null;
  }

  public void printNode()
  {
    NodeSplitTree.count++;
    System.out.println(this + " L: " + leftChildren + " R: " + rightChildren);
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