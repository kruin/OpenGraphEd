package operation;

import java.util.Vector;
import java.awt.Color;
import graphStructure.*;
import operation.extenders.*;
import dataStructure.binaryHeap.*;

public class MinimumSpanningTreeOperation
{
  public static Graph getMinimumSpanningTree(Graph g)
  {
    return getMinimumSpanningTree(g, false);
  }

  public static Graph getMinimumSpanningTree(Graph g, boolean copyData)
  {
    LogEntry logEntry = g.startLogEntry("Minimum Spanning Tree");
    if ( g.getNumNodes() < 2 )
    {
      logEntry.setData("Graph had less than 2 Nodes");
      g.stopLogEntry(logEntry);
      return null;
    }
    if ( !ConnectivityOperation.isConnected(g) )
    {
      ConnectivityOperation.makeConnected(g);
    }

    g.clearNodeLabels();
    g.removeEdgeDirections();

    Vector nodes = g.createNodeExtenders(new MSTNodeEx().getClass());
    g.createEdgeExtenders(new MSTEdgeEx().getClass());

    Vector mstEdges = new Vector();
    BinaryHeap bh = new BinaryHeap();
    MSTNodeEx currentNode = (MSTNodeEx)nodes.firstElement();
    MSTNodeEx otherNode;
    MSTEdgeEx linkEdge;
    double length;
    Vector incidentEdges;
    currentNode.setCost(0);
    bh.insert(currentNode);
    for (int w=1; w<nodes.size(); w++)
    {
      currentNode = (MSTNodeEx)nodes.elementAt(w);
      currentNode.setCost(Double.MAX_VALUE);
      bh.insert(currentNode);
    }

    while (!bh.isEmpty())
    {
      currentNode = (MSTNodeEx)bh.extractMin();
      if ( currentNode.getLinkEdge() != null )
      {
        mstEdges.addElement(currentNode.getLinkEdge().getRef());
      }
      currentNode.setMarked(true);

      incidentEdges = currentNode.incidentEdges();
      for (int j=0; j<incidentEdges.size(); j++)
      {
        linkEdge = (MSTEdgeEx)incidentEdges.elementAt(j);
        otherNode = (MSTNodeEx)linkEdge.otherEndFrom(currentNode);
        length = linkEdge.getLength();
        if (!otherNode.isMarked() && otherNode.getCost() > length )
        {
          otherNode.setLinkEdge(linkEdge);
          otherNode.setCost(length);
          bh.decreaseKey(otherNode);
        }
      }
    }
    g.stopLogEntry(logEntry);
    return g.copyEdges(mstEdges, copyData);
  }

  public static void drawMinimumSpanningTree(Graph g)
  {
    Graph gc = getMinimumSpanningTree(g, true);
    Edge anEdge;
    Node aNode;

    Vector nodes = g.getNodes();
    for ( int i=0; i<nodes.size(); i++ )
    {
      aNode = (Node)nodes.elementAt(i);
      g.changeNodeColor(aNode, Node.DEFAULT_COLOR, true);
      g.changeNodeLabel(aNode, "", true);
      g.changeNodeDrawX(aNode, false, true);
    }

    Vector edges = g.getEdges();
    for ( int i=0; i<edges.size(); i++ )
    {
      anEdge = (Edge)edges.elementAt(i);
      g.changeEdgeColor(anEdge, Edge.DEFAULT_COLOR, true);
    }

    if ( gc != null )
    {
      Vector mstEdges = gc.getEdges();
      for ( int i=0; i<mstEdges.size(); i++ )
      {
        anEdge = (Edge)mstEdges.elementAt(i);
        g.changeEdgeColor(anEdge.getCopy(), Color.green, true);
      }
    }
    g.markForRepaint();
  }
}