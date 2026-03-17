package operation;

import java.util.Vector;
import java.util.Enumeration;
import dataStructure.pqTree.*;
import graphStructure.*;
import graphException.*;
import operation.extenders.*;

public class EmbedOperation
{
  public static void embed(Graph g) throws Exception
  {
    embed(g, true);
  }

  public static void embed(Graph g, boolean check) throws Exception
  {
    LogEntry logEntry = g.startLogEntry("Embedding");
    if ( check && !PlanarityOperation.isPlanar(g) )
    {
      logEntry.setData("Graph was not Planar");
      g.stopLogEntry(logEntry);
      throw new GraphException("Graph is not planar!");
    }
    else
    {
      Vector graphs = BiconnectivityOperation.getBiconnectedComponents(g, true);
      Vector edges;
      g.deleteAllEdges();

      Node aNode, sNode, dNode;
      Edge anEdge;
      Graph aGraph;

      for ( int i=0; i<graphs.size(); i++ )
      {
        aGraph = (Graph)graphs.elementAt(i);
        if ( aGraph.getNumNodes() > 2 )
        {
          PQNodeEx tNode = upwardEmbed(aGraph);
          if ( tNode != null )
          {
            entireEmbed(aGraph, tNode);
          }
        }
        // associate with each edge in each biconnected component its corresponding
        // edge in the original graph.
        edges = aGraph.getEdges();
        for ( int h=0; h<edges.size(); h++ )
        {
          anEdge = (Edge)edges.elementAt(h);
          sNode = (Node)anEdge.getStartNode();
          dNode = (Node)anEdge.getEndNode();
          if ( anEdge.getDirectedSourceNode() != null )
          {
            anEdge.setCopy(new Edge((Edge)anEdge.getMasterCopy(), anEdge.getDirectedSourceNode().getMasterCopy(), 
                (Node)sNode.getMasterCopy(), (Node)dNode.getMasterCopy()));
          }
          else
          {
            anEdge.setCopy(new Edge((Edge)anEdge.getMasterCopy(), null, 
                (Node)sNode.getMasterCopy(), (Node)dNode.getMasterCopy()));
          }
        }

        Vector nodes = aGraph.getNodes();
        for ( int j=0; j<nodes.size(); j++ )
        {
          aNode = (Node)nodes.elementAt(j);
          for ( int k=0; k<aNode.incidentEdges().size(); k++ )
          {
            anEdge = (Edge)aNode.incidentEdges().elementAt(k);
            sNode = aNode;
            while ( sNode.getCopy() != null )
            {
              sNode = (Node)sNode.getCopy();
            }
            g.addEdgeNoCheck(sNode, anEdge.getCopy());
          }
        }
      }
      g.stopLogEntry(logEntry);
    }
  }

  private static PQNodeEx upwardEmbed(Graph g) throws Exception
  {
    LogEntry logEntry = g.startLogEntry("Upward Embedding");
    if ( g.getNumNodes() == 2 )
    {
      g.stopLogEntry(logEntry);
      return null;
    }

    PQNodeEx tNode = null;
    Vector edges;
    Vector fullLeaves;
    Vector upwardEdges = new Vector();
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

      tNode = (PQNodeEx)nodesInStOrder.lastElement();
      PQNodeEx currentNode = (PQNodeEx)nodesInStOrder.firstElement();
      upwardEdges.addElement(new Vector());
      enumEdges = currentNode.incidentEdges().elements();
      while ( enumEdges.hasMoreElements() )
      {
        pqTree.getRoot().addChild( ((PQEdgeEx)enumEdges.nextElement()).getPQNode() );
      }

      for ( int j=2; j<=nodesInStOrder.size(); j++ )
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
          throw new Exception("A PQ-Tree reduction returned a null tree during upwardEmbed!");
        }

        fullLeaves = pertRoot.getFullLeaves();
        if ( fullLeaves != null )
        {
          upwardEdges.addElement(fullLeaves);
        }
        else
        {
          throw new Exception("*** ERROR no full leaves were returned during embedding!");
        }

        if ( j < nodesInStOrder.size() )
        {
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

          if ( pertRoot.isQNode() &&
             ( !pertRoot.isFull() || pertRoot.isPseudoNode() ) )
          {
            PQNode to = pertRoot.getFullLeavesTo();
            PQNode from = pertRoot.getFullLeavesFrom();
            pertRoot.replaceFullChildrenWith(newRoot);
            PQDNode dNode = new PQDNode(currentNode);
            dNode.setParent(newRoot.getParent());
            if ( from != null )
            {
              from.getSiblings().replacePQNode(newRoot, dNode);
              dNode.getSiblings().addPQNode(from);
              newRoot.getSiblings().replacePQNode(from, dNode);
              dNode.getSiblings().addPQNode(newRoot);
              dNode.setDirection(newRoot);
            }
            else if ( to != null )
            {
              to.getSiblings().replacePQNode(newRoot, dNode);
              dNode.getSiblings().addPQNode(to);
              newRoot.getSiblings().replacePQNode(to, dNode);
              dNode.getSiblings().addPQNode(newRoot);
              dNode.setDirection(to);
            }
            else
            {
              throw new Exception("*** ERROR neither from or to existed when adding dNode!");
            }
            if ( pertRoot.hasOnlyTwoChildren() && !pertRoot.isPseudoNode() )
            {
              if ( pertRoot.getEndMostChildren().size() == 2 )
              {
                if ( ((PQNode)pertRoot.getEndMostChildren().PQNodeAt(0)).isDNode() ||
                     ((PQNode)pertRoot.getEndMostChildren().PQNodeAt(1)).isDNode() )
                {
                  throw new Exception("*** ERROR pNode was created with dNode children!");
                }
              }
              else
              {
                throw new Exception("*** ERROR endMostChildren did not have size 2!");
              }
              pertRoot.convertToPNode();
            }
            else if ( pertRoot.hasOnlyOneChild() && !pertRoot.isPseudoNode() )
            {
              if ( pertRoot.getEndMostChildren().size() == 1 )
              {
                if ( ((PQNode)pertRoot.getEndMostChildren().PQNodeAt(0)).isDNode() )
                {
                  throw new Exception("*** ERROR pNode was created with dNode child!");
                }
              }
              else
              {
                throw new Exception("*** ERROR endMostChildren did not have size 1!");
              }
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
        }
      }

      boolean reverse = false;
      int indexOfNodeToReverse = 0;
      Vector edgesOfNodeToReverse, edgesOfNodeReversed;
      for ( int h=nodesInStOrder.size()-1; h>=0; h-- )
      {
        fullLeaves = (Vector)upwardEdges.elementAt(h);
        PQNode pqNode;
        for ( int k=0; k< fullLeaves.size(); k++ )
        {
          pqNode = (PQNode)fullLeaves.elementAt(k);
          if ( pqNode.isDNode() )
          {
            reverse = ((PQDNode)pqNode).readInReverseDirection();

            if ( reverse )
            {
              indexOfNodeToReverse = ((PQNodeEx)pqNode.getData()).getStNumber()-1;
              edgesOfNodeToReverse = (Vector)upwardEdges.elementAt(indexOfNodeToReverse);
              edgesOfNodeReversed = new Vector();
              for ( int x=edgesOfNodeToReverse.size()-1; x>=0; x-- )
              {
                if ( ((PQNode)edgesOfNodeToReverse.elementAt(x)).isDNode() )
                {
                  ((PQDNode)edgesOfNodeToReverse.elementAt(x)).toggleReadInReverseDirection();
                }
                edgesOfNodeReversed.addElement( edgesOfNodeToReverse.elementAt(x) );
              }
              upwardEdges.setElementAt(edgesOfNodeReversed, indexOfNodeToReverse);
            }
          }
        }
      }

      for ( int h=0; h<nodesInStOrder.size(); h++ )
      {
        currentNode = (PQNodeEx)nodesInStOrder.elementAt(h);
        fullLeaves = (Vector)upwardEdges.elementAt(h);
        currentNode.resetIncidentEdges();
        PQNode pqNode;
        for ( int k=0; k< fullLeaves.size(); k++ )
        {
          pqNode = (PQNode)fullLeaves.elementAt(k);
          if ( !pqNode.isDNode() )
          {
            currentNode.addIncidentEdgeNoCheck((PQEdgeEx)pqNode.getData());
          }
        }
      }
    }
    catch (Exception e)
    {
      System.out.println("PQTree error during embedding test");
      g.stopLogEntry(logEntry);
      throw e;
    }
    g.stopLogEntry(logEntry);
    return tNode;
  }

  private static void entireEmbed(Graph g, PQNodeEx tNode)
  {
    LogEntry logEntry = g.startLogEntry("Entire Embedding");
    Vector nodes = g.getNodeExtenders();
    for ( int i=0; i<nodes.size(); i++ )
    {
      ((PQNodeEx)nodes.elementAt(i)).setIsOld(false);
    }
    entireEmbedHelper(g, tNode);
    for ( int i=0; i<nodes.size(); i++ )
    {
      ((PQNodeEx)nodes.elementAt(i)).setIsOld(false);
    }
    g.stopLogEntry(logEntry);
  }

  private static void entireEmbedHelper(Graph g, PQNodeEx aNode)
  {
    aNode.setIsOld(true);
    PQNodeEx otherNode;
    PQEdgeEx currentEdge;
    Vector incidentEdges = aNode.incidentEdges();
    for ( int i=incidentEdges.size()-1; i>=0; i-- )
    {
      currentEdge = (PQEdgeEx)incidentEdges.elementAt(i);
      otherNode = (PQNodeEx)currentEdge.otherEndFrom(aNode);
      if ( otherNode.getStNumber() < aNode.getStNumber() )
      {
        otherNode.addIncidentEdgeNoCheck(currentEdge); // FIXME what order?
        if ( !otherNode.isOld() )
        {
          entireEmbedHelper(g, otherNode);
        }
      }
    }
  }
}