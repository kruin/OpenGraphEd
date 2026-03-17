package userInterface.modes;

import java.awt.event.*;
import java.awt.*;
import java.awt.geom.*;

import userInterface.GraphEditor;

public class ResizeListener extends GraphEditorListener
{
  private boolean horizontalResize, verticalResize;
  private boolean dragging;
  private boolean dragged;
  private Point lastPoint;

  public ResizeListener(GraphEditorListener listener)
  {
    super(listener);
    graph.setDrawSelected(false);
    horizontalResize = verticalResize = false;
    editor.changeToNormalCursor();
    dragging = false;
    dragged = false;
  }

  public boolean isResizeListener() { return true; }

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
    if ( graph.getNumNodes() > 1 && event.getButton() != MouseEvent.BUTTON3 )
    {
      dragging = true;
      graph.newMemento("Resize Graph");
      graph.scaleTo(graph.getBounds(), true); // memento
      editor.startScaleTo();
      lastPoint = event.getPoint();
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
      if ( graph.getNumNodes() > 1 )
      {
        Rectangle2D.Double bounds = graph.getBounds();
        double width = bounds.width;
        double height = bounds.height;
        if ( horizontalResize )
        {
          width-= lastPoint.x - event.getPoint().x;
        }
        if ( verticalResize )
        {
          height-= lastPoint.y - event.getPoint().y;
        }
  
        Rectangle2D.Double rect = new Rectangle2D.Double ( bounds.getMinX(),
                                                           bounds.getMinY(),
                                                           width, height );
        lastPoint = event.getPoint();
        editor.scaleTo(rect);
        editor.updateShapes();
        editor.setPreferredSize();
        editor.repaint();
        dragged = true;
      }
    }
  }

  public void mouseReleased(MouseEvent event)
  {
    if ( dragging )
    {  
      if ( graph.getNumNodes() > 1 )
      {
        dragging = false;
        graph.updateEdgeCurveAngles();
        mouseMoved(event);
        editor.updateShapes();
        editor.setPreferredSize();
        editor.repaint();
      }
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
      editor.stopScaleTo();
    }
  }

  public void mouseMoved(MouseEvent event)
  {
    super.mouseMoved(event);
    if ( !dragging && graph.getNumNodes() > 1 )
    {
      Rectangle2D.Double bounds = graph.getBounds();
      if ( Math.abs( ((int)bounds.getMaxX() + GraphEditor.DRAW_BUFFER)
                     - event.getPoint().x ) <= 1 )
      {
        horizontalResize = true;
        if ( Math.abs( ((int)bounds.getMaxY() + GraphEditor.DRAW_BUFFER)
                       - event.getPoint().y ) <= 1 )
        {
          verticalResize = true;
          editor.changeToDiagonalResizeCursor();
        }
        else
        {
          verticalResize = false;
          editor.changeToHorizontalResizeCursor();
        }
      }
      else
      {
        horizontalResize = false;
        if ( Math.abs( ((int)bounds.getMaxY() + GraphEditor.DRAW_BUFFER)
                       - event.getPoint().y ) <= 1 )
        {
          verticalResize = true;
          editor.changeToVerticalResizeCursor();
        }
        else
        {
          verticalResize = false;
          editor.changeToNormalCursor();
        }
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