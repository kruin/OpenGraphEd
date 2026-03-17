package userInterface.modes;

import java.awt.event.*;
import java.util.Vector;
import java.awt.geom.*;

import userInterface.GraphEditor;
import graphStructure.*;

public class RotateListener extends GraphEditorListener
{
  private Location rotatePivotPoint;
  private Location rotateAxisPoint;
  private Location newRotateAxisPoint;
  private int minX, maxX, minY, maxY;
  private double totalAngle;
  private boolean dragged;
  private boolean dragging;

  public RotateListener(GraphEditorListener listener)
  {
    super(listener);
    totalAngle = 0;
    graph.setDrawSelected(false);
    editor.changeToRotateCursor();
    dragged = false;
    dragging = false;
  }

  public boolean isRotateListener() { return true; }

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
    if ( graph.getNumNodes() > 1 &&
         event.getPoint().x >= GraphEditor.DRAW_BUFFER &&
         event.getPoint().x <= editor.getWidth() - GraphEditor.DRAW_BUFFER &&
         event.getPoint().y >= GraphEditor.DRAW_BUFFER &&
         event.getPoint().y <= editor.getHeight() - GraphEditor.DRAW_BUFFER &&
         event.getButton() != MouseEvent.BUTTON3 )
    {
      rotatePivotPoint = graph.getCenterPointLocation();
      rotateAxisPoint = new Location( event.getPoint().x - GraphEditor.DRAW_BUFFER,
                                      event.getPoint().y - GraphEditor.DRAW_BUFFER );
      editor.setPointToDraw( new Ellipse2D.Double(
                               rotatePivotPoint.intX() - Node.RADIUS + GraphEditor.DRAW_BUFFER,
                               rotatePivotPoint.intY() - Node.RADIUS + GraphEditor.DRAW_BUFFER,
                               Node.RADIUS * 2, Node.RADIUS * 2 ) );
      Rectangle2D.Double bounds = graph.getBounds();
      minX = (int)bounds.getMinX();
      minY = (int)bounds.getMinY();
      graph.newMemento("Rotate Graph");
      graph.rotate(rotatePivotPoint, 0.0, true);
      editor.startRotate();
      dragging = true;
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
    super.mouseMoved(event);
    if ( dragging &&
         graph.getNumNodes() > 1 && rotatePivotPoint != null )
    {
      newRotateAxisPoint = new Location( event.getPoint().x - GraphEditor.DRAW_BUFFER,
                                         event.getPoint().y - GraphEditor.DRAW_BUFFER );
      double angle = Node.angleBetween( rotateAxisPoint, rotatePivotPoint,
                                        newRotateAxisPoint );
      rotateAxisPoint = newRotateAxisPoint;
      editor.rotate(rotatePivotPoint, angle);
      dragged = true;

      Vector nodes = graph.getNodes();
      if ( !nodes.isEmpty() )
      {
        Rectangle2D.Double bounds = graph.getBounds();
        minX = (int)bounds.getMinX();
        minY = (int)bounds.getMinY();
        maxX = (int)bounds.getMaxX();
        maxY = (int)bounds.getMaxY();
      }

      if ( maxX > (editor.getWidth() - 2*GraphEditor.DRAW_BUFFER) ||
           minX <= 0 ||
           maxY > (editor.getHeight() - 2*GraphEditor.DRAW_BUFFER) ||
           minY <= 0 )
      {
        editor.rotate(rotatePivotPoint, -1.0*angle);
      }
      editor.setPreferredSize();
      editor.repaint();
    }
  }

  public void mouseReleased(MouseEvent event)
  {
    if ( dragging )
    {  
      if ( dragged )
      {
        graph.doneMemento();
        controller.newUndo();
      }
      else
      {
        graph.abortMemento();
      }
      dragged = false;
      dragging = false;
      editor.stopRotate();
      if ( graph.getNumNodes() > 1 )
      {
        rotatePivotPoint = null;
        editor.repaint();
      }
    }
  }

  public void mouseClicked(MouseEvent event)
  {
    super.mouseClicked(event);
  }

  // Unused event handlers
  public void keyPressed(KeyEvent event) {}
  public void keyTyped(KeyEvent event) {}
  public void keyReleased(KeyEvent event) {}
}