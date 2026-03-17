package operation;

import java.util.Vector;
import graphStructure.*;
import operation.extenders.*;

public class TreeOperation
{
  public static boolean hasCycles(Graph g)
  {
    LogEntry logEntry = g.startLogEntry("Test For Cycles");
    boolean result = false;
    if ( !PlanarityOperation.isPlanar(g) )
    {
      result = true;
    }
    else
    {
      Vector graphs = ConnectivityOperation.getConnectedComponents(g);
      Graph graph;
      for ( int j=0; j<graphs.size(); j++ )
      {
        graph = (Graph)graphs.elementAt(j);
        DepthFirstSearchOperation.depthFirstSearch(graph);
        Vector edges = graph.getEdgeExtenders();
        for ( int i=0; i<edges.size(); i++ )
        {
          if ( ((DFSEdgeEx)edges.elementAt(i)).isBackEdge() )
          {
            result = true;
            break;
          }
        }
      }
    }
    g.stopLogEntry(logEntry);
    return result;
  }
  
  public static boolean isBinaryTree(Graph g, Node root)
  {
    if ( !ConnectivityOperation.isConnected(g) )
    {
      return false;
    }
    if ( hasCycles(g) )
    {
      return false;
    }
    if ( root.getNumEdges() > 2 )
    {
      return false;
    }
    Vector nodes = g.getNodes();
    Node currentNode;
    for ( int i=0; i<nodes.size(); i++ )
    {
      currentNode = (Node)nodes.elementAt(i);
      if ( currentNode != root && currentNode.getNumEdges() > 3 )
      {
        return false;
      }
    }
    return true;
  }
}
