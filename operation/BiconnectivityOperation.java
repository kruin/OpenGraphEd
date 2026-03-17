package operation;

import java.util.Vector;
import java.util.Stack;
import java.util.Enumeration;
import java.util.Random;
import java.awt.Color;
import graphStructure.*;
import graphException.*;
import operation.extenders.*;

public class BiconnectivityOperation
{
  public static Vector getBiconnectedComponents(Graph g)
  {
    return getBiconnectedComponents( g, false );
  }

  public static Vector getBiconnectedComponents(Graph g, boolean copyData)
  {
    LogEntry logEntry = g.startLogEntry("Get Biconnected Components");
    Vector graphs = new Vector();
    Vector oldGraphs = ConnectivityOperation.getConnectedComponents(g, copyData);
    Graph oldGraph;
    for ( int j=0; j<oldGraphs.size(); j++ )
    {
      oldGraph = (Graph)oldGraphs.elementAt(j);
      if ( oldGraph.getNumNodes() <= 2 )
      {
        graphs.addElement(oldGraph.copy(copyData));
      }
      else
      {
        Vector newEdges = new Vector();
        Graph graph = oldGraph.copy(copyData);
        Enumeration enum1, enum2;
        BiCompNodeEx tempNode, otherNode;
        BiCompEdgeEx tempEdge;
        int i = 0;
        boolean flag, hasUnusedEdges, alreadyVisited = false;
        Stack nodeStack = new Stack();
        Stack edgeStack = new Stack();

        Vector nodes = graph.createNodeExtenders(new BiCompNodeEx().getClass());
        enum1 = nodes.elements();
        while ( enum1.hasMoreElements() )
        {
          tempNode = (BiCompNodeEx)enum1.nextElement();
          tempNode.setNumber(0);
          tempNode.setLowNumber(0);
          tempNode.setParent(null);
        }

        Vector edges = graph.createEdgeExtenders(new BiCompEdgeEx().getClass());
        enum1 = edges.elements();
        while ( enum1.hasMoreElements() )
        {
          tempEdge = (BiCompEdgeEx)enum1.nextElement();
          tempEdge.setIsUsed(false);
        }

        tempNode = (BiCompNodeEx)nodes.firstElement();

        do
        {
          if ( !alreadyVisited )
          {
            i++;
            tempNode.setNumber(i);
            tempNode.setLowNumber(i);
            nodeStack.push(tempNode);
          }

          enum1 = tempNode.incidentEdges().elements();
          flag = false;
          hasUnusedEdges = false;
          alreadyVisited = false;

          while ( enum1.hasMoreElements() )
          {
            tempEdge = (BiCompEdgeEx)enum1.nextElement();
            if ( !tempEdge.isUsed() )
            {
              tempEdge.setIsUsed(true);
              tempEdge.setWasAdded(true);
              edgeStack.push(tempEdge);
              hasUnusedEdges = true;
              otherNode = (BiCompNodeEx)tempEdge.otherEndFrom(tempNode);
              if ( otherNode.getNumber() == 0 )
              {
                otherNode.setParent(tempNode);
                tempNode = otherNode;
              }
              else
              {
                tempNode.setLowNumber(Math.min(tempNode.getLowNumber(),otherNode.getNumber()));
                alreadyVisited = true;
              }
              break;
            }
          }

          if ( !hasUnusedEdges )
          {
            if ( tempNode.getParent().getNumber() != 1 )
            {
              if ( tempNode.getLowNumber() < tempNode.getParent().getNumber() )
              {
                tempNode.getParent().setLowNumber( Math.min( tempNode.getParent().getLowNumber(),
                                                             tempNode.getLowNumber() ) );
              }
              else
              {
                Graph newGraph = new Graph(oldGraph);
                while (nodeStack.peek() != tempNode )
                {
                  newGraph.addNode(((BiCompNodeEx)nodeStack.pop()).getRef());
                }
                if ( nodeStack.peek() == tempNode )
                {
                  newGraph.addNode(((BiCompNodeEx)nodeStack.pop()).getRef());
                }
                newGraph.addNode(((BiCompNodeEx)tempNode.getParent()).getRef());
                newEdges.removeAllElements();
                while ( !((BiCompEdgeEx)edgeStack.peek()).isBetween(tempNode, tempNode.getParent()) )
                {
                  tempEdge = ((BiCompEdgeEx)edgeStack.pop());
                  tempEdge.setWasAdded(false);
                  newEdges.addElement(tempEdge);
                }
                if ( ((BiCompEdgeEx)edgeStack.peek()).isBetween(tempNode, tempNode.getParent()) )
                {
                  tempEdge = ((BiCompEdgeEx)edgeStack.pop());
                  tempEdge.setWasAdded(false);
                  newEdges.addElement(tempEdge);
                }
                graphs.addElement(newGraph.copyEdges(EdgeExtender.toEdge(newEdges), copyData));
              }
              tempNode = (BiCompNodeEx)tempNode.getParent();
              alreadyVisited = true;
              flag = true;
            }
            if ( !flag )
            {
              Graph newGraph = new Graph(oldGraph);
              while (nodeStack.peek() != tempNode )
              {
                newGraph.addNode(((BiCompNodeEx)nodeStack.pop()).getRef());
              }
              if ( nodeStack.peek() == tempNode )
              {
                newGraph.addNode(((BiCompNodeEx)nodeStack.pop()).getRef());
              }
              newGraph.addNode(((BiCompNodeEx)nodes.firstElement()).getRef());

              newEdges.removeAllElements();
              while ( !((BiCompEdgeEx)edgeStack.peek()).isBetween(tempNode, (BiCompNodeEx)nodes.firstElement()) )
              {
                tempEdge = ((BiCompEdgeEx)edgeStack.pop());
                tempEdge.setWasAdded(false);
                newEdges.addElement(tempEdge);
              }
              if ( ((BiCompEdgeEx)edgeStack.peek()).isBetween(tempNode, (BiCompNodeEx)nodes.firstElement()) )
              {
                tempEdge = ((BiCompEdgeEx)edgeStack.pop());
                tempEdge.setWasAdded(false);
                newEdges.addElement(tempEdge);
              }

              graphs.addElement(newGraph.copyEdges(EdgeExtender.toEdge(newEdges), copyData));

              enum2 = ((BiCompNodeEx)nodes.firstElement()).incidentEdges().elements();
              boolean flag2 = false;
              while ( enum2.hasMoreElements() )
              {
                if ( !((BiCompEdgeEx)enum2.nextElement()).isUsed() )
                  flag2 = true;
              }
              if ( !flag2 )
              {
                break;
              }
              tempNode = (BiCompNodeEx)nodes.firstElement();
              alreadyVisited = true;
            }
          }
        }
        while ( true );
      }
    }
    logEntry.setData(graphs.size() + " Biconnected Components found");
    g.stopLogEntry(logEntry);
    return graphs;
  }

  public static Vector findSeparatingNodes(Graph g)
  {
    // Shimon Even - Graph Algorithms - p 62
    LogEntry logEntry = g.startLogEntry("Find Separator Nodes");
    g.createNodeExtenders(new BiCompNodeEx().getClass());
    g.createEdgeExtenders(new BiCompEdgeEx().getClass());
    Vector separatingNodes = new Vector();
    Vector nodesFromEachComponent = ConnectivityOperation.getNodeFromEachConnectedComponent(g, true);
    BiCompNodeEx startNode;
    for ( int j=0; j<nodesFromEachComponent.size(); j++ )
    {
      startNode = (BiCompNodeEx)nodesFromEachComponent.elementAt(j);
      Vector connectedNodes = ConnectivityOperation.getConnectedNodes(g, startNode);

      if ( !startNode.hasNoIncidentEdges() )
      {
        Enumeration enum1, enum2;
        BiCompNodeEx tempNode, otherNode;
        BiCompEdgeEx tempEdge;
        int i = 0;
        Vector edges;
        boolean flag, hasUnusedEdges, alreadyVisited = false;
        int subGraphNumber = 1;
        Stack nodeStack = new Stack();
        Stack edgeStack = new Stack();

        enum1 = connectedNodes.elements();
        while ( enum1.hasMoreElements() )
        {
          tempNode = (BiCompNodeEx)enum1.nextElement();
          tempNode.setNumber(0);
          tempNode.setLowNumber(0);
          tempNode.setParent(null);
          tempNode.setSubGraphNumber(0);
        }

        edges = g.getEdgeExtenders(NodeExtender.toNode(connectedNodes));
        enum1 = edges.elements();
        while ( enum1.hasMoreElements() )
        {
          tempEdge = (BiCompEdgeEx)enum1.nextElement();
          tempEdge.setIsUsed(false);
          tempEdge.setSubGraphNumber(0);
          tempEdge.setWasAdded(false);
        }

        tempNode = (BiCompNodeEx)connectedNodes.firstElement();
        do
        {
          if ( !alreadyVisited )
          {
            i++;
            tempNode.setNumber(i);
            tempNode.setLowNumber(i);
            nodeStack.push(tempNode);
          }

          enum1 = tempNode.incidentEdges().elements();
          flag = false;
          hasUnusedEdges = false;
          alreadyVisited = false;

          while ( enum1.hasMoreElements() )
          {
            tempEdge = (BiCompEdgeEx)enum1.nextElement();
            if ( !tempEdge.isUsed() )
            {
              tempEdge.setIsUsed(true);
              tempEdge.setWasAdded(true);
              edgeStack.push(tempEdge);
              hasUnusedEdges = true;
              otherNode = (BiCompNodeEx)tempEdge.otherEndFrom(tempNode);
              if ( otherNode.getNumber() == 0 )
              {
                otherNode.setParent(tempNode);
                tempNode = otherNode;
              }
              else
              {
                tempNode.setLowNumber(Math.min(tempNode.getLowNumber(),otherNode.getNumber()));
                alreadyVisited = true;
              }
              break;
            }
          }

          if ( !hasUnusedEdges )
          {
            if ( tempNode.getParent().getNumber() != 1 )
            {
              if ( tempNode.getLowNumber() < tempNode.getParent().getNumber() )
              {
                tempNode.getParent().setLowNumber( Math.min( tempNode.getParent().getLowNumber(),
                                                             tempNode.getLowNumber() ) );
              }
              else
              {
                while (nodeStack.peek() != tempNode )
                {
                  ((BiCompNodeEx)nodeStack.pop()).setSubGraphNumber(subGraphNumber);
                }
                if ( nodeStack.peek() == tempNode )
                {
                  ((BiCompNodeEx)nodeStack.pop()).setSubGraphNumber(subGraphNumber);
                }
                while ( !((BiCompEdgeEx)edgeStack.peek()).isBetween(tempNode, tempNode.getParent()) )
                {
                  tempEdge = ((BiCompEdgeEx)edgeStack.pop());
                  tempEdge.setWasAdded(false);
                  tempEdge.setSubGraphNumber(subGraphNumber);
                }
                if ( ((BiCompEdgeEx)edgeStack.peek()).isBetween(tempNode, tempNode.getParent()) )
                {
                  tempEdge = ((BiCompEdgeEx)edgeStack.pop());
                  tempEdge.setWasAdded(false);
                  tempEdge.setSubGraphNumber(subGraphNumber);
                }
                subGraphNumber++;
                if ( !((BiCompNodeEx)tempNode.getParent()).isOld() )
                {
                  ((BiCompNodeEx)tempNode.getParent()).setSubGraphNumber(0);
                  ((BiCompNodeEx)tempNode.getParent()).setIsOld(true);
                  separatingNodes.addElement(tempNode.getParent());
                }
              }
              tempNode = (BiCompNodeEx)tempNode.getParent();
              alreadyVisited = true;
              flag = true;
            }
            if ( !flag )
            {
              while (nodeStack.peek() != tempNode )
              {
                ((BiCompNodeEx)nodeStack.pop()).setSubGraphNumber(subGraphNumber);
              }
              if ( nodeStack.peek() == tempNode )
              {
                ((BiCompNodeEx)nodeStack.pop()).setSubGraphNumber(subGraphNumber);
              }
              ((BiCompNodeEx)connectedNodes.firstElement()).setSubGraphNumber(subGraphNumber);
              while ( !((BiCompEdgeEx)edgeStack.peek()).isBetween(tempNode, (BiCompNodeEx)connectedNodes.firstElement()) )
              {
                tempEdge = ((BiCompEdgeEx)edgeStack.pop());
                tempEdge.setWasAdded(false);
                tempEdge.setSubGraphNumber(subGraphNumber);
              }
              if ( ((BiCompEdgeEx)edgeStack.peek()).isBetween(tempNode, (BiCompNodeEx)connectedNodes.firstElement()) )
              {
                tempEdge = ((BiCompEdgeEx)edgeStack.pop());
                tempEdge.setWasAdded(false);
                tempEdge.setSubGraphNumber(subGraphNumber);
              }

              subGraphNumber++;
              enum2 = ((BiCompNodeEx)connectedNodes.firstElement()).incidentEdges().elements();
              boolean flag2 = false;
              while ( enum2.hasMoreElements() )
              {
                if ( !((BiCompEdgeEx)enum2.nextElement()).isUsed() )
                  flag2 = true;
              }
              if ( !flag2 )
              {
                break;
              }
              tempNode = (BiCompNodeEx)connectedNodes.firstElement();
              if ( !tempNode.isOld() )
              {
                tempNode.setSubGraphNumber(0);
                tempNode.setIsOld(true);
                separatingNodes.addElement(tempNode);
              }
              alreadyVisited = true;
            }
          }
        }
        while ( true );

        enum1 = separatingNodes.elements();
        while ( enum1.hasMoreElements() )
        {
          tempNode = (BiCompNodeEx)enum1.nextElement();
          tempNode.setParent(null);
          tempNode.setIsOld(false);
        }

        enum1 = edges.elements();
        while ( enum1.hasMoreElements() )
        {
          tempEdge = (BiCompEdgeEx)enum1.nextElement();
          tempEdge.setIsUsed(false);
          tempEdge.setWasAdded(false);
        }
      }
    }
    logEntry.setData(separatingNodes.size() + " nodes found");
    g.stopLogEntry(logEntry);
    return separatingNodes;
  }

  public static boolean makeBiconnected(Graph g) throws Exception
  {
    return makeBiconnected(g, true);
  }

  public static boolean makeBiconnected(Graph g, boolean check) throws Exception
  {
    LogEntry logEntry = g.startLogEntry("Make Biconnected");
    if ( check && !PlanarityOperation.isPlanar(g) )
    {
      logEntry.setData("Graph was not Planar");
      g.stopLogEntry(logEntry);
      throw new GraphException("Graph is not planar!");
    }
    else
    {
      if ( !isBiconnected(g) )
      {
        int counter = 0;
        ConnectivityOperation.makeConnected(g);
        EmbedOperation.embed(g, false);
        Vector separators = findSeparatingNodes(g);
        Vector edges;
        Enumeration enumSeparators = separators.elements();
        Enumeration enumEdges;
        BiCompNodeEx separatorNode, firstNode, secondNode;
        BiCompEdgeEx currentEdge, nextEdge;
        BiCompEdgeEx newEdge;

        while ( enumSeparators.hasMoreElements() )
        {
          separatorNode = (BiCompNodeEx)enumSeparators.nextElement();
          edges = separatorNode.incidentEdges();
          enumEdges = edges.elements();
          while ( enumEdges.hasMoreElements() )
          {
            currentEdge = (BiCompEdgeEx)enumEdges.nextElement();
            nextEdge = (BiCompEdgeEx)currentEdge.getNextInOrderFrom(separatorNode);
            if ( !g.isTriangle( separatorNode.getRef(), currentEdge.getRef(), nextEdge.getRef() ) &&
                 ( currentEdge.getSubGraphNumber() != nextEdge.getSubGraphNumber() ||
                   currentEdge.getSubGraphNumber() == 0 ) )
            {
              firstNode = (BiCompNodeEx)currentEdge.otherEndFrom(separatorNode);
              secondNode = (BiCompNodeEx)nextEdge.otherEndFrom(separatorNode);
              newEdge  = new BiCompEdgeEx( (BiCompNodeEx)firstNode,
                                           (BiCompNodeEx)secondNode);
              newEdge.setIsGenerated(true);
              g.addEdge( newEdge, currentEdge.getPreviousInOrderFrom(firstNode), nextEdge );
              counter++;
              if ( edges.size() == 2 )
              {
                break;
              }
            }
          }
        }
        logEntry.setData(counter + " edges added");
        g.stopLogEntry(logEntry);
        return true;
      }
      g.stopLogEntry(logEntry);
      return false;
    }
  }

  public static boolean isBiconnected(Graph g)
  {
    LogEntry logEntry = g.startLogEntry("Test Biconnectivity");
    boolean isBiconnected = getBiconnectedComponents(g, false).size() <= 1;
    g.stopLogEntry(logEntry);
    return isBiconnected;
  }

  public static void displayBiconnectedComponents(Graph g)
  {
    Vector graphs = getBiconnectedComponents( g, true );
    Random rand = new Random();
    Vector nodes, edges;
    Color aColor;
    Node aNode;
    Edge anEdge;
    for ( int i=0; i<graphs.size(); i++ )
    {
      aColor = new Color( rand.nextInt(256), rand.nextInt(256),
                          rand.nextInt(256) );
      nodes = ((Graph)graphs.elementAt(i)).getNodes();
      edges = ((Graph)graphs.elementAt(i)).getEdges();
      for ( int j=0; j<nodes.size(); j++ )
      {
        aNode = (Node)((Node)nodes.elementAt(j)).getMasterCopy();
        g.changeNodeColor(aNode, aColor, true);
        g.changeNodeDrawX(aNode, false, true);
        g.changeNodeLabel(aNode, "", true);
      }
      for ( int j=0; j<edges.size(); j++ )
      {
        anEdge = (Edge)((Edge)edges.elementAt(j)).getMasterCopy();
        g.changeEdgeColor(anEdge, aColor, true);
        g.changeEdgeDirection(anEdge, null, true);
      }
    }

    nodes = NodeExtender.toNode(findSeparatingNodes(g));
    for ( int i=0; i<nodes.size(); i++ )
    {
      aNode = (Node)nodes.elementAt(i);
      g.changeNodeColor(aNode, Color.red, true);
      g.changeNodeDrawX(aNode, true, true);
    }
    g.markForRepaint();
  }
}