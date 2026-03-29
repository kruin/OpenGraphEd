package userInterface;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.awt.geom.*;
import java.awt.image.*;
//tvo
//import com.sun.image.codec.jpeg.*; //replaced by:
import javax.imageio.ImageIO.*;
import graphStructure.*;
import userInterface.fileUtils.*;
import userInterface.modes.*;
/**
 * This class defines the GraphEditor object which is the core of the
 * application.<br>
 * <br>
 * The GraphEditor allows display of and operations on a Graph.
 *
 * @author Jon Harris
 */
public class GraphEditor extends JPanel
{
  private static Cursor rotateCursor = Toolkit.getDefaultToolkit().createCustomCursor(
    Toolkit.getDefaultToolkit().getImage(GraphEditor.class.getResource("/images/RotateCursor.gif")), new Point( 16, 16 ), "Rotate" );
  private static Cursor normalCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
  private static Cursor moveCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
  private static Cursor diagResizeCursor = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
  private static Cursor horiResizeCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
  private static Cursor vertResizeCursor = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);

  public static Color borderColor = Color.lightGray;
  public static Color backgroundColor = Color.white;
  public static int DRAW_BUFFER = 15;

  private GraphEditorInfoWindow infoWindow;
  private GraphEditorLogWindow logWindow;
  private GraphEditorListener listener;
  private Line2D.Double lineToDraw;
  private Ellipse2D.Double pointToDraw;
  private Rectangle2D.Double rectangleToDraw;
  private Polygon polygonToDraw;
  private QuadCurve2D.Double curveToDraw;
  private Color lineToDrawColor;
  private Color polygonToDrawColor;
  private Color curveToDrawColor;
  private BufferedImage bufferedImage;
  private int imageOffsetX;
  private int imageOffsetY;
  private AffineTransform affineTransform;
  private boolean createImage;
  private Vector nodesToRedraw;
  private Vector edgesToRedraw;
  private Rectangle2D.Double oldBounds;
  private double angle;

  /**
   * Constructor for class GraphEditor.
   */
  public GraphEditor(GraphController controller, GraphEditorInfoWindow infoWindow,
                     GraphEditorLogWindow logWindow)
  {
    Graph newGraph = new Graph();
    newGraph.setGridArea(3, getDrawHeight(),
                         3, getDrawWidth(), false);
    initialize(controller, newGraph, infoWindow, logWindow);
  }

  /**
   * Constructor for class GraphEditor, specifying the Graph it contains.
   *
   * @param Graph g: The Graph Object that is displayed / modified by this component.
   */
  public GraphEditor(GraphController controller, Graph g,
                     GraphEditorInfoWindow infoWindow, GraphEditorLogWindow logWindow)
  {
    initialize(controller, g, infoWindow, logWindow);
  }

  public void changeToNormalCursor() {  setCursor(normalCursor); }
  public void changeToDiagonalResizeCursor() {  setCursor(diagResizeCursor); }
  public void changeToHorizontalResizeCursor() {  setCursor(horiResizeCursor); }
  public void changeToVerticalResizeCursor() {  setCursor(vertResizeCursor); }
  public void changeToRotateCursor() { setCursor(rotateCursor); }
  public void changeToMoveCursor() { setCursor(moveCursor); }

  // Initialize the editor's components.
  private void initialize(GraphController controller, Graph g,
                          GraphEditorInfoWindow infoWindow,
                          GraphEditorLogWindow logWindow)
  {
    listener = new EditListener(g, this, controller);
    this.infoWindow = infoWindow;
    this.logWindow = logWindow;
    setBackground(borderColor);
    setPreferredSize();
    lineToDraw = null;
    pointToDraw = null;
    polygonToDraw = null;
    curveToDraw = null;
    addEventHandlers();
    updateShapes();
    bufferedImage = null;
    createImage = true;
    nodesToRedraw = null;
    edgesToRedraw = null;
    affineTransform = null;
  }

  public void changeToEditMode()
  {
    if ( !isInEditMode() )
    {
      removeEventHandlers();
      initShapes();
      listener = new EditListener(listener);
      addEventHandlers();
      updateShapes();
      repaint();
    }
  }

  public boolean isInEditMode() { return listener.isEditListener(); }

  public void changeToMoveMode()
  {
    if ( !isInMoveMode() )
    {
      removeEventHandlers();
      initShapes();
      listener = new MoveListener(listener);
      addEventHandlers();
      updateShapes();
      repaint();
    }
  }

  public boolean isInMoveMode() { return listener.isMoveListener(); }

  public void changeToRotateMode()
  {
    if ( !isInRotateMode() )
    {
      removeEventHandlers();
      initShapes();
      listener = new RotateListener(listener);
      addEventHandlers();
      updateShapes();
      repaint();
    }
  }

  public boolean isInRotateMode() { return listener.isRotateListener(); }

  public void changeToResizeMode()
  {
    if ( !isInResizeMode() )
    {
      removeEventHandlers();
      initShapes();
      listener = new ResizeListener(listener);
      addEventHandlers();
      updateShapes();
      repaint();
    }
  }

  public boolean isInGridMode() { return listener.isGridListener(); }

  /**
   * Switch to grid mode using explicit grid counts and explicit cell sizes.
   * For Kruin drawings these parameters are row/column counts plus row height
   * and column width, not a total drawing area.
   */
  public void changeToKruinGridMode(int rows, int rowHeight, int cols, int colWidth)
  {
    rowHeight = Math.max(2, rowHeight);
    colWidth = Math.max(2, colWidth);
    rows = Math.max(2, rows);
    cols = Math.max(2, cols);

    getGraph().setGrid(rows, rowHeight, cols, colWidth, false);

    if ( !isInGridMode() )
    {
      removeEventHandlers();
      initShapes();
      listener = new GridListener(listener);
      addEventHandlers();
      updateShapes();
      repaint();
    }
    setPreferredSize();
  }

  /**
   * Switch to Kruin grid mode with a dynamic grid size.
   *
   * The cell size stays fixed (rowHeight/colWidth), but the number of rows and
   * columns is derived from the current graph bounds so the grid extends behind
   * the whole image: structure plus projections.
   */
  public void changeToDynamicKruinGridMode(int rowHeight, int colWidth)
  {
    rowHeight = Math.max(2, rowHeight);
    colWidth = Math.max(2, colWidth);

    Rectangle2D.Double bounds = getGraph().getBounds(2*DRAW_BUFFER, 2*DRAW_BUFFER);

    int requiredWidth = Math.max(getDrawWidth(), (int)Math.ceil(bounds.getMaxX()) - 2*DRAW_BUFFER);
    int requiredHeight = Math.max(getDrawHeight(), (int)Math.ceil(bounds.getMaxY()) - 2*DRAW_BUFFER);

    int cols = Math.max(2, (int)Math.ceil((double)requiredWidth / (double)colWidth) + 1);
    int rows = Math.max(2, (int)Math.ceil((double)requiredHeight / (double)rowHeight) + 1);

    changeToKruinGridMode(rows, rowHeight, cols, colWidth);
  }
  public void changeToGridMode()
  {
    String gridString = null;
    if ( getGraph().getGridRows() > 0  )
    {
      gridString = (String)JOptionPane.showInputDialog(
                             getGraphController().getGraphWindow(),
                             "Use commas to separate the number of rows and columns",
                             "Set Grid Size",
                             JOptionPane.PLAIN_MESSAGE,
                             null, null,
                             String.valueOf(getGraph().getGridRows() +
                                            "," + getGraph().getGridCols()));
    }
    else
    {
      gridString = (String)JOptionPane.showInputDialog(
                             getGraphController().getGraphWindow(),
                             "Use commas to separate the number of rows and columns",
                             "Set Grid Size",
                             JOptionPane.PLAIN_MESSAGE,
                             null, null,
                             String.valueOf((getGraph().getNumNodes()-1) +
                                            "," + (getGraph().getNumNodes()-1)));
    }
    int rows = 0;
    int cols = 0;
    if ( gridString != null )
    {
      try
      {
        StringTokenizer tok = new StringTokenizer(gridString, ",");
        if ( tok.countTokens() != 2 )
        {
          throw new NumberFormatException("Rows and Columns must be separated by a comma");
        }
        rows = Integer.parseInt(tok.nextToken());
        cols = Integer.parseInt(tok.nextToken());
        if ( rows < 2 || cols < 2 )
        {
          throw new NumberFormatException("Rows and Columns must both be greater than 1");
        }
        String sizeString = (String)JOptionPane.showInputDialog(
                              getGraphController().getGraphWindow(),
                              "Use commas to separate the height and width of the rows and columns",
                              "Set Grid Size",
                              JOptionPane.PLAIN_MESSAGE,
                              null, null,
                              String.valueOf(getDrawWidth()/(cols-1) + "," +
                                             getDrawHeight()/(rows-1)) );
        int rowHeight = 0;
        int colWidth = 0;
        if ( sizeString != null )
        {
          try
          {
            tok = new StringTokenizer(sizeString, ",");
            if ( tok.countTokens() != 2 )
            {
              throw new NumberFormatException("Row and Column width must be separated by a comma");
            }
            colWidth = Integer.parseInt(tok.nextToken());
            rowHeight = Integer.parseInt(tok.nextToken());
            if ( rowHeight < 2 || colWidth < 2 )
            {
              throw new NumberFormatException("Rows and Column width must both be greater than 1");
            }
            getGraph().setGrid(rows, rowHeight, cols, colWidth, false);
            if ( !isInGridMode() )
            {
              removeEventHandlers();
              initShapes();
              listener = new GridListener(listener);
              addEventHandlers();
              updateShapes();
              repaint();
            }
            setPreferredSize();
          }
          catch ( NumberFormatException nfe )
          {
            JOptionPane.showMessageDialog(getGraphController().getGraphWindow(),
                                          "The width of the rows and columns for the grid must be integers greater than 1 separated by a comma",
                                          "Unable to switch to grid mode", JOptionPane.ERROR_MESSAGE);
          }
        }

      }
      catch ( NumberFormatException nfe )
      {
        JOptionPane.showMessageDialog(getGraphController().getGraphWindow(),
                                      "The number of rows and columns for the grid must be integers greater than 1 separated by a comma",
                                      "Unable to switch to grid mode", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public boolean isInResizeMode() { return listener.isResizeListener(); }

  public String getModeString() { return listener.getModeString(); }

  public String getShowString() { return getGraph().getShowString(); }

  public void allowNodeSelection(int numSelectionsAllowed)
  {
    listener.allowNodeSelection(numSelectionsAllowed);
  }

  public void allowTriangleSelection()
  {
    listener.allowTriangleSelection();
  }

  public Node[] getSpecialNodeSelections()
  {
    Node specialNodeSelections[] = new Node[listener.getSpecialNodeSelections().size()];
    listener.getSpecialNodeSelections().toArray(specialNodeSelections);
    return specialNodeSelections;
  }

  private void initShapes()
  {
    lineToDraw = null;
    pointToDraw = null;
    rectangleToDraw = null;
    polygonToDraw = null;
    curveToDraw = null;
  }

  public void setGraph(Graph g)
  {
    listener.setGraph(g);
    setPreferredSize();
  }

  public Graph getGraph() { return listener.getGraph(); }

  public GraphController getGraphController() { return listener.getGraphController(); }

  public void setPreferredSize()
  {
    Rectangle2D.Double bounds = getGraph().getBounds(2*DRAW_BUFFER, 2*DRAW_BUFFER);
    int gridWidth = getGraph().getGridWidth() + 2*DRAW_BUFFER;
    int gridHeight = getGraph().getGridHeight() + 2*DRAW_BUFFER;
    Dimension dim = getPreferredSize();
    if ( getGraph().getDrawGrid() && (gridWidth > (int)bounds.getMaxX() ||
                                      gridHeight > (int)bounds.getMaxY()) )
    {
      if ( gridWidth != dim.width || gridHeight != dim.height )
      {
        setPreferredSize(new Dimension( gridWidth, gridHeight ));
      }
    }
    else
    {
      if ( bounds.getMaxX() != dim.width || bounds.getMaxY() != dim.height )
      {
        setPreferredSize(new Dimension( (int)(bounds.getMaxX()),
                                        (int)(bounds.getMaxY()) ));
      }
    }
  }

  public int getDrawWidth() { return getWidth() - 2*DRAW_BUFFER; }

  public int getDrawHeight() { return getHeight() - 2*DRAW_BUFFER; }

  public void saveImage(String fileName)
  {
    BufferedImage imageToSave = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
    paint(imageToSave.getGraphics());
    if (imageToSave != null)
    {
      try
      {
        FileOutputStream out = new FileOutputStream(fileName);
        try
        {
          if (fileName.substring(fileName.lastIndexOf('.')+1).equalsIgnoreCase(Utils.gif))
          {
            GIFOutputStream.writeGIF(out, imageToSave);
          }
          else
          {
  //tvo
	      //        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
     //       JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(imageToSave);
     //       param.setQuality(1.0f, false);
      //      encoder.setJPEGEncodeParam(param);
       //     encoder.encode(imageToSave);
          }
          out.close();
        }
        catch (IOException ioe)
        {
          System.out.println("Error writing image to file");
          ioe.printStackTrace();
        }
      }
      catch (FileNotFoundException fnfe)
      {
        System.out.println("Could not locate the file to save the image to!\n");
        fnfe.printStackTrace();
      }
    }
  }

  // Add the event handlers to the canvas.
  private void addEventHandlers()
  {
    addMouseListener(listener);
    addMouseMotionListener(listener);
    addKeyListener(listener);
    if ( listener instanceof FocusListener )
    {
      addFocusListener((FocusListener)listener);
    }
    if ( listener instanceof AncestorListener )
    {
      addAncestorListener((AncestorListener)listener);
    }
  }

  // Remove the event handlers from the canvas.
  private void removeEventHandlers()
  {
    removeMouseListener(listener);
    removeMouseMotionListener(listener);
    removeKeyListener(listener);
    if ( listener instanceof FocusListener )
    {
      removeFocusListener((FocusListener)listener);
    }
    if ( listener instanceof AncestorListener )
    {
      removeAncestorListener((AncestorListener)listener);
    }
  }

  public void setLineToDraw( Line2D.Double lineToDraw )
  {
    this.lineToDraw = lineToDraw;
  }

  public void setLineToDrawColor(Color aColor)
  {
    lineToDrawColor = aColor;
  }

  public void setPointToDraw( Ellipse2D.Double pointToDraw )
  {
    this.pointToDraw = pointToDraw;
  }

  public void setRectangleToDraw( Rectangle2D.Double rectangleToDraw )
  {
    this.rectangleToDraw = rectangleToDraw;
  }

  public Rectangle2D.Double getRectangleToDraw() { return rectangleToDraw; }

  public void setPolygonToDraw( Polygon polygonToDraw )
  {
    this.polygonToDraw = polygonToDraw;
  }

  public Polygon getPolygonToDraw() { return polygonToDraw; }

  public void setPolygonToDrawColor(Color aColor)
  {
    polygonToDrawColor = aColor;
  }

  public void setCurveToDraw( QuadCurve2D.Double curveToDraw )
  {
    this.curveToDraw = curveToDraw;
  }

  public QuadCurve2D.Double getCurveToDraw() { return curveToDraw; }

  public void setCurveToDrawColor(Color aColor)
  {
    curveToDrawColor = aColor;
  }

  public void redo()
  {
    getGraph().redo();
    //setDrawGrid(getGraph().peekUndo() != null && getGraph().peekUndo().getTitle().equals("Display Schnyder Embedding"));
    updateShapes();
    repaint();
  }

  public void undo()
  {
    getGraph().undo();
    //setDrawGrid(getGraph().peekUndo() != null && getGraph().peekUndo().getTitle().equals("Display Schnyder Embedding"));
    updateShapes();
    repaint();
  }

  public void updateShapes()
  {
    if ( isInGridMode() )
    {
      getGraph().setDrawGrid(true);
    }
    else if ( isInResizeMode() )
    {
      if ( getGraph().getNumNodes() > 1 )
      {
        rectangleToDraw = getGraph().getBounds(2, 2);
        rectangleToDraw.setRect( rectangleToDraw.getX() + DRAW_BUFFER-1,
                                 rectangleToDraw.getY() + DRAW_BUFFER-1,
                                 rectangleToDraw.getWidth(),
                                 rectangleToDraw.getHeight() );
      }
      else
      {
        rectangleToDraw = null;
      }
      pointToDraw = null;
      getGraph().setDrawGrid(false);
    }
    else if ( isInRotateMode() )
    {
      if ( getGraph().getNumNodes() > 1 )
      {
        Location location = getGraph().getCenterPointLocation();
        pointToDraw = new Ellipse2D.Double( location.intX() - Node.RADIUS + DRAW_BUFFER,
                                            location.intY() - Node.RADIUS + DRAW_BUFFER,
                                            Node.RADIUS * 2, Node.RADIUS * 2 );
      }
      else
      {
        pointToDraw = null;
      }
      rectangleToDraw = null;
      getGraph().setDrawGrid(false);
    }
    else
    {
      pointToDraw = null;
      rectangleToDraw = null;
      getGraph().setDrawGrid(false);
    }
  }

  public void startTranslateNode(Node aNode)
  {
    nodesToRedraw = new Vector(1);
    nodesToRedraw.addElement(aNode);
    if ( aNode.getNumEdges() > 0 )
    {
      edgesToRedraw = new Vector(aNode.getNumEdges());
      edgesToRedraw.addAll(aNode.incidentEdges());
    }
    createImage(true);
  }

  public void startTranslateNodes(Vector nodes)
  {
    nodesToRedraw = nodes;
    edgesToRedraw = getGraph().getEdges(nodes);
    createImage(true);
  }

  public void stopTranslateNodes()
  {
    nodesToRedraw = null;
    edgesToRedraw = null;
    createImage(true);
  }

  public void startTranslateEdge(Edge anEdge)
  {
    edgesToRedraw = new Vector(1);
    edgesToRedraw.addElement(anEdge);
  }

  public void stopTranslateEdge()
  {
    edgesToRedraw = null;
    createImage(true);
  }

  public void startTranslate()
  {
    createImage(true);
  }

  public void translate( int dx, int dy )
  {
    getGraph().translate( dx, dy, false);
    affineTransform = AffineTransform.getTranslateInstance(
      dx + imageOffsetX + DRAW_BUFFER,
      dy + imageOffsetY + DRAW_BUFFER);
  }

  public void stopTranslate()
  {
    affineTransform = null;
    createImage(true);
  }

  public void startRotate()
  {
    createImage(true);
    angle = 0;
  }

  public void rotate( Location pivotPoint, double angle )
  {
    this.angle += angle;
    getGraph().rotate( pivotPoint, angle, false );
    affineTransform = AffineTransform.getTranslateInstance(
      imageOffsetX + DRAW_BUFFER, imageOffsetY + DRAW_BUFFER);
    affineTransform.rotate( Math.toRadians(this.angle),
                            pivotPoint.intX() - imageOffsetX,
                            pivotPoint.intY() - imageOffsetY );
  }

  public void stopRotate()
  {
    affineTransform = null;
    createImage(true);
  }

  public void startScaleTo()
  {
    oldBounds = getGraph().getBounds();
    createImage(true);
  }

  public void scaleTo( Rectangle2D.Double newBounds )
  {
    getGraph().scaleTo( newBounds, false );
    affineTransform = AffineTransform.getTranslateInstance(
      imageOffsetX + DRAW_BUFFER, imageOffsetY + DRAW_BUFFER);
    affineTransform.scale(
      newBounds.width / oldBounds.width,
      newBounds.height / oldBounds.height );
  }

  public void stopScaleTo()
  {
    affineTransform = null;
    getGraph().refreshEdgeCurves();
    createImage(true);
  }

  private void createImage(boolean forceCreation)
  {
    Graph g = getGraph();
    createImage( g, g.getBounds(), forceCreation );
  }

  private void createImage( Graph aGraph, Rectangle2D.Double bounds,
                            boolean forceCreation)
  {
    if ( aGraph.hasChangedSinceLastDraw() || forceCreation )
    {
      bufferedImage = new BufferedImage( (int)bounds.getWidth() + 2*(Node.RADIUS+2)+200,
                                         (int)bounds.getHeight() + 2*(Node.RADIUS+2)+20,
                                         BufferedImage.TYPE_INT_ARGB );
      imageOffsetX = (int)(bounds.getMinX()-Node.RADIUS-2);
      imageOffsetY = (int)(bounds.getMinY()-Node.RADIUS-2);
      Graphics2D g2i = bufferedImage.createGraphics();
      g2i.setColor(new Color(255,255,255,0));
      g2i.fillRect( 0, 0,
                    bufferedImage.getWidth(),
                    bufferedImage.getHeight() );

      if ( nodesToRedraw != null )
      {
        for ( int i=0; i<nodesToRedraw.size(); i++ )
        {
          ((Node)nodesToRedraw.elementAt(i)).setIsVisible(false);
        }
      }
      if ( edgesToRedraw != null )
      {
        for ( int i=0; i<edgesToRedraw.size(); i++ )
        {
          ((Edge)edgesToRedraw.elementAt(i)).setIsVisible(false);
        }
      }

      aGraph.draw(g2i, -1*imageOffsetX, -1*imageOffsetY);

      if ( nodesToRedraw != null )
      {
        for ( int i=0; i<nodesToRedraw.size(); i++ )
        {
          ((Node)nodesToRedraw.elementAt(i)).setIsVisible(true);
        }
      }
      if ( edgesToRedraw != null )
      {
        for ( int i=0; i<edgesToRedraw.size(); i++ )
        {
          ((Edge)edgesToRedraw.elementAt(i)).setIsVisible(true);
        }
      }
    }
  }

  /**
   * Draw the current the current graph plus the outline of any potential
   * edges being added to the screen.
   *
   * @param Graphics aPen: The Graphics object to draw with.
   */
  public void paint(Graphics aPen)
  {
    if ( getGraph().logChangedSinceLastDraw() )
    {
      logWindow.update();
    }
    super.paint(aPen);
    Graphics2D g2 = (Graphics2D)aPen;
    Graph aGraph = listener.getGraph();
    Rectangle2D.Double bounds = aGraph.getBounds();
    g2.setColor(backgroundColor);
    g2.fillRect( DRAW_BUFFER, DRAW_BUFFER,
                 getWidth()-2*DRAW_BUFFER,
                 getHeight()-2*DRAW_BUFFER );
    if ( getGraph().getDrawGrid() )
    {
      getGraph().drawGrid(g2, DRAW_BUFFER, DRAW_BUFFER);
    }
    createImage(aGraph, bounds, false);
    if ( affineTransform == null )
    {
      g2.drawImage( bufferedImage, imageOffsetX + DRAW_BUFFER,
                                   imageOffsetY + DRAW_BUFFER, this);
    }
    else
    {
      g2.drawImage( bufferedImage, affineTransform, this);
    }

    if ( edgesToRedraw != null )
    {
      for ( int i=0; i<edgesToRedraw.size(); i++ )
      {
        ((Edge)edgesToRedraw.elementAt(i)).draw( g2, DRAW_BUFFER, DRAW_BUFFER,
                                                 aGraph.getDrawSelected() );
      }
    }
    if ( nodesToRedraw != null )
    {
      for ( int i=0; i<nodesToRedraw.size(); i++ )
      {
        ((Node)nodesToRedraw.elementAt(i)).draw( g2, DRAW_BUFFER, DRAW_BUFFER,
                                                 aGraph.getDrawSelected(),
                                                 aGraph.getShowCoords(),
                                                 aGraph.getShowLabels() );
      }
    }

    if ( rectangleToDraw != null )
    {
      g2.setStroke(new BasicStroke( (float)Edge.THICKNESS ));
      g2.setColor(Color.green);
      g2.draw( rectangleToDraw );
    }
    if ( polygonToDraw != null )
    {
      g2.setStroke(new BasicStroke( (float)Edge.THICKNESS ));
      g2.setColor(polygonToDrawColor);
      g2.fill( polygonToDraw );
    }
    if ( curveToDraw != null )
    {
      g2.setStroke(new BasicStroke( (float)Edge.THICKNESS ));
      g2.setColor(curveToDrawColor);
      g2.draw( curveToDraw );
    }
    if ( lineToDraw != null )
    {
      if ( lineToDraw.getX2() < DRAW_BUFFER ||
           lineToDraw.getX2() > getWidth() - DRAW_BUFFER ||
           lineToDraw.getY2() < DRAW_BUFFER ||
           lineToDraw.getY2() > getHeight() - DRAW_BUFFER )
      {
        float dash1[] = {(float)Edge.GENERATED_DASH_LENGTH};
        g2.setStroke(new BasicStroke( (float)Edge.THICKNESS,
                                      BasicStroke.CAP_BUTT,
                                      BasicStroke.JOIN_MITER,
                                      10.0f, dash1, 0.0f ));
      }
      else
      {
        g2.setStroke(new BasicStroke( (float)Edge.THICKNESS ));
      }
      g2.setColor( lineToDrawColor );
      g2.draw( lineToDraw );
    }
    if ( pointToDraw != null )
    {
      g2.setStroke(new BasicStroke( (float)Node.LINE_THICKNESS ));
      g2.setColor(Color.green);
      g2.draw( pointToDraw );
    }
    revalidate();
  }

  public void update()
  {
    updateShapes();
    infoWindow.update();
    setPreferredSize();
    repaint();
  }

  public void prepareForClose()
  {
    listener.prepareForClose();
  }
}