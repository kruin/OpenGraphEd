package operation;

import graphException.*;
import graphStructure.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import operation.extenders.*;
import operation.opentree.*;

public class KruinTreeDrawOperation
{
  /**
   * Keep one empty grid column between the root axis and each child box.
   * This preserves the open-tree character while keeping the reserved box minimal.
   */
  private static final int ROOT_SIDE_GAP = 1;

  /**
   * Global placement of the complete drawing, including projection nodes.
   */
  private static final int DRAWING_OFFSET_X = 4;
  private static final int DRAWING_OFFSET_Y = 2;

  /**
   * Projection guides must stay outside the tree box.
   * With the tree starting at x=4 and y=2, place the left projection at x=2
   * and the top projection at y=0.
   */
  private static final int LEFT_PROJECTION_X = 2;
  private static final int TOP_PROJECTION_Y = 0;

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

        /**
         * Method 1-4: https://docs.google.com/#folders/folder.0.0BzkqeCBoFVgzY2FlMTcyMjQtMWI0Zi00OGMwLWFjODktYzA5ZjhhY2Q2MGI4
         * (Dutch)
         */
        if ( method == 1 )
        {
          domainTreeMethod(rootEx);
        }
        else if ( method == 2 )
        {
      //    inwardTreeMethod(g, rootEx);
        }
        else if ( method == 3 )
        {
      //    thirdMethod(g, rootEx);
     //     compactTreeMethod(g, rootEx);
        }
        else if ( method == 4 )
        {
     //     verticalTreeMethod(g, rootEx);
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

        correctGridCoordinates(rootEx, DRAWING_OFFSET_X + rootEx.getBoundX(), DRAWING_OFFSET_Y);

        // --- OPEN LANGUAGE TREE DEBUG / TEST ---
        debugOpenLanguageProjection(rootEx);
        // --- END OPEN LANGUAGE TREE DEBUG / TEST ---

        int widthIncrement = 20;//doubled in graphcontroller-kruinGridMode()
        int heightIncrement = 20;//ditto, or KruinDialog-WIDTH 400
        g.setGridArea(gridHeight + DRAWING_OFFSET_Y + 5, heightIncrement,
                      gridWidth + DRAWING_OFFSET_X + 5, widthIncrement, true);

        KruinNodeEx aNode;
        KruinEdgeEx anEdge;
        for ( int i=0; i<nodes.size(); i++ )
        {
          aNode = (KruinNodeEx)nodes.elementAt(i);
          g.relocateNode( aNode.getRef(),
                          new Location( aNode.getGridX()*widthIncrement,
                                        aNode.getGridY()*heightIncrement ),
                          true );

          g.changeNodeLabel(aNode.getRef(), "#"+i+"#"+ aNode.getLabel(), true);
          g.changeNodeColor(aNode.getRef(), Color.yellow, true);
        }

        for ( int i=0; i<edges.size(); i++ )
        {
          anEdge = (KruinEdgeEx)edges.elementAt(i);
          g.straightenEdge(anEdge.getRef(), true);
        }

        Node matrixNode;

        for ( int i=0; i<nodes.size(); i++ )
        {
          aNode = (KruinNodeEx)nodes.elementAt(i);

          matrixNode = g.createNode(new Location(LEFT_PROJECTION_X*widthIncrement,
                                                  aNode.getGridY()*heightIncrement));
          if ( aNode.getSubTreeSize() == 1 )
          {
            g.changeNodeLabel(matrixNode, aNode.getIndex()+": "+ aNode.getLabel(), true);
            g.changeNodeColor(matrixNode, Color.green, true);
          }

          matrixNode = g.createNode(new Location(aNode.getGridX()*widthIncrement,
                                                  TOP_PROJECTION_Y*heightIncrement));
          g.changeNodeColor(matrixNode, Color.orange, true);
        }
        g.stopLogEntry(logEntry);
      }
    }
    finally
    {
      root.setSpecialSelected(oldSpecialSelected);
    }
  }

  private static int buildTree(KruinNodeEx root) //get size of tree and all subtrees
  {
    Vector children = root.getChildren();
    KruinNodeEx child;
    int size = 1;
    for ( int i=0; i<children.size(); i++ )
    {
      child = (KruinNodeEx)children.elementAt(i);
      if ( child != root.getParent() )/** not hierarchical*/
      {
        child.setParent(root);
        size += buildTree(child);
      }
    }
    root.setSubTreeSize(size);
    return size;
  }

  private static void drawnTreeMethod(Graph g, KruinNodeEx root)
  /*
    deploy the nodes in the same order they were drawn in the editor.
    The node that was drawn first will be the highest node (closest to the root)and so on
  */
  {
    for ( int i=1; i< g.getNumNodes(); i++ )
    {
      if ( g.getNodeAt(i).getNumEdges() == 1)
      {
        Graph gc = KruinShortestPathOperation.getShortestPath(g, g.getNodeAt(0), g.getNodeAt(i), true);
        shortestPathMethod(gc,g,root);
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
      if  (root.getSubTreeDone())
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
        if (!(root.getNode().isSelected()))
        {
          domainRule(root, leftChild, rightChild);
        }
        else
        {
          root.setLabel("Focus -"+root.getLabel());
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
   * Bottom-up box integration for the Kruin layout.
   *
   * Strategy:
   * 1. each child already owns its smallest reserved bounding box;
   * 2. place the left child box completely to the left of the root axis;
   * 3. place the right child box completely to the right of the root axis;
   * 4. place both children below the root, with a vertical offset based on the height
   *    of their already reserved box.
   *
   * This keeps the tree open, prevents nodes from collapsing onto one axis,
   * and computes the parent box exactly as the union of the reserved child boxes.
   */
  static void domainRule( KruinNodeEx root, KruinNodeEx left, KruinNodeEx right )
  {
    int leftShiftX = -(rightExtent(left) + ROOT_SIDE_GAP);
    int leftShiftY = 1;

    /**
     * Place the right subtree below the full reserved box of the left subtree.
     * This preserves the Kruin character: growth in width AND depth.
     * It also prevents sibling leaves from ending up on the same grid line.
     */
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

  private static KruinNodeEx findLastNodeWithSizeGreaterThan(KruinNodeEx root, double num)
  {
    if ( root.getSubTreeSize() < num )
    {
      if ( root.getParent() != null )
      {
        return root.getParent();
      }
      else
      {
        return root;
      }
    }
    else
    {
      if ( root.getRightChild() == null)
      {
        return findLastNodeWithSizeGreaterThan(root.getLeftChild(), num);
      }
      else
      {
        return findLastNodeWithSizeGreaterThan(root.getRightChild(), num);
      }
    }
  }

  private static void otherRule( KruinNodeEx root, KruinNodeEx child )
  {
    child.shiftY(child.getBoundHeight() + 1);

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

  private static double log2(double x)
  {
    return Math.log(x)/Math.log(2.0);
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

  // ---------------------------------------------------------------------------
  // OPEN LANGUAGE TREE DEBUG / TEST
  // ---------------------------------------------------------------------------

  private static void debugOpenLanguageProjection(KruinNodeEx selectedRoot)
  {
    try
    {
      OpenLanguageGrid grid = new OpenLanguageGrid();
      Map nodeMap = new HashMap();

      OpenLanguageNode openRoot = buildOpenLanguageSubtree(selectedRoot, null, grid, nodeMap);

      System.out.println("======================================");
      System.out.println("OPEN LANGUAGE TREE DEBUG");
      System.out.println("Selected root label   : " + openRoot.getLabel());
      System.out.println("Logical projection    : " + safe(openRoot.getCategoryValue()));
      System.out.println("Syntactical projection: " + safe(openRoot.getSyntacticReflection()));

      List lexicalLeaves = new ArrayList();
      collectPlacedLexicalLeaves(openRoot, nodeMap, lexicalLeaves);

      System.out.print("Lexical projection    : ");
      if ( lexicalLeaves.size() == 0 )
      {
        System.out.println("(none)");
      }
      else
      {
        for ( int i=0; i<lexicalLeaves.size(); i++ )
        {
          if ( i > 0 )
          {
            System.out.print(" ");
          }
          System.out.print((String)lexicalLeaves.get(i));
        }
        System.out.println();
      }
      System.out.println("======================================");
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
  }

  private static OpenLanguageNode buildOpenLanguageSubtree(KruinNodeEx source,
                                                           OpenLanguageNode parent,
                                                           OpenLanguageGrid grid,
                                                           Map nodeMap)
  {
    int id = source.getIndex();
    OpenLanguageNode node = new OpenLanguageNode(id, source.getLabel());
    node.setCreatedAtStep(0);
    node.setCategoryValue(source.getLabel());

    if ( parent != null )
    {
      node.setParent(parent.getId());
    }

    if ( grid.isFree(source.getGridX(), source.getGridY()) )
    {
      node.place(source.getGridX(), source.getGridY());
      grid.occupy(source.getGridX(), source.getGridY(), node.getId());
    }
    else
    {
      node.deferPlacement();
    }

    nodeMap.put(Integer.valueOf(node.getId()), node);

    KruinNodeEx left = source.getLeftChild();
    KruinNodeEx right = source.getRightChild();

    if ( left == null && right == null )
    {
      node.setLexicalValue(source.getLabel());
      return node;
    }

    OpenLanguageNode leftNode = null;
    OpenLanguageNode rightNode = null;

    if ( left != null )
    {
      leftNode = buildOpenLanguageSubtree(left, node, grid, nodeMap);
    }

    if ( right != null )
    {
      rightNode = buildOpenLanguageSubtree(right, node, grid, nodeMap);
    }

    if ( leftNode != null && rightNode != null )
    {
      node.setChildren(leftNode.getId(), rightNode.getId());
      node.setExpandedAtStep(0);
      node.setSyntacticReflection(leftNode.getLabel() + "-" + rightNode.getLabel());
    }

    return node;
  }

  private static void collectPlacedLexicalLeaves(OpenLanguageNode node, Map nodeMap, List out)
  {
    Integer leftId = node.getLeftChildId();
    Integer rightId = node.getRightChildId();

    if ( leftId == null && rightId == null )
    {
      if ( node.isPlaced() && node.getLexicalValue() != null )
      {
        out.add(node.getLexicalValue());
      }
      return;
    }

    if ( leftId != null )
    {
      OpenLanguageNode leftNode = (OpenLanguageNode)nodeMap.get(leftId);
      if ( leftNode != null )
      {
        collectPlacedLexicalLeaves(leftNode, nodeMap, out);
      }
    }

    if ( rightId != null )
    {
      OpenLanguageNode rightNode = (OpenLanguageNode)nodeMap.get(rightId);
      if ( rightNode != null )
      {
        collectPlacedLexicalLeaves(rightNode, nodeMap, out);
      }
    }
  }

  private static String safe(String s)
  {
    if ( s == null )
    {
      return "(none)";
    }
    return s;
  }
}
