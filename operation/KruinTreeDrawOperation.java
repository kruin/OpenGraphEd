
package operation;

import graphException.*;
import graphStructure.*;
import java.awt.Color;
import java.util.Vector;
import operation.extenders.*;

public class KruinTreeDrawOperation
{
  private static final int DRAWING_OFFSET_X = 4;
  private static final int DRAWING_OFFSET_Y = 4;

  /**
   * Keep one empty grid column between the root axis and each child box.
   * This preserves the open-tree character while keeping the reserved box minimal.
   */
  private static final int ROOT_SIDE_GAP = 1;

  public static void displayKruinTreeDrawing(Graph g, Node root, int method,
                                            int width, int height) throws Exception
  {
    boolean oldSpecialSelected = root.isSpecialSelected();
    root.setSpecialSelected(true);
    LogEntry logEntry = g.startLogEntry("Kruin Tree Drawing");
    try
    {
      if ( !ConnectivityOperation.isConnected(g) )
      {
        logEntry.setData("Graph was not connected");
        g.stopLogEntry(logEntry);
        throw new GraphException("Graph is not connected!");
      }
      else if ( TreeOperation.hasCycles(g) )
      {
        logEntry.setData("Graph had cycles");
        g.stopLogEntry(logEntry);
        throw new GraphException("Graph has Cycles!");
      }
      else if ( method != 4 && !TreeOperation.isBinaryTree(g, root) )
      {
        logEntry.setData("Graph was not a Binary Tree");
        g.stopLogEntry(logEntry);
        throw new GraphException("Graph is not a Binary Tree!");
      }
      else
      {
        Vector nodes = g.createNodeExtenders(KruinNodeEx.class);
        Vector edges = g.createEdgeExtenders(KruinEdgeEx.class);
        KruinNodeEx rootEx = (KruinNodeEx)root.getExtender();

        buildTree(rootEx);

        if ( method == 1 )
        {
          domainTreeMethod(rootEx);
        }
        else if ( method == 5 )
        {
          drawnTreeMethod(g, rootEx);
        }
        else
        {
          return;
        }

        int gridWidth = rootEx.getBoundWidth();
        int gridHeight = rootEx.getBoundHeight();

        /*
         * Place the whole open tree four grid lines right and four down.
         * Use boundX for the left margin so the compact bounding box remains intact.
         */
        correctGridCoordinates(rootEx, DRAWING_OFFSET_X + rootEx.getBoundX(), DRAWING_OFFSET_Y);

        int widthIncrement = 20;
        int heightIncrement = 20;

        g.setGridArea(gridHeight + DRAWING_OFFSET_Y + 5, heightIncrement,
                      gridWidth + DRAWING_OFFSET_X + 5, widthIncrement, true);

        KruinNodeEx aNode;
        KruinEdgeEx anEdge;
        for ( int i=0; i<nodes.size(); i++ )
        {
          aNode = (KruinNodeEx)nodes.elementAt(i);
          g.relocateNode(aNode.getRef(),
                         new Location(aNode.getGridX()*widthIncrement,
                                      aNode.getGridY()*heightIncrement),
                         true);

          g.changeNodeLabel(aNode.getRef(), "#"+i+"#"+aNode.getLabel(), true);
          g.changeNodeColor(aNode.getRef(), Color.yellow, true);
        }

        for ( int i=0; i<edges.size(); i++ )
        {
          anEdge = (KruinEdgeEx)edges.elementAt(i);
          g.straightenEdge(anEdge.getRef(), true);
        }

        g.stopLogEntry(logEntry);
      }
    }
    finally
    {
      root.setSpecialSelected(oldSpecialSelected);
    }
  }

  private static int buildTree(KruinNodeEx root)
  {
    Vector children = root.getChildren();
    KruinNodeEx child;
    int size = 1;
    for ( int i=0; i<children.size(); i++ )
    {
      child = (KruinNodeEx)children.elementAt(i);
      if ( child != root.getParent() )
      {
        child.setParent(root);
        size += buildTree(child);
      }
    }
    root.setSubTreeSize(size);
    return size;
  }

  private static void drawnTreeMethod(Graph g, KruinNodeEx root)
  {
    for ( int i=1; i<g.getNumNodes(); i++ )
    {
      if ( g.getNodeAt(i).getNumEdges() == 1 )
      {
        Graph gc = KruinShortestPathOperation.getShortestPath(g, g.getNodeAt(0), g.getNodeAt(i), true);
        shortestPathMethod(gc, g, root);
      }
    }
  }

  private static void shortestPathMethod(Graph pathGraph, Graph g, KruinNodeEx root)
  {
    if ( root.getSubTreeSize() == 1 )
    {
      initializeLeaf(root);
    }
    else
    {
      if ( root.getSubTreeDone() )
      {
        shortestPathMethod(pathGraph, g, root.getLeftChild());
        domainRule(root, root.getLeftChild(), root.getRightChild());
        root.setSubTreeDone(true);
      }
    }
  }

  private static void domainTreeMethod(KruinNodeEx root)
  {
    KruinNodeEx leftChild = root.getLeftChild();
    KruinNodeEx rightChild = root.getRightChild();

    if ( leftChild == null && rightChild == null )
    {
      initializeLeaf(root);
    }
    else
    {
      if ( leftChild != null )
      {
        domainTreeMethod(leftChild);
      }
      if ( rightChild != null )
      {
        domainTreeMethod(rightChild);
      }

      if ( leftChild != null && rightChild != null )
      {
        if ( !(root.getNode().isSelected()) )
        {
          domainRule(root, leftChild, rightChild);
        }
        else
        {
          root.setLabel("Focus -" + root.getLabel());
          domainRule(root, rightChild, leftChild);
        }
      }
      else if ( leftChild != null )
      {
        otherRule(root, leftChild);
      }
      else
      {
        otherRule(root, rightChild);
      }
    }
  }

  private static void initializeLeaf(KruinNodeEx root)
  {
    root.setGridX(0);
    root.setGridY(0);
    root.setBoundX(0);
    root.setBoundY(0);
    root.setBoundWidth(0);
    root.setBoundHeight(0);
  }

  /**
   * Compact bottom-up box integration for the open tree.
   *
   * Each child first owns its smallest reserved bounding box.
   * The left subtree is placed completely left of the root axis;
   * the right subtree is placed completely right of the root axis and below
   * the reserved left box, so sibling leaves do not collapse onto one line.
   */
  static void domainRule(KruinNodeEx root, KruinNodeEx left, KruinNodeEx right)
  {
    int leftShiftX = -(rightExtent(left) + ROOT_SIDE_GAP);
    int leftShiftY = 1;

    int rightShiftX = leftExtent(right) + ROOT_SIDE_GAP;
    int rightShiftY = leftShiftY + left.getBoundHeight() + 1;

    left.shiftX(leftShiftX);
    left.shiftY(leftShiftY);

    right.shiftX(rightShiftX);
    right.shiftY(rightShiftY);

    int minX = Math.min(0, Math.min(boxMinX(left), boxMinX(right)));
    int maxX = Math.max(0, Math.max(boxMaxX(left), boxMaxX(right)));
    int maxY = Math.max(0, Math.max(boxMaxY(left), boxMaxY(right)));

    root.setGridX(0);
    root.setGridY(0);
    root.setBoundX(-minX);
    root.setBoundY(0);
    root.setBoundWidth(maxX - minX);
    root.setBoundHeight(maxY);
  }

  private static void otherRule(KruinNodeEx root, KruinNodeEx child)
  {
    child.shiftY(1);

    int minX = Math.min(0, boxMinX(child));
    int maxX = Math.max(0, boxMaxX(child));
    int maxY = Math.max(0, boxMaxY(child));

    root.setGridX(0);
    root.setGridY(0);
    root.setBoundWidth(maxX - minX);
    root.setBoundHeight(maxY);
    root.setBoundX(-minX);
    root.setBoundY(0);
  }

  private static int leftExtent(KruinNodeEx node)
  {
    return node.getBoundX();
  }

  private static int rightExtent(KruinNodeEx node)
  {
    return node.getBoundWidth() - node.getBoundX();
  }

  private static int boxMinX(KruinNodeEx node)
  {
    return node.getGridX() - node.getBoundX();
  }

  private static int boxMaxX(KruinNodeEx node)
  {
    return node.getGridX() + node.getBoundWidth() - node.getBoundX();
  }

  private static int boxMaxY(KruinNodeEx node)
  {
    return node.getGridY() + node.getBoundHeight();
  }

  private static void correctGridCoordinates(KruinNodeEx root, int shiftX, int shiftY)
  {
    root.shiftX(shiftX);
    root.shiftY(shiftY);
    if ( root.getLeftChild() != null )
    {
      correctGridCoordinates(root.getLeftChild(), root.getGridX(), root.getGridY());
    }
    if ( root.getRightChild() != null )
    {
      correctGridCoordinates(root.getRightChild(), root.getGridX(), root.getGridY());
    }
  }
}
