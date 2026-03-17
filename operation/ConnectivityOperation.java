package operation;

import java.util.Vector;
import java.util.Enumeration;
import graphStructure.*;
import operation.extenders.*;

public class ConnectivityOperation
{
  public static Vector getConnectedComponents(Graph g)
  {
    return getConnectedComponents(g, false);
  }

  public static Vector getConnectedComponents(Graph g, boolean copyData)
  {
    LogEntry logEntry = g.startLogEntry("Get Connected Components");
    Vector graphs = new Vector();
    
    Vector nodes = g.createNodeExtenders(new DFSNodeEx().getClass());
    g.createEdgeExtenders(new DFSEdgeEx().getClass());
    Graph newGraph;
    
    for ( int i=0; i<nodes.size(); i++ )
    {
      if ( ((DFSNodeEx)nodes.elementAt(i)).hasNoIncidentEdges() )
      {
        newGraph = g.copyNode(((DFSNodeEx)nodes.elementAt(i)).getRef(), copyData);
        graphs.addElement(newGraph);
      }
      else if ( ((DFSNodeEx)nodes.elementAt(i)).getNumber() == 0 )
      {
        newGraph = g.copyNodes(NodeExtender.toNode(DepthFirstSearchOperation.depthFirstSearch(g, (DFSNodeEx)nodes.elementAt(i), false)), copyData);
        graphs.addElement(newGraph);
      }
    }
    logEntry.setData(graphs.size() + " Connected Components found");
    g.stopLogEntry(logEntry);
    return graphs;
  }

  // used by findseparating nodes in getbicomp
  public static Vector getConnectedNodes(Graph g, DFSNodeEx aNode)
  {
    LogEntry logEntry = g.startLogEntry("Get Connected Nodes");
    Vector returnVector = null;
    if ( aNode.hasNoIncidentEdges() )
    {
      returnVector = new Vector();
      returnVector.addElement(aNode);
    }
    else
    {
      returnVector = DepthFirstSearchOperation.depthFirstSearch(g, aNode, true);
    }
    logEntry.setData(returnVector.size() + " Connected Nodes found");
    return returnVector;
  }

  public static Vector getNodeFromEachConnectedComponent(Graph g)
  {
    return getNodeFromEachConnectedComponent(g, false);
  }

  public static Vector getNodeFromEachConnectedComponent(Graph g, boolean reuseExtenders)
  {
    LogEntry logEntry = g.startLogEntry("Get Node from each Connected Component");
    Vector nodesFromEachComponent = new Vector();
    Vector nodes;
    if ( reuseExtenders )
    {
      nodes = g.getNodeExtenders();
    }
    else
    {
      nodes = g.createNodeExtenders(new DFSNodeEx().getClass());
      g.createEdgeExtenders(new DFSEdgeEx().getClass());
    }

    for ( int i=0; i<nodes.size(); i++ )
    {
      if ( ((DFSNodeEx)nodes.elementAt(i)).hasNoIncidentEdges() )
      {
        nodesFromEachComponent.addElement(nodes.elementAt(i));
      }
      else if ( ((DFSNodeEx)nodes.elementAt(i)).getNumber() == 0 )
      {
        nodesFromEachComponent.addElement(nodes.elementAt(i));
        DepthFirstSearchOperation.depthFirstSearch(g, (DFSNodeEx)nodes.elementAt(i), false);
      }
    }
    logEntry.setData(nodesFromEachComponent.size() + " Nodes found");
    g.stopLogEntry(logEntry);
    return nodesFromEachComponent;
  }

  public static boolean isConnected(Graph g)
  {
    LogEntry logEntry = g.startLogEntry("Test Connectivity");
    if ( g.getNumNodes() <= 1 )
    {
      g.stopLogEntry(logEntry);
      return true;
    }
    Enumeration enumNodes = g.getNodes().elements();
    while ( enumNodes.hasMoreElements() )
    {
      if ( ((Node)enumNodes.nextElement()).hasNoIncidentEdges() )
      {
        g.stopLogEntry(logEntry);
        return false;
      }
    }

    DepthFirstSearchOperation.depthFirstSearch(g);
    Vector nodes = g.getNodeExtenders();

    enumNodes = nodes.elements();
    while ( enumNodes.hasMoreElements() )
    {
      if ( ((DFSNodeEx)enumNodes.nextElement()).getNumber() == 0 )
      {
        g.stopLogEntry(logEntry);
        return false;
      }
    }
    g.stopLogEntry(logEntry);
    return true;
  }

  public static void makeConnected(Graph g)
  {
    LogEntry logEntry = g.startLogEntry("Make Connected");
    Vector nodesToConnect = getNodeFromEachConnectedComponent(g);
    int counter = 0;
    if ( nodesToConnect.size() > 1 )
    {
      Node startNode, endNode;
      startNode = ((DFSNodeEx)nodesToConnect.elementAt(0)).getRef();
      for ( int i=1; i<nodesToConnect.size(); i++ )
      {
        endNode = ((DFSNodeEx)nodesToConnect.elementAt(i)).getRef();
        g.addGeneratedEdgeNoCheck(startNode, endNode, true);
        startNode = endNode;
        counter++;
      }
    }
    logEntry.setData(counter + " edges added");
    g.stopLogEntry(logEntry);
  }
}