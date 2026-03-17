package  operation;
import graphException.*;
import graphStructure.*;
import java.awt.Color;
import java.util.Vector;
import operation.extenders.*;

public class KruinTreeDrawOperation
{
  public static void displayKruinTreeDrawing(Graph g, Node root, int method,
                                            int width, int height) throws Exception
  {
      root.setSpecialSelected(true);
    LogEntry logEntry = g.startLogEntry("Kruin Tree Drawing");
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
    //	  thirdMethod(g, rootEx);
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

     correctGridCoordinates(rootEx, 2 + gridWidth, 2);



     int widthIncrement = 20;//doubled in graphcontroller-kruinGridMode()
      int heightIncrement = 20;//ditto, or KruinDialog-WIDTH 400
 g.setGridArea(gridWidth+1, widthIncrement,gridHeight+1, heightIncrement, true);

      KruinNodeEx aNode;
      KruinEdgeEx anEdge;
      for ( int i=0; i<nodes.size(); i++ )
      {
        aNode = (KruinNodeEx)nodes.elementAt(i);
         g.relocateNode( aNode.getRef(),
                        new Location( aNode.getGridX()*widthIncrement,
                                      aNode.getGridY()*heightIncrement ),
                        true );

      aNode.setLabel("#"+i+"#"+ aNode.getLabel() );
      aNode.setColor(Color.yellow);
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

        	matrixNode = g.createNode(new Location(10, aNode.getGridY()*heightIncrement));
        	if ( aNode.getSubTreeSize() == 1 ){
        	matrixNode.setLabel(aNode.getIndex()+": "+ aNode.getLabel());
                matrixNode.setColor(Color.green);
        	}
        	g.createNode(new Location(aNode.getGridX()*widthIncrement,10));
                matrixNode.setColor(Color.orange);
      }
      g.stopLogEntry(logEntry);
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
    	root.setGridX(0);
        root.setGridY(0);

        root.setBoundWidth(0);
        root.setBoundHeight(0);
    }
    else
    { if  (root.getSubTreeDone())
        {
        shortestPathMethod(pathGraph, g, root.getLeftChild());
    domainRule(root, root.getLeftChild(), root.getRightChild());
        root.setSubTreeDone(true);
    }

    }
  }

      private static void domainTreeMethod(KruinNodeEx root)
  {
    if ( root.getSubTreeSize() == 1 )
    {
    	root.setGridX(0);
        root.setGridY(0);

        root.setBoundWidth(0);
        root.setBoundHeight(0);

    } else {
      domainTreeMethod(root.getLeftChild());
      domainTreeMethod(root.getRightChild());
      if (!(root.getNode().isSelected())) {
        domainRule(root,root.getLeftChild(),root.getRightChild());
      }else{
	root.setLabel("Focus -"+root.getLabel());
	  domainRule(root,root.getRightChild(),root.getLeftChild());
      }
    }
}
   static void domainRule( KruinNodeEx root,KruinNodeEx left,KruinNodeEx right )   {
   //KruinNodeEx  left        = root.getLeftChild();
   //KruinNodeEx  right       = root.getRightChild();
   int       shiftLeft   = left.getBoundWidth();
	left.shiftX(-1*(shiftLeft+1));
    left.shiftY(shiftLeft+1 );     /*45 degrees, same shift, */
	int shiftRight = shiftLeft + 2 + left.getBoundWidth();
     right.shiftX(shiftRight);
     right.shiftY(shiftRight);
     root.setGridX(0);
     root.setGridY(0);
     root.setBoundWidth(root.getBoundWidth()+ shiftLeft+1+1+ shiftRight);
     root.setBoundHeight(root.getBoundHeight() + shiftLeft +1+1+ shiftRight);
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
	      // ||
//tvo  (gc >=  root.getRightChild().getSubTreeSize() ))
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
    child.shiftY(1);
    root.setGridX(0);
    root.setGridY(0);
    root.setBoundWidth(child.getBoundWidth());
    root.setBoundHeight(child.getBoundHeight()+1);
    root.setBoundX(child.getBoundX());
    root.setBoundY(0);
    //System.out.println("otherRule: " + root);
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
}
