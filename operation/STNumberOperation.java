package operation;

import java.util.Vector;
import java.util.Stack;
import java.util.Enumeration;
import graphStructure.*;
import graphException.*;
import operation.extenders.*;

public class STNumberOperation
{
  public static Vector stNumber(Graph g) throws Exception
  {
    return stNumber(g, true);
  }

  public static Vector stNumber(Graph g, boolean check) throws Exception
  {
    LogEntry logEntry = g.startLogEntry("ST Numbering");
    if ( check && !ConnectivityOperation.isConnected(g) )
    {
      logEntry.setData("Graph was not Connected");
      g.stopLogEntry(logEntry);
      throw new GraphException("Graph is not connected!");
    }
    else if ( check && !BiconnectivityOperation.isBiconnected(g) )
    {
      logEntry.setData("Graph was not BiConnected");
      g.stopLogEntry(logEntry);
      throw new GraphException("Graph is not biconnected!");
    }
    else
    {
      Vector nodes = g.createNodeExtenders((new STNodeEx()).getClass());
      Vector edges = g.createEdgeExtenders((new STEdgeEx()).getClass());
      Vector nodesInStOrder = new Vector();
      if ( nodes.size() == 1 )
      {
        ((STNodeEx)nodes.firstElement()).setStNumber(0);
        nodesInStOrder.addElement(nodes.firstElement());
        g.stopLogEntry(logEntry);
        return nodesInStOrder;
      }
      STNodeEx aNode, s, t;
      STEdgeEx anEdge;
      Stack stack = new Stack();
      Stack tempStack = new Stack();
      Vector path;
      int i;
      DepthFirstSearchOperation.depthFirstSearch(g, true);
      Enumeration enumNodes;
      Enumeration enumEdges;
      s = null;
      t = null;

      enumNodes = nodes.elements();
      while ( enumNodes.hasMoreElements() )
      {
        aNode = (STNodeEx)enumNodes.nextElement();
        if ( aNode.getNumber() == 1 )
        {
          t = aNode;
          aNode.setIsOld(true);
        }
        else if ( aNode.getNumber() == 2 )
        {
          s = aNode;
          aNode.setIsOld(true);
        }
        else
        {
          aNode.setIsOld(false);
        }
      }

      enumEdges = edges.elements();
      while ( enumEdges.hasMoreElements() )
      {
        anEdge = (STEdgeEx)enumEdges.nextElement();
        if ( anEdge.isBetween(s, t) )
        {
          anEdge.setIsOld(true);
        }
        else
        {
          anEdge.setIsOld(false);
        }
      }

      stack.push(t);
      stack.push(s);
      i = 1;
      do
      {
        aNode = (STNodeEx)stack.pop();
        if ( aNode == t )
        {
          aNode.setStNumber(i);
          nodesInStOrder.addElement(aNode);
          break;
        }
        else
        {
          path = pathFinder(aNode);
          if ( path.isEmpty() )
          {
            aNode.setStNumber(i);
            nodesInStOrder.addElement(aNode);
            i++;
          }
          else
          {
            enumEdges = path.elements();
            while ( enumEdges.hasMoreElements() )
            {
              tempStack.push(aNode);
              anEdge = (STEdgeEx)enumEdges.nextElement();
              aNode = (STNodeEx)anEdge.otherEndFrom(aNode);
            }
            while ( !tempStack.isEmpty() )
            {
              stack.push(tempStack.pop());
            }
          }
        }
      }
      while (true);
      g.stopLogEntry(logEntry);
      return nodesInStOrder;
    }
  }

  private static Vector pathFinder(STNodeEx v)
  {
    // Shimon Even - Graph Algorithms - p 181
    Vector path = new Vector();
    Enumeration enumEdges, enumEdges2;
    STEdgeEx anEdge, tempEdge;
    STNodeEx aNode;

    // case 1
    enumEdges = v.incidentEdges().elements();
    while ( enumEdges.hasMoreElements() )
    {
      anEdge = (STEdgeEx)enumEdges.nextElement();
      if ( !anEdge.isOld() && anEdge.isBackEdge() &&
           ((STNodeEx)anEdge.otherEndFrom(v)).getNumber() < v.getNumber() )
      {
        anEdge.setIsOld(true);
        path.addElement(anEdge);
        return path;
      }
    }

    // case 2
    enumEdges = v.incidentEdges().elements();
    while ( enumEdges.hasMoreElements() )
    {
      anEdge = (STEdgeEx)enumEdges.nextElement();
      if ( !anEdge.isOld() && !anEdge.isBackEdge() &&
           ((STNodeEx)anEdge.otherEndFrom(v)).getParent() == v  &&
           ((STNodeEx)anEdge.otherEndFrom(v)).getNumber() > v.getNumber())
      {
        anEdge.setIsOld(true);
        path.addElement(anEdge);
        aNode = (STNodeEx)anEdge.otherEndFrom(v);
        boolean found;
        while ( !aNode.isOld() )
        {
          enumEdges2 = aNode.incidentEdges().elements();
          found = false;
          while ( enumEdges2.hasMoreElements() )
          {
            tempEdge = (STEdgeEx)enumEdges2.nextElement();
            if ( !tempEdge.isOld() &&
                 ((STNodeEx)tempEdge.otherEndFrom(aNode)).getNumber() == aNode.getLowNumber() )
            {
              aNode.setIsOld(true);
              tempEdge.setIsOld(true);
              path.addElement(tempEdge);
              found = true;
              break;
            }
          }
          if ( !found)
          {
            enumEdges2 = aNode.incidentEdges().elements();
            while ( enumEdges2.hasMoreElements() )
            {
              tempEdge = (STEdgeEx)enumEdges2.nextElement();
              if ( !tempEdge.isOld() &&
                   ((STNodeEx)tempEdge.otherEndFrom(aNode)).getParent() == aNode &&
                   ((STNodeEx)tempEdge.otherEndFrom(aNode)).getLowNumber() == aNode.getLowNumber() )
              {
                aNode.setIsOld(true);
                tempEdge.setIsOld(true);
                path.addElement(tempEdge);
                aNode = (STNodeEx)tempEdge.otherEndFrom(aNode);
                break;
              }
            }
          }
        }
        return path;
      }
    }

    // case 3
    enumEdges = v.incidentEdges().elements();
    while ( enumEdges.hasMoreElements() )
    {
      anEdge = (STEdgeEx)enumEdges.nextElement();
      if ( !anEdge.isOld() && anEdge.isBackEdge() &&
           ((STNodeEx)anEdge.otherEndFrom(v)).getNumber() > v.getNumber() )
      {
        anEdge.setIsOld(true);
        path.addElement(anEdge);
        aNode = (STNodeEx)anEdge.otherEndFrom(v);
        while ( !aNode.isOld() )
        {
          enumEdges2 = aNode.incidentEdges().elements();
          while ( enumEdges2.hasMoreElements() )
          {
            tempEdge = (STEdgeEx)enumEdges2.nextElement();
            if ( !tempEdge.isOld() &&
                 ((STNodeEx)tempEdge.otherEndFrom(aNode)) == aNode.getParent() )
            {
              aNode.setIsOld(true);
              tempEdge.setIsOld(true);
              path.addElement(tempEdge);
              aNode = (STNodeEx)tempEdge.otherEndFrom(aNode);
              break;
            }
          }
        }
        return path;
      }
    }
    // case 4
    return path;
  }

  public static void displayStNumbering(Graph g) throws Exception
  {
    Vector graphs = BiconnectivityOperation.getBiconnectedComponents(g, true);
    BiconnectivityOperation.displayBiconnectedComponents(g);

    Vector nodes = g.getNodes();
    g.clearNodeLabels(true);
    g.removeEdgeDirections(true);
    STNodeEx aNode;
    NodeExtender aNodeCopy;
    for ( int j=0; j<graphs.size(); j++ )
    {
      nodes = stNumber( (Graph)graphs.elementAt(j) );
      for ( int i=0; i<nodes.size(); i++ )
      {
        aNode = (STNodeEx)nodes.elementAt(i);
        aNodeCopy = (NodeExtender)aNode.getMasterCopy();
        if ( aNodeCopy.getLabel().length() > 0 )
        {
          aNodeCopy.appendLabel(",");
        }
        aNodeCopy.appendLabel(String.valueOf(aNode.getStNumber()));
      }
    }
    String tempLabel;
    nodes = g.getNodes();
    Node tempNode;
    for ( int i=0; i<nodes.size(); i++ )
    {
      tempNode = (Node)nodes.elementAt(i);
      tempLabel = tempNode.getLabel();
      tempNode.setLabel("");
      g.changeNodeLabel(tempNode, tempLabel, true);
    }
    g.markForRepaint();
  }
}