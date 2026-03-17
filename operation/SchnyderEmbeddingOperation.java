package operation;

import java.util.Vector;
import java.awt.Color;
import graphStructure.*;
import graphException.*;
import operation.extenders.*;

public class SchnyderEmbeddingOperation
{
  public static void straightLineGridEmbed(Graph g, int width,
                                           int height) throws Exception
  {
    straightLineGridEmbed(g, true, width, height);
  }

  public static void straightLineGridEmbed( Graph g, boolean check,
                                            int width,
                                            int height ) throws Exception
  {
    LogEntry logEntry = g.startLogEntry("Schnyder Straight Line Grid Embedding");
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
      straightLineGridEmbed(g, NormalLabelOperation.normalLabel(g, false),
                            width, height, logEntry);
    }
  }

  public static void straightLineGridEmbed( Graph g, Node fNode, Node sNode,
                                            Node tNode, int width,
                                            int height ) throws Exception
  {
    LogEntry logEntry = g.startLogEntry("Schnyder Straight Line Grid Embedding");
    straightLineGridEmbed(g, NormalLabelOperation.normalLabel(g, fNode,
                                                              sNode, tNode),
                          width, height, logEntry);
  }

  private static void straightLineGridEmbed( Graph g, Vector rootNodes,
                                             int width, int height,
                                             LogEntry logEntry ) throws Exception
  {
    Vector oldNodes = g.getNodeExtenders();
    Vector oldEdges = g.getEdgeExtenders();

    g.createNodeExtenders(new SchnyderNodeEx().getClass());
    g.createEdgeExtenders(new SchnyderEdgeEx().getClass());

    Vector nodes = g.getNodeExtenders();
    Vector edges = g.getEdgeExtenders();

    SchnyderNodeEx newNode;
    NormalNodeEx oldNode;
    for ( int i=0; i<oldNodes.size(); i++ )
    {
      oldNode = (NormalNodeEx)oldNodes.elementAt(i);
      newNode = (SchnyderNodeEx)nodes.elementAt(i);
      newNode.setR1Parent((SchnyderNodeEx)oldNode.getR1Parent().getRef().getExtender());
      newNode.setR2Parent((SchnyderNodeEx)oldNode.getR2Parent().getRef().getExtender());
      newNode.setR3Parent((SchnyderNodeEx)oldNode.getR3Parent().getRef().getExtender());
      newNode.setCanonicalNumber(oldNode.getCanonicalNumber());
    }

    SchnyderEdgeEx newEdge;
    NormalEdgeEx oldEdge;
    for ( int i=0; i<oldEdges.size(); i++ )
    {
      oldEdge = (NormalEdgeEx)oldEdges.elementAt(i);
      newEdge = (SchnyderEdgeEx)edges.elementAt(i);
      newEdge.setNormalLabel(oldEdge.getNormalLabel());
    }

    SchnyderNodeEx firstNode =  (SchnyderNodeEx)((NormalNodeEx)rootNodes.elementAt(2)).getRef().getExtender();
    SchnyderNodeEx secondNode = (SchnyderNodeEx)((NormalNodeEx)rootNodes.elementAt(1)).getRef().getExtender();
    SchnyderNodeEx thirdNode =  (SchnyderNodeEx)((NormalNodeEx)rootNodes.elementAt(0)).getRef().getExtender();

    traverseTree(1, firstNode);
    traverseTree(2, secondNode);
    traverseTree(3, thirdNode);
    firstNode.setTX(2, 1);
    firstNode.setTX(3, 1);
    secondNode.setTX(1, 1);
    secondNode.setTX(3, 1);
    thirdNode.setTX(1, 1);
    thirdNode.setTX(2, 1);

    traverseTree2(1, firstNode);
    traverseTree2(2, secondNode);
    traverseTree2(3, thirdNode);

    traverseTree3(1, firstNode);
    traverseTree3(2, secondNode);
    traverseTree3(3, thirdNode);

    firstNode.setRX(1, g.getNumNodes()-2);
    firstNode.setRX(2, 1);
    firstNode.setRX(3, 0);
    firstNode.setPX(1, 0);

    secondNode.setRX(1, 0);
    secondNode.setRX(2, g.getNumNodes()-2);
    secondNode.setRX(3, 1);
    secondNode.setPX(2, 0);

    thirdNode.setRX(1, 1);
    thirdNode.setRX(2, 0);
    thirdNode.setRX(3, g.getNumNodes()-2);
    thirdNode.setPX(3, 0);

    g.setGridArea(g.getNumNodes()-1, height, g.getNumNodes()-1, width, true);
    int widthIncrement = g.getGridColWidth();
    int heightIncrement = g.getGridRowHeight();
    for ( int i=0; i<nodes.size(); i++ )
    {
      newNode = (SchnyderNodeEx)nodes.elementAt(i);
      g.relocateNode( newNode.getRef(),
                      new Location( (newNode.getRX(1)-newNode.getPX(3))*widthIncrement,
                              (newNode.getRX(2)-newNode.getPX(1))*heightIncrement ),
                      true );
    }
    for ( int i=0; i<edges.size(); i++ )
    {
      newEdge = (SchnyderEdgeEx)edges.elementAt(i);
      g.straightenEdge(newEdge.getRef(), true);
    }
    g.stopLogEntry(logEntry);
  }

  private static void traverseTree(int treeNumber, SchnyderNodeEx currentNode) throws Exception
  {
    Vector incidentEdges = currentNode.incidentEdges();
    SchnyderEdgeEx currentEdge;
    if ( currentNode.getRXParent(treeNumber) != currentNode )
    {
      currentNode.setPX(treeNumber, currentNode.getRXParent(treeNumber).getPX(treeNumber)+1);
    }
    else
    {
      currentNode.setPX(treeNumber, 1);
    }
    int count = 0;
    for ( int i=0; i<incidentEdges.size(); i++ )
    {
      currentEdge = (SchnyderEdgeEx)incidentEdges.elementAt(i);
      if ( currentEdge.getNormalLabel() == treeNumber &&
           currentEdge.getNormalLabelSourceNode() == currentEdge.otherEndFrom(currentNode) )
      {
        traverseTree(treeNumber, (SchnyderNodeEx)currentEdge.otherEndFrom(currentNode));
        count+=((SchnyderNodeEx)currentEdge.otherEndFrom(currentNode)).getTX(treeNumber);
      }
    }
    currentNode.setTX(treeNumber, 1+count);
  }

  private static void traverseTree2(int treeNumber, SchnyderNodeEx currentNode) throws Exception
  {
    Vector incidentEdges = currentNode.incidentEdges();
    SchnyderEdgeEx currentEdge;

    if ( treeNumber == 1 )
    {
      currentNode.setTemp(1, currentNode.getTX(2) + currentNode.getRXParent(treeNumber).getTemp(1));
      currentNode.setTemp(2, currentNode.getTX(3) + currentNode.getRXParent(treeNumber).getTemp(2));
    }
    else if ( treeNumber == 2 )
    {
      currentNode.setTemp(3, currentNode.getTX(1) + currentNode.getRXParent(treeNumber).getTemp(3));
      currentNode.setTemp(4, currentNode.getTX(3) + currentNode.getRXParent(treeNumber).getTemp(4));
    }
    else if ( treeNumber == 3 )
    {
      currentNode.setTemp(5, currentNode.getTX(1) + currentNode.getRXParent(treeNumber).getTemp(5));
      currentNode.setTemp(6, currentNode.getTX(2) + currentNode.getRXParent(treeNumber).getTemp(6));
    }

    for ( int i=0; i<incidentEdges.size(); i++ )
    {
      currentEdge = (SchnyderEdgeEx)incidentEdges.elementAt(i);
      if ( currentEdge.getNormalLabel() == treeNumber &&
           currentEdge.getNormalLabelSourceNode() == currentEdge.otherEndFrom(currentNode) )
      {
        traverseTree2(treeNumber, (SchnyderNodeEx)currentEdge.otherEndFrom(currentNode));
      }
    }
  }

  private static void traverseTree3(int treeNumber, SchnyderNodeEx currentNode) throws Exception
  {
    Vector incidentEdges = currentNode.incidentEdges();
    SchnyderEdgeEx currentEdge;

    if ( treeNumber == 1 )
    {
      currentNode.setRX(treeNumber, currentNode.getTemp(3) + currentNode.getTemp(5) - currentNode.getTX(treeNumber));
    }
    else if ( treeNumber == 2 )
    {
      currentNode.setRX(treeNumber, currentNode.getTemp(1) + currentNode.getTemp(6) - currentNode.getTX(treeNumber));
    }
    else if ( treeNumber == 3 )
    {
      currentNode.setRX(treeNumber, currentNode.getTemp(2) + currentNode.getTemp(4) - currentNode.getTX(treeNumber));
    }

    for ( int i=0; i<incidentEdges.size(); i++ )
    {
      currentEdge = (SchnyderEdgeEx)incidentEdges.elementAt(i);
      if ( currentEdge.getNormalLabel() == treeNumber &&
           currentEdge.getNormalLabelSourceNode() == currentEdge.otherEndFrom(currentNode) )
      {
        traverseTree3(treeNumber, (SchnyderNodeEx)currentEdge.otherEndFrom(currentNode));
      }
    }
  }

  public static void displayStraightLineGridEmbedding(Graph g, Node fNode,
                                                      Node sNode, Node tNode,
                                                      int width,
                                                      int height) throws Exception
  {
    straightLineGridEmbed(g, fNode, sNode, tNode, width, height);
    g.markForRepaint();
  }

  public static void displayStraightLineGridEmbedding(Graph g, int width,
                                                      int height) throws Exception
  {
    straightLineGridEmbed(g, width, height);
    g.markForRepaint();
  }

  public static void displayNormalLabeling(Graph g, Node fNode, Node sNode,
                                           Node tNode, int width,
                                           int height) throws Exception
  {
    displayStraightLineGridEmbedding(g, fNode, sNode, tNode, width, height);
    displayNormalLabeling(g);
  }

  public static void displayNormalLabeling(Graph g, int width,
                                           int height) throws Exception
  {
    displayStraightLineGridEmbedding(g, width, height);
    displayNormalLabeling(g);
  }

  private static void displayNormalLabeling(Graph g) throws Exception
  {
    Vector nodes = g.getNodeExtenders();
    Vector edges = g.getEdgeExtenders();
    SchnyderNodeEx currentNode;
    SchnyderEdgeEx currentEdge;

    for ( int j=0; j<nodes.size(); j++ )
    {
      currentNode = (SchnyderNodeEx)nodes.elementAt(j);
      if ( currentNode.getCanonicalNumber() == 1 )
      {
        g.changeNodeColor(currentNode, Color.blue, true);
      }
      else if ( currentNode.getCanonicalNumber() == 2 )
      {
        g.changeNodeColor(currentNode, Color.green, true);
      }
      else if ( currentNode.getCanonicalNumber() == nodes.size() )
      {
        g.changeNodeColor(currentNode, Color.red, true);
      }
      else
      {
        g.changeNodeColor(currentNode, Color.darkGray, true);
      }
    }

    for (int i=0; i<edges.size(); i++)
    {
      currentEdge = (SchnyderEdgeEx)edges.elementAt(i);
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
        g.changeEdgeDirection(currentEdge, null, true);
      }
    }
    g.markForRepaint();
  }


  public static void displayCanonicalOrdering(Graph g, Node fNode, Node sNode,
                                              Node tNode, int width,
                                              int height) throws Exception
  {
    displayStraightLineGridEmbedding(g, fNode, sNode, tNode, width,
                                     height);
    displayCanonicalOrdering(g);
  }

  public static void displayCanonicalOrdering(Graph g, int width,
                                              int height) throws Exception
  {
    displayStraightLineGridEmbedding(g, width, height);
    displayCanonicalOrdering(g);
  }

  private static void displayCanonicalOrdering(Graph g) throws Exception
  {
    Vector nodes = g.getNodeExtenders();
    SchnyderNodeEx currentNode;
    for ( int i=0; i<nodes.size(); i++ )
    {
      currentNode = (SchnyderNodeEx)nodes.elementAt(i);
      g.changeNodeDrawX(currentNode, false, true);
      g.changeNodeLabel(currentNode, String.valueOf(currentNode.getCanonicalNumber()), true);
      if ( currentNode.getCanonicalNumber() == 1 ||
           currentNode.getCanonicalNumber() == 2 ||
           currentNode.getCanonicalNumber() == nodes.size() )
      {
        g.changeNodeColor(currentNode, Color.green, true);
      }
      else
      {
        g.changeNodeColor(currentNode, Node.DEFAULT_COLOR, true);
      }
    }
    Vector edges = g.getEdgeExtenders();
    SchnyderEdgeEx currentEdge;
    for ( int i=0; i<edges.size(); i++ )
    {
      currentEdge = (SchnyderEdgeEx)edges.elementAt(i);
      g.changeEdgeColor(currentEdge, Edge.DEFAULT_COLOR, true);
      g.changeEdgeDirection(currentEdge, null, true);
    }
    g.markForRepaint();
  }
}