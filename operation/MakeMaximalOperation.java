package operation;

import java.util.Vector;
import java.util.Enumeration;
import graphStructure.*;
import graphException.*;
import operation.extenders.*;

public class MakeMaximalOperation
{
  public static boolean makeMaximal(Graph g) throws Exception
  {
    return makeMaximal(g, true);
  }

  public static boolean makeMaximal(Graph g, boolean check) throws Exception
  {
    LogEntry logEntry = g.startLogEntry("Make Maximal Planar");
    if ( check && g.getNumNodes() < 3 )
    {
      logEntry.setData("Graph had less than 3 Nodes");
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
      int counter = 0;
      if ( g.getNumEdges() < g.getNumNodes() * 3 - 6 )
      {
        if ( !BiconnectivityOperation.makeBiconnected(g, false) )
        {
          EmbedOperation.embed(g, false);
        }
        Vector nodes = g.createNodeExtenders(new MakeMaxNodeEx().getClass());
        g.createEdgeExtenders(new MakeMaxEdgeEx().getClass());
        Enumeration enumNodes = nodes.elements();
        Enumeration enumEdges;
        MakeMaxNodeEx currentNode, firstNode, secondNode;
        MakeMaxEdgeEx currentEdge, secondEdge, newEdge;
        Vector duplicateEdgeRunIndices;

        while ( enumNodes.hasMoreElements() )
        {
          currentNode = (MakeMaxNodeEx)enumNodes.nextElement();
          enumEdges = currentNode.incidentEdges().elements();
          while ( enumEdges.hasMoreElements() )
          {
            currentEdge = (MakeMaxEdgeEx)enumEdges.nextElement();
            secondEdge = (MakeMaxEdgeEx)currentEdge.getNextInOrderFrom(currentNode);
            firstNode = (MakeMaxNodeEx)currentEdge.otherEndFrom(currentNode);
            secondNode = (MakeMaxNodeEx)secondEdge.otherEndFrom(currentNode);
            if ( currentEdge.getPreviousInOrderFrom(firstNode).otherEndFrom(firstNode) != secondNode )
            {
              newEdge = new MakeMaxEdgeEx( firstNode, secondNode);
              newEdge.setIsGenerated(true);
              newEdge.setIsOld(true);
              g.addEdge( newEdge, currentEdge.getPreviousInOrderFrom(firstNode), secondEdge );
              counter++;
            }
          }
        }

        EdgeInterface sortedEdges[] = g.sortEdges(g.getEdgeExtenders());
        duplicateEdgeRunIndices = new Vector();
        boolean run = false;

        for ( int i=0; i<sortedEdges.length-1; i++ )
        {
          if ( sortedEdges[i].equals(sortedEdges[i+1]) )
          {
            if ( !run )
            {
              duplicateEdgeRunIndices.addElement(Integer.valueOf(i));
            }
            run = true;
          }
          else
          {
            run = false;
          }
        }

        int index;
        boolean original;
        for ( int i=0; i<duplicateEdgeRunIndices.size(); i++ )
        {
          index = ((Integer)duplicateEdgeRunIndices.elementAt(i)).intValue();
          original = false;
          do
          {
            currentEdge = (MakeMaxEdgeEx)sortedEdges[index];
            if ( !currentEdge.isOld() )
            {
              original = true;
            }
            index++;
          }
          while ( sortedEdges[index].equals(sortedEdges[index-1]) );

          if ( !original )
          {
            ((MakeMaxEdgeEx)sortedEdges[index-1]).setIsOld(false);
          }

          index = ((Integer)duplicateEdgeRunIndices.elementAt(i)).intValue();
          do
          {
            currentEdge = (MakeMaxEdgeEx)sortedEdges[index];
            if ( currentEdge.isOld() && g.isInQuadrilateral(currentEdge.getRef()))
            {
              g.flip(currentEdge.getRef());
            }
            index++;
          }
          while ( sortedEdges[index].equals(sortedEdges[index-1]) );
        }
        logEntry.setData(counter + " edges added");
        g.stopLogEntry(logEntry);
        return true;
      }
      logEntry.setData(counter + " edges added");
      g.stopLogEntry(logEntry);
      return false;
    }
  }
}