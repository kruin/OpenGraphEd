package userInterface.modes;

import java.awt.event.*;
import javax.swing.event.*;
import java.awt.Point;
import java.awt.Color;
import java.awt.geom.*;
import userInterface.GraphController;
import userInterface.GraphEditor;

import graphStructure.*;

public class GridListener extends EditListener
{
  private boolean undoOrthogonal;
  
  public GridListener(GraphEditorListener listener)
  {
    super(listener);
    init();
  }

  public GridListener(Graph graph, GraphEditor editor, GraphController controller)
  {
    super(graph, editor, controller);
    init();
  }

  private void init()
  {
    editor.changeToNormalCursor();
    undoOrthogonal = false;
  }

  public boolean isEditListener() { return false; }
  
  public boolean isGridListener() { return true; }

  /**
   * This method is triggered when the user clicks the mouse within the
   * GraphEditor.<br>
   * <br>
   *
   * @param MouseEvent event: The MouseEvent that triggered this method.
   */
  public void mouseClicked(MouseEvent event)
  {
    Location ePoint = new Location( event.getPoint().x - GraphEditor.DRAW_BUFFER,
                                    event.getPoint().y - GraphEditor.DRAW_BUFFER );
    Edge anEdge = graph.edgeAt(ePoint);
    Node aNode = graph.nodeAt(ePoint);
    if ( numSpecialSelectionsAllowed == 0 &&
         event.getButton() != MouseEvent.BUTTON3 &&
         event.getPoint().x >= GraphEditor.DRAW_BUFFER - Node.RADIUS &&
         event.getPoint().x <= editor.getWidth() - GraphEditor.DRAW_BUFFER + Node.RADIUS &&
         event.getPoint().y >= GraphEditor.DRAW_BUFFER - Node.RADIUS &&
         event.getPoint().y <= editor.getHeight() - GraphEditor.DRAW_BUFFER + Node.RADIUS &&
         aNode == null && anEdge == null )
    {
      Node gridNode = graph.nodeAt(graph.getClosestGridLocation(ePoint));
      if ( gridNode == null )
      {  
        graph.newMemento("Create New Node");
        graph.createNode( graph.getClosestGridLocation(ePoint) );
        graph.doneMemento();
        controller.newUndo();
        editor.setPreferredSize();
        editor.update();
      }
      else
      {
        graph.toggleNodeSelection(gridNode);
        editor.repaint();
      }
    }
    else
    {
      super.mouseClicked(event);
    }
  }
  
  /**
   * This method is triggered when the user presses a mouse button
   * within the GraphEditor. This typically means the user has begun
   * dragging an object, and actions are taken accordingly depending
   * on the mode of the GraphEditor.
   *
   * @param MouseEvent event: The MouseEvent that triggered this method.
   */
  public void mousePressed(MouseEvent event)
  {
    Location ePoint = new Location( event.getPoint().x - GraphEditor.DRAW_BUFFER,
                                    event.getPoint().y - GraphEditor.DRAW_BUFFER );
    Edge anEdge = graph.edgeAt(ePoint);
    Node aNode = graph.nodeAt(ePoint);
    if ( numSpecialSelectionsAllowed == 0 &&
         event.getButton() != MouseEvent.BUTTON3 &&
         event.getPoint().x >= GraphEditor.DRAW_BUFFER - Node.RADIUS &&
         event.getPoint().x <= editor.getWidth() - GraphEditor.DRAW_BUFFER + Node.RADIUS &&
         event.getPoint().y >= GraphEditor.DRAW_BUFFER - Node.RADIUS &&
         event.getPoint().y <= editor.getHeight() - GraphEditor.DRAW_BUFFER + Node.RADIUS &&
         aNode == null && anEdge != null && anEdge.isSelected() )
    {
      graph.newMemento("Orthogonalize Selected Edge");
      undoOrthogonal = !anEdge.isOrthogonal();
      graph.orthogonalizeEdge(anEdge,true); // memento
      editor.startTranslateEdge(anEdge);
      dragStartLocation = new Location(event.getPoint());
      dragEdge = anEdge;
    }
    else
    {
      super.mousePressed(event);
    }
  }

  /**
   * This method is triggered when the user drags the mouse within
   * the GraphEditor. Actions are taken accordingly depending
   * on the mode of the GraphEditor. (ex: drag a Node, draw a potential
   * Edge).
   *
   * @param MouseEvent event: The MouseEvent that triggered this method.
   */
  public void mouseDragged(MouseEvent event)
  {
    if ( numSpecialSelectionsAllowed == 0 &&
         (dragNode != null || dragEdge != null) )
    {
      super.mouseMoved(event);
      dragged = true;
      Location ePoint = new Location( event.getPoint().x - GraphEditor.DRAW_BUFFER,
                                      event.getPoint().y - GraphEditor.DRAW_BUFFER );
      if ( dragNode != null && dragNode.isSelected() )
      {
        if ( ePoint.intX() < editor.getWidth() - 2*GraphEditor.DRAW_BUFFER &&
             ePoint.intY() < editor.getHeight() - 2*GraphEditor.DRAW_BUFFER )
        
        {
          graph.relocateNode(dragNode, graph.getClosestGridLocation(ePoint), false);
          graph.refreshOrthogonalEdges(dragNode.incidentEdges());
          editor.setPreferredSize();
          editor.repaint();
        }
      }
      else if ( dragNode != null && !dragNode.isSelected() )
      {
        dragStartLocation = new Location(event.getPoint());
        Location gridLocation = graph.getClosestGridLocation(ePoint);
        editor.setLineToDraw( new Line2D.Double( dragNode.getLocation().intX() +
                                                 GraphEditor.DRAW_BUFFER,
                                                 dragNode.getLocation().intY() +
                                                 GraphEditor.DRAW_BUFFER,
                                                 gridLocation.intX()+
                                                 GraphEditor.DRAW_BUFFER,
                                                 gridLocation.intY() +
																								 GraphEditor.DRAW_BUFFER) ); 
        dragStartLocation.translate( -1 * GraphEditor.DRAW_BUFFER,
                                     -1 * GraphEditor.DRAW_BUFFER );
        Node dragToNode = null;
        if ( event.isShiftDown() )
        {
          dragToNode = graph.nodeAt(dragStartLocation);
        }
        else
        {
          dragToNode = nodeSplitTree.nodeAt(dragStartLocation);
        }
        if ( dragToNode == null )
        {
          if ( event.isShiftDown() )
          {
            dragToNode = graph.nodeAt(graph.getClosestGridLocation(dragStartLocation));
          }
          else
          {
            dragToNode = nodeSplitTree.nodeAt(graph.getClosestGridLocation(dragStartLocation));
          }
        }
        if ( dragToNode != null )
        {
          editor.setLineToDrawColor(Edge.DEFAULT_COLOR);
          if ( !graph.isOnGrid(dragToNode.getLocation()) )
          {
            editor.setLineToDraw( new Line2D.Double( dragNode.getLocation().intX() +
                                                     GraphEditor.DRAW_BUFFER,
                                                     dragNode.getLocation().intY() +
																										 GraphEditor.DRAW_BUFFER,
																										 ePoint.intX() +
																										 GraphEditor.DRAW_BUFFER,
																										 ePoint.intY() +
																										 GraphEditor.DRAW_BUFFER) );
          }
          Edge dragEdge = (Edge)dragNode.incidentEdgeWith(dragToNode);
          if ( dragEdge != null )
          {
            editor.setCurveToDrawColor(Color.green);
            if ( dragEdge.isCurved() )
            {
              editor.setCurveToDraw(dragEdge.getCurve(GraphEditor.DRAW_BUFFER,
                                                    GraphEditor.DRAW_BUFFER));
            }
            if ( dragEdge.isDirected() )
            {
              if ( dragNode == dragEdge.getDirectedSourceNode() )
              {
                editor.setLineToDrawColor(new Color(155,155,255));
                editor.setPolygonToDraw(null);
              }
              else
              {
                editor.setPolygonToDrawColor(GraphEditor.backgroundColor);
                editor.setPolygonToDraw(dragEdge.getDirectionArrow(dragToNode,
                                          GraphEditor.DRAW_BUFFER,
                                          GraphEditor.DRAW_BUFFER, 1, 1));
                editor.setLineToDrawColor(Color.green);
              }
            }
            else
            {
              editor.setPolygonToDrawColor(Color.green);
              editor.setPolygonToDraw(dragEdge.getDirectionArrow(dragNode,
                                        GraphEditor.DRAW_BUFFER,
                                        GraphEditor.DRAW_BUFFER, 1, 1));
              editor.setLineToDrawColor(Color.green);
            }
          }
          else
          {
            editor.setCurveToDraw(null);
          }
        }
        else
        {
          editor.setLineToDrawColor(new Color(155,155,255));
          editor.setPolygonToDraw(null);
          editor.setCurveToDraw(null);
        }
        editor.repaint();
      }
      else
      {  
        if ( ePoint.intX() < editor.getWidth() - 2*GraphEditor.DRAW_BUFFER &&
             ePoint.intY() < editor.getHeight() - 2*GraphEditor.DRAW_BUFFER )
        
        {
          graph.relocateEdge(dragEdge, ePoint, false);
          graph.orthogonalizeEdge(dragEdge, false);
          editor.setPreferredSize();
          editor.update();
        }
      }
    }
    else
    {
      super.mouseDragged(event);
    }
  }

  /**
   * This method is triggered when the user releases the mouse button
   * within the GraphEditor. This typically denotes the end of a mouse
   * drag by the user. Actions are taken accordingly depending
   * on the mode of the GraphEditor. (ex: Stop moving a Node, create an
   * Edge).
   *
   * @param MouseEvent event: The MouseEvent that triggered this method.
   */
  public void mouseReleased(MouseEvent event)
  {
    if ( numSpecialSelectionsAllowed == 0 &&
         (dragNode != null || dragEdge != null) )
    {
      if ( dragNode != null )
      {
        if ( dragged )
        {  
          if ( dragNode.isSelected() )
          {
            if ( dragged )
            {
              graph.doneMemento();
              controller.newUndo();
              graph.markForRepaint();
              editor.update();
            }
            else
            {
              graph.abortMemento();
            }
            editor.stopTranslateNodes();
          }
          else
          {
             // Check to see if we have let go on a node
            Location ePoint = new Location( event.getPoint().x - GraphEditor.DRAW_BUFFER,
                                            event.getPoint().y - GraphEditor.DRAW_BUFFER );
            Node aNode = graph.nodeAt(ePoint);
            if ( aNode == null )
            {
              aNode = graph.nodeAt(graph.getClosestGridLocation(ePoint));
            }
            if (aNode != dragNode)
            {
              if ( aNode != null )
              {
                Edge tempEdge = (Edge)dragNode.incidentEdgeWith(aNode);
                if ( tempEdge != null )
                {
                  graph.newMemento("Direct Edge");
                  if ( tempEdge.getDirectedSourceNode() == aNode )
                  {
                    graph.changeEdgeDirection(tempEdge, null, true);
                  }
                  else
                  {
                    graph.changeEdgeDirection(tempEdge, dragNode, true);
                  }
                  graph.doneMemento();
                  controller.newUndo();
                  editor.update();
                }
                else
                {
                  graph.newMemento("Create New Edge");
                  graph.addEdge(dragNode, aNode);
                  graph.doneMemento();
                  controller.newUndo();
                  editor.update();
                }
                editor.setPolygonToDraw(null);
                editor.setCurveToDraw(null);
              }
              else if ( createNodeOnEdgeDrop &&
                        ePoint.intX() >= 0 &&
                        ePoint.intX() <= editor.getWidth() - 2*GraphEditor.DRAW_BUFFER &&
                        ePoint.intY() >= 0 &&
                        ePoint.intY() <= editor.getHeight() - 2*GraphEditor.DRAW_BUFFER )
              {
                graph.newMemento("Create New Edge and End Node");
                aNode = graph.createNode( graph.getClosestGridLocation(ePoint) );
                graph.addEdge(dragNode, aNode);
                graph.doneMemento();
                controller.newUndo();
                editor.setPreferredSize();
                editor.update();
              }
            }
          }
        }
        nodeSplitTree = null;
        editor.setLineToDraw(null);
        dragNode = null;
        editor.repaint();
      }
      else if ( dragEdge != null )
      {
        if ( dragged )
        {
          graph.doneMemento();
          controller.newUndo();
          graph.markForRepaint();
          editor.update();
        }
        else
        {
          if ( undoOrthogonal  )
          {
            undoOrthogonal = false;
            graph.undoMemento();
            graph.abortMemento();
          }
        }
        editor.stopTranslateEdge();
        dragEdge = null;
      }
      dragStartLocation = null;
      dragged = false;
    }
    else
    {
      super.mouseReleased(event);
    }
  }

  /**
   * This method is triggered when the user presses a key on the
   * keyboard while within the GraphEditor. If any Nodes or Edges
   * are selected, these Nodes and Edges are deleted.
   *
   * @param KeyEvent event: The KeyEvent that triggered this method.
   */
  public void keyPressed(KeyEvent event)
  {
    if ( numSpecialSelectionsAllowed == 0 &&
         (event.getKeyCode() == KeyEvent.VK_DELETE ||
          event.getKeyCode() == KeyEvent.VK_DECIMAL) ) // check for del w/ and w/out numlock
    {
      controller.removeSelected();
      editor.update();
    }
  }

  public void actionPerformed(ActionEvent e)
  {
    if ( e.getSource() == gridButton )
    {
      if ( aNode != null )
      {
        if ( !graph.isOnGrid(aNode.getLocation()) )
        {
          graph.newMemento("Snap Node To Grid");
          graph.relocateNode( aNode, graph.getClosestGridLocation(aNode.getLocation()),
                              true );
          graph.updateEdges(aNode.incidentEdges(), true);
          graph.doneMemento();
          controller.newUndo();
          editor.repaint();
          popupLocation = new Point( aNode.getLocation().intX()+GraphEditor.DRAW_BUFFER,
                                     aNode.getLocation().intY()+GraphEditor.DRAW_BUFFER);
          showPopup(aNode, popupLocation);
        }
      }
      else if ( anEdge != null )
      {
        graph.newMemento("Make Edge Orthogonal");
        
        graph.orthogonalizeEdge( anEdge, true );
        graph.doneMemento();
        controller.newUndo();
        editor.repaint();
        popupLocation = new Point(anEdge.getCenterLocation().intX()+GraphEditor.DRAW_BUFFER,
                                  anEdge.getCenterLocation().intY()+GraphEditor.DRAW_BUFFER);
        showPopup(anEdge, popupLocation);
      }
    }
    else
    {
      super.actionPerformed(e);
    }
  }

  public void focusGained(FocusEvent e) { }

  public void focusLost(FocusEvent e)
  {
    
  }

  public void ancestorMoved(AncestorEvent event)
  {
    
  }

  public void ancestorAdded(AncestorEvent event) { }
  public void ancestorRemoved(AncestorEvent event) { }

  // Unused event handlers
  public void keyTyped(KeyEvent event) {}
  public void keyReleased(KeyEvent event) {}  
  
}
