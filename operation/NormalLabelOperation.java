package operation;

import java.util.Vector;
import java.awt.Color;
import graphStructure.*;
import graphException.*;
import operation.extenders.*;

public class NormalLabelOperation
{
  public static Vector normalLabel(Graph g) throws Exception
  {
    return normalLabel(g, true);
  }

  public static Vector normalLabel(Graph g, boolean check) throws Exception
  {
    LogEntry logEntry = g.startLogEntry("Normal Labeling");
    if ( check && g.getNumNodes() <= 2 )
    {
      logEntry.setData("Graph did not have 3 Nodes");
      g.stopLogEntry(logEntry);
      throw new GraphException("3 or more nodes required!");
    }
    else if ( check && !PlanarityOperation.isPlanar(g) )
    {
      logEntry.setData("Graph was not Planar");
      g.stopLogEntry(logEntry);
      throw new GraphException("Graph is not planar!");
    }
    else
    {
      return normalLabel(g, CanonicalOrderOperation.canonicalOrder(g, false), logEntry);
    }
  }

  public static Vector normalLabel(Graph g, Node fNode, Node sNode, Node tNode) throws Exception
  {
    LogEntry logEntry = g.startLogEntry("Normal Labeling");
    return normalLabel(g, CanonicalOrderOperation.canonicalOrder(g, fNode,
                                                                 sNode, tNode), logEntry);
  }

  private static Vector normalLabel(Graph g, Vector nodesInCanonicalOrder, LogEntry logEntry) throws Exception
  {
    Vector rootNodes = new Vector(3);
    g.createNodeExtenders(new NormalNodeEx().getClass());
    g.createEdgeExtenders(new NormalEdgeEx().getClass());
    CanNodeEx canNode;
    Vector tempVector = new Vector(nodesInCanonicalOrder.size());
    for ( int i=0; i<nodesInCanonicalOrder.size(); i++ )
    {
      canNode = (CanNodeEx)nodesInCanonicalOrder.elementAt(i);
      ((NormalNodeEx)canNode.getRef().getExtender()).setCanonicalNumber(canNode.getCanonicalNumber());
      tempVector.addElement(canNode.getRef().getExtender());
    }
    nodesInCanonicalOrder.removeAllElements();
    nodesInCanonicalOrder = tempVector;
    int numNodes = nodesInCanonicalOrder.size();
    NormalNodeEx currentNode, otherNode;
    Vector incidentEdges;
    NormalEdgeEx currentEdge;

    NormalNodeEx firstNode = (NormalNodeEx)nodesInCanonicalOrder.elementAt(numNodes-1);
    NormalNodeEx secondNode = (NormalNodeEx)nodesInCanonicalOrder.elementAt(numNodes-2);
    NormalNodeEx thirdNode = (NormalNodeEx)nodesInCanonicalOrder.elementAt(0);

    firstNode.setR1Parent(firstNode);
    firstNode.setR2Parent(firstNode);
    firstNode.setR3Parent(firstNode);
    secondNode.setR1Parent(secondNode);
    secondNode.setR2Parent(secondNode);
    secondNode.setR3Parent(secondNode);
    thirdNode.setR1Parent(thirdNode);
    thirdNode.setR2Parent(thirdNode);
    thirdNode.setR3Parent(thirdNode);

    rootNodes.addElement(firstNode);
    rootNodes.addElement(secondNode);
    rootNodes.addElement(thirdNode);

    if ( numNodes > 3 )
    {
      for ( int i=numNodes-3; i>0; i-- )
      {
        currentNode = (NormalNodeEx)nodesInCanonicalOrder.elementAt(i);
        incidentEdges = currentNode.incidentEdgesToSmallerCanonicalNumber();
        currentEdge = (NormalEdgeEx)incidentEdges.elementAt(0);
        currentEdge.setNormalLabel(2);
        currentNode.setR2Parent((NormalNodeEx)currentEdge.otherEndFrom(currentNode));

        for ( int j=1; j<incidentEdges.size()-1; j++ )
        {
          currentEdge = (NormalEdgeEx)incidentEdges.elementAt(j);
          currentEdge.setNormalLabel(1);
          ((NormalNodeEx)currentEdge.otherEndFrom(currentNode)).setR1Parent(currentNode);
        }

        currentEdge = (NormalEdgeEx)incidentEdges.elementAt(incidentEdges.size()-1);
        currentEdge.setNormalLabel(3);
        currentNode.setR3Parent((NormalNodeEx)currentEdge.otherEndFrom(currentNode));
      }

      incidentEdges = thirdNode.incidentEdges();
      for ( int j=0; j<incidentEdges.size(); j++ )
      {
        currentEdge = (NormalEdgeEx)incidentEdges.elementAt(j);
        otherNode = (NormalNodeEx)currentEdge.otherEndFrom(thirdNode);
        if ( otherNode.getCanonicalNumber() > 2 )
        {
          currentEdge.setNormalLabel(1);
          otherNode.setR1Parent(thirdNode);
        }
      }
    }
    g.stopLogEntry(logEntry);
    return rootNodes;
  }

  public static void displayNormalLabeling(Graph g, Node fNode, Node sNode,
                                           Node tNode) throws Exception
  {
    normalLabel(g, fNode, sNode, tNode);
    displayNormalLabeling(g, g.getNodeExtenders(), g.getEdgeExtenders());
    g.markForRepaint();
  }

  public static void displayNormalLabeling(Graph g) throws Exception
  {
    normalLabel(g);
    displayNormalLabeling(g, g.getNodeExtenders(), g.getEdgeExtenders());
    g.markForRepaint();
  }

  private static void displayNormalLabeling(Graph g, Vector nodes, Vector edges) throws Exception
  {
    NormalNodeEx currentNode;
    NormalEdgeEx currentEdge;

    for ( int j=0; j<nodes.size(); j++ )
    {
      currentNode = (NormalNodeEx)nodes.elementAt(j);
      g.changeNodeDrawX( currentNode, false, true );
      if ( currentNode.getCanonicalNumber() == 1 )
      {
        g.changeNodeColor( currentNode, Color.blue, true );
      }
      else if ( currentNode.getCanonicalNumber() == 2 )
      {
        g.changeNodeColor( currentNode, Color.green, true );
      }
      else if ( currentNode.getCanonicalNumber() == nodes.size() )
      {
        g.changeNodeColor( currentNode, Color.red, true );
      }
      else
      {
        g.changeNodeColor( currentNode, Color.darkGray, true );
      }
    }

    for (int i=0; i<edges.size(); i++)
    {
      currentEdge = (NormalEdgeEx)edges.elementAt(i);
      if ( currentEdge.getNormalLabel() == 1 )
      {
        g.changeEdgeColor(currentEdge, Color.red, true);
        //if ( currentEdge.getNormalLabelSourceNode() != null )
          g.changeEdgeDirection(currentEdge, currentEdge.getNormalLabelSourceNode(), true);
      }
      else if ( currentEdge.getNormalLabel() == 2 )
      {
        g.changeEdgeColor(currentEdge, Color.green, true);
        //if ( currentEdge.getNormalLabelSourceNode() != null )
          g.changeEdgeDirection(currentEdge, currentEdge.getNormalLabelSourceNode(), true);
      }
      else if ( currentEdge.getNormalLabel() == 3 )
      {
        g.changeEdgeColor(currentEdge, Color.blue, true);
        //if ( currentEdge.getNormalLabelSourceNode() != null )
          g.changeEdgeDirection(currentEdge, currentEdge.getNormalLabelSourceNode(), true);
      }
      else
      {
        g.changeEdgeColor(currentEdge, Color.black, true);
        currentEdge.setDirectedFrom(null);
      }
    }
  }
}