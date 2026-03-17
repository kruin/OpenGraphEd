package operation;

import java.util.Vector;
import java.util.Enumeration;
import java.awt.Color;
import graphStructure.*;
import operation.extenders.*;

public class DepthFirstSearchOperation
{
  public static void depthFirstSearch(Graph g)
  {
    depthFirstSearch(g, false);
  }

  public static void depthFirstSearch(Graph g, boolean reuseExtenders)
  {
    if ( !reuseExtenders )
    {
      g.createNodeExtenders(new DFSNodeEx().getClass());
      g.createEdgeExtenders(new DFSEdgeEx().getClass());
    }
    Vector nodes = g.getNodeExtenders();

    depthFirstSearch(g, (DFSNodeEx)nodes.firstElement(), true);
  }

  public static Vector depthFirstSearch(Graph g, DFSNodeEx startNode, boolean reset)
  {
    LogEntry logEntry = g.startLogEntry("Depth First Search");
    Vector nodes = g.getNodeExtenders();
    Vector edges = g.getEdgeExtenders();

    if ( reset )
    {
      resetDFSData( nodes, edges );
    }
    Vector returnVector = new Vector(); // should this be ref or ex?
    Enumeration enum1, enum2;
    DFSNodeEx tempNode, otherNode;
    DFSEdgeEx tempEdge;
    int i = 0;
    boolean flag, hasUnusedEdges, alreadyVisited = false;

    tempNode = startNode;

    do
    {
      if ( !alreadyVisited )
      {
        i++;
        tempNode.setNumber(i);
        tempNode.setLowNumber(i);
        returnVector.addElement(tempNode);
      }

      enum1 = tempNode.incidentEdges().elements();
      flag = false;
      hasUnusedEdges = false;
      alreadyVisited = false;

      while ( enum1.hasMoreElements() )
      {
        tempEdge = (DFSEdgeEx)enum1.nextElement();
        if ( !tempEdge.isUsed() )
        {
          tempEdge.setIsUsed(true);
          hasUnusedEdges = true;
          otherNode = (DFSNodeEx)tempEdge.otherEndFrom(tempNode);
          if ( otherNode.getNumber() == 0 )
          {
            tempEdge.setIsBackEdge(false);
            otherNode.setParent(tempNode);
            tempNode = otherNode;
          }
          else
          {
            tempEdge.setIsBackEdge(true);
            tempNode.setLowNumber(Math.min(tempNode.getLowNumber(),otherNode.getNumber()));
            alreadyVisited = true;
          }
          break;
        }
      }

      if ( !hasUnusedEdges && tempNode.getParent() != null )
      {
        if ( tempNode.getParent().getNumber() != 1 )
        {
          if ( tempNode.getLowNumber() < tempNode.getParent().getNumber() )
          {
            tempNode.getParent().setLowNumber( Math.min( tempNode.getParent().getLowNumber(),
                                                         tempNode.getLowNumber() ) );
          }
          tempNode = tempNode.getParent();
          alreadyVisited = true;
          flag = true;
        }
        if ( !flag )
        {
          enum2 = startNode.incidentEdges().elements();
          boolean flag2 = false;
          while ( enum2.hasMoreElements() )
          {
            if ( !((DFSEdgeEx)enum2.nextElement()).isUsed() )
              flag2 = true;
          }
          if ( !flag2 )
          {
            break;
          }
          tempNode = startNode;
          alreadyVisited = true;
        }
      }
    }
    while ( true );
    logEntry.setData(returnVector.size() + " Nodes Visited");
    g.stopLogEntry(logEntry);
    return returnVector;
  }

  public static void displayDepthFirstSearch(Graph g)
  {
    Vector graphs = ConnectivityOperation.getConnectedComponents(g, true);
    Graph currentGraph;
    Vector edges, nodes;
    DFSNodeEx currentNode;
    DFSEdgeEx currentEdge;
    for ( int i=0; i<graphs.size(); i++ )
    {
      currentGraph = (Graph)graphs.elementAt(i);
      if ( currentGraph.getNumNodes() == 1 )
      {
        g.changeNodeColor( ((Node)currentGraph.getNodes().firstElement()).getCopy(),
                           Color.green, true);
        g.changeNodeDrawX( ((Node)currentGraph.getNodes().firstElement()).getCopy(),
                           false, true);
      }
      else
      {
        depthFirstSearch(currentGraph, false);
        nodes = currentGraph.getNodeExtenders();
        for ( int j=0; j<nodes.size(); j++ )
        {
          currentNode = (DFSNodeEx)nodes.elementAt(j);
          g.changeNodeColor( currentNode.getCopy(), Color.gray, true);
          g.changeNodeDrawX( currentNode.getCopy(), false, true);
          if ( currentNode.getParent() != null )
          {
            g.changeEdgeDirection( currentNode.incidentEdgeWith(currentNode.getParent()).getCopy(),
                                   currentNode.getParent().getCopy(), true);
          }
        }
        g.changeNodeColor( ((DFSNodeEx)nodes.firstElement()).getCopy(),
                                      Color.green, true);
        edges = currentGraph.getEdgeExtenders();
        for ( int j=0; j<edges.size(); j++ )
        {
          currentEdge = (DFSEdgeEx)edges.elementAt(j);
          if ( currentEdge.isBackEdge() )
          {
            g.changeEdgeColor( currentEdge.getCopy(), Color.blue, true);
            g.changeEdgeDirection( currentEdge.getCopy(), null, true);
          }
          else
          {
            g.changeEdgeColor( currentEdge.getCopy(), Color.green, true);
          }
        }
      }
      currentGraph.resetCopyData();
    }
    g.markForRepaint();
  }

  private static void resetDFSData(Vector nodes, Vector edges)
  {
    Enumeration enum1;
    DFSNodeEx tempNode;
    DFSEdgeEx tempEdge;
    enum1 = nodes.elements();
    while ( enum1.hasMoreElements() )
    {
      tempNode = (DFSNodeEx)enum1.nextElement();
      tempNode.setNumber(0);
      tempNode.setLowNumber(0);
      tempNode.setParent(null);
    }

    enum1 = edges.elements();
    while ( enum1.hasMoreElements() )
    {
      tempEdge = (DFSEdgeEx)enum1.nextElement();
      tempEdge.setIsUsed(false);
      tempEdge.setIsBackEdge(false);
    }
  }
}