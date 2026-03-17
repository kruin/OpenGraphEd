package operation;

import java.util.Vector;
import java.util.Enumeration;
import dataStructure.pqTree.*;
import graphStructure.*;
import operation.extenders.*;

public class PlanarityOperation
{
  public static boolean isPlanar(Graph g)
  {
    LogEntry logEntry = g.startLogEntry("Test Planarity");
    if ( !numEdgesLessThanOrEqualThreeTimesNumVerticesMinusSix(g) )
    {
      logEntry.setData("Edges were more than 3V-6");
      g.stopLogEntry(logEntry);
      return false;
    }
    Vector graphs = BiconnectivityOperation.getBiconnectedComponents(g,false);
    Graph aGraph;

    for ( int i=0; i<graphs.size(); i++ )
    {
      aGraph = (Graph)graphs.elementAt(i);
      if ( !isPlanarHelper(aGraph) )
      {
        g.stopLogEntry(logEntry);
        return false;
      }
    }
    g.stopLogEntry(logEntry);
    return true;
  }

  private static boolean isPlanarHelper(Graph g)
  {
    LogEntry logEntry = g.startLogEntry("Test Planarity Helper");
    if ( g.getNumNodes() <= 2 )
    {
      g.stopLogEntry(logEntry);
      return true;
    }

    Vector edges;
    Enumeration enumEdges;
    PQEdgeEx anEdge;

    try
    {
      PQTree pqTree = new PQTree();
      Vector nodesInStOrder = STNumberOperation.stNumber(g, false);

      g.createNodeExtenders((new PQNodeEx()).getClass());
      edges = g.createEdgeExtenders((new PQEdgeEx()).getClass());
      for ( int i=0; i<edges.size(); i++ )
      {
        anEdge = (PQEdgeEx)edges.elementAt(i);
        anEdge.setPQNode(new PQNode(anEdge));
      }

      STNodeEx stNode;
      for ( int i=0; i<nodesInStOrder.size(); i++ )
      {
        stNode = (STNodeEx)nodesInStOrder.elementAt(i);
        ((PQNodeEx)stNode.getRef().getExtender()).setStNumber(stNode.getStNumber());
        nodesInStOrder.setElementAt(stNode.getRef().getExtender(), i);
      }

      PQNodeEx currentNode = (PQNodeEx)nodesInStOrder.firstElement();
      enumEdges = currentNode.incidentEdges().elements();
      while ( enumEdges.hasMoreElements() )
      {
        pqTree.getRoot().addChild( ((PQEdgeEx)enumEdges.nextElement()).getPQNode() );
      }

      for ( int j=2; j<nodesInStOrder.size(); j++ )
      {
        edges = new Vector();
        currentNode = (PQNodeEx)nodesInStOrder.elementAt(j-1);
        enumEdges = currentNode.incidentEdges().elements();
        while ( enumEdges.hasMoreElements() )
        {
          anEdge = (PQEdgeEx)enumEdges.nextElement();
          if ( ((PQNodeEx)anEdge.otherEndFrom(currentNode)).getStNumber() < j )
          {
            edges.addElement( anEdge.getPQNode() );
          }
        }

        PQNode pertRoot = pqTree.reduction(edges);

        if ( pqTree.isNullTree() )
        {
          g.stopLogEntry(logEntry);
          return false;
        }

        PQNode newRoot = new PQNode();
        edges = new Vector();
        enumEdges = currentNode.incidentEdges().elements();
        while ( enumEdges.hasMoreElements() )
        {
          anEdge = (PQEdgeEx)enumEdges.nextElement();
          if ( ((PQNodeEx)anEdge.otherEndFrom(currentNode)).getStNumber() > j )
          {
            edges.addElement( anEdge.getPQNode() );
          }
        }

        if ( edges.size() == 1 )
        {
          newRoot = (PQNode)edges.firstElement();
        }
        else
        {
          enumEdges = edges.elements();
          while ( enumEdges.hasMoreElements() )
          {
            newRoot.addChild((PQNode)enumEdges.nextElement());
          }
        }

        if ( pertRoot.isQNode() )
        {
             if ( pertRoot.isFull() )
             {
               System.out.println("isPseudo: " + pertRoot.isPseudoNode());
             }

          
          
          pertRoot.replaceFullChildrenWith(newRoot);
          if ( !pertRoot.isPseudoNode() )
          {
            if ( pertRoot.hasOnlyTwoChildren() )
            {
              pertRoot.convertToPNode();
            }
            else if ( pertRoot.hasOnlyOneChild() && !pertRoot.isPseudoNode() )
            {
              if ( pertRoot == pqTree.getRoot() )
              {
                newRoot.becomeRoot();
                pqTree.setRoot(newRoot);
              }
              else
              {
                pertRoot.getParent().replaceChild(pertRoot, newRoot);
              }
            }
          }
        }
        else
        {
          if ( pertRoot == pqTree.getRoot() )
          {
            pqTree.setRoot(newRoot);
          }
          else
          {
            pertRoot.getParent().replaceChild(pertRoot, newRoot);
          }
        }
        pertRoot.clear();
        // update leaves?
      }
    }
    catch (Exception e)
    {
      System.out.println("PQTree error during planarity test");
      e.printStackTrace();
      g.stopLogEntry(logEntry);
      return false;
    }
    g.stopLogEntry(logEntry);
    return true;
  }

  public static boolean numEdgesLessThanOrEqualThreeTimesNumVerticesMinusSix(Graph g)
  {
    if ( g.getNumNodes() == 2 )
    {
      return true;
    }
    int threeTimeNumVerticesMinusSix = 3*g.getNumNodes()-6;
    return g.getNumEdges() <= threeTimeNumVerticesMinusSix;
  }
}