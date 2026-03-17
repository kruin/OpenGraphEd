package userInterface.modes;

import java.util.Vector;
import java.awt.event.*;
import java.awt.*;

import userInterface.GraphController;
import userInterface.GraphEditor;
import userInterface.menuAndToolBar.*;
import graphStructure.*;

public abstract class GraphEditorListener implements MouseListener,
                                                     MouseMotionListener,
                                                     KeyListener
{
  public static boolean SINGLE_CLICK_ADD_NODE = true;
  public static boolean ADD_NODE_ON_EDGE_DROP = true;
  
  protected Graph graph;
  protected GraphEditor editor;
  protected GraphController controller;
  private CursorPositionThread cursorThread;

  private Vector specialSelectedNodes;
  protected int numSpecialSelectionsAllowed;
  protected int numSpecialSelections;
  private boolean triangleSelection;

  public GraphEditorListener(GraphEditorListener listener)
  {
    this.graph = listener.graph;
    this.editor = listener.editor;
    this.controller = listener.controller;
    this.cursorThread = listener.cursorThread;
    this.graph.markForRepaint();
  }

  public GraphEditorListener(Graph graph, GraphEditor editor, GraphController controller)
  {
    this.graph = graph;
    this.editor = editor;
    this.controller = controller;
    this.cursorThread = null;
    this.graph.markForRepaint();
  }

  public void setGraph(Graph g) { graph = g; }

  public Graph getGraph() { return graph; }

  public GraphController getGraphController() { return controller; }

  public boolean isEditListener() { return false; }

  public boolean isMoveListener() { return false; }

  public boolean isRotateListener() { return false; }

  public boolean isResizeListener() { return false; }

  public boolean isGridListener() { return false; }
  
  public String getModeString()
  {
    if ( isEditListener() )
    {
      return "Edit";
    }
    else if ( isMoveListener() )
    {
      return "Move";
    }
    else if ( isRotateListener() )
    {
      return "Rotate";
    }
    else if ( isResizeListener() )
    {
      return "Resize";
    }
    else if ( isGridListener() )
    {
      return "Grid";
    }
    else
    {
      return "";
    }
  }
  
  public void prepareForClose() { /*overriden*/ }

  private boolean checkOnSameFace(Node aNode)
  {
    if ( specialSelectedNodes.isEmpty() )
    {
      return true;
    }
    else if ( specialSelectedNodes.size() == 1 )
    {
      return aNode.neighbours().contains( specialSelectedNodes.elementAt(0) );
    }
    else if ( specialSelectedNodes.size() == 2 )
    {
      Node firstNode = (Node)specialSelectedNodes.elementAt(0);
      Edge tempEdge = (Edge)firstNode.incidentEdgeWith(
        (Node)specialSelectedNodes.elementAt(1));
      return tempEdge.getNextInOrderFrom(firstNode).otherEndFrom(firstNode) == aNode ||
             tempEdge.getPreviousInOrderFrom(firstNode).otherEndFrom(firstNode) == aNode;
    }
    else
    {
      return false;
    }
  }

  public void allowNodeSelection(int numSelectionsAllowed)
  {
    numSpecialSelectionsAllowed = numSelectionsAllowed;
    numSpecialSelections = 0;
    if ( specialSelectedNodes != null && specialSelectedNodes.size() > 0 )
    {
      for ( int i=0; i<specialSelectedNodes.size(); i++ )
      {
        ((Node)specialSelectedNodes.elementAt(i)).setSpecialSelected(false);
      }
    }
    specialSelectedNodes = new Vector(numSelectionsAllowed);
    triangleSelection = false;
  }

  public void allowTriangleSelection()
  {
    numSpecialSelectionsAllowed = 3;
    numSpecialSelections = 0;
    if ( specialSelectedNodes != null && specialSelectedNodes.size() > 0 )
    {
      for ( int i=0; i<specialSelectedNodes.size(); i++ )
      {
        ((Node)specialSelectedNodes.elementAt(i)).setSpecialSelected(false);
      }
    }
    specialSelectedNodes = new Vector(numSpecialSelectionsAllowed);
    triangleSelection = true;
  }

  public Vector getSpecialNodeSelections() { return specialSelectedNodes; }

  public void mouseClicked(MouseEvent event)
  {
    if ( numSpecialSelectionsAllowed != 0 &&
         event.getPoint().x >= GraphEditor.DRAW_BUFFER - Node.RADIUS &&
         event.getPoint().x <= editor.getWidth() - GraphEditor.DRAW_BUFFER + Node.RADIUS &&
         event.getPoint().y >= GraphEditor.DRAW_BUFFER - Node.RADIUS &&
         event.getPoint().y <= editor.getHeight() - GraphEditor.DRAW_BUFFER + Node.RADIUS )
    {
      Point ePoint = new Point( event.getPoint().x - GraphEditor.DRAW_BUFFER,
                                event.getPoint().y - GraphEditor.DRAW_BUFFER );
      {
        Node aNode = graph.nodeAt(ePoint);
        if ( event.getClickCount() == 1 && aNode != null )
        {
          if ( specialSelectedNodes.contains(aNode) )
          {
            specialSelectedNodes.removeElement(aNode);
            aNode.setSpecialSelected(false);
            graph.markForRepaint();
            // FIXME use selection here

            numSpecialSelections--;
            controller.nodesSelectedByEditor( numSpecialSelections,
                                              numSpecialSelectionsAllowed );
          }
          else if ( numSpecialSelections < numSpecialSelectionsAllowed )
          {
            if ( !triangleSelection || checkOnSameFace(aNode) )
            {
              specialSelectedNodes.addElement(aNode);
              aNode.setSpecialSelected(true);
              graph.markForRepaint();

              numSpecialSelections++;
              if ( triangleSelection && numSpecialSelections == 3 )
              {
                // fix the node order so it is 'clockwise'
                Edge edgeOne = null, edgeTwo = null, tempEdge;
                Node nodeOne = (Node)specialSelectedNodes.elementAt(0);
                Node nodeTwo = (Node)specialSelectedNodes.elementAt(1);
                Node nodeThree = (Node)specialSelectedNodes.elementAt(2);
                Vector incidentEdges = nodeOne.incidentEdges();
                for ( int i=0; i<incidentEdges.size(); i++ )
                {
                  tempEdge = (Edge)incidentEdges.elementAt(i);
                  if ( tempEdge.otherEndFrom(nodeOne) == nodeTwo )
                  {
                    edgeOne = tempEdge;
                  }
                  else if ( tempEdge.otherEndFrom(nodeOne) == nodeThree )
                  {
                    edgeTwo = tempEdge;
                  }
                }
                if ( edgeOne.getNextInOrderFrom(nodeOne) != edgeTwo )
                {
                  // swap nodes two and three in the order
                  specialSelectedNodes.setElementAt(nodeThree, 1);
                  specialSelectedNodes.setElementAt(nodeTwo, 2);
                }
              }
              controller.nodesSelectedByEditor( numSpecialSelections,
                                                numSpecialSelectionsAllowed );
            }
          }
          editor.repaint();
        }
      }
    }
  }

  public void mouseMoved(MouseEvent event)
  {
    Point cursorPoint = event.getPoint();
    cursorPoint.translate(-1*GraphEditor.DRAW_BUFFER, -1*GraphEditor.DRAW_BUFFER);
    controller.updateCursorLocation(cursorPoint);
  }

  /**
   * This method is triggered when the user moves the mouse into the
   * GraphEditor.<br>
   * <br>
   * The keyboard focus is switched to the GraphEditor.
   */
  public void mouseEntered(MouseEvent event)
  {
    editor.requestFocus();
    mouseMoved(event);
    cursorThread = new CursorPositionThread(controller.getMenuAndToolBar());
    cursorThread.mouseIn(true);
    cursorThread.start();
  }

  public void mouseExited(MouseEvent event)
  {
    editor.repaint();
    cursorThread.mouseIn(false);
  }

  public void resetGraph()
  {
    //super.resetGraph();
    graph.setDrawSelected(false);
    editor.changeToNormalCursor();
    numSpecialSelectionsAllowed = 0;
    specialSelectedNodes = null;
    numSpecialSelections = 0;
    triangleSelection = false;
  }
}