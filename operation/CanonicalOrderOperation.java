package operation;

import java.util.Vector;
import java.util.Enumeration;
import java.awt.Color;
import graphStructure.*;
import graphException.*;
import operation.extenders.*;

public class CanonicalOrderOperation
{
  private static CanNodeEx candidateAccess = null;

  public static Vector canonicalOrder(Graph g) throws Exception
  {
    return canonicalOrder(g, true);
  }

  public static Vector canonicalOrder(Graph g, boolean check) throws Exception
  {
    LogEntry logEntry = g.startLogEntry("Canonical Ordering");
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
      if ( !MakeMaximalOperation.makeMaximal(g, false) )
      {
        // embed if the make maximal op didn't do it already.
        EmbedOperation.embed(g, false);
      }
      Node [] outerFaceTriangle = g.getRandomTriangularFace();
      if ( outerFaceTriangle == null )
      {
        logEntry.setData("Could not find third outer face Node");
        g.stopLogEntry(logEntry);
        throw new Exception("Could not find third node for canonical!");
      }
      return canonicalOrder(g, outerFaceTriangle[0], outerFaceTriangle[1],
                               outerFaceTriangle[2], logEntry);
    }
  }

  public static Vector canonicalOrder(Graph g, Node fNode, Node sNode,
                                      Node tNode) throws Exception
  {
    return canonicalOrder(g, fNode, sNode, tNode, null);
  }

  private static Vector canonicalOrder(Graph g, Node fNode, Node sNode,
                                      Node tNode, LogEntry logEntry) throws Exception
  {
    if ( logEntry == null )
    {
      logEntry = g.startLogEntry("Canonical Ordering");
    }
    Vector nodesInCanonicalOrder = new Vector();
    Vector nodes = g.createNodeExtenders(new CanNodeEx().getClass());
    g.createEdgeExtenders(new CanEdgeEx().getClass());
    Vector incidentEdges, edges;
    Enumeration enumEdges;
    CanNodeEx firstNode, secondNode, thirdNode, currentNode, otherNode;
    CanNodeEx otherNode2;
    CanEdgeEx tempEdge;
    // use given exterior triangle...
    firstNode = (CanNodeEx)fNode.getExtender();
    secondNode = (CanNodeEx)sNode.getExtender();
    thirdNode = (CanNodeEx)tNode.getExtender();
    for ( int i=0; i<nodes.size(); i++ )
    {
      ((CanNodeEx)nodes.elementAt(i)).setOuterFaceEdgeCount(0);
      ((CanNodeEx)nodes.elementAt(i)).setIsOnOuterFace(false);
      ((CanNodeEx)nodes.elementAt(i)).setCanonicalNumber(-1);
      ((CanNodeEx)nodes.elementAt(i)).setCandidateLeft(null);
      ((CanNodeEx)nodes.elementAt(i)).setCandidateRight(null);
    }

    if ( thirdNode == null )
    {
      logEntry.setData("Could not find third outer face Node");
      g.stopLogEntry(logEntry);
      throw new Exception("*** ERROR the third node was not found in canonical!");
    }

    firstNode.setCanonicalNumber(1);
    firstNode.setIsOnOuterFace(true);
    secondNode.setCanonicalNumber(2);
    secondNode.setIsOnOuterFace(true);
    thirdNode.setCanonicalNumber(nodes.size());
    thirdNode.setIsOnOuterFace(true);

    enumEdges = firstNode.incidentEdges().elements();
    while ( enumEdges.hasMoreElements() )
    {
      ((CanNodeEx)((CanEdgeEx)enumEdges.nextElement()).otherEndFrom(firstNode)).incrementOuterFaceEdgeCount();
    }
    enumEdges = secondNode.incidentEdges().elements();
    while ( enumEdges.hasMoreElements() )
    {
      ((CanNodeEx)((CanEdgeEx)enumEdges.nextElement()).otherEndFrom(secondNode)).incrementOuterFaceEdgeCount();
    }
    enumEdges = thirdNode.incidentEdges().elements();
    while ( enumEdges.hasMoreElements() )
    {
      ((CanNodeEx)((CanEdgeEx)enumEdges.nextElement()).otherEndFrom(thirdNode)).incrementOuterFaceEdgeCount();
    }

    candidateAccess = thirdNode;
    candidateAccess.setCandidateRight(candidateAccess);
    candidateAccess.setCandidateLeft(candidateAccess);

    for ( int canonicalNumber = nodes.size(); canonicalNumber > 2; canonicalNumber-- )
    {
      currentNode = candidateAccess;
      if (candidateAccess == null)
      {
        logEntry.setData("Ran out of Nodes to process");
        g.stopLogEntry(logEntry);
        throw new Exception("Ran out of candidates! (" + canonicalNumber + ")");
      }
      currentNode.setCanonicalNumber(canonicalNumber);
      nodesInCanonicalOrder.addElement(currentNode);
      if ( candidateAccess.getCandidateLeft() == candidateAccess )
      {
        candidateAccess = null;
      }
      else if ( candidateAccess.getCandidateLeft() == candidateAccess.getCandidateRight() )
      {
        candidateAccess = candidateAccess.getCandidateLeft();
        candidateAccess.setCandidateRight(candidateAccess);
        candidateAccess.setCandidateLeft(candidateAccess);
      }
      else
      {
        currentNode.getCandidateRight().setCandidateLeft(currentNode.getCandidateLeft());
        currentNode.getCandidateLeft().setCandidateRight(currentNode.getCandidateRight());
        candidateAccess = currentNode.getCandidateLeft();
      }
      currentNode.setCandidateLeft(null);
      currentNode.setCandidateRight(null);

      edges = currentNode.incidentEdges();

      // update outer-face
      for ( int i=0; i<edges.size(); i++ )
      {
        tempEdge = (CanEdgeEx)edges.elementAt(i);
        otherNode = (CanNodeEx)tempEdge.otherEndFrom(currentNode);
        if ( otherNode.getCanonicalNumber() == -1 )
        {
          if ( !otherNode.isOnOuterFace() )
          {
            otherNode.setIsOnOuterFace(true);
            incidentEdges = otherNode.incidentEdges();
            for ( int m=0; m<incidentEdges.size(); m++ )
            {
              otherNode2 = (CanNodeEx)((CanEdgeEx)incidentEdges.elementAt(m)).otherEndFrom(otherNode);
              if ( otherNode2.getCanonicalNumber() == -1 )
              {
                otherNode2.incrementOuterFaceEdgeCount();
                verifyCandidate( otherNode2, firstNode, secondNode);
              }
            }
          }
          otherNode.decrementOuterFaceEdgeCount();
          verifyCandidate(otherNode, firstNode, secondNode);
        }
      }
    }

    nodesInCanonicalOrder.addElement(secondNode);
    nodesInCanonicalOrder.addElement(firstNode);
    g.stopLogEntry(logEntry);
    return nodesInCanonicalOrder;
  }

  private static void verifyCandidate( CanNodeEx potentialCandidate,
                                       CanNodeEx firstNode, CanNodeEx secondNode )
  {
    if ( potentialCandidate.getCandidateLeft() != null &&
         potentialCandidate.getOuterFaceEdgeCount() != 2 )
    {
      // remove this old candidate
      if ( potentialCandidate.getCandidateLeft() == potentialCandidate )
      {
        candidateAccess = null;
        potentialCandidate.setCandidateLeft(null);
        potentialCandidate.setCandidateRight(null);
      }
      else if ( potentialCandidate.getCandidateLeft() == potentialCandidate.getCandidateRight() )
      {
        candidateAccess = potentialCandidate.getCandidateLeft();
        candidateAccess.setCandidateLeft(candidateAccess);
        candidateAccess.setCandidateRight(candidateAccess);
        potentialCandidate.setCandidateLeft(null);
        potentialCandidate.setCandidateRight(null);
      }
      else
      {
        potentialCandidate.getCandidateLeft().setCandidateRight(potentialCandidate.getCandidateRight());
        potentialCandidate.getCandidateRight().setCandidateLeft(potentialCandidate.getCandidateLeft());
        if ( potentialCandidate == candidateAccess )
        {
          candidateAccess = potentialCandidate.getCandidateLeft();
        }
        potentialCandidate.setCandidateLeft(null);
        potentialCandidate.setCandidateRight(null);
      }
    }
    else if ( potentialCandidate.getCandidateLeft() == null &&
              potentialCandidate.getOuterFaceEdgeCount() == 2 &&
              potentialCandidate.isOnOuterFace() &&
              potentialCandidate != firstNode && potentialCandidate != secondNode )
    {
      // add a new candidate
      if ( candidateAccess == null )
      {
        candidateAccess = potentialCandidate;
        candidateAccess.setCandidateRight(candidateAccess);
        candidateAccess.setCandidateLeft(candidateAccess);
      }
      else if ( candidateAccess.getCandidateLeft() == candidateAccess )
      {
        candidateAccess.setCandidateLeft(potentialCandidate);
        candidateAccess.setCandidateRight(potentialCandidate);
        potentialCandidate.setCandidateLeft(candidateAccess);
        potentialCandidate.setCandidateRight(candidateAccess);
      }
      else
      {
        potentialCandidate.setCandidateRight(candidateAccess);
        potentialCandidate.setCandidateLeft(candidateAccess.getCandidateLeft());
        potentialCandidate.getCandidateRight().setCandidateLeft(potentialCandidate);
        potentialCandidate.getCandidateLeft().setCandidateRight(potentialCandidate);
      }
    }
  }

  public static void displayCanonicalOrdering(Graph g) throws Exception
  {
    displayCanonicalOrdering(g, canonicalOrder(g));
  }

  public static void displayCanonicalOrdering(Graph g, Node firstNode,
                                              Node secondNode,
                                              Node thirdNode) throws Exception
  {
    displayCanonicalOrdering(g, canonicalOrder(g, firstNode, secondNode, thirdNode));
  }

  public static void displayCanonicalOrdering(Graph g, Vector nodes)
  {
    CanNodeEx currentNode;
    for ( int i=0; i<nodes.size(); i++ )
    {
      currentNode = (CanNodeEx)nodes.elementAt(i);
      g.changeNodeLabel( currentNode, String.valueOf(currentNode.getCanonicalNumber()), true);
      g.changeNodeDrawX( currentNode, false, true );
      if ( currentNode.getCanonicalNumber() == 1 ||
           currentNode.getCanonicalNumber() == 2 ||
           currentNode.getCanonicalNumber() == nodes.size() )
      {
        g.changeNodeColor( currentNode, Color.green, true );
      }
      else
      {
        g.changeNodeColor( currentNode, Node.DEFAULT_COLOR, true );
      }
    }
    Vector edges = g.getEdgeExtenders();
    CanEdgeEx currentEdge;
    for ( int i=0; i<edges.size(); i++ )
    {
      currentEdge = (CanEdgeEx)edges.elementAt(i);
      g.changeEdgeColor( currentEdge, Edge.DEFAULT_COLOR, true );
      g.changeEdgeDirection( currentEdge, null, true );
    }
    g.markForRepaint();
  }
}
