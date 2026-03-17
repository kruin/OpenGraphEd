package operation;

import java.util.Vector;
import graphStructure.*;
import graphException.*;
import operation.extenders.*;

public class ChanTreeDrawOperation
{
  public static void displayChanTreeDrawing(Graph g, Node root, int method, 
                                            int width, int height) throws Exception
  {
    LogEntry logEntry = g.startLogEntry("Chan Tree Drawing");
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
      Vector nodes = g.createNodeExtenders(ChanNodeEx.class);
      Vector edges = g.createEdgeExtenders(ChanEdgeEx.class);
      ChanNodeEx rootEx = (ChanNodeEx)root.getExtender(); 
      buildTree(rootEx);
      if ( method == 1 )
      {
        firstMethod(g, rootEx); 
      }
      else if ( method == 2 )
      {
        secondMethod(g, rootEx);
      }
      else if ( method == 3 )
      {
        thirdMethod(g, rootEx);
      }
      else
      {
        return;
      }
      correctGridCoordinates(rootEx, rootEx.getBoundX(), 0);
      int gridWidth = rootEx.getBoundWidth();
      int gridHeight = rootEx.getBoundHeight();
      
      g.setGridArea(rootEx.getBoundHeight()+1, height,
                    rootEx.getBoundWidth()+1, width, true);
      int widthIncrement = g.getGridColWidth();
      int heightIncrement = g.getGridRowHeight();
      
      ChanNodeEx aNode;
      ChanEdgeEx anEdge;
      for ( int i=0; i<nodes.size(); i++ )
      {
        aNode = (ChanNodeEx)nodes.elementAt(i);
        g.relocateNode( aNode.getRef(),
                        new Location( aNode.getGridX()*widthIncrement,
                                      aNode.getGridY()*heightIncrement ),
                        true );
      }
      for ( int i=0; i<edges.size(); i++ )
      {
        anEdge = (ChanEdgeEx)edges.elementAt(i);
        g.straightenEdge(anEdge.getRef(), true);
      }
      g.stopLogEntry(logEntry);
    }
  }
  
  private static int buildTree(ChanNodeEx root)
  {
    Vector children = root.getChildren();
    ChanNodeEx child;
    int size = 1;
    for ( int i=0; i<children.size(); i++ )
    {
      child = (ChanNodeEx)children.elementAt(i);
      if ( child != root.getParent() )
      {
        child.setParent(root);
        size+= buildTree(child);
      }
    }
    root.setSubTreeSize(size);
    return size;
  }
  
  private static void firstMethod(Graph g, ChanNodeEx root)
  {
    if ( root.getSubTreeSize() == 1 )
    {
      root.setGridX(0);
      root.setGridY(0);
      root.setBoundX(0);
      root.setBoundY(0);
      root.setBoundWidth(0);
      root.setBoundHeight(0);
    }
    else
    { 
      firstMethod(g, root.getLeftChild());
      if ( root.getRightChild() != null )
      {
        firstMethod(g, root.getRightChild());
        if ( root.getLeftChild().getSubTreeSize() <
             root.getRightChild().getSubTreeSize() )
        {
          leftRule( root, root.getLeftChild(), root.getRightChild() );
        }
        else
        {
          rightRule( root, root.getLeftChild(), root.getRightChild() );
        }
      }
      else
      {
        otherRule( root, root.getLeftChild() );
      }
    }
  }
  
  private static void secondMethod(Graph g, ChanNodeEx root)
  {
    secondMethod(g, root, 0, 0);
  }
  
  private static void secondMethod(Graph g, ChanNodeEx root,
                                   int biggestLeftSubTreeSize,
                                   int biggestRightSubTreeSize)
  {
    if ( root.getSubTreeSize() == 1 )
    {
      root.setGridX(0);
      root.setGridY(0);
      root.setBoundX(0);
      root.setBoundY(0);
      root.setBoundWidth(0);
      root.setBoundHeight(0);
    }
    else
    { 
      if ( root.getLeftChild().getSubTreeSize() > biggestLeftSubTreeSize )
      {
        biggestLeftSubTreeSize = root.getLeftChild().getSubTreeSize();
      }
      if ( root.getRightChild() != null &&
           root.getRightChild().getSubTreeSize() > biggestRightSubTreeSize )
      {
        biggestRightSubTreeSize = root.getRightChild().getSubTreeSize();
      }
      secondMethod(g, root.getLeftChild(), biggestLeftSubTreeSize,
                   biggestRightSubTreeSize);
      if ( root.getRightChild() != null )
      {
        secondMethod(g, root.getRightChild(), biggestLeftSubTreeSize,
                     biggestRightSubTreeSize);
        if ( root.getLeftChild().getSubTreeSize() + biggestRightSubTreeSize <
             root.getRightChild().getSubTreeSize() + biggestLeftSubTreeSize )
        {
          leftRule( root, root.getLeftChild(), root.getRightChild() );
        }
        else
        {
          rightRule( root, root.getLeftChild(), root.getRightChild() );
        }
      }
      else
      {
        otherRule( root, root.getLeftChild() );
      }
    }
  }
  
  private static double log2(double x)
  {
    return Math.log(x)/Math.log(2.0);
  }
    
  private static ChanNodeEx findLastNodeWithSizeGreaterThan(ChanNodeEx root, double num)
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
      if ( root.getRightChild() == null ||
           root.getLeftChild().getSubTreeSize() >=
           root.getRightChild().getSubTreeSize() )
      {
        return findLastNodeWithSizeGreaterThan(root.getLeftChild(), num);
      }
      else
      {
        return findLastNodeWithSizeGreaterThan(root.getRightChild(), num);
      }
    }
  }
  
  private static void thirdMethod(Graph g, ChanNodeEx root)
  {
    double a = g.getNumNodes() / Math.pow(2, Math.sqrt(2*log2(g.getNumNodes())));
    ChanNodeEx kNode = findLastNodeWithSizeGreaterThan(root, g.getNumNodes()-a);
    //System.out.println("n-a: " + (g.getNumNodes()-a) + " kNode: " + kNode);
    boolean left = false;
    if ( kNode == kNode.getParent().getLeftChild() )
    {
      left = true;
    }
    kNode = kNode.getParent();
    thirdMethod1(g, root, kNode);
    int shift;
    if ( left )
    {
      shift = root.getBoundX();
    }
    else
    {
      shift = root.getBoundWidth() - root.getBoundX();
    }
    //System.out.println("second part");
    thirdMethod2(g, root, kNode, false, left, shift);
  }
  
  private static void thirdMethod1(Graph g, ChanNodeEx root, ChanNodeEx kNode)
  {
    if ( root.getSubTreeSize() == 1 )
    {
      root.setGridX(0);
      root.setGridY(0);
      root.setBoundX(0);
      root.setBoundY(0);
      root.setBoundWidth(0);
      root.setBoundHeight(0);
    }
    else
    { 
      if ( root != kNode )
      {
        thirdMethod1(g, root.getLeftChild(), kNode);
        if ( root.getRightChild() != null )
        {
          thirdMethod1(g, root.getRightChild(), kNode);
          if ( root.getLeftChild().getSubTreeSize() <
               root.getRightChild().getSubTreeSize() )
          {
            leftRule( root, root.getLeftChild(), root.getRightChild() );
          }
          else
          {
            rightRule( root, root.getLeftChild(), root.getRightChild() );
          }
        }
        else
        {
          otherRule( root, root.getLeftChild() );
        }
      }
    }
  }
  
  private static void thirdMethod2(Graph g, ChanNodeEx root, ChanNodeEx kNode, boolean passedKNode, boolean left, int shift )
  {
    if ( root.getSubTreeSize() == 1 )
    {
      root.setGridX(0);
      root.setGridY(0);
      root.setBoundX(0);
      root.setBoundY(0);
      root.setBoundWidth(0);
      root.setBoundHeight(0);
    }
    else
    {
      if ( root == kNode )
      {
        thirdMethod2(g, root.getLeftChild(), kNode, true, left, shift);
        if ( root.getRightChild() != null )
        {
          thirdMethod2(g, root.getRightChild(), kNode, true, left, shift);
          if ( left )
          {
            extendedRightRule(root, root.getLeftChild(), root.getRightChild(), shift);
          }
          else
          {
            extendedLeftRule(root, root.getLeftChild(), root.getRightChild(), shift);
          }
        }
        else
        {
          extendedOtherRule(root, root.getLeftChild(), shift);
        }
      }
      else if ( passedKNode )
      {
        thirdMethod2(g, root.getLeftChild(), kNode, passedKNode, left, shift);
        if ( root.getRightChild() != null )
        {
          thirdMethod2(g, root.getRightChild(), kNode, passedKNode, left, shift);
          if ( left )
          {
            rightRule( root, root.getLeftChild(), root.getRightChild() );
          }
          else
          {
            leftRule( root, root.getLeftChild(), root.getRightChild() );
          }
        }
        else
        {
          otherRule( root, root.getLeftChild() );
        }
      }
      else
      {
        thirdMethod2(g, root.getLeftChild(), kNode, passedKNode, left, shift);
        if ( root.getRightChild() != null )
        {
          thirdMethod2(g, root.getRightChild(), kNode, passedKNode, left, shift);
          if ( root.getLeftChild().getSubTreeSize() <
               root.getRightChild().getSubTreeSize() )
          {
            leftRule( root, root.getLeftChild(), root.getRightChild() );
          }
          else
          {
            rightRule( root, root.getLeftChild(), root.getRightChild() );
          }
        }
        else
        {
          otherRule( root, root.getLeftChild() );
        }
      }
    }
  }
  
  private static void leftRule( ChanNodeEx root, ChanNodeEx left, ChanNodeEx right )
  {
    left.shiftX(-1*(left.getBoundWidth()-left.getBoundX()+1));
    left.shiftY(1);
    right.shiftY(1+left.getBoundHeight());
    root.setGridX(0);
    root.setGridY(0);
    root.setBoundWidth(Math.max(right.getBoundX(), 1+left.getBoundWidth()) +
                       right.getBoundWidth()-right.getBoundX());
    root.setBoundHeight(left.getBoundHeight()+right.getBoundHeight()+1);
    root.setBoundX(Math.max(right.getBoundX(), 1+left.getBoundWidth()));
    root.setBoundY(0);
    //System.out.println("leftRule: " + root);
  }
  
  private static void rightRule( ChanNodeEx root, ChanNodeEx left, ChanNodeEx right )
  {
    right.shiftX(1+right.getBoundX());
    right.shiftY(1);
    left.shiftY(1+right.getBoundHeight());
    root.setGridX(0);
    root.setGridY(0);
    root.setBoundWidth(left.getBoundX() +
                       Math.max(1+right.getBoundWidth(), left.getBoundWidth()-left.getBoundX()));
    root.setBoundHeight(left.getBoundHeight()+right.getBoundHeight()+1);
    root.setBoundX(left.getBoundX());
    root.setBoundY(0);
    //System.out.println("rightRule: " + root);
  }
  
  private static void otherRule( ChanNodeEx root, ChanNodeEx child )
  {
    child.shiftY(1);
    root.setGridX(0);
    root.setGridY(0);
    root.setBoundWidth(child.getBoundWidth());
    root.setBoundHeight(child.getBoundHeight()+1);
    root.setBoundX(child.getBoundX());
    root.setBoundY(0);
    //System.out.println("otherRule: " + root);
  }
  
  private static void extendedLeftRule( ChanNodeEx root, ChanNodeEx left, ChanNodeEx right, int shift )
  {
    left.shiftX(-1*(left.getBoundWidth()-left.getBoundX()+1));
    left.shiftY(1);
    right.shiftX(shift);
    right.shiftY(1+left.getBoundHeight());
    root.setGridX(0);
    root.setGridY(0);
    root.setBoundWidth(shift +
                       Math.max(right.getBoundWidth()-shift, 1+left.getBoundWidth()));
    root.setBoundHeight(left.getBoundHeight()+right.getBoundHeight()+1);
    root.setBoundX(Math.max(right.getBoundWidth()-shift, 1+left.getBoundWidth()));
    root.setBoundY(0);
    //System.out.println("extendedleftRule: " + root);
  }
  
  private static void extendedRightRule( ChanNodeEx root, ChanNodeEx left, ChanNodeEx right, int shift )
  {
    right.shiftX(1+right.getBoundX());
    right.shiftY(1);
    left.shiftX(-1*shift);
    left.shiftY(1+right.getBoundHeight());
    root.setGridX(0);
    root.setGridY(0);
    root.setBoundWidth(shift +
                       Math.max(1+right.getBoundWidth(), left.getBoundWidth())-shift);
    root.setBoundHeight(left.getBoundHeight()+right.getBoundHeight()+1);
    root.setBoundX(shift);
    root.setBoundY(0);
    //System.out.println("extendedrightRule: " + root);
  }
  
  private static void extendedOtherRule(ChanNodeEx root, ChanNodeEx child, int shift)
  {
    child.shiftX(-1*shift);
    child.shiftY(1);
    root.setGridX(0);
    root.setGridY(0);
    root.setBoundWidth(child.getBoundWidth());
    root.setBoundHeight(child.getBoundHeight()+1);
    root.setBoundX(shift);
    root.setBoundY(0);
    //System.out.println("extendedotherRule: " + root);
  }
  
  private static void correctGridCoordinates(ChanNodeEx root, int shiftX, int shiftY)
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
