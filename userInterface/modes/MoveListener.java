package userInterface.modes;

import java.awt.event.*;
import java.awt.geom.Rectangle2D;

import userInterface.GraphEditor;
import graphStructure.*;

public class MoveListener extends GraphEditorListener
{
  private Location dragStartLocation;
  private int minX, maxX, minY, maxY;
  private boolean dragged;
  private boolean dragging;

  public MoveListener(GraphEditorListener listener)
  {
    super(listener);
    graph.setDrawSelected(false);
    editor.changeToMoveCursor();
    dragging = false;
    dragged = false;
  }

  public boolean isMoveListener() { return true; }

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
    if ( event.getPoint().x >= GraphEditor.DRAW_BUFFER &&
         event.getPoint().x <= editor.getWidth() - GraphEditor.DRAW_BUFFER &&
         event.getPoint().y >= GraphEditor.DRAW_BUFFER &&
         event.getPoint().y <= editor.getHeight() - GraphEditor.DRAW_BUFFER &&
         event.getButton() != MouseEvent.BUTTON3 )
    {
      dragStartLocation = new Location( event.getPoint().x - GraphEditor.DRAW_BUFFER,
                                        event.getPoint().y - GraphEditor.DRAW_BUFFER );
      Rectangle2D.Double bounds = graph.getBounds();
      maxX = (int)bounds.getMaxX();
      minX = (int)bounds.getMinX();
      maxY = (int)bounds.getMaxY();
      minY = (int)bounds.getMinY();
      graph.newMemento("Move Graph");
      graph.translate(0, 0, true); // memento
      editor.startTranslate();
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
    if ( dragging )
    {  
      Location ePoint = new Location( event.getPoint().x - GraphEditor.DRAW_BUFFER,
                                      event.getPoint().y - GraphEditor.DRAW_BUFFER );
      int transX = ePoint.intX() -dragStartLocation.intX();
      int transY = ePoint.intY() -dragStartLocation.intY();
      dragStartLocation = ePoint;
  
      if ( maxX + transX > editor.getWidth() - 2*GraphEditor.DRAW_BUFFER )
      {
        transX = (editor.getWidth() - 2*GraphEditor.DRAW_BUFFER) - maxX;
      }
      else if ( minX + transX < 0 )
      {
        transX = 0 - minX;
      }
      if ( maxY + transY > editor.getHeight() - 2*GraphEditor.DRAW_BUFFER )
      {
        transY = (editor.getHeight() - 2*GraphEditor.DRAW_BUFFER) - maxY;
      }
      else if ( minY + transY < 0 )
      {
        transY = 0 - minY;
      }
      maxX+=transX;
      minX+=transX;
      maxY+=transY;
      minY+=transY;
      if ( transX != 0 || transY != 0 )
      {
        dragged = true;
        controller.newUndo();
        editor.translate(transX, transY);
        editor.setPreferredSize();
        editor.repaint();
      }
    }
  }

  public void mouseReleased(MouseEvent event)
  {
    dragStartLocation = null;
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
      editor.stopTranslate();
      editor.repaint();
      dragging = false;
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