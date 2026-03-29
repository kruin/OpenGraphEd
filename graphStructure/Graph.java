package graphStructure;

import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.geom.*;

import dataStructure.DoublyLinkedList;
import graphStructure.mementos.*;

/**
 * This class represents the model of the graph object, that stores all the data
 * that makes up the structure of the graph, and methods to perform operations
 * on this data such as saving to and loading from a file, adding nodes or edges
 * etc...
 *
 * @author Jon Harris
 */
public class Graph
{
  private String    label;
  private Vector    nodes;
  private boolean drawSelected;
  private Color drawColor;
  private String filePath;
  private DoublyLinkedList mementos;
  private MementoGrouper currentMemento;
  private boolean showCoords;
  private boolean showLabels;
  private boolean trackUndos;
  private boolean hasChangedSinceLastSave;
  private boolean hasChangedSinceLastDraw;
  private boolean logChangedSinceLastDraw;
  private Vector logEntries;
  private LogEntry currentLogEntry;
  private Graph parent;
  private boolean drawGrid;
  private int gridRows;
  private int gridCols;
  private int gridColWidth;
  private int gridRowHeight;

  /**
   * Constructor for class Graph.
   */
  public Graph()
  {
    label = "";
    nodes = new Vector();
    drawColor = null;
    drawSelected = true;
    filePath = "";
    mementos = new DoublyLinkedList();
    currentMemento = null;
    trackUndos = true;
    hasChangedSinceLastSave = false;
    hasChangedSinceLastDraw = true;
    showCoords = false;
    showLabels = true;
    logEntries = new Vector();
    currentLogEntry = null;
    parent = null;
    drawGrid = false;
    gridRows = -1;
    gridCols = -1;
  }

  /**
   * Constructor for class Graph that assigns a label to the Graph.
   *
   * @param String aLabel: The label to assign to the Graph.
   */
  public Graph(String aLabel)
  {
    this();
    label = aLabel;
  }

  /**
   * Constructor for class Graph that assigns a label to the Graph and
   * provides the initial node set for the Graph.
   *
   * @param String aLabel: The label to assign to the Graph.
   * @param Vector initialNodes: A Vector containing the initial Nodes for the Graph.
   */
  public Graph(String aLabel, Vector initialNodes)
  {
    this();
    label = aLabel;
    nodes = initialNodes;
  }

  public Graph(Graph aGraph)
  {
    parent = aGraph;
    label = new String(aGraph.label);
    drawSelected = aGraph.drawSelected;
    trackUndos = aGraph.trackUndos;
    hasChangedSinceLastSave = aGraph.hasChangedSinceLastSave;
    hasChangedSinceLastDraw = true;
    if ( aGraph.drawColor == null )
    {
      drawColor = null;
    }
    else
    {
      drawColor = new Color(aGraph.drawColor.getRGB());
    }
    filePath = new String(aGraph.filePath);
    nodes = new Vector();
    mementos = new DoublyLinkedList();
    currentMemento = null;
    showCoords = aGraph.showCoords;
    showLabels = aGraph.showLabels;
    logEntries = new Vector();
    currentLogEntry = null;
  }

  public String getShowString()
  {
    if ( showCoords )
    {
      return "Show Coordinates";
    }
    else
    {
      if ( showLabels )
      {
        return "Show Labels";
      }
      else
      {
        return "Show Nothing";
      }
    }
  }
  
  public void shareMementos(Graph aGraph) { mementos = aGraph.mementos; }

  public void setTrackUndos(boolean tu) { trackUndos = tu; initUndo(); }

  private void initUndo()
  {
    mementos = new DoublyLinkedList();
    currentMemento = null;
  }
  
  public boolean getTrackUndos() { return trackUndos; }

  public void setDrawSelected(boolean draw) { drawSelected = draw; }

  public boolean getDrawSelected() { return drawSelected; }

  public void setDrawColor(Color aColor) { drawColor = aColor; }

  public Color getDrawColor() { return drawColor; }

  public void setShowCoords(boolean c)
  {
    showCoords = c;
    hasChangedSinceLastDraw = true;
  }

  public boolean getShowCoords() { return showCoords; }

  public void setShowLabels(boolean l)
  {
    showLabels = l;
    hasChangedSinceLastDraw = true;
  }

  public boolean getShowLabels() { return showLabels; }

  public String getFilePath() { return filePath; }

  public String getFileName()
  {
    if ( filePath.indexOf('\\') == -1 )
    {
      return filePath.substring( 0, filePath.lastIndexOf('.') );
    }
    else
    {
      return filePath.substring( filePath.lastIndexOf('\\')+1, filePath.lastIndexOf('.') );
    }
  }

  public void setFilePath(String fp) { filePath = fp; }

  public boolean hasChangedSinceLastSave() { return hasChangedSinceLastSave; }

  public boolean hasChangedSinceLastDraw() { return hasChangedSinceLastDraw; }

  public boolean logChangedSinceLastDraw() { return logChangedSinceLastDraw; }

  public void markForRepaint() { hasChangedSinceLastDraw = true; }

  private int getRowHeight(int numRows, int height)
  {
    if ( numRows <= 1 )
    {
      numRows = 2;
    }
    return Math.max(1, height / (numRows-1)); 
  }

  private int getColWidth(int numCols, int width)
  {
    if ( numCols <= 1 )
    {
      numCols = 2;
    }
    return Math.max(1, width / (numCols-1));
  }

  public void setGrid(int numRows, int rowHeight, int numCols, int colWidth,
                      boolean addMemento)
  {
    if ( addMemento && currentMemento != null && trackUndos )
    {
      currentMemento.addMemento(GridSizeMemento.createGridSizeMemento(this));
    }
    if ( numRows <= 1 )
    {
      numRows = 2;
    }
    if ( numCols <= 1 )
    {
      numCols = 2;
    }
    gridRows = numRows;
    gridRowHeight = Math.max(1, rowHeight);
    gridCols = numCols;
    gridColWidth = Math.max(1, colWidth);
  }

  public void setGridArea(int numRows, int height, int numCols, int width,
                          boolean addMemento)
  {
    if ( addMemento && currentMemento != null && trackUndos )
    {
      currentMemento.addMemento(GridSizeMemento.createGridSizeMemento(this));
    }
    if ( numRows <= 1 )
    {
      numRows = 2;
    }
    gridRows = numRows;
    gridRowHeight = getRowHeight(numRows, height);
    if ( numCols <= 1 )
    {
      numCols = 2;
    }
    gridCols = numCols;
    gridColWidth = getColWidth(numCols, width);
  }

  public int getGridRows() { return gridRows; }

  public int getGridCols() { return gridCols; }

  public int getGridColWidth() { return gridColWidth; }

  public int getGridRowHeight() { return gridRowHeight; }

  public void setDrawGrid(boolean draw) { drawGrid = draw; }

  public boolean getDrawGrid()
  {
    return drawGrid &&
           gridRows >= 2 &&
           gridCols >= 2 &&
           gridRowHeight >= 1 &&
           gridColWidth >= 1;
  }

  public int getGridHeight()
  {
    if ( gridRows < 2 || gridRowHeight < 1 )
    {
      return 0;
    }
    return (gridRows-1)*gridRowHeight;
  }

  public int getGridWidth()
  {
    if ( gridCols < 2 || gridColWidth < 1 )
    {
      return 0;
    }
    return (gridCols-1)*gridColWidth;
  }

  public void drawGrid(Graphics2D g2, int xOffset, int yOffset)
  {
    if ( !getDrawGrid() )
    {
      return;
    }

    int x = 0 + xOffset;
    int y = 0 + yOffset;
    g2.setColor(Color.gray); 
    for ( int i=0; i<gridRows; i++ )
    {
      y = i*gridRowHeight + yOffset;
      g2.drawLine(xOffset, y, gridColWidth*(gridCols-1) + xOffset, y);
    }
    for ( int i=0; i<gridCols; i++ )
    {
      x = i*gridColWidth + xOffset;
      g2.drawLine(x, yOffset, x, gridRowHeight*(gridRows-1) + yOffset);
    }
  }

  public Location getClosestGridLocation(Location location)
  {
    int safeGridRowHeight = Math.max(1, gridRowHeight);
    int safeGridColWidth = Math.max(1, gridColWidth);
    int safeGridRows = Math.max(2, gridRows);
    int safeGridCols = Math.max(2, gridCols);

    int row = (int)Math.round(location.doubleY() / safeGridRowHeight);
    int col = (int)Math.round(location.doubleX() / safeGridColWidth);
    if ( row < 0 )
    {
      row = 0;
    }
    if ( col < 0 )
    {
      col = 0;
    }
    if ( row > safeGridRows-1 )
    {
      row = safeGridRows-1;
    }
    if ( col > safeGridCols-1 )
    {
      col = safeGridCols-1;
    }
    return new Location( col*safeGridColWidth, row*safeGridRowHeight);
  }

  public boolean isOnGrid(Location location)
  {
    if ( gridColWidth < 1 || gridRowHeight < 1 )
    {
      return false;
    }
    return location.intX() % gridColWidth == 0 &&
           location.intY() % gridRowHeight == 0;
  }

  public void newMemento(String title)
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    if ( trackUndos )
    {
      if ( currentMemento != null )
      {
        mementos.enqueueAfterCurrent(currentMemento);
      }      
      currentMemento = new MementoGrouper(title);
    }
  }

  public void renameMemento(String title)
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    if ( trackUndos )
    {
      currentMemento.setTitle(title);
    }
  }

  public void doneMemento()
  {
    if ( trackUndos )
    {
      if ( currentMemento != null )
      {
        currentMemento.removeUselessMementos();
        if ( currentMemento.size() > 0 )
        {
          mementos.enqueueAfterCurrent(currentMemento);
        }
        currentMemento = null;
      }
    }
  }

  public void undoMemento()
  {
    if ( currentMemento != null )
    {
      currentMemento.apply(this);
    }
  }
  
  public void abortMemento()
  {
    if ( trackUndos )
    {
      currentMemento = null;
    }
  }

  public boolean hasMoreUndos()
  {
    return mementos.getCurrent() != null;
  }

  public void undo()
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    if ( trackUndos )
    {
      if ( mementos.getCurrent() != null )
      {
        ((MementoGrouper)mementos.getCurrent()).apply(this);
        mementos.toPrev();
      }
      else
      {
        mementos.toHead();
      }
    }
  }

  public MementoGrouper peekUndo()
  {
    return (MementoGrouper)mementos.getCurrent();
  }

  public boolean hasMoreRedos()
  {
    return mementos.hasNext();
  }

  public void redo()
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    if ( trackUndos )
    {
      if ( mementos.hasNext() )
      {
        mementos.toNext();
        ((MementoGrouper)mementos.getCurrent()).apply(this);
      }
      else
      {
        mementos.toTail();
      }
    }
  }

  public MementoGrouper peekRedo()
  {
    if ( mementos.hasNext() )
    {
      mementos.toNext();
      MementoGrouper mg = (MementoGrouper)mementos.getCurrent();
      mementos.toPrev();
      return mg;
    }
    else
    {
      return null;
    }
  }
  
  public String getLogString()
  {
    String returnString = "";
    for ( int i=0; i<logEntries.size(); i++ )
    {
      returnString+= "Log Entry " + (i+1) + 
                     "---------------------" +
                     ((LogEntry)logEntries.elementAt(i)).infoString() +
                     "---------------------\n\n";
    }
    return returnString;
  }
  
  public Vector getLogEntries() { return logEntries; }
  
  public LogEntry startLogEntry(String operationName)
  {
    if ( parent != null )
    {
      LogEntry newEntry = new LogEntry(operationName, this, System.currentTimeMillis());
      parent.startLogEntry(newEntry);
      return newEntry;
    }
    else
    {
      LogEntry newEntry = new LogEntry(operationName, this, System.currentTimeMillis());
      if ( currentLogEntry == null )
      {
        logEntries.addElement(newEntry);
      }
      else
      {
        currentLogEntry.addSubEntry(newEntry);
      }
      currentLogEntry = newEntry;
      return newEntry;
    }
  }
  
  private void startLogEntry(LogEntry logEntry)
  {
    if ( parent != null )
    {
      parent.startLogEntry(logEntry);
    }
    else
    {
      if ( currentLogEntry == null )
      {
        logEntries.addElement(logEntry);
      }
      else
      {
        currentLogEntry.addSubEntry(logEntry);
      }
      currentLogEntry = logEntry;
    }
  }
  
  public void stopLogEntry(LogEntry logEntry)
  {
    if ( parent != null )
    {
      parent.stopLogEntry(logEntry);
      return;
    }
    else
    {
      logChangedSinceLastDraw = true;
    }
    logEntry.updateTimeTaken(System.currentTimeMillis());
    currentLogEntry = logEntry.getParentEntry();
  }
  
  public Rectangle2D.Double getBounds()
  {
    return getBounds( 0, 0 );
  }

  public Rectangle2D.Double getBounds( int xAdd, int yAdd )
  {
    return getBounds( nodes, xAdd, yAdd );
  }

  public Rectangle2D.Double getBounds( Vector someNodes )
  {
    return getBounds( someNodes, 0, 0 );
  }

  public Rectangle2D.Double getBounds( Vector someNodes, int xAdd, int yAdd )
  {
    double minX = 0;
    double minY = 0;
    double maxX = 0;
    double maxY = 0;
    if ( !someNodes.isEmpty() )
    {
      Location aLocation = ((Node)someNodes.firstElement()).getLocation();
      maxX = minX = aLocation.doubleX();
      maxY = minY = aLocation.doubleY();

      for ( int i=1; i<someNodes.size(); i++ )
      {
        aLocation = ((Node)someNodes.elementAt(i)).getLocation();
        if ( aLocation.doubleX() < minX )
        {
          minX = aLocation.doubleX();
        }
        else if ( aLocation.doubleX() > maxX )
        {
          maxX = aLocation.doubleX();
        }
        if ( aLocation.doubleY() < minY )
        {
          minY = aLocation.doubleY();
        }
        else if ( aLocation.doubleY() > maxY )
        {
          maxY = aLocation.doubleY();
        }
      }

      Vector edges = getCurvedEdges(someNodes);

      Rectangle2D rect;
      for ( int i=0; i<edges.size(); i++ )
      {
        //aLocation = ((Edge)edges.elementAt(i)).getCenterLocation();

        rect = ((Edge)edges.elementAt(i)).getQuadCurve().getBounds2D();

        if ( rect.getMinX() < minX )
        {
          minX = rect.getMinX();
        }
        if ( rect.getMaxX() > maxX )
        {
          maxX = rect.getMaxX();
        }
        if ( rect.getMinY() < minY )
        {
          minY = rect.getMinY();
        }
        if ( rect.getMaxY() > maxY )
        {
          maxY = rect.getMaxY();
        }
      }
    }
    return new Rectangle2D.Double( minX, minY,
                                   maxX - minX + xAdd,
                                   maxY - minY + yAdd );
  }

  public Location getCenterPointLocation()
  {
    Node currentNode;
    int xAcc = 0;
    int yAcc = 0;

    if ( nodes.size() > 0 )
    {
      for ( int i=0; i<nodes.size(); i++ )
      {
        currentNode = (Node)nodes.elementAt(i);
        xAcc+= currentNode.getX();
        yAcc+= currentNode.getY();
      }
      return new Location(xAcc/nodes.size(), yAcc/nodes.size());
    }
    else
    {
      return new Location(0, 0);
    }
  }

  public static Node partitionAroundMedianX(Vector pNodes, Vector lesser, Vector greater)
  {
    Node mNode = getMedianXNode(pNodes);
    Node currentNode;

    if ( pNodes.size() > 1 )
    {
      if ( pNodes.size() %2 == 1 )
      {
        lesser.ensureCapacity(pNodes.size()/2);
        greater.ensureCapacity(pNodes.size()/2);
        lesser.removeAllElements();
        greater.removeAllElements();
      }
      else
      {
        lesser.ensureCapacity(pNodes.size()/2);
        greater.ensureCapacity(pNodes.size()/2-1);
        lesser.removeAllElements();
        greater.removeAllElements();
      }

      for ( int i=0; i<pNodes.size(); i++ )
      {
        currentNode = (Node)pNodes.elementAt(i);
        if ( currentNode.getX() < mNode.getX() )
        {
          lesser.addElement(currentNode);
        }
        else if ( currentNode.getX() > mNode.getX() )
        {
          greater.addElement(currentNode);
        }
      }

      for ( int i=0; i<pNodes.size(); i++ )
      {
        currentNode = (Node)pNodes.elementAt(i);
        if ( currentNode != mNode && currentNode.getX() == mNode.getX() )
        {
          if ( lesser.size() < pNodes.size()/2 )
          {
            lesser.addElement(currentNode);
          }
          else
          {
            greater.addElement(currentNode);
          }
        }
      }
    }
    return mNode;
  }

  public Node getMedianXNode()
  {
    return getMedianXNode(nodes);
  }

  public static Node getMedianXNode(Vector sNodes)
  {
    if ( sNodes.size() > 0 )
    {
      Node mNodes[] = new Node[sNodes.size()];
      sNodes.toArray(mNodes);
      return quickSelectX(mNodes, mNodes.length/2);
    }
    return null;
  }

  private static Node quickSelectX(Node mNodes[], int k)
  {
    if ( mNodes.length == 1 )
    {
      return mNodes[0];
    }
    Node medianNode = getMedianOfMediansXNode(mNodes);
    int lesserCount= 0, equalCount = 0, greaterCount = 0;
    for ( int i=0; i<mNodes.length; i++ )
    {
      if ( mNodes[i].getX() < medianNode.getX() )
      {
        lesserCount++;
      }
      else if ( mNodes[i].getX() > medianNode.getX() )
      {
        greaterCount++;
      }
    }
    Node lesserNodes[] = new Node[lesserCount];
    Node equalNodes[] = new Node[mNodes.length-lesserCount-greaterCount];
    Node greaterNodes[] = new Node[greaterCount];
    lesserCount = equalCount = greaterCount = 0;
    for ( int i=0; i<mNodes.length; i++ )
    {
      if ( mNodes[i].getX() < medianNode.getX() )
      {
        lesserNodes[lesserCount++] = mNodes[i];
      }
      else if ( mNodes[i].getX() > medianNode.getX() )
      {
        greaterNodes[greaterCount++] = mNodes[i];
      }
      else
      {
        equalNodes[equalCount++] = mNodes[i];
      }
    }
    if ( k < lesserNodes.length )
    {
      return quickSelectX(lesserNodes, k);
    }
    else if ( k < lesserNodes.length + equalNodes.length )
    {
      return medianNode;
    }
    else
    {
      return quickSelectX(greaterNodes, k - lesserNodes.length - equalNodes.length);
    }
  }

  public static Node getMedianOfMediansXNode(Node mNodes[])
  {
    do
    {
      mNodes = findMediansX(mNodes);
    }
    while ( mNodes.length != 1 );
    return mNodes[0];
  }

  private static Node[] findMediansX(Node mNodes[])
  {
    Node medians[];
    int leftOver = mNodes.length % 5;
    Node temp[] = new Node[5];
    Node currentNode, switcherNode;
    int i = 0, j = 0, m = 0;
    if ( leftOver == 0 )
    {
      medians = new Node[mNodes.length/5];
    }
    else
    {
      medians = new Node[mNodes.length/5+1];
    }
    for ( i=0, m=0; i<mNodes.length; i++ )
    {
      if ( i > 0 && i % 5 == 0 )
      {
        medians[m++] = temp[2];
      }

      currentNode = mNodes[i];
      switcherNode = null;

      for ( j=0; j<i%5; j++ )
      {
        if ( temp[j].getX() > currentNode.getX() )
        {
          switcherNode = temp[j];
          break;
        }
      }
      temp[j] = currentNode;
      if ( switcherNode != null )
      {
        for ( ; j<i%5; j++ )
        {
          currentNode = temp[j+1];
          temp[j+1] = switcherNode;
          switcherNode = currentNode;
        }
      }

    }
    if ( leftOver != 0 )
    {
      medians[m] = temp[leftOver/2];
    }
    else
    {
      medians[m] = temp[2];
    }
    return medians;
  }

  public static Node partitionAroundMedianY(Vector pNodes, Vector lesser, Vector greater)
  {
    Node mNode = getMedianYNode(pNodes);
    Node currentNode;

    if ( pNodes.size() > 1 )
    {
      if ( pNodes.size() %2 == 1 )
      {
        lesser.ensureCapacity(pNodes.size()/2);
        greater.ensureCapacity(pNodes.size()/2);
        lesser.removeAllElements();
        greater.removeAllElements();
      }
      else
      {
        lesser.ensureCapacity(pNodes.size()/2);
        greater.ensureCapacity(pNodes.size()/2-1);
        lesser.removeAllElements();
        greater.removeAllElements();
      }

      for ( int i=0; i<pNodes.size(); i++ )
      {
        currentNode = (Node)pNodes.elementAt(i);
        if ( currentNode.getY() < mNode.getY() )
        {
          lesser.addElement(currentNode);
        }
        else if ( currentNode.getY() > mNode.getY() )
        {
          greater.addElement(currentNode);
        }
      }

      for ( int i=0; i<pNodes.size(); i++ )
      {
        currentNode = (Node)pNodes.elementAt(i);
        if ( currentNode != mNode && currentNode.getY() == mNode.getY() )
        {
          if ( lesser.size() < pNodes.size()/2 )
          {
            lesser.addElement(currentNode);
          }
          else
          {
            greater.addElement(currentNode);
          }
        }
      }
    }
    return mNode;
  }

  public Node getMedianYNode()
  {
    return getMedianYNode(nodes);
  }

  public static Node getMedianYNode(Vector sNodes)
  {
    if ( sNodes.size() > 0 )
    {
      Node mNodes[] = new Node[sNodes.size()];
      sNodes.toArray(mNodes);
      return quickSelectY(mNodes, mNodes.length/2);
    }
    return null;
  }

  private static Node quickSelectY(Node mNodes[], int k)
  {
    if ( mNodes.length == 1 )
    {
      return mNodes[0];
    }
    Node medianNode = getMedianOfMediansYNode(mNodes);
    int lesserCount= 0, equalCount = 0, greaterCount = 0;
    for ( int i=0; i<mNodes.length; i++ )
    {
      if ( mNodes[i].getY() < medianNode.getY() )
      {
        lesserCount++;
      }
      else if ( mNodes[i].getY() > medianNode.getY() )
      {
        greaterCount++;
      }
    }
    Node lesserNodes[] = new Node[lesserCount];
    Node equalNodes[] = new Node[mNodes.length-lesserCount-greaterCount];
    Node greaterNodes[] = new Node[greaterCount];
    lesserCount = equalCount = greaterCount = 0;
    for ( int i=0; i<mNodes.length; i++ )
    {
      if ( mNodes[i].getY() < medianNode.getY() )
      {
        lesserNodes[lesserCount++] = mNodes[i];
      }
      else if ( mNodes[i].getY() > medianNode.getY() )
      {
        greaterNodes[greaterCount++] = mNodes[i];
      }
      else
      {
        equalNodes[equalCount++] = mNodes[i];
      }
    }
    if ( k < lesserNodes.length )
    {
      return quickSelectY(lesserNodes, k);
    }
    else if ( k < lesserNodes.length + equalNodes.length )
    {
      return medianNode;
    }
    else
    {
      return quickSelectY(greaterNodes, k - lesserNodes.length - equalNodes.length);
    }
  }

  public static Node getMedianOfMediansYNode(Node mNodes[])
  {
    do
    {
      mNodes = findMediansY(mNodes);
    }
    while ( mNodes.length != 1 );
    return mNodes[0];
  }

  private static Node[] findMediansY(Node mNodes[])
  {
    Node medians[];
    int leftOver = mNodes.length % 5;
    Node temp[] = new Node[5];
    Node currentNode, switcherNode;
    int i = 0, j = 0, m = 0;
    if ( leftOver == 0 )
    {
      medians = new Node[mNodes.length/5];
    }
    else
    {
      medians = new Node[mNodes.length/5+1];
    }
    for ( i=0, m=0; i<mNodes.length; i++ )
    {
      if ( i > 0 && i % 5 == 0 )
      {
        medians[m++] = temp[2];
      }

      currentNode = mNodes[i];
      switcherNode = null;

      for ( j=0; j<i%5; j++ )
      {
        if ( temp[j].getY() > currentNode.getY() )
        {
          switcherNode = temp[j];
          break;
        }
      }
      temp[j] = currentNode;
      if ( switcherNode != null )
      {
        for ( ; j<i%5; j++ )
        {
          currentNode = temp[j+1];
          temp[j+1] = switcherNode;
          switcherNode = currentNode;
        }
      }

    }
    if ( leftOver != 0 )
    {
      medians[m] = temp[leftOver/2];
    }
    else
    {
      medians[m] = temp[2];
    }
    return medians;
  }
  
  public Graph copy()
  {
    return copyNodes(nodes);
  }
  
  /**
   * Returns a copy of the graph, maintaining the order of edges at each node.  
   * @param multiLevel - If true, nodes and edges will set their
   * copy field to the value of the copy field of the node or edge they are copying.
   * @return The copy of the graph
   */
  public Graph copy( boolean keepReferences )
  {
    return copyNodes(nodes, keepReferences);
  }


  public Graph copyNodes(Vector nodeVector)
  {
    return copyNodes( nodeVector, false );
  }

  // copies all nodes in the nodeVector and ALL of their edges.
  public Graph copyNodes( Vector nodeVector, boolean keepReferences )
  {
    Graph newGraph = new Graph(this);
    Vector incidentEdges;
    Node newNode, currentNode;
    Edge newEdge, currentEdge;
    Vector sourceEdges = getEdges(nodeVector);
    // Vectors to maintain original copy references of nodes and edges of input graph.
    // These references are borrowed to allow dual access between copy and source.
    Vector copyNodes = new Vector(nodes.size());
    Vector copyEdges = new Vector(sourceEdges.size());
    
    for ( int i=0; i<nodeVector.size(); i++ )
    {
      currentNode = (Node)nodeVector.elementAt(i);
      copyNodes.addElement(currentNode.getCopy());
      newNode = new Node(currentNode);
      newNode.setCopy(currentNode);
      currentNode.setCopy(newNode);
      newGraph.addNode(newNode);
    }

    for ( int i=0; i<sourceEdges.size(); i++ )
    {
      currentEdge = (Edge)sourceEdges.elementAt(i);
      copyEdges.addElement(currentEdge.getCopy());
      if ( currentEdge.getDirectedSourceNode() != null )
      {
        newEdge = new Edge( currentEdge, currentEdge.getDirectedSourceNode().getCopy(),
            currentEdge.getStartNode().getCopy(), currentEdge.getEndNode().getCopy() );
      }
      else
      {
        newEdge = new Edge( currentEdge, null,
            currentEdge.getStartNode().getCopy(), currentEdge.getEndNode().getCopy() );
      }
      newEdge.setCopy(currentEdge);
      currentEdge.setCopy(newEdge);
    }

    for ( int i=0; i<nodeVector.size(); i++ )
    {
      currentNode = (Node)nodeVector.elementAt(i);
      incidentEdges = currentNode.incidentEdges();
      for ( int j=0; j<incidentEdges.size(); j++ )
      {
        currentEdge = (Edge)incidentEdges.elementAt(j);
        currentNode.getCopy().addIncidentEdgeNoCheck(currentEdge.getCopy());
      }
    }

    for ( int i=0; i<nodeVector.size(); i++ )
    {
      currentNode = (Node)nodeVector.elementAt(i);
      if ( !keepReferences )
      {
        currentNode.getCopy().setCopy(null);
      }
      currentNode.setCopy((Node)copyNodes.elementAt(i));
    }

    for ( int i=0; i<sourceEdges.size(); i++ )
    {
      currentEdge = (Edge)sourceEdges.elementAt(i);
      if ( !keepReferences )
      {
        currentEdge.getCopy().setCopy(null);
      }
      currentEdge.setCopy((Edge)copyEdges.elementAt(i));
    }
    return newGraph;
  }

  public Graph copyNode(Node aNode)
  {
    return copyNode( aNode, false , false );
  }

  public Graph copyNode( Node aNode, boolean keepCopyReferences )
  {
    return copyNode( aNode, keepCopyReferences , false );
  }

  public Graph copyNode(Node aNode, boolean keepCopyReferences,
                        boolean updateCopyReferences)
  {
    Graph newGraph = new Graph(this);
    Node newNode = new Node(aNode);
    if ( updateCopyReferences )
    {
      newNode.setCopy(aNode.getCopy());
    }
    else
    {
      newNode.setCopy(aNode);
    }
    newGraph.addNode(newNode);
    if ( !keepCopyReferences )
    {
      newNode.setCopy(null);
    }
    return newGraph;
  }

  // copies all of the edges in the vector and any related nodes.
  
  public Graph copyEdges( Vector edges )
  {
    return copyEdges( edges, false );
  }

  public Graph copyEdges( Vector edges, boolean keepCopyReferences )
  {
    Graph newGraph = new Graph(this);
    Node newNode, currentNode;
    Edge newEdge, currentEdge;
    Vector sourceNodes = new Vector(2*edges.size());
    Vector copyNodes;
    Vector newEdges;

    for ( int j=0; j<edges.size(); j++ )
    {
      currentEdge = (Edge)edges.elementAt(j);
      currentEdge.setIsAdded(false);
      ((Node)currentEdge.getStartNode()).setIsAdded(false);
      ((Node)currentEdge.getEndNode()).setIsAdded(false);
    }

    for ( int j=0; j<edges.size(); j++ )
    {
      currentEdge = (Edge)edges.elementAt(j);
      if ( !((Node)currentEdge.getStartNode()).isAdded() )
      {
        ((Node)currentEdge.getStartNode()).setIsAdded(true);
        sourceNodes.addElement(currentEdge.getStartNode());
      }
      if ( !((Node)currentEdge.getEndNode()).isAdded() )
      {
        ((Node)currentEdge.getEndNode()).setIsAdded(true);
        sourceNodes.addElement(currentEdge.getEndNode());
      }
    }

    copyNodes = new Vector(sourceNodes.size());
    for ( int i=0; i<sourceNodes.size(); i++ )
    {
      currentNode = (Node)sourceNodes.elementAt(i);
      newNode = new Node(currentNode);
      newNode.setCopy(currentNode);
      copyNodes.addElement(currentNode.getCopy());
      currentNode.setCopy(newNode);
      newGraph.addNode(newNode);
    }

    for ( int j=0; j<edges.size(); j++ )
    {
      currentEdge = (Edge)edges.elementAt(j);
      if ( !currentEdge.isAdded() )
      {
        currentEdge.setIsAdded(true);
        if ( currentEdge.getDirectedSourceNode() != null )
        {
          newEdge = new Edge( currentEdge, currentEdge.getDirectedSourceNode().getCopy(), 
              currentEdge.getStartNode().getCopy(), currentEdge.getEndNode().getCopy() );
        }
        else
        {
          newEdge = new Edge( currentEdge, null, 
              currentEdge.getStartNode().getCopy(), currentEdge.getEndNode().getCopy() );
        }
        newEdge.setCopy(currentEdge);
        currentEdge.getStartNode().getCopy().addIncidentEdgeNoCheck(newEdge);
        currentEdge.getEndNode().getCopy().addIncidentEdgeNoCheck(newEdge);
      }
    }

    for ( int i=0; i<sourceNodes.size(); i++ )
    {
      currentNode = (Node)sourceNodes.elementAt(i);
      if ( !keepCopyReferences )
      {
        currentNode.getCopy().setCopy(null);
      }
      currentNode.setCopy((Node)copyNodes.elementAt(i));
      currentNode.setIsAdded(false);
    }

    newEdges = newGraph.getEdges();
    for ( int j=0; j<newEdges.size(); j++ )
    {
      currentEdge = (Edge)edges.elementAt(j);
      newEdge = (Edge)newEdges.elementAt(j);
      if ( !keepCopyReferences )
      {
        newEdge.setCopy(null);
      }
      currentEdge.setIsAdded(false);
    }
    return newGraph;
  }

  public void resetCopyData()
  {
    Node aNode;
    Edge anEdge;
    Enumeration enumEdges, enumNodes;
    enumNodes = nodes.elements();
    while ( enumNodes.hasMoreElements() )
    {
      aNode = (Node)enumNodes.nextElement();
      aNode.setCopy(null);
      enumEdges = aNode.incidentEdges().elements();
      while ( enumEdges.hasMoreElements() )
      {
        anEdge = (Edge)enumEdges.nextElement();
        anEdge.setCopy(null);
      }
    }
  }

  /**
   * Returns the label of this Graph
   *
   * @return String: The label of this Graph.
   */
  public String getLabel() { return label; }

  /**
   * Returns the Nodes contained within this Graph.
   *
   * @return Vector: A Vector containing the Nodes of this Graph.
   */
  public Vector getNodes() { return nodes; }

  public Node getNodeAt(int index)
  {
    if ( nodes.isEmpty() || index < 0 || index > nodes.size()-1 )
    {
      return null;
    }
    else
    {
      return (Node)nodes.elementAt(index);
    }
  }

  public int getNumNodes() { return nodes.size(); }

  /**
   * Sets the label of this Graph.
   *
   * @param String newLabel: The new label for this Graph.
   */
  public void setLabel(String newLabel) { label = newLabel; }

  public boolean edgeNumbersAreInSync()
  {
    return getNumEdges() == getEdges().size();
  }

  public void makeAllEdgesStraight()
  {
    Vector edges = getEdges();
    Edge anEdge;
    for ( int i=0; i<edges.size(); i++ )
    {
      anEdge = (Edge)edges.elementAt(i);
      if ( anEdge.isCurved() )
      {
        anEdge.makeStraight();
      }
    }
  }

  public int getNumEdges()
  {
    return getNumEdges(nodes);
  }

  public int getNumEdges(Vector nodeVector)
  {
    int numEdges = 0;
    Enumeration allNodes = nodeVector.elements();
    while(allNodes.hasMoreElements())
    { 
      numEdges+= ((Node)allNodes.nextElement()).getNumEdges();
    }
    return numEdges/2;
  }

  public int getNumGeneratedEdges()
  {
    int numGenerated = 0;
    Vector edges = getEdges();
    for ( int i=0; i<edges.size(); i++ )
    {
      if ( ((Edge)edges.elementAt(i)).isGenerated() )
      {
        numGenerated++;
      }
    }
    return numGenerated;
  }

  public int getNumCurvedEdges()
  {
    int numCurved = 0;
    Vector edges = getEdges();
    for ( int i=0; i<edges.size(); i++ )
    {
      if ( ((Edge)edges.elementAt(i)).isCurved() )
      {
        numCurved++;
      }
    }
    return numCurved;
  }

  /**
   * Returns all of the Edges of this Graph as retrieved by asking
   * all of the Nodes in this Graph for their adjacent Edges.
   *
   * @return Vector: A Vector containing the Edges of this Graph.
   */
  public Vector getEdges()
  {
    return getEdges(nodes);
  }

  // get all edges incident to the nodes in the vector
  
  public Vector getEdges(Vector nodeVector)
  {
    return getEdges(nodeVector, false);
  }

  public Vector getCurvedEdges(Vector nodeVector)
  {
    return getEdges(nodeVector, true);
  }

  private Vector getEdges(Vector nodeVector, boolean onlyCurved)
  {
    Vector edges;
    Enumeration allNodes;
    Enumeration someEdges;
    Edge anEdge;
    int numEdges = getNumEdges(nodeVector);
    edges = new Vector(numEdges);

    allNodes = nodeVector.elements();
    while(allNodes.hasMoreElements())
    {
      someEdges = ((Node)allNodes.nextElement()).incidentEdges().elements();
      while(someEdges.hasMoreElements())
      {
        ((Edge)someEdges.nextElement()).setIsAdded(false);
      }
    }

    allNodes = nodeVector.elements();
    while(allNodes.hasMoreElements())
    {
      someEdges = ((Node)allNodes.nextElement()).incidentEdges().elements();
      while(someEdges.hasMoreElements())
      {
        anEdge = (Edge)someEdges.nextElement();
        if ( !anEdge.isAdded() )
        {
          if ( !onlyCurved || anEdge.isCurved() )
          {
            edges.addElement(anEdge);
          }
          anEdge.setIsAdded(true);
        }
      }
    }

    allNodes = nodeVector.elements();
    while(allNodes.hasMoreElements())
    {
      someEdges = ((Node)allNodes.nextElement()).incidentEdges().elements();
      while(someEdges.hasMoreElements())
      {
        ((Edge)someEdges.nextElement()).setIsAdded(false);
      }
    }
    return edges;
  }

  public Node[] getRandomTriangularFace()
  {
    Node triangleNodes[] = new Node[3];
    triangleNodes[0] = (Node)nodes.firstElement();
    Edge tempEdge = (Edge)triangleNodes[0].incidentEdges().firstElement();
    triangleNodes[1] = (Node)tempEdge.otherEndFrom(triangleNodes[0]);
    triangleNodes[2] = (Node)tempEdge.getNextInOrderFrom(triangleNodes[0]).otherEndFrom(triangleNodes[0]);
    if ( triangleNodes[2] ==
         tempEdge.getPreviousInOrderFrom(triangleNodes[1]).otherEndFrom(triangleNodes[1]) )
    {
      return triangleNodes;
    }
    else
    {
      return null;
    }
  }

  /**
   * Returns a String representation of this Graph including it's label,
   * number of Nodes, and number of Edges.
   *
   * @return String: A String representation of this Graph.
   */
  public String toString()
  {
    return(label + "(" + nodes.size() + " nodes, " + getNumEdges() + " edges)");
  }

  public void printAll()
  {
    for ( int i=0; i<nodes.size(); i++ )
    {
      ((Node)nodes.elementAt(i)).printAll();
    }
  }

  /**
   * Adds the given Node to the Nodes contained within this Graph.
   *
   * @param Node aNode: The Node to add to this Graph.
   */
  public void addNode(Node aNode)
  {
    addNode(aNode, true);
  }

  public void addNode(Node aNode, boolean addMemento)
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    nodes.addElement(aNode);
    if ( addMemento && currentMemento != null && trackUndos )
    {
      currentMemento.addMemento(NodeMemento.createCreateMemento(aNode));
    }
  }

  public Node createNode(Location aPoint)
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    Node newNode = new Node(aPoint);
    nodes.addElement(newNode);
    if ( currentMemento != null && trackUndos )
    {
      currentMemento.addMemento(NodeMemento.createCreateMemento(newNode));
    }
    return newNode;
  }

  public void translateNode(Node aNode, int dx, int dy, boolean createMemento )
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    boolean memento = currentMemento != null && trackUndos && createMemento;
    if ( memento )
    {
      currentMemento.addMemento(NodeMovementMemento.createMoveMemento(aNode));
    }
    aNode.translate(dx, dy);
    Edge anEdge;
    Vector edges = aNode.incidentEdges();
    for ( int i=0; i<edges.size(); i++ )
    {
      anEdge = (Edge)edges.elementAt(i);
      if ( memento )
      {
        currentMemento.addMemento(EdgeMovementMemento.createMoveMemento(anEdge));
      }
      anEdge.update();
    }
  }

  public void relocateNode(NodeInterface aNode, Location aLocation, boolean createMemento )
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    boolean memento = currentMemento != null && trackUndos && createMemento;
    if ( memento )
    {
      currentMemento.addMemento(NodeMovementMemento.createMoveMemento(aNode));
    }
    aNode.setLocation(aLocation);
  }

  public void translateNodes( Vector someNodes, int dx, int dy, boolean createMemento )
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    translate(someNodes, dx, dy, createMemento);
  }
  
  private void translate( Vector someNodes, int dx, int dy, boolean createMemento )
  {
    Node aNode;
    boolean memento = currentMemento != null && trackUndos && createMemento;
    for ( int i=0; i<someNodes.size(); i++ )
    {
      aNode = (Node)someNodes.elementAt(i);
      if ( memento )
      {
        currentMemento.addMemento(NodeMovementMemento.createMoveMemento(aNode));
      }
      aNode.translate(dx, dy);
    }
    Vector edges = getEdges(nodes);
    Edge anEdge;
    for ( int i=0; i<edges.size(); i++ )
    {
      anEdge = (Edge)edges.elementAt(i);
      if ( memento )
      {
        currentMemento.addMemento(EdgeMovementMemento.createMoveMemento(anEdge));
      }
      anEdge.update();
    }
  }

  public void relocateEdge( Edge anEdge, Location newLocation, boolean createMemento )
  {
    if ( currentMemento != null && trackUndos && createMemento )
    {
      currentMemento.addMemento(EdgeMovementMemento.createMoveMemento(anEdge));
    }
    anEdge.setCenterLocation(newLocation);
  }
  
  public void curveEdge( Edge anEdge, int dx, int dy, boolean createMemento )
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    if ( currentMemento != null && trackUndos && createMemento )
    {
      currentMemento.addMemento(EdgeMovementMemento.createMoveMemento(anEdge));
    }
    anEdge.makeCurved();
    anEdge.translate(dx, dy);
  }

  public void orthogonalizeEdge( Edge anEdge, boolean createMemento )
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    if ( currentMemento != null && trackUndos && createMemento )
    {
      currentMemento.addMemento(EdgeMovementMemento.createMoveMemento(anEdge));
    }
    anEdge.makeOrthogonal();
  }
  
  public void straightenEdge( EdgeInterface anEdge, boolean createMemento )
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    if ( currentMemento != null && trackUndos && createMemento )
    {
      currentMemento.addMemento(EdgeMovementMemento.createMoveMemento(anEdge));
    }
    anEdge.makeStraight();
  }

  public void straightenEdges(boolean createMemento)
  {
    straightenEdges(getEdges(), createMemento);
  }
  
  public void straightenEdges(Vector edges, boolean createMemento)
  {
    for ( int i=0; i<edges.size(); i++ )
    {
      straightenEdge((Edge)edges.elementAt(i), createMemento);
    }
  }
  
  public void updateEdge( EdgeInterface anEdge, boolean createMemento )
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    if ( currentMemento != null && trackUndos && createMemento )
    {
      currentMemento.addMemento(EdgeMovementMemento.createMoveMemento(anEdge));
    }
    anEdge.update();
  }
  
  public void updateEdges(Vector edges, boolean createMemento)
  {
    for ( int i=0; i<edges.size(); i++ )
    {
      updateEdge((Edge)edges.elementAt(i), createMemento);
    }
  }
  
  public void refreshEdgeCurves()
  {
    Vector edges = getEdges();
    for ( int i=0; i<edges.size(); i++ )
    {
      ((Edge)edges.elementAt(i)).update();
    }
  }

  public void updateEdgeCurveAngles()
  {
    Vector edges = getEdges();
    for ( int i=0; i<edges.size(); i++ )
    {
      ((Edge)edges.elementAt(i)).initCurveAngles();
    }
  }

  
  public void refreshOrthogonalEdges(Vector edges)
  {
    Edge anEdge;
    Location location;
    for ( int i=0; i<edges.size(); i++ )
    {  
      anEdge = (Edge)edges.elementAt(i);
      if ( anEdge.isOrthogonal() )
      {  
        location = anEdge.getOrthogonalLocation();
        if ( location == null )
        {
          anEdge.setCenterLocation(anEdge.getNormalLocation());
        }
        else
        {  
          anEdge.setCenterLocation(location);
        }
      }
      else
      {
        anEdge.update();
      }
    }
  }
  
  public void changeNodeLabel ( NodeInterface aNode, String label, boolean createMemento )
  {
    if ( !label.equals(aNode.getLabel()) )
    {
      hasChangedSinceLastSave = true;
      hasChangedSinceLastDraw = true;
      if ( currentMemento != null && trackUndos && createMemento )
      {
        currentMemento.addMemento(NodeLabelMemento.createLabelMemento(aNode));
      }
      aNode.setLabel( label );
    }
  }

  public void changeNodeDrawX ( NodeInterface aNode, boolean drawX, boolean createMemento )
  {
    if ( drawX != aNode.getDrawX() )
    {
      hasChangedSinceLastSave = true;
      hasChangedSinceLastDraw = true;
      if ( currentMemento != null && trackUndos && createMemento )
      {
        currentMemento.addMemento(NodeDrawXMemento.createDrawXMemento(aNode));
      }
      aNode.setDrawX(drawX);
    }
  }

  public void changeNodeColor ( NodeInterface aNode, Color aColor, boolean createMemento )
  {
    if ( !aColor.equals( aNode.getColor() ) )
    {
      hasChangedSinceLastSave = true;
      hasChangedSinceLastDraw = true;
      if ( currentMemento != null && trackUndos && createMemento )
      {
        currentMemento.addMemento(NodeColorMemento.createColorMemento(aNode));
      }
      aNode.setColor( aColor );
    }
  }

  public void changeEdgeColor ( EdgeInterface anEdge, Color aColor, boolean createMemento )
  {
    if ( !aColor.equals( anEdge.getColor() ) )
    {
      hasChangedSinceLastSave = true;
      hasChangedSinceLastDraw = true;
      if ( currentMemento != null && trackUndos && createMemento )
      {
        currentMemento.addMemento(EdgeColorMemento.createColorMemento(anEdge));
      }
      anEdge.setColor( aColor );
    }
  }

  public void changeEdgeDirection( EdgeInterface anEdge, NodeInterface sourceNode, boolean createMemento )
  {
    if ( (sourceNode != null && !sourceNode.equals( anEdge.getDirectedSourceNode() )) ||
         (sourceNode == null && anEdge.getDirectedSourceNode() != null) )
    {
      hasChangedSinceLastSave = true;
      hasChangedSinceLastDraw = true;
      if ( currentMemento != null && trackUndos && createMemento &&
           ((sourceNode != null && !sourceNode.equals(anEdge.getDirectedSourceNode())) ||
            (sourceNode == null && anEdge.getDirectedSourceNode() != null)) )
      {
        currentMemento.addMemento(EdgeDirectionMemento.createDirectionMemento(anEdge));
      }
      anEdge.setDirectedFrom( sourceNode );
    }
  }

  public boolean isTriangle(Node sourceNode, Edge firstEdge, Edge secondEdge)
  {
    Node firstNode = (Node)firstEdge.otherEndFrom(sourceNode);
    Node secondNode = (Node)secondEdge.otherEndFrom(sourceNode);
    return firstEdge.getPreviousInOrderFrom(firstNode).otherEndFrom(firstNode) == secondNode;
  }

  public boolean isInQuadrilateral(Edge anEdge)
  {
    Node firstNode = (Node)anEdge.getStartNode();
    Node secondNode = (Node)anEdge.otherEndFrom(firstNode);
    return ( anEdge.getNextInOrderFrom(firstNode).otherEndFrom(firstNode) ==
             anEdge.getPreviousInOrderFrom(secondNode).otherEndFrom(secondNode) &&
             anEdge.getPreviousInOrderFrom(firstNode).otherEndFrom(firstNode) ==
             anEdge.getNextInOrderFrom(secondNode).otherEndFrom(secondNode) );
  }

  public void flip(Edge anEdge)
  {
    Node firstNode = (Node)anEdge.getStartNode();
    Node secondNode = (Node)anEdge.getEndNode();
    Node newFirstNode = (Node)((Edge)anEdge.getPreviousInOrderFrom(firstNode)).otherEndFrom(firstNode);
    Node newSecondNode = (Node)((Edge)anEdge.getNextInOrderFrom(firstNode)).otherEndFrom(firstNode);
    Edge newFirstPrevEdge = (Edge)anEdge.getNextInOrderFrom(secondNode);
    Edge newSecondPrevEdge = (Edge)anEdge.getNextInOrderFrom(firstNode);
    // It is assumed that edges being flipped are NOT directed...
    Edge newEdge = new Edge(anEdge, null, newFirstNode, newSecondNode );

    deleteEdge(anEdge);
    addEdge(newEdge, newFirstPrevEdge, newSecondPrevEdge);
  }

  public void addEdge( EdgeInterface newEdge, EdgeInterface startPrevEdge,
                       EdgeInterface endPrevEdge )
  {
    addEdge( newEdge, startPrevEdge, endPrevEdge, true );
  }

  public void addEdge( EdgeInterface newEdge, EdgeInterface startPrevEdge,
                       EdgeInterface endPrevEdge, boolean addMemento )
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    NodeInterface startNode = newEdge.getStartNode();
    NodeInterface endNode = newEdge.getEndNode();
    startNode.addEdgeBetween( newEdge,
                              startPrevEdge,
                              startPrevEdge.getNextInOrderFrom(startNode) );
    endNode.addEdgeBetween( newEdge,
                            endPrevEdge,
                            endPrevEdge.getNextInOrderFrom(endNode) );
    if ( addMemento && currentMemento != null && trackUndos )
    {
      Edge a, b, c;
      if ( newEdge instanceof EdgeExtender )
      {
        a = ((EdgeExtender)newEdge).getRef();
      }
      else
      {
        a = (Edge)newEdge;
      }
      if ( startPrevEdge instanceof EdgeExtender )
      {
        b = ((EdgeExtender)startPrevEdge).getRef();
      }
      else
      {
        b = (Edge)startPrevEdge;
      }
      if ( endPrevEdge instanceof EdgeExtender )
      {
        c = ((EdgeExtender)endPrevEdge).getRef();
      }
      else
      {
        c = (Edge)endPrevEdge;
      }
      currentMemento.addMemento(EdgeBetweenMemento.createCreateMemento(a, b, c));
    }
  }

  private void addEdge(Node start, Node end, boolean addMemento )
  {
    // First make the edge
    Edge anEdge = new Edge(start, end);
    // Now tell the nodes about the edge
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    if ( start.addIncidentEdge(anEdge) && end.addIncidentEdge(anEdge) &&
         addMemento && currentMemento != null && trackUndos )
    {
      currentMemento.addMemento(EdgeBetweenMemento.createCreateMemento(anEdge,
        (Edge)anEdge.getPreviousInOrderFrom(start),
        (Edge)anEdge.getPreviousInOrderFrom(end)));
    }
    if ( !edgeNumbersAreInSync() )
    {
      System.out.println("error2: " + anEdge);
    }
  }

  public void addEdge(Node start, Node end)
  {
    addEdge(start, end, true);
  }

  public void addEdgeNoCheck(Edge anEdge)
  {
    addEdgeNoCheck(anEdge, true);
  }

  public void addEdgeNoCheck(Edge anEdge, boolean addMemento)
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    anEdge.getStartNode().addIncidentEdgeNoCheck(anEdge);
    anEdge.getEndNode().addIncidentEdgeNoCheck(anEdge);
    if ( addMemento && currentMemento != null && trackUndos )
    {
      currentMemento.addMemento(EdgeBetweenMemento.createCreateMemento(anEdge,
        (Edge)anEdge.getPreviousInOrderFrom(anEdge.getStartNode()),
        (Edge)anEdge.getPreviousInOrderFrom(anEdge.getEndNode())));
    }
  }


  public void addEdgeNoCheck(NodeInterface aNode, EdgeInterface anEdge)
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    aNode.addIncidentEdgeNoCheck(anEdge);
  }

  public void addEdgeNoCheck(Node start, Node end)
  {
    addEdgeNoCheck(start, end, true);
  }

  public void addEdgeNoCheck(Node start, Node end, boolean addMemento)
  {
    hasChangedSinceLastSave = true;
      hasChangedSinceLastDraw = true;
    Edge anEdge = new Edge(start, end);
    start.addIncidentEdgeNoCheck(anEdge);
    end.addIncidentEdgeNoCheck(anEdge);
    if ( addMemento && currentMemento != null && trackUndos )
    {
      currentMemento.addMemento(EdgeBetweenMemento.createCreateMemento(anEdge,
        (Edge)anEdge.getPreviousInOrderFrom(start),
        (Edge)anEdge.getPreviousInOrderFrom(end)));
    }
    if ( !edgeNumbersAreInSync() )
    {
      System.out.println("error4: " + anEdge);
    }
  }

  public void addGeneratedEdgeNoCheck(Node start, Node end)
  {
    addGeneratedEdgeNoCheck(start, end, true);
  }

  public void addGeneratedEdgeNoCheck(Node start, Node end, boolean addMemento)
  {
    hasChangedSinceLastSave = true;
      hasChangedSinceLastDraw = true;
    Edge anEdge = new Edge(start, end);
    anEdge.setIsGenerated(true);
    start.addIncidentEdgeNoCheck(anEdge);
    end.addIncidentEdgeNoCheck(anEdge);
    if ( addMemento && currentMemento != null && trackUndos )
    {
      currentMemento.addMemento(EdgeBetweenMemento.createCreateMemento(anEdge,
        (Edge)anEdge.getPreviousInOrderFrom(start),
        (Edge)anEdge.getPreviousInOrderFrom(end)));
    }
  }

  /**
   * Adds the given Edge to the Graph by searching the Nodes for
   * ones with the given labels and creates a new Edge
   * using the Nodes found within the Graph.
   *
   * @param String startLabel: The label of the first end Node of the new Edge to add.
   * @param String endLabel: The label of the second end Node of the new Edge to add.
   */
  public void addEdge(String startLabel, String endLabel)
  {
    Node start, end;
    start = nodeNamed(startLabel);
    end = nodeNamed(endLabel);
    if ((start != null) && (end != null))
      addEdge(start, end);
  }

  /**
   * Removes the given Edge from this Graph by telling the end
   * Nodes of the Edge to delete the Edge from their incident
   * Edge list.
   *
   * @param Edge anEdge: The Edge to remove from this Graph.
   */
  public void deleteEdge(Edge anEdge)
  {
    deleteEdge(anEdge, true);
  }

  public void deleteEdge(Edge anEdge, boolean createMemento)
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    if ( createMemento && currentMemento != null && trackUndos )
    {
      currentMemento.addMemento(EdgeBetweenMemento.createDeleteMemento(anEdge,
        (Edge)anEdge.getPreviousInOrderFrom(anEdge.getStartNode()),
        (Edge)anEdge.getPreviousInOrderFrom(anEdge.getEndNode())));
    }
    anEdge.getStartNode().deleteIncidentEdge(anEdge);
    anEdge.getEndNode().deleteIncidentEdge(anEdge);
  }

  /**
   * Removes the given Node from this Graph, deleting any Edges
   * incident to it.
   *
   * @param Node aNode: The Node to remove from this Graph.
   */
  public void deleteNode(Node aNode)
  {
    deleteNode(aNode, true);
  }

  public void deleteNode(Node aNode, boolean addMemento)
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    Enumeration someEdges = aNode.incidentEdgesInReverse().elements();
    while(someEdges.hasMoreElements())
    {
      Edge anEdge = (Edge)someEdges.nextElement();
      deleteEdge(anEdge, addMemento);
    }
    nodes.removeElement(aNode);
    if ( addMemento && currentMemento != null && trackUndos )
    {
      currentMemento.addMemento(NodeMemento.createDeleteMemento(aNode));
    }
  }

  public void makeGeneratedEdgePermanent(Edge anEdge)
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    if ( anEdge.isGenerated() )
    {
      anEdge.setIsGenerated(false);
      if ( currentMemento != null && trackUndos )
      {
        currentMemento.addMemento(EdgeMemento.createPreserveGeneratedMemento(anEdge));
      }
    }
  }

  public void makeGeneratedEdgesPermanent()
  {
    Enumeration enumEdges = getEdges().elements();
    while ( enumEdges.hasMoreElements() )
    {
      makeGeneratedEdgePermanent((Edge)enumEdges.nextElement());
    }
  }

  public void deleteGeneratedEdges()
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    Enumeration enumNodes = nodes.elements();
    Node currentNode;
    Vector edges;
    Edge currentEdge;
    while ( enumNodes.hasMoreElements() )
    {
      currentNode = (Node)enumNodes.nextElement();
      edges = currentNode.incidentEdges();
      for ( int i=0; i<edges.size(); i++ )
      {
        currentEdge = (Edge)edges.elementAt(i);
        if ( currentEdge.isGenerated() )
        {
          deleteEdge(currentEdge);
        }
      }
    }
  }

  public void removeEdgeDirections()
  {
    removeEdgeDirections(true);
  }

  public void removeEdgeDirections(boolean createMemento)
  {
    Vector edges = getEdges();
    for ( int i=0; i<edges.size(); i++ )
    {
      changeEdgeDirection((Edge)edges.elementAt(i), null, createMemento);
    }
  }

  public void clearNodeLabels()
  {
    clearNodeLabels(true);
  }

  public void clearNodeLabels(boolean createMemento)
  {
    for ( int i=0; i<nodes.size(); i++ )
    {
      changeNodeLabel((Node)nodes.elementAt(i), "", createMemento);
    }
  }

  /**
   * Returns the Node with the given Label, or Null if none exists.
   *
   * @param String aLabel: The label of the Node to retrieve.
   * @return Node: The Node with label matching the given label.
   */
  public Node nodeNamed(String aLabel)
  {
    for (int i=0; i<nodes.size(); i++)
    {
      Node aNode = (Node)nodes.elementAt(i);
      if (aNode.getLabel().equals(aLabel))
        return aNode;
    }
    return null;
  }

  /**
   * Returns the Node at the given location, or Null if none exists.
   *
   * @param Point p: The Point representing the location of the Node to retrieve.
   * @return Node: The Node with location matching the given location.
   */
  public Node nodeAt(Point p)
  {
    return nodeAt(new Location(p));
  }

  public Node nodeAt(Location p)
  {
    for (int i=0; i<nodes.size(); i++)
    {
      Node aNode = (Node)nodes.elementAt(i);
      int distance = (p.intX() - aNode.getLocation().intX()) * (p.intX() - aNode.getLocation().intX()) +
                     (p.intY() - aNode.getLocation().intY()) * (p.intY() - aNode.getLocation().intY());
      if (distance <= (Node.RADIUS * Node.RADIUS))
        return aNode;
    }
    return null;
  }

  /**
   * Returns the Edge whose midpoint is at (or very close to) the given location,
   * or Null if none exists.
   *
   * @param Point p: The Point representing the location of the Edge to retrieve.
   * @return Edge: The Edge with location matching the given location.
   */
  public Edge edgeAt(Location p)
  {
    Vector edges = getEdges();
    int midPointX, midPointY;
    for (int i=0; i<edges.size(); i++)
    {
      Edge anEdge = (Edge)edges.elementAt(i);
      midPointX = anEdge.getCenterLocation().intX();
      midPointY = anEdge.getCenterLocation().intY();
      int distance = (p.intX() - midPointX) * (p.intX() - midPointX) +
                     (p.intY() - midPointY) * (p.intY() - midPointY);
      if ( distance <= (Node.RADIUS * Node.RADIUS) )
      {
        return anEdge;
      }
    }
    return null;
  }

  public Vector getNodesInRectangle( Rectangle2D.Double rect )
  {
    Vector nodesToReturn = new Vector();
    Node aNode;
    for ( int i=0; i<nodes.size(); i++ )
    {
      aNode = (Node)nodes.elementAt(i);
      if ( rect.contains( aNode.getX(), aNode.getY() ) )
      {
        nodesToReturn.addElement(aNode);
      }
    }
    return nodesToReturn;
  }

  public Vector getEdgesInRectangle( Rectangle2D.Double rect )
  {
    Vector edgesToReturn = new Vector();
    Vector edges = getEdges();
    Edge anEdge;
    for ( int i=0; i<edges.size(); i++ )
    {
      anEdge = (Edge)edges.elementAt(i);
      if ( rect.contains( anEdge.getCenterLocation().intX(),
                          anEdge.getCenterLocation().intY() ) )
      {
        edgesToReturn.addElement(anEdge);
      }
    }
    return edgesToReturn;
  }

  /**
   * Returns the Edge that passes through the given location,
   * or Null if none exists.
   *
   * @param Point p: The Point that the Edge to retrieve must pass through.
   * @return Edge: The Edge passing through the given location.
   */
  /*public Edge edgeContainingPoint(Point p)
  {
    Vector edges = getEdges();
    Edge anEdge;
    double distance = 0;
    for (int i=0; i<edges.size(); i++)
    {
      anEdge = (Edge)edges.elementAt(i);
      distance = Line2D.ptLineDist(anEdge.getStartNode().getLocation().intX(),
                                   anEdge.getStartNode().getLocation().intY(),
                                   anEdge.getEndNode().getLocation().intX(),
                                   anEdge.getEndNode().getLocation().intY(),
                                   p.x,
                                   p.y);
      if (distance == 0)
        return anEdge;
    }
    return null;
  }*/

  /**
   * Returns all of the Nodes in this Graph that have been selected.
   *
   * @return Vector: A Vector containing all of the selected Nodes of this Graph.
   */
  public Vector selectedNodes()
  {
    Vector selected = new Vector();
    Enumeration allNodes = nodes.elements();
    Node aNode;
    while(allNodes.hasMoreElements())
    {
      aNode = (Node)allNodes.nextElement();
      if (aNode.isSelected())
        selected.addElement(aNode);
    }
    return selected;
  }

  /**
   * Returns all of the Edges in this Graph that have been selected.
   *
   * @return Vector: A Vector containing all of the selected Edges of this Graph.
   */
  public Vector selectedEdges()
  {
    Vector selected = new Vector();
    Enumeration allEdges = getEdges().elements();
    Edge anEdge;
    while(allEdges.hasMoreElements())
    {
      anEdge = (Edge)allEdges.nextElement();
      if (anEdge.isSelected())
        selected.addElement(anEdge);
    }
    return selected;
  }

  public void unselectAll()
  {
    hasChangedSinceLastDraw = true;
    Enumeration highlightedEdges = selectedEdges().elements();
    while (highlightedEdges.hasMoreElements())
    {
      ((Edge)highlightedEdges.nextElement()).setSelected(false);
    }
    Enumeration highlightedNodes = selectedNodes().elements();
    while (highlightedNodes.hasMoreElements())
    {
      ((Node)highlightedNodes.nextElement()).setSelected(false);
    }
  }

  public void deleteSelected()
  {
    hasChangedSinceLastDraw = true;
    Enumeration highlightedEdges = selectedEdges().elements();
    while (highlightedEdges.hasMoreElements())
    {
      deleteEdge((Edge)highlightedEdges.nextElement());
    }
    Enumeration highlightedNodes = selectedNodes().elements();
    while (highlightedNodes.hasMoreElements())
    {
      deleteNode((Node)highlightedNodes.nextElement());
    }
  }

  public void toggleEdgeSelection(Edge anEdge)
  {
    hasChangedSinceLastDraw = true;
    anEdge.toggleSelected();
  }

  public void toggleNodeSelection(Node aNode)
  {
    hasChangedSinceLastDraw = true;
    aNode.toggleSelected();
  }

  public void selectNodes(Vector sNodes)
  {
    hasChangedSinceLastDraw = true;
    for ( int i=0; i<sNodes.size(); i++ )
    {
      ((Node)sNodes.elementAt(i)).setSelected(true);
    }
  }

  public void selectEdges(Vector sEdges)
  {
    hasChangedSinceLastDraw = true;
    for ( int i=0; i<sEdges.size(); i++ )
    {
      ((Edge)sEdges.elementAt(i)).setSelected(true);
    }
  }

  public void deleteAll()
  {
    hasChangedSinceLastDraw = true;
    Enumeration nodeEnum = new Vector(nodes).elements();
    while (nodeEnum.hasMoreElements())
    {
      deleteNode((Node)nodeEnum.nextElement());
    }
  }

  public void resetColors(boolean createMemento)
  {
    hasChangedSinceLastDraw = true;
    Node aNode;
    for ( int i=0; i<nodes.size(); i++ )
    {
      aNode = (Node)nodes.elementAt(i);
      changeNodeColor(aNode, Node.DEFAULT_COLOR, createMemento);
      changeNodeDrawX(aNode, false, createMemento);
      //aNode.setLabel("");
    }
    Vector edges = getEdges();
    for ( int i=0; i<edges.size(); i++ )
    {
      changeEdgeColor((Edge)edges.elementAt(i), Edge.DEFAULT_COLOR, createMemento);
    }
  }

  /**
   * Draws this Graph using the default Colors
   *
   * @param Graphics aPen: The Graphics object to use to draw this Graph.
   */
  public void draw(Graphics2D g2)
  {
    draw(g2, 0, 0);
  }

  public void draw(Graphics2D g2, int xOffset, int yOffset)
  {
    hasChangedSinceLastDraw = false;
    logChangedSinceLastDraw = false;
    Vector edges = getEdges();
    // Draw the edges first
    for (int i=0; i<edges.size(); i++)
    {
      ((Edge)edges.elementAt(i)).draw(g2, xOffset, yOffset,
                                      drawSelected);
    }
    // Draw the nodes now
    for (int i=0; i<nodes.size(); i++)
    {
      ((Node)nodes.elementAt(i)).draw(g2, xOffset, yOffset,
                                      drawSelected, showCoords, showLabels);
    }
  }

  public void rotate(Location pivotPoint, double angle, boolean createMemento)
  {
    boolean memento = currentMemento != null && trackUndos && createMemento;
    
    Node currentNode;
    for ( int i=0; i<nodes.size(); i++ )
    {
      currentNode = (Node)nodes.elementAt(i);
      if ( memento )
      {
        currentMemento.addMemento(NodeMovementMemento.createMoveMemento(currentNode));
      }
      currentNode.rotate(pivotPoint, angle);
    }
    Vector edges = getEdges();
    Edge anEdge;
    for ( int i=0; i<edges.size(); i++ )
    {
      anEdge = (Edge)edges.elementAt(i);
      if ( memento )
      {
        currentMemento.addMemento(EdgeMovementMemento.createMoveMemento(anEdge));
      }
      anEdge.rotate(pivotPoint, angle);
      anEdge.update();
    }
  }

  public void translate(int dx, int dy, boolean createMemento)
  {
    hasChangedSinceLastSave = true;
    hasChangedSinceLastDraw = true;
    translate(nodes, dx, dy, createMemento);
  }

  /**
   * Save this Graph to the File that is provided as a Parameter. The label,
   * size, number of Nodes and number of Edges is outputted and the saveTo
   * method of each Node and Edge in the Graph is called.
   *
   * @param PrintWriter aFile: The file to save to that is open/ready for output.
   */
  public void saveTo(PrintWriter aFile)
  {
    aFile.println(label);
    aFile.println(gridRows);
    aFile.println(gridRowHeight);
    aFile.println(gridCols);
    aFile.println(gridColWidth);
    // Output the nodes
    aFile.println(nodes.size());
    enumerateNodeAndEdgeIndices();
    for (int i=0; i<nodes.size(); i++)
    {
      ((Node)nodes.elementAt(i)).saveTo(aFile);
    }
    // Output the edges
    Vector edges = getEdges();
    aFile.println(edges.size());
    for (int i=0; i<edges.size(); i++)
    {
      ((Edge)edges.elementAt(i)).saveTo(aFile);
    }
    hasChangedSinceLastSave = false;
  }

  /**
   * Load this Graph from the File that is provided as a Parameter. The label
   * and scale are read in, and each Node and Edge stored in the file is loaded
   * by calling its loadFrom method.<br>
   * <br>
   * Note that after the Nodes and Edges are loaded, it is necessary to go through
   * the Graph and connected the Nodes and Edges properly.
   *
   * @param BufferedReader aFile: The file to load from that is open/ready for input.
   */
  public static Graph loadFrom(BufferedReader aFile) throws IOException
  {
    Node aNode;
    int numNodes,numEdges;
    // Read the label from the file and make the graph
    Graph aGraph = new Graph(aFile.readLine());
    aGraph.gridRows = Integer.valueOf(aFile.readLine()).intValue();
    aGraph.gridRowHeight = Integer.valueOf(aFile.readLine()).intValue();
    aGraph.gridCols = Integer.valueOf(aFile.readLine()).intValue();
    aGraph.gridColWidth = Integer.valueOf(aFile.readLine()).intValue();
    // Get the nodes and edges
    numNodes = Integer.valueOf(aFile.readLine()).intValue();
    Vector allEdgeIndices = new Vector();
    for (int i=0; i<numNodes; i++)
    {
      aGraph.addNode(Node.loadFrom(aFile, allEdgeIndices));
    }
    // Now connect them with new edges
    numEdges = Integer.valueOf(aFile.readLine()).intValue();
    Vector edges = new Vector(numEdges);
    for (int i=0; i<numEdges; i++)
    {
      edges.addElement( Edge.loadFrom(aFile, aGraph.nodes) );
    }

    Vector edgeIndices;
    for (int i=0; i<numNodes; i++)
    {
      aNode = (Node)aGraph.nodes.elementAt(i);
      edgeIndices = (Vector)allEdgeIndices.elementAt(i);
      for ( int j=0; j<edgeIndices.size(); j++ )
      {
        aNode.addIncidentEdgeNoCheck(
          (Edge)edges.elementAt( ((Integer)edgeIndices.elementAt(j)).intValue()-1 ) );
      }
    }

    return (aGraph);
  }

  public boolean hasNodes()
  {
    return nodes.size() != 0;
  }

  public void enumerateNodeAndEdgeIndices()
  {
    for ( int i=0; i<nodes.size(); i++ )
    {
      ((Node)nodes.elementAt(i)).setIndex(i+1);
    }
    Vector edges = getEdges();
    for ( int i=0; i<edges.size(); i++ )
    {
      ((Edge)edges.elementAt(i)).setIndex(i+1);
    }
  }

  public EdgeInterface[] sortEdges()
  {
    return sortEdges(getEdges());
  }

  public EdgeInterface[] sortEdges(Vector edges)
  {
    int count[] = new int[nodes.size()];
    EdgeInterface sortedEdges[] = new EdgeInterface[edges.size()];
    EdgeInterface sortedEdges2[] = new EdgeInterface[edges.size()];
    int i=0;
    for ( i=0; i<nodes.size(); i++ )
    {
      count[i] = 0;
      ((Node)nodes.elementAt(i)).setIndex(i+1);
    }
    for ( i=0; i<edges.size(); i++ )
    {
      count[((EdgeInterface)edges.elementAt(i)).getHigherIndex()-1]++;
    }
    for ( i=1; i<nodes.size(); i++ )
    {
      count[i]+=count[i-1];
    }
    for ( i=edges.size()-1; i>=0; i-- )
    {
      sortedEdges[count[((EdgeInterface)edges.elementAt(i)).getHigherIndex()-1]-1] = (EdgeInterface)edges.elementAt(i);
      count[((EdgeInterface)edges.elementAt(i)).getHigherIndex()-1]--;
    }

    for ( i=0; i<nodes.size(); i++ )
    {
      count[i] = 0;
    }
    for ( i=0; i<edges.size(); i++ )
    {
      count[sortedEdges[i].getLowerIndex()-1]++;
    }
    for ( i=1; i<nodes.size(); i++ )
    {
      count[i]+=count[i-1];
    }
    for ( i=edges.size()-1; i>=0; i-- )
    {
      sortedEdges2[count[sortedEdges[i].getLowerIndex()-1]-1] = sortedEdges[i];
      count[sortedEdges[i].getLowerIndex()-1]--;
    }
    return sortedEdges2;
  }

  public void deleteAllEdges()
  {
    // This is for embed...
    Vector edges = getEdges();
    Edge edge;
    for ( int i=0; i<edges.size(); i++ )
    {
      edge = (Edge)edges.elementAt(i);
      if ( currentMemento != null && trackUndos )
      {
        currentMemento.addMemento(EdgeBetweenMemento.createChangeMemento(edge));
      }
    }

    Node node;
    for ( int i=0; i<nodes.size(); i++ )
    {
      node = (Node)nodes.elementAt(i);
      if ( currentMemento != null && trackUndos )
      {
        currentMemento.addMemento(NodeChangeMemento.createChangeMemento(node));
      }
      node.resetIncidentEdges();
    }
  }

  public boolean checkForDuplicateEdges()
  {
    EdgeInterface sortedEdges[] = sortEdges();
    for ( int i=0; i<sortedEdges.length-1; i++ )
    {
      if ( sortedEdges[i].equals(sortedEdges[i+1]) )
      {
        return true;
      }
    }
    return false;
  }

  public void scaleTo(Rectangle2D.Double newBounds, boolean createMemento )
  {
    boolean memento = currentMemento != null && trackUndos && createMemento;
    if ( !nodes.isEmpty() )
    {
      Rectangle2D.Double oldBounds = getBounds();
      double xFactor = newBounds.getWidth() / oldBounds.getWidth();
      double yFactor = newBounds.getHeight() / oldBounds.getHeight();
      Node currentNode;

      for ( int i=0; i<nodes.size(); i++ )
      {
        currentNode = (Node)nodes.elementAt(i);
        if ( memento )
        {
          currentMemento.addMemento(NodeMovementMemento.createMoveMemento(currentNode));
        }
        currentNode.scaleBy( oldBounds.getMinX(), oldBounds.getMinY() , xFactor, yFactor);
      }
      Vector edges = getEdges();
      Edge anEdge;
      for ( int i=0; i<edges.size(); i++ )
      {
        anEdge = (Edge)edges.elementAt(i);
        if ( memento )
        {
          currentMemento.addMemento(EdgeMovementMemento.createMoveMemento(anEdge));
        }
        anEdge.scaleBy( oldBounds.getMinX(), oldBounds.getMinY() , xFactor, yFactor);
      }
    }
  }

  public Vector createNodeExtenders(Class NodeExtenderClass)
  {
    Vector newVector = new Vector(nodes.size());
    Node currentNode;
    NodeExtender currentNodeExtender;
    for ( int i=0; i<nodes.size(); i++ )
    {
      currentNode = (Node)nodes.elementAt(i);
      try
      {
        currentNodeExtender = (NodeExtender)NodeExtenderClass.newInstance();
        currentNode.setExtender(currentNodeExtender);
        currentNodeExtender.setRef(currentNode);
        newVector.addElement(currentNodeExtender);
      }
      catch (Exception e) { e.printStackTrace(); }
    }
    return newVector;
  }

  public Vector createEdgeExtenders(Class EdgeExtenderClass)
  {
    Vector edges = getEdges();
    Vector newVector = new Vector(edges.size());
    Edge currentEdge;
    EdgeExtender currentEdgeExtender;
    for ( int i=0; i<edges.size(); i++ )
    {
      currentEdge = (Edge)edges.elementAt(i);
      try
      {
        currentEdgeExtender = (EdgeExtender)EdgeExtenderClass.newInstance();
        currentEdge.setExtender(currentEdgeExtender);
        currentEdgeExtender.setRef(currentEdge);
        newVector.addElement(currentEdgeExtender);
      }
      catch (Exception e) { e.printStackTrace(); }
    }
    return newVector;
  }

  public Vector getNodeExtenders()
  {
    Vector newVector = new Vector(nodes.size());
    Node currentNode;
    NodeExtender currentNodeExtender;
    for ( int i=0; i<nodes.size(); i++ )
    {
      currentNode = (Node)nodes.elementAt(i);
      currentNodeExtender = currentNode.getExtender();
      if ( currentNodeExtender != null )
      {
        newVector.addElement(currentNodeExtender);
      }
    }
    return newVector;
  }

  public Vector getEdgeExtenders()
  {
    Vector edges = getEdges();
    Vector newVector = new Vector(edges.size());
    Edge currentEdge;
    EdgeExtender currentEdgeExtender;
    for ( int i=0; i<edges.size(); i++ )
    {
      currentEdge = (Edge)edges.elementAt(i);
      currentEdgeExtender = currentEdge.getExtender();
      if ( currentEdgeExtender != null )
      {
        newVector.addElement(currentEdgeExtender);
      }
    }
    return newVector;
  }

  public Vector getEdgeExtenders(Vector nodeVector)
  {
    Vector edges = getEdges(nodeVector);
    Vector newVector = new Vector(edges.size());
    Edge currentEdge;
    EdgeExtender currentEdgeExtender;
    for ( int i=0; i<edges.size(); i++ )
    {
      currentEdge = (Edge)edges.elementAt(i);
      currentEdgeExtender = currentEdge.getExtender();
      if ( currentEdgeExtender != null )
      {
        newVector.addElement(currentEdgeExtender);
      }
    }
    return newVector;
  }
  
  public void permuteNodeOrder()
  {
    java.util.Random rand = new java.util.Random();
    Object temp;
    int j;
    for ( int i=0; i<nodes.size(); i++ )
    {
      j = rand.nextInt(nodes.size());
      temp = nodes.elementAt(i);
      nodes.setElementAt(nodes.elementAt(j), i);
      nodes.setElementAt(temp, j);    
    }
  }
}