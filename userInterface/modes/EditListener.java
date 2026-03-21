package userInterface.modes;

import java.awt.event.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

import userInterface.GraphController;
import userInterface.GraphEditor;

import java.util.Vector;
import dataStructure.nodeSplitTree.*;
import graphStructure.*;

public class EditListener extends GraphEditorListener implements ActionListener,
                                                                 FocusListener,
                                                                 AncestorListener

{
  protected javax.swing.Timer counter;
  protected int animationDelay = 700;
  protected Vector edgesToAnimateVector;
  protected Vector oldEdgeColorVector;
  protected Node animateSourceNode;
  protected int sourceEdgeIndex;
  protected int animateEdgeIndex;
  protected Color oldNodeColor;
  protected Location dragStartLocation;
  protected Node dragNode;
  protected Edge dragEdge;
  protected Vector selectedNodeVector;
  protected Vector selectedEdgeVector;
  protected NodeSplitTree nodeSplitTree;
  protected boolean createNodeOnEdgeDrop;
  protected int minX, maxX, minY, maxY;
  protected boolean dragged;
  protected boolean undoCurve;

  protected JButton closeButton;
  protected JButton selectButton;
  protected JButton labelButton;
  protected JButton colorButton;
  protected JButton makeStraightButton;
  protected JButton undirectButton;
  protected JButton makePermanentButton;
  protected JButton gridButton;
  protected Node aNode;
  protected Edge anEdge;
  protected JPanel popupPanel;
  protected JLayeredPane layeredPane;
  protected JRootPane rootPane;
  protected JWindow popupWindow;
  protected JLayeredPane wLayeredPane;
  protected JRootPane wRootPane;
  protected boolean layerInited;
  protected Point popupLocation; // to re-show a popup if necessary
  protected boolean windowPopup;
  protected boolean applyToSelected;

  public EditListener(GraphEditorListener listener)
  {
    super(listener);
    init();
  }

  public EditListener(Graph graph, GraphEditor editor, GraphController controller)
  {
    super(graph, editor, controller);
    init();
  }

  private void init()
  {
    graph.setDrawSelected(true);
    editor.changeToNormalCursor();
    nodeSplitTree = null;
    dragged = false;
    undoCurve = false;
    aNode = null;
    anEdge = null;
    layerInited = false;
    windowPopup = false;
    applyToSelected = false;
  }
  public boolean isEditListener() { return true; }

  /**
   * This method is triggered when the user clicks the mouse within the
   * GraphEditor.<br>
   * <br>
   *
   * @param MouseEvent event: The MouseEvent that triggered this method.
   */
  public void mouseClicked(MouseEvent event)
  {
    if ( numSpecialSelectionsAllowed != 0 )
    {
      super.mouseClicked(event);
    }
    else if ( event.getButton() != MouseEvent.BUTTON3 &&
              event.getPoint().x >= GraphEditor.DRAW_BUFFER - Node.RADIUS &&
              event.getPoint().x <= editor.getWidth() - GraphEditor.DRAW_BUFFER + Node.RADIUS &&
              event.getPoint().y >= GraphEditor.DRAW_BUFFER - Node.RADIUS &&
              event.getPoint().y <= editor.getHeight() - GraphEditor.DRAW_BUFFER + Node.RADIUS )
    {
      Location ePoint = new Location( event.getPoint().x - GraphEditor.DRAW_BUFFER,
                                      event.getPoint().y - GraphEditor.DRAW_BUFFER );
      if ( event.isControlDown() )
      {
        Node aNode = graph.nodeAt(ePoint);
        if ( aNode != null )
        {
          if ( event.isShiftDown() )
          {
            drawIncidentEdgesInOrder(aNode, true);
          }
          else
          {
            drawIncidentEdgesInOrder(aNode, false);
          }
        }
        Edge anEdge = graph.edgeAt(ePoint);
        if (anEdge != null)
        {
          drawIncidentEdgesInOrder(anEdge);
        }
      }
      else
      {
        Node aNode = graph.nodeAt(ePoint);
        Edge anEdge = graph.edgeAt(ePoint);

        // select a node or edge
        if ( aNode != null || anEdge != null )
        {
          if ( aNode != null )
          {
            graph.toggleNodeSelection(aNode);
            editor.repaint();
          }
          else if ( anEdge != null )
          {
            graph.toggleEdgeSelection(anEdge);
            editor.repaint();
          }
        }
        // add a node
        else if ( aNode == null &&
                  ( event.getClickCount() == 2 || SINGLE_CLICK_ADD_NODE  ) &&
                  event.getPoint().x >= GraphEditor.DRAW_BUFFER &&
                  event.getPoint().x <= editor.getWidth() - GraphEditor.DRAW_BUFFER &&
                  event.getPoint().y >= GraphEditor.DRAW_BUFFER &&
                  event.getPoint().y <= editor.getHeight() - GraphEditor.DRAW_BUFFER )
        {
          graph.newMemento("Create New Node");
            graph.createNode(ePoint);
            graph.doneMemento();
            controller.newUndo();
            editor.setPreferredSize();
            editor.update();
        }
      }
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
    if ( numSpecialSelectionsAllowed == 0 )
    {
      if ( event.getButton() != MouseEvent.BUTTON3 )
      {
        if ( event.getPoint().x >= GraphEditor.DRAW_BUFFER - Node.RADIUS &&
             event.getPoint().x <= editor.getWidth() - GraphEditor.DRAW_BUFFER + Node.RADIUS &&
             event.getPoint().y >= GraphEditor.DRAW_BUFFER - Node.RADIUS &&
             event.getPoint().y <= editor.getHeight() - GraphEditor.DRAW_BUFFER + Node.RADIUS )
        {
          Location ePoint = new Location( event.getPoint().x - GraphEditor.DRAW_BUFFER,
                                          event.getPoint().y - GraphEditor.DRAW_BUFFER );
          // First check to see if we are about to drag a node
          Node aNode = graph.nodeAt(ePoint);
          if ( aNode != null )
          {
            dragStartLocation = new Location(event.getPoint());
            dragNode = aNode;

            if ( aNode.isSelected() )
            {
              if ( event.isControlDown() )
              {
                //change to move cursor.
                selectedNodeVector = graph.selectedNodes();
                if ( !selectedNodeVector.isEmpty() )
                {
                  graph.newMemento("Move Group of Selected Nodes");
                  Rectangle2D.Double bounds = graph.getBounds(selectedNodeVector);
                  maxX = (int)bounds.getMaxX();
                  minX = (int)bounds.getMinX();
                  maxY = (int)bounds.getMaxY();
                  minY = (int)bounds.getMinY();
                  graph.translateNodes(selectedNodeVector, 0, 0, true);
                  editor.startTranslateNodes(selectedNodeVector);
                }
              }
              else
              {
                graph.newMemento("Move Selected Node");
                graph.translateNode(aNode, 0, 0, true); // memento
                editor.startTranslateNode(aNode);
                maxX = aNode.getX();
                minX = aNode.getX();
                maxY = aNode.getY();
                minY = aNode.getY();
              }
            }
            else if ( event.isControlDown() || ADD_NODE_ON_EDGE_DROP )
            {
              // change to drop cursor
              createNodeOnEdgeDrop = true;
              maxX = minX = aNode.getX();
              maxY = minY = aNode.getY();
              nodeSplitTree = new NodeSplitTree(graph.getNodes());
            }
            else
            {
              // change to attach cursor
              createNodeOnEdgeDrop = false;
              maxX = minX = aNode.getX();
              maxY = minY = aNode.getY();
              nodeSplitTree = new NodeSplitTree(graph.getNodes());
            }
          }
          else
          {
            Edge anEdge = graph.edgeAt(ePoint);
            if ( anEdge != null )
            {
              if ( anEdge.isSelected() )
              {
                graph.newMemento("Curve Selected Edge");
                undoCurve = !anEdge.isCurved();
                graph.curveEdge(anEdge, 0, 0, true); // memento
                editor.startTranslateEdge(anEdge);
                dragStartLocation = new Location(event.getPoint());
                dragEdge = anEdge;
                maxX = anEdge.getCenterLocation().intX();
                minX = anEdge.getCenterLocation().intX();
                maxY = anEdge.getCenterLocation().intY();
                minY = anEdge.getCenterLocation().intY();
              }
            }
            else
            {
              dragStartLocation = new Location(event.getPoint());
            }
          }
        }
        hidePopup();
      }
      else
      {
        Location ePoint = new Location( event.getPoint().x - GraphEditor.DRAW_BUFFER,
                                        event.getPoint().y - GraphEditor.DRAW_BUFFER );
        aNode = graph.nodeAt(ePoint);
        if ( aNode != null )
        {
          graph.enumerateNodeAndEdgeIndices();
          popupLocation = event.getPoint();
          applyToSelected = event.isControlDown();
          showPopup(aNode, event.getPoint());
        }
        else
        {
          anEdge = graph.edgeAt(ePoint);
          if ( anEdge != null )
          {
            graph.enumerateNodeAndEdgeIndices();
            popupLocation = event.getPoint();
            applyToSelected = event.isControlDown();
            showPopup(anEdge, event.getPoint());
          }
          else
          {
            hidePopup();
          }
        }
      }
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
    if ( numSpecialSelectionsAllowed == 0 )
    {
      if ( dragNode != null )
      {
        dragged = true;
        Location ePoint = new Location( event.getPoint().x - GraphEditor.DRAW_BUFFER,
                                     event.getPoint().y - GraphEditor.DRAW_BUFFER );
        if (dragNode.isSelected())
        {
          int transX = ePoint.intX() -dragNode.getLocation().intX();
          int transY = ePoint.intY() -dragNode.getLocation().intY();

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
            if ( event.isControlDown() )
            {
              graph.translateNodes(selectedNodeVector, transX, transY, false);
            }
            else
            {
              graph.translateNode(dragNode, transX, transY, false);
            }
            editor.setPreferredSize();
            editor.repaint();
          }
        }
        else
        {
          dragStartLocation = new Location(event.getPoint());
          editor.setLineToDraw( new Line2D.Double( dragNode.getLocation().intX() +
                                                   GraphEditor.DRAW_BUFFER,
                                                   dragNode.getLocation().intY() +
                                                   GraphEditor.DRAW_BUFFER,
                                                   dragStartLocation.intX(),
                                                   dragStartLocation.intY() ) );
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
          if ( dragToNode != null )
          {
            editor.setLineToDrawColor(Edge.DEFAULT_COLOR);
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
      }
      else if ( dragEdge != null )
      {
        dragged = true;
        Location ePoint = new Location( event.getPoint().x - GraphEditor.DRAW_BUFFER,
                                        event.getPoint().y - GraphEditor.DRAW_BUFFER );
        Location edgeLocation = dragEdge.getCenterLocation();
        int transX = ePoint.intX() - edgeLocation.intX();
        int transY = ePoint.intY() - edgeLocation.intY();

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
          graph.curveEdge(dragEdge, transX, transY, false);
          editor.setPreferredSize();
          editor.update();
        }
      }
      else if ( dragStartLocation != null )
      {
        dragged = true;
        double startX = dragStartLocation.doubleX();
        double startY = dragStartLocation.doubleY();
        if ( event.getPoint().x < dragStartLocation.intX() )
        {
          startX = event.getPoint().x;
        }
        if ( event.getPoint().y < dragStartLocation.intY() )
        {
          startY = event.getPoint().y;
        }
        double width = Math.abs( event.getPoint().x - dragStartLocation.intX() );
        double height = Math.abs( event.getPoint().y - dragStartLocation.intY() );
        editor.setRectangleToDraw( new Rectangle2D.Double( startX, startY,
                                                           width, height ) );
        editor.repaint();
      }
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
    if ( dragNode != null )
    {
      // change to normal cursor
      if ( !dragNode.isSelected() )
      {
        // Check to see if we have let go on a node
        Location ePoint = new Location( event.getPoint().x - GraphEditor.DRAW_BUFFER,
                                        event.getPoint().y - GraphEditor.DRAW_BUFFER );
        Node aNode = graph.nodeAt(ePoint);
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
            aNode = graph.createNode( ePoint );
            graph.addEdge(dragNode, aNode);
            graph.doneMemento();
            controller.newUndo();
            editor.setPreferredSize();
            editor.update();
          }
        }
        nodeSplitTree = null;
        editor.setLineToDraw(null);
        editor.repaint();
      }
      else
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
      }
      dragNode = null;
      editor.stopTranslateNodes();
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
        if ( undoCurve )
        {
          undoCurve = false;
          dragEdge.makeStraight();
        }
        graph.abortMemento();
      }
      editor.stopTranslateEdge();
      dragEdge = null;
    }
    else
    {
      if ( dragged )
      {
        Rectangle2D.Double rect = editor.getRectangleToDraw();
        rect.setRect( rect.x - GraphEditor.DRAW_BUFFER,
                      rect.y - GraphEditor.DRAW_BUFFER,
                      rect.width, rect.height );
        Vector nodes = graph.getNodesInRectangle( rect );
        Vector edges = graph.getEdgesInRectangle( rect );
        graph.selectNodes(nodes);
        graph.selectEdges(edges);
        editor.setRectangleToDraw(null);
        editor.repaint();
      }
    }
    dragStartLocation = null;
    dragged = false;
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

  public void drawIncidentEdgesInOrder(Node aNode, boolean reverse)
  {
    if ( counter == null || !counter.isRunning() )
    {
      animateSourceNode = aNode;
      oldEdgeColorVector = new Vector();
      if ( reverse )
      {
        edgesToAnimateVector = aNode.incidentEdgesInReverse();
      }
      else
      {
        edgesToAnimateVector = aNode.incidentEdges();
      }
      oldNodeColor = aNode.getColor();
      for ( int i=0; i<edgesToAnimateVector.size(); i++ )
      {
        oldEdgeColorVector.addElement( ((Edge)edgesToAnimateVector.elementAt(i)).getColor() );
      }

      animateEdgeIndex = 0;
      counter = new javax.swing.Timer(animationDelay, this);
      counter.start();
    }
  }

  public void drawIncidentEdgesInOrder(Edge anEdge)
  {
    if ( counter == null || !counter.isRunning() )
    {
      edgesToAnimateVector = anEdge.edgesFromSameCycle();
      sourceEdgeIndex = edgesToAnimateVector.size();
      edgesToAnimateVector.addAll(anEdge.edgesFromSameCycleOnOtherSide());
      animateSourceNode = null;
      oldEdgeColorVector = new Vector();
      for ( int i=0; i<edgesToAnimateVector.size(); i++ )
      {
        oldEdgeColorVector.addElement( ((Edge)edgesToAnimateVector.elementAt(i)).getColor() );
      }

      animateEdgeIndex = 0;
      counter = new javax.swing.Timer(animationDelay, this);
      counter.start();
    }
  }

  public void actionPerformed(ActionEvent e)
  {
    if ( e.getSource() == counter )
    {
      if ( animateSourceNode != null )
      {
        Edge tempEdge;
        if ( animateEdgeIndex < edgesToAnimateVector.size() )
        {
          tempEdge = (Edge)edgesToAnimateVector.elementAt(animateEdgeIndex);
          tempEdge.setColor(Color.cyan);
          animateEdgeIndex++;
        }
        else
        {
          if ( animateEdgeIndex == edgesToAnimateVector.size() )
          {
            animateSourceNode.setColor(Color.cyan);
            animateEdgeIndex++;
          }
          else
          {
            counter.stop();
            for ( int i=0; i<edgesToAnimateVector.size(); i++ )
            {
              ((Edge)edgesToAnimateVector.elementAt(i)).setColor((Color)oldEdgeColorVector.elementAt(i));
            }
            animateSourceNode.setColor(oldNodeColor);
          }
        }
      }
      else
      {
        Edge tempEdge;

        if ( animateEdgeIndex < edgesToAnimateVector.size() )
        {
          tempEdge = (Edge)edgesToAnimateVector.elementAt(animateEdgeIndex);
          if ( animateEdgeIndex < sourceEdgeIndex )
          {
            tempEdge.setColor(Color.cyan);
          }
          else
          {
            tempEdge.setColor(Color.orange);
          }
          animateEdgeIndex++;
        }
        else
        {
          counter.stop();
          for ( int i=0; i<edgesToAnimateVector.size(); i++ )
          {
            ((Edge)edgesToAnimateVector.elementAt(i)).setColor((Color)oldEdgeColorVector.elementAt(i));
          }
        }
      }
      graph.markForRepaint();
      editor.repaint();
    }
    else
    {
      if ( e.getSource() == labelButton )
      {
        if ( aNode != null )
        {
          String newLabel = (String)JOptionPane.showInputDialog(
                                      null,
                                      "Please Enter the Text for the New Label",
                                      "Set Node Label",
                                      JOptionPane.PLAIN_MESSAGE,
                                      null,
                                      null,
                                      aNode.getLabel());
          if ( newLabel != null )
          {
            if ( applyToSelected && ( selectedNodeVector.size() +
                              selectedEdgeVector.size() ) > 1 )
            {
              graph.newMemento("Change Selected Node Labels");
              for ( int i=0; i<selectedNodeVector.size(); i++ )
              {
                graph.changeNodeLabel( (Node)selectedNodeVector.elementAt(i), newLabel, true );
              }
              graph.doneMemento();
            }
            else
            {
              graph.newMemento("Change Node Label");
              graph.changeNodeLabel( aNode, newLabel, true );
              graph.doneMemento();
            }
            controller.newUndo();
            editor.repaint();
            showPopup(aNode, popupLocation);
          }
        }
      }
      else if ( e.getSource() == colorButton )
      {
        if ( aNode != null )
        {
          Color newColor = JColorChooser.showDialog(
                             null,
                             "Set Node Color",
                             aNode.getColor() );
          if ( newColor != null )
          {
            if ( applyToSelected && selectedNodeVector.size() > 1 )
            {
              graph.newMemento("Change Selected Node Colours");
              for ( int i=0; i<selectedNodeVector.size(); i++ )
              {
                graph.changeNodeColor( (Node)selectedNodeVector.elementAt(i), newColor, true );
              }
              graph.doneMemento();
            }
            else
            {
              graph.newMemento("Change Node Color");
              graph.changeNodeColor( aNode, newColor, true );
              graph.doneMemento();
            }
            controller.newUndo();
            editor.repaint();
            showPopup(aNode, popupLocation);
          }
        }
        else if ( anEdge != null )
        {
          Color newColor = JColorChooser.showDialog(
                             null,
                             "Set Edge Color",
                             anEdge.getColor() );
          if ( newColor != null )
          {
            if ( applyToSelected && selectedEdgeVector.size() > 1 )
            {
              graph.newMemento("Change Selected Edge Colours");
              for ( int i=0; i<selectedEdgeVector.size(); i++ )
              {
                graph.changeEdgeColor( (Edge)selectedEdgeVector.elementAt(i), newColor, true );
              }
              graph.doneMemento();
            }
            else
            {
              graph.newMemento("Change Edge Color");
              graph.changeEdgeColor( anEdge, newColor, true );
              graph.doneMemento();
            }
            controller.newUndo();
            editor.repaint();
            showPopup(anEdge, popupLocation);
          }
        }
      }
      else if ( e.getSource() == makeStraightButton )
      {
        Location diff = anEdge.getCenterLocation();
        if ( applyToSelected && ( selectedNodeVector.size() +
                                  selectedEdgeVector.size() ) > 1 )
        {
          graph.newMemento("Make Selected Edges Straight");
          for ( int i=0; i<selectedEdgeVector.size(); i++ )
          {
            graph.straightenEdge( (Edge)selectedEdgeVector.elementAt(i), true );
          }
          graph.doneMemento();
        }
        else
        {
          graph.newMemento("Straighten Edge");
          graph.straightenEdge(anEdge, true);
          graph.doneMemento();
        }
        controller.newUndo();
        editor.update();
        int dx = anEdge.getCenterLocation().intX() - diff.intX();
        int dy = anEdge.getCenterLocation().intY() - diff.intY();
        popupLocation.translate(dx, dy);
        showPopup(anEdge, popupLocation);
      }
      else if ( e.getSource() == undirectButton )
      {
        if ( applyToSelected && ( selectedNodeVector.size() +
                                  selectedEdgeVector.size() ) > 1 )
        {
          graph.newMemento("Make Selected Edges Undirected");
          for ( int i=0; i<selectedEdgeVector.size(); i++ )
          {
            graph.changeEdgeDirection( (Edge)selectedEdgeVector.elementAt(i), null, true );
          }
          graph.doneMemento();
        }
        else
        {
          graph.newMemento("Make Edge Undirected");
          graph.changeEdgeDirection(anEdge, null, true);
          graph.doneMemento();
        }
        controller.newUndo();
        editor.update();
        showPopup(anEdge, popupLocation);
      }
      else if ( e.getSource() == makePermanentButton )
      {
        if ( applyToSelected && ( selectedNodeVector.size() +
                                  selectedEdgeVector.size() ) > 1 )
        {
          graph.newMemento("Make Generated Edges Permanent");
          for ( int i=0; i<selectedEdgeVector.size(); i++ )
          {
            graph.makeGeneratedEdgePermanent((Edge)selectedEdgeVector.elementAt(i));
          }
          graph.doneMemento();
        }
        else
        {
          graph.newMemento("Make Generated Edge Permanent");
          graph.makeGeneratedEdgePermanent(anEdge);
          graph.doneMemento();
        }
        controller.newUndo();
        editor.update();
        showPopup(anEdge, popupLocation);
      }
      else if ( e.getSource() == selectButton )
      {
        if ( aNode != null )
        {
          aNode.toggleSelected();
          if ( aNode.isSelected() )
          {
            selectButton.setText("Toggle Select (T)");
          }
          else
          {
            selectButton.setText("Toggle Select (F)");
          }
          graph.markForRepaint();
          editor.repaint();
        }
        else if ( anEdge != null )
        {
          anEdge.toggleSelected();
          if ( anEdge.isSelected() )
          {
            selectButton.setText("Toggle Select (T)");
          }
          else
          {
            selectButton.setText("Toggle Select (F)");
          }
          graph.markForRepaint();
          editor.repaint();
        }
      }
      else if ( e.getSource() == closeButton )
      {
        hidePopup();
      }
    }
  }

  public void focusGained(FocusEvent e) { }

  public void focusLost(FocusEvent e)
  {
    if ( e.getOppositeComponent() != popupWindow &&
         e.getOppositeComponent() != labelButton &&
         e.getOppositeComponent() != colorButton &&
         e.getOppositeComponent() != makeStraightButton &&
         e.getOppositeComponent() != closeButton )
    {
      hidePopup();
    }
  }

  public void ancestorMoved(AncestorEvent event)
  {
    hidePopup();
  }

  public void ancestorAdded(AncestorEvent event) { }
  public void ancestorRemoved(AncestorEvent event) { }

  protected void showPopup(Node aNode, Point location)
  {
    hidePopup();
    popupPanel = new JPanel();
    popupPanel.setBorder(BorderFactory.createEtchedBorder());

    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints layoutCons = new GridBagConstraints();
    popupPanel.setLayout(layout);

    JLabel label;

    selectedNodeVector = graph.selectedNodes();
    selectedEdgeVector = graph.selectedEdges();
    if ( applyToSelected && selectedNodeVector.size() > 1 )
    {
      label = new JLabel("Edit All Selected");
    }
    else
    {
      label = new JLabel("Index: " + aNode.getIndex());
    }
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(1,1,1,1);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(label, layoutCons);
    popupPanel.add(label);

    label = new JLabel("X, Y: " + aNode.getX() + ", " + aNode.getY());
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(1,1,1,1);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(label, layoutCons);
    popupPanel.add(label);

    JPanel labelPanel = new JPanel();
    labelPanel.setLayout( new BorderLayout() );
    labelButton = new JButton("Set Label: ");
    if ( applyToSelected && selectedNodeVector.size() > 1 )
    {
      labelButton.setToolTipText("Set all Selected Nodes' Label");
    }
    else
    {
      labelButton.setToolTipText("Set this Node's Label");
    }
    labelButton.addActionListener(this);
    labelPanel.add(labelButton, BorderLayout.WEST);
    label = new JLabel("\"" + aNode.getLabel() + "\"");
    labelPanel.add(label, BorderLayout.CENTER);
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(1,1,1,1);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(labelPanel, layoutCons);
    popupPanel.add(labelPanel);

    JPanel colorPanel = new JPanel();
    colorPanel.setLayout( new BorderLayout() );
    colorButton = new JButton("Set Color:");
    if ( applyToSelected && selectedNodeVector.size() > 1 )
    {
      colorButton.setToolTipText("Set all Selected Nodes' Color");
    }
    else
    {
      colorButton.setToolTipText("Set this Node's Color");
    }
    colorButton.addActionListener(this);
    colorPanel.add(colorButton, BorderLayout.WEST);
    JPanel subColorPanel = new JPanel();
    subColorPanel.setBackground(aNode.getColor());
    colorPanel.add(subColorPanel, BorderLayout.CENTER);
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(1,1,1,1);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(colorPanel, layoutCons);
    popupPanel.add(colorPanel);

    if ( aNode.isSelected() )
    {
      selectButton = new JButton("Toggle Select (T) ");
    }
    else
    {
      selectButton = new JButton("Toggle Select (F) ");
    }
    selectButton.setToolTipText("Toggle Node Selection");
    selectButton.addActionListener(this);
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(1,1,1,1);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(selectButton, layoutCons);
    popupPanel.add(selectButton);

    if ( isGridListener() &&!graph.isOnGrid(aNode.getLocation()) )
    {
      gridButton = new JButton("Snap To Grid");
      gridButton.addActionListener(this);
      layoutCons.gridx = GridBagConstraints.RELATIVE;
      layoutCons.gridy = GridBagConstraints.RELATIVE;
      layoutCons.gridwidth = GridBagConstraints.REMAINDER;
      layoutCons.gridheight = 1;
      layoutCons.fill = GridBagConstraints.BOTH;
      layoutCons.insets = new Insets(1,1,1,1);
      layoutCons.anchor = GridBagConstraints.NORTH;
      layoutCons.weightx = 1.0;
      layoutCons.weighty = 1.0;
      layout.setConstraints(gridButton, layoutCons);
      popupPanel.add(gridButton);
    }
    
    closeButton = new JButton("Close");
    closeButton.setToolTipText("Close this Window");
    closeButton.addActionListener(this);
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(1,1,1,1);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(closeButton, layoutCons);
    popupPanel.add(closeButton);

    showPopup(location);
  }

  protected void showPopup(Edge anEdge, Point location)
  {
    hidePopup();
    popupPanel = new JPanel();
    popupPanel.setBorder(BorderFactory.createEtchedBorder());

    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints layoutCons = new GridBagConstraints();
    popupPanel.setLayout(layout);

    JLabel label;

    selectedNodeVector = graph.selectedNodes();
    selectedEdgeVector = graph.selectedEdges();
    if ( applyToSelected && selectedEdgeVector.size() > 1 )
    {
      label = new JLabel("Edit All Selected");
    }
    else
    {
      label = new JLabel("Index: " + anEdge.getIndex());
    }
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(1,1,1,1);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(label, layoutCons);
    popupPanel.add(label);

    label = new JLabel("s.X, s.Y: " + anEdge.getStartNode().getX() + ", " + anEdge.getStartNode().getY());
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(1,1,1,1);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(label, layoutCons);
    popupPanel.add(label);

    label = new JLabel("e.X, e.Y: " + anEdge.getEndNode().getX() + ", " + anEdge.getEndNode().getY());
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(1,1,1,1);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(label, layoutCons);
    popupPanel.add(label);

    JPanel colorPanel = new JPanel();
    colorPanel.setLayout( new BorderLayout() );
    colorButton = new JButton("Color: ");
    if ( applyToSelected && selectedEdgeVector.size() > 1 )
    {
      colorButton.setToolTipText("Set all Selected Edges' Color");
    }
    else
    {
      colorButton.setToolTipText("Set this Edge's Color");
    }
    colorButton.addActionListener(this);
    colorPanel.add(colorButton, BorderLayout.WEST);
    JPanel subColorPanel = new JPanel();
    subColorPanel.setBackground(anEdge.getColor());
    colorPanel.add(subColorPanel, BorderLayout.CENTER);
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(1,1,1,1);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(colorPanel, layoutCons);
    popupPanel.add(colorPanel);

    if ( applyToSelected && selectedEdgeVector.size() > 1 )
    {
      boolean anyIsCurved = false;
      for ( int x=0; x<selectedEdgeVector.size(); x++ )
      {
        if ( ((Edge)selectedEdgeVector.elementAt(x)).isCurved() ||
             ((Edge)selectedEdgeVector.elementAt(x)).isOrthogonal() )
        {
          anyIsCurved = true;
        }
      }
      if ( anyIsCurved )
      {
        makeStraightButton = new JButton("Make Straight");
        makeStraightButton.setToolTipText("Make all Selected Edges Straight");
        makeStraightButton.addActionListener(this);
        layoutCons.gridx = GridBagConstraints.RELATIVE;
        layoutCons.gridy = GridBagConstraints.RELATIVE;
        layoutCons.gridwidth = GridBagConstraints.REMAINDER;
        layoutCons.gridheight = 1;
        layoutCons.fill = GridBagConstraints.BOTH;
        layoutCons.insets = new Insets(1,1,1,1);
        layoutCons.anchor = GridBagConstraints.NORTH;
        layoutCons.weightx = 1.0;
        layoutCons.weighty = 1.0;
        layout.setConstraints(makeStraightButton, layoutCons);
        popupPanel.add(makeStraightButton);
      }
    }
    else
    {
      if ( anEdge.isCurved() || anEdge.isOrthogonal() )
      {
        makeStraightButton = new JButton("Make Straight");
        makeStraightButton.setToolTipText("Make this Edge Straight");
        makeStraightButton.addActionListener(this);
        layoutCons.gridx = GridBagConstraints.RELATIVE;
        layoutCons.gridy = GridBagConstraints.RELATIVE;
        layoutCons.gridwidth = GridBagConstraints.REMAINDER;
        layoutCons.gridheight = 1;
        layoutCons.fill = GridBagConstraints.BOTH;
        layoutCons.insets = new Insets(1,1,1,1);
        layoutCons.anchor = GridBagConstraints.NORTH;
        layoutCons.weightx = 1.0;
        layoutCons.weighty = 1.0;
        layout.setConstraints(makeStraightButton, layoutCons);
        popupPanel.add(makeStraightButton);
      }
    }

    if ( isGridListener() && !anEdge.isOrthogonal() && anEdge.isCurved() )
    {
      gridButton = new JButton("Make Orthogonal");
      gridButton.addActionListener(this);
      layoutCons.gridx = GridBagConstraints.RELATIVE;
      layoutCons.gridy = GridBagConstraints.RELATIVE;
      layoutCons.gridwidth = GridBagConstraints.REMAINDER;
      layoutCons.gridheight = 1;
      layoutCons.fill = GridBagConstraints.BOTH;
      layoutCons.insets = new Insets(1,1,1,1);
      layoutCons.anchor = GridBagConstraints.NORTH;
      layoutCons.weightx = 1.0;
      layoutCons.weighty = 1.0;
      layout.setConstraints(gridButton, layoutCons);
      popupPanel.add(gridButton);
    }
    
    if ( applyToSelected && selectedEdgeVector.size() > 1 )
    {
      boolean anyIsDirected = false;
      for ( int x=0; x<selectedEdgeVector.size(); x++ )
      {
        if ( ((Edge)selectedEdgeVector.elementAt(x)).isDirected() )
        {
          anyIsDirected = true;
        }
      }
      if ( anyIsDirected )
      {
        undirectButton = new JButton("Remove Directions");
        undirectButton.setToolTipText("Make All Selected Edges Undirected");
        undirectButton.addActionListener(this);
        layoutCons.gridx = GridBagConstraints.RELATIVE;
        layoutCons.gridy = GridBagConstraints.RELATIVE;
        layoutCons.gridwidth = GridBagConstraints.REMAINDER;
        layoutCons.gridheight = 1;
        layoutCons.fill = GridBagConstraints.BOTH;
        layoutCons.insets = new Insets(1,1,1,1);
        layoutCons.anchor = GridBagConstraints.NORTH;
        layoutCons.weightx = 1.0;
        layoutCons.weighty = 1.0;
        layout.setConstraints(undirectButton, layoutCons);
        popupPanel.add(undirectButton);
      }
    }
    else
    {
      if ( anEdge.isDirected() )
      {
        undirectButton = new JButton("Remove Direction");
        undirectButton.setToolTipText("Make this Edge Undirected");
        undirectButton.addActionListener(this);
        layoutCons.gridx = GridBagConstraints.RELATIVE;
        layoutCons.gridy = GridBagConstraints.RELATIVE;
        layoutCons.gridwidth = GridBagConstraints.REMAINDER;
        layoutCons.gridheight = 1;
        layoutCons.fill = GridBagConstraints.BOTH;
        layoutCons.insets = new Insets(1,1,1,1);
        layoutCons.anchor = GridBagConstraints.NORTH;
        layoutCons.weightx = 1.0;
        layoutCons.weighty = 1.0;
        layout.setConstraints(undirectButton, layoutCons);
        popupPanel.add(undirectButton);
      }
    }

    if ( applyToSelected && selectedEdgeVector.size() > 1 )
    {
      boolean anyIsGenerated = false;
      for ( int x=0; x<selectedEdgeVector.size(); x++ )
      {
        if ( ((Edge)selectedEdgeVector.elementAt(x)).isGenerated() )
        {
          anyIsGenerated = true;
        }
      }
      if ( anyIsGenerated )
      {
        makePermanentButton = new JButton("Make Permanent");
        makePermanentButton.setToolTipText("Make All Generated Edges Permanent");
        makePermanentButton.addActionListener(this);
        layoutCons.gridx = GridBagConstraints.RELATIVE;
        layoutCons.gridy = GridBagConstraints.RELATIVE;
        layoutCons.gridwidth = GridBagConstraints.REMAINDER;
        layoutCons.gridheight = 1;
        layoutCons.fill = GridBagConstraints.BOTH;
        layoutCons.insets = new Insets(1,1,1,1);
        layoutCons.anchor = GridBagConstraints.NORTH;
        layoutCons.weightx = 1.0;
        layoutCons.weighty = 1.0;
        layout.setConstraints(makePermanentButton, layoutCons);
        popupPanel.add(makePermanentButton);
      }
    }
    else
    {
      if ( anEdge.isGenerated() )
      {
        makePermanentButton = new JButton("Make Permanent");
        makePermanentButton.setToolTipText("Make this Generated Edge Permanent");
        makePermanentButton.addActionListener(this);
        layoutCons.gridx = GridBagConstraints.RELATIVE;
        layoutCons.gridy = GridBagConstraints.RELATIVE;
        layoutCons.gridwidth = GridBagConstraints.REMAINDER;
        layoutCons.gridheight = 1;
        layoutCons.fill = GridBagConstraints.BOTH;
        layoutCons.insets = new Insets(1,1,1,1);
        layoutCons.anchor = GridBagConstraints.NORTH;
        layoutCons.weightx = 1.0;
        layoutCons.weighty = 1.0;
        layout.setConstraints(makePermanentButton, layoutCons);
        popupPanel.add(makePermanentButton);
      }
    }

    if ( anEdge.isSelected() )
    {
      selectButton = new JButton("Toggle Select (T) ");
    }
    else
    {
      selectButton = new JButton("Toggle Select (F) ");
    }
    selectButton.setToolTipText("Toggle Node Selection");
    selectButton.addActionListener(this);
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(1,1,1,1);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(selectButton, layoutCons);
    popupPanel.add(selectButton);

    closeButton = new JButton("Close");
    closeButton.setToolTipText("Close this Window");
    closeButton.addActionListener(this);
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(1,1,1,1);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(closeButton, layoutCons);
    popupPanel.add(closeButton);

    showPopup(location);
  }

  private void showPopup(Point location)
  {
    if ( !layerInited )
    {
      layerInited = true;
      Container container = editor.getTopLevelAncestor();
      if ( container instanceof RootPaneContainer )
      {
        RootPaneContainer rootPaneContainer = (RootPaneContainer)container;
        layeredPane = rootPaneContainer.getLayeredPane();
        rootPane = rootPaneContainer.getRootPane();
      }
      else
      {
        layeredPane = null;
        rootPane = null;
      }
      popupWindow = new JWindow(getParentWindow(editor));
      popupWindow.setFocusableWindowState(false);
      popupWindow.addFocusListener(this);
      wLayeredPane = popupWindow.getLayeredPane();
      wRootPane = popupWindow.getRootPane();
    }
    location = SwingUtilities.convertPoint(editor, location, rootPane);
    popupPanel.setSize(popupPanel.getPreferredSize());

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Rectangle screenBounds;
    Insets screenInsets;
    GraphicsConfiguration gc = null;
    // Try to find GraphicsConfiguration, that includes mouse
    // pointer position
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gd = ge.getScreenDevices();
    for(int i = 0; i < gd.length; i++)
    {
      if(gd[i].getType() == GraphicsDevice.TYPE_RASTER_SCREEN)
      {
        GraphicsConfiguration dgc = gd[i].getDefaultConfiguration();
        if(dgc.getBounds().contains(location))
        {
          gc = dgc;
          break;
        }
      }
    }

    if(gc != null)
    {
      // If we have GraphicsConfiguration use it to get
      // screen bounds and insets
      screenInsets = toolkit.getScreenInsets(gc);
      screenBounds = gc.getBounds();
    }
    else
    {
      // If we don't have GraphicsConfiguration use primary screen
      // and empty insets
      screenInsets = new Insets(0, 0, 0, 0);
      screenBounds = new Rectangle(toolkit.getScreenSize());
    }

    int scrWidth = screenBounds.width - Math.abs(screenInsets.left+screenInsets.right);
    int scrHeight = screenBounds.height - Math.abs(screenInsets.top+screenInsets.bottom);

    Dimension size;

    size = popupPanel.getPreferredSize();

    Point screenLocation = new Point(location);
    SwingUtilities.convertPointToScreen(screenLocation, rootPane);

    windowPopup = true;
    if( (screenLocation.x + size.width) > screenBounds.x + scrWidth )
    {
      screenLocation.x = screenBounds.x + scrWidth - size.width;
      windowPopup = true;
    }

    if( (screenLocation.y + size.height) > screenBounds.y + scrHeight)
    {
      screenLocation.y = screenBounds.y + scrHeight - size.height;
      windowPopup = true;
    }

    if( screenLocation.x < screenBounds.x )
    {
      screenLocation.x = screenBounds.x;
      windowPopup = true;
    }
    if( screenLocation.y < screenBounds.y )
    {
      screenLocation.y = screenBounds.y;
      windowPopup = true;
    }

    if ( location.y + popupPanel.getHeight() > rootPane.getHeight() ||
         location.x + popupPanel.getWidth() > rootPane.getWidth() )
    {
      windowPopup = true;
    }

    if ( windowPopup )
    {
      popupWindow.setLocation(screenLocation.x, screenLocation.y);
      popupPanel.setLocation(0,0);
      popupPanel.setVisible(true);
      popupWindow.setSize(popupPanel.getSize());
      popupWindow.setVisible(true);
      wLayeredPane.add(popupPanel, JLayeredPane.POPUP_LAYER);
    }
    else
    {
      popupPanel.setLocation(location.x, location.y);
      popupPanel.setVisible(true);
      layeredPane.add(popupPanel, JLayeredPane.POPUP_LAYER);
    }
  }

  public void hidePopup()
  {
    if ( windowPopup )
    {
      if ( popupPanel != null )
      {
        popupPanel.setVisible(false);
        wLayeredPane.remove(popupPanel);
        popupPanel = null;
        popupWindow.setVisible(false);
      }
    }
    else
    {
      if ( popupPanel != null )
      {
        popupPanel.setVisible(false);
        layeredPane.remove(popupPanel);
        popupPanel = null;
      }
    }
  }

  // Unused event handlers
  public void keyTyped(KeyEvent event) {}
  public void keyReleased(KeyEvent event) {}

  private Window getParentWindow(Component owner)
  {
    Window window = null;

    if (owner instanceof Window)
    {
      window = (Window)owner;
    }
    else if (owner != null)
    {
      window = SwingUtilities.getWindowAncestor(owner);
    }
    if (window == null)
    {
      window = new Frame();
    }
    return window;
  }

  public void prepareForClose()
  {
    hidePopup();
  }
}