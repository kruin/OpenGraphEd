package graphStructure;

import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.geom.*;
import java.awt.font.*;

/**
 * This class represents a node object that can be used as end points for edges.
 * Nodes can be added, deleted or moved by using the
 * interface of the editor component.
 *
 * @author Jon Harris
 */
public class Node implements NodeInterface
{
  /** The radius in pixels to use when drawing this Node in the UI */
  public static int RADIUS = 5;
  public static int LINE_THICKNESS = 3;
  public static int DASH_LENGTH = 2;
  public static double MIN_FOR_SCALE = 10;
  /** The Color to use when drawing this Node in the UI */
  public static Color DEFAULT_COLOR = Color.blue;
  public static Color TEXT_COLOR = Color.black;
  public static Color SELECTED_COLOR = Color.black;
  public static Color SPECIAL_SELECTED_COLOR = Color.red;
  public static boolean OPAQUE_TEXT = true;

  public static Font drawTextFont = new Font("Courier", Font.BOLD, 12);

  private static TextLayout thisTl;

  private String label;
  private Color color;
  private boolean drawX;
  private Location location;
  private Edge accessEdge;
  private boolean isSelected;
  private boolean isSpecialSelected;
  private int index;
  private int numEdges;
  private NodeInterface copy;
  private boolean isAdded;
  private boolean isVisible;

  private NodeExtender extender;

  public boolean isAdded() { return isAdded; }

  public void setIsAdded(boolean added) { isAdded = added; }

  public Color getColor() { return color; }

  public void setColor(Color aColor) { color = aColor; }

  public int getIndex() { return index; }

  public void setIndex(int index) { this.index = index; }

  public void setCopy(NodeInterface aCopy) { copy = aCopy; }

  public NodeInterface getCopy() { return copy; }

  public NodeInterface getMasterCopy()
  {
    if ( copy == null )
    {
      return null;
    }
    Node cp = this;
    while ( cp.copy != null )
    {
      cp = (Node)cp.copy;
    };
    return cp;
  }
  
  public void setDrawX(boolean draw) { drawX = draw; }

  public boolean getDrawX() { return drawX; }

  public int getNumEdges() { return numEdges; }

  public void setIsVisible(boolean v) { isVisible = v; }

  /**
   * Constructor for class Node
   */
  protected Node()
  {
    initialize();
  }

  /**
   * Constructor for class Node which assigns a label to the Node.
   *
   * @param String aLabel: A label to assign to the Node.
   */
  protected Node(String aLabel)
  {
    initialize();
    label = aLabel;
  }

  public Node(int x, int y)
  {
    initialize();
    location = new Location(x,y);
  }
  
  /**
   * Constructor for class Node which constructs the Node at the given location.
   *
   * @param Point aPoint: The point at which to locate the new Node.
   */
  protected Node(Location aPoint)
  {
    initialize();
    location = new Location(aPoint);
  }

  public Node(Node aNode)
  {
    initialize();
    location = new Location(aNode.getLocation());
    label = new String(aNode.label);
    if ( aNode.color != null )
    {
      color = new Color( aNode.color.getRGB() );
    }
    drawX = aNode.drawX;
    isSelected = aNode.isSelected;
    isVisible = aNode.isVisible;
  }

  /**
   * Constructor for class Node which assigns a label to the Node,
   * and constructs the Node at the given location.
   *
   * @param String aLabel: A label to assign to the Node.
   * @param Point aPoint: The point at which to locate the new Node.
   */
  protected Node(String aLabel, Point aPoint)
  {
    initialize();
    label = aLabel;
    location = new Location(aPoint);
  }

  /**
   * Returns the X coordinate of this Node's location.
   *
   * @return int: The X coordinate of this Node's location.
   */
  public int getX()
  {
    return location.intX();
  }

  /**
   * Returns the Y coordinate of this Node's location.
   *
   * @return int: The Y coordinate of this Node's location.
   */
  public int getY()
  {
    return location.intY();
  }

  // Initialize the node's data.
  private void initialize()
  {
    label = "";
    location = new Location(0,0);
    accessEdge = null;
    isSelected = false;
    isSpecialSelected = false;
    color = DEFAULT_COLOR;
    numEdges = 0;
    isVisible = true;
  }

  /**
   * Returns whether or not this Node is equal to the given Object.<br>
   * To be equal, the Object must be a Node and have the same location.
   *
   * @return boolean: Whether or not this Node is equal to the given Object.
   */
  public boolean equals(Object compareNode)
  {
    if ( compareNode instanceof Node )
    {
      return location.equals(((Node)compareNode).getLocation());
    }
    else if ( compareNode instanceof Point )
    {
      return location.equals((Point)compareNode);
    }
    else if ( compareNode instanceof Location )
    {
      return location.equals((Location)compareNode);
    }
    else
    {
      return false;
    }
  }

  public boolean contains(Point p, int radius)
  {
    int distance = (p.x - location.intX()) * (p.x - location.intX()) +
                   (p.y - location.intY()) * (p.y - location.intY());
    return (distance <= (radius * radius));
  }

  /**
   * Returns the label of this Node.
   *
   * @return String: This Node's label.
   */
  public String getLabel() { return label; }

  /**
   * Returns the location of this Node as a Point.
   *
   * @return Point: The location of this Node.
   */
  public Location getLocation() { return location; }

  /**
   * Returns whether or not this Node is selected.
   *
   * @return boolean: Whether or not this Node is selected.
   */
  public boolean isSelected() { return isSelected; }

  public boolean isSpecialSelected() { return isSpecialSelected; }

  public boolean hasNoIncidentEdges()
  {
    return accessEdge == null;
  }

  public boolean hasOnlyOneIncidentEdge()
  {
    if ( hasNoIncidentEdges() )
    {
      return false;
    }
    return accessEdge.getNextInOrderFrom(this) == accessEdge;
  }

  public boolean hasOnlyTwoIncidentEdges()
  {
    if ( hasNoIncidentEdges() || hasOnlyOneIncidentEdge() )
    {
      return false;
    }
    return accessEdge.getNextInOrderFrom(this).getNextInOrderFrom(this) == accessEdge;
  }

  /**
   * Returns a Vector of the Edge objects that are incident to this Node.
   *
   * @return Vector: The Edges that are incident to this Node.
   */
  public Vector incidentEdges()
  {
    Vector edgeVector = new Vector(numEdges);
    if ( accessEdge != null )
    {
      Edge currentEdge = accessEdge;
      do
      {
        edgeVector.addElement( currentEdge );
        currentEdge = (Edge)currentEdge.getNextInOrderFrom(this);
      }
      while ( currentEdge != accessEdge );
    }
    return edgeVector;
  }

  public EdgeIterator incidentEdgesIterator()
  {
    return new EdgeIterator(this, accessEdge);
  }

  public Vector incidentEdgesInReverse()
  {
    Vector edgeVector = new Vector(numEdges);
    if ( accessEdge != null )
    {
      Edge currentEdge = (Edge)accessEdge.getPreviousInOrderFrom(this);
      do
      {
        edgeVector.addElement( currentEdge );
        currentEdge = (Edge)currentEdge.getPreviousInOrderFrom(this);
      }
      while ( currentEdge != (Edge)accessEdge.getPreviousInOrderFrom(this) );
    }
    return edgeVector;
  }

  public EdgeIterator incidentEdgeInReverseIterator()
  {
    return new EdgeIterator(this, accessEdge);
  }

  public Vector incidentOutgoingEdges()
  {
    Vector edgeVector = new Vector(numEdges);
    if ( accessEdge != null )
    {
      Edge currentEdge = accessEdge;
      do
      {
        if ( currentEdge.getDirectedSourceNode() == null ||
             currentEdge.getDirectedSourceNode() == this )
        {
          edgeVector.addElement( currentEdge );
        }
        currentEdge = (Edge)currentEdge.getNextInOrderFrom(this);
      }
      while ( currentEdge != accessEdge );
    }
    return edgeVector;
  }

  public EdgeIterator incidentOutgoingEdgesIterator()
  {
    return new EdgeIterator(this, accessEdge);
  }

  public EdgeInterface incidentEdgeWith(NodeInterface aNode)
  {
    Vector edges = incidentEdges();
    Edge currentEdge;
    Edge returnEdge = null;
    for ( int i=0; i<edges.size(); i++ )
    {
      currentEdge = (Edge)edges.elementAt(i);
      if ( currentEdge.otherEndFrom(this) == aNode )
      {
        returnEdge = currentEdge;
      }
    }
    return returnEdge;
  }

  /**
   * Sets the label of this Node to the given label.
   *
   * @param String newLabel: The new label to assign to this Node.
   */
  public void setLabel(String newLabel) { label = newLabel; }

  public void appendLabel(String newLabel) { label+= newLabel; }

  /**
   * Sets the location of this Node to the given Point's location.
   *
   * @param Point aPoint: The Point describing the new location for this node.
   */

  public void setLocation(Location aLocation)
  {
    location = new Location(aLocation);
  }

  /**
   * Sets the location of this Node to the given x and y coordinates.
   *
   * @param int x: The x coordinate of the new location for this node.
   * @param int y: The y coordinate of the new location for this node.
   */
  public void setLocation(int x, int y) { location = new Location(x, y); }

  public void setLocation(double x, double y) { location = new Location(x, y); }

  public void translate(int transX, int transY)
  {
    location = new Location( location.intX() + transX, location.intY() + transY );
  }

  public void rotate(Location referencePoint, double angle)
  {
    double cos = Math.cos(Math.toRadians(angle));
    double sin = Math.sin(Math.toRadians(angle));
    double tempX = location.doubleX() - referencePoint.doubleX();
    double tempY = location.doubleY() - referencePoint.doubleY();
    location.setX( (cos * tempX - sin * tempY) + referencePoint.doubleX() );
    location.setY( (sin * tempX + cos * tempY) + referencePoint.doubleY() );
  }

  public static double angleBetween(Location p1, Location p2, Location p3)
  {
    double ax = p1.intX();
    double ay = p1.intY();
    double bx = p2.intX();
    double by = p2.intY();
    double cx = p3.intX();
    double cy = p3.intY();
    double crossProduct = (ax-bx)*(cy-by)-(ay-by)*(cx-bx);
    double dotProduct = (ax-bx)*(cx-bx)+(ay-by)*(cy-by);
    double tan = Math.abs( Math.toDegrees( Math.atan( crossProduct / dotProduct ) ) );
    double angle = tan;
    if ( dotProduct < 0 )
    {
      angle = 180.0 - angle;
    }
    if ( crossProduct < 0 )
    {
      angle = angle * -1;
    }
    return angle;
  }

  public static double angleBetween(Node node1, Node node2, Node node3, Node node4)
  {
    double x1 = node1.location.intX();
    double x2 = node2.location.intX();
    double x3 = node3.location.intX();
    double x4 = node4.location.intX();
    double y1 = node1.location.intY();
    double y2 = node2.location.intY();
    double y3 = node3.location.intY();
    double y4 = node4.location.intY();

    double ax, ay, bx, by, cx, cy;

    if ( x1 == x3 && y1 == y3 )
    {
      ax = x2;
      ay = y2;
      bx = x1;
      by = y1;
      cx = x4;
      cy = y4;
    }
    else if ( x1 == x4 && y1 == y4 )
    {
      ax = x2;
      ay = y2;
      bx = x1;
      by = y1;
      cx = x3;
      cy = y3;
    }
    else if ( x2 == x3 && y2 == y3 )
    {
      ax = x1;
      ay = y1;
      bx = x2;
      by = y2;
      cx = x4;
      cy = y4;
    }
    else if ( x2 == x4 && y2 == y4 )
    {
      ax = x1;
      ay = y1;
      bx = x2;
      by = y2;
      cx = x3;
      cy = y3;
    }
    else
    {
      double numerator = (x4-x3)*(y1-y3)-(y4-y3)*(x1-x3);
      double denominator = (y4-y3)*(x2-x1)-(x4-x3)*(y2-y1);
      if ( denominator == 0 )
      {
        return 0.0; // lines are parallel;
      }
      double temp = numerator / denominator;
      ax = x1;
      ay = y1;
      bx = x1 + temp*(x2-x1);
      by = y1 + temp*(y2-y1);
      cx = x3;
      cy = y3;
    }

    double crossProduct = (ax-bx)*(cy-by)-(ay-by)*(cx-bx); // cross product
    double dotProduct = (ax-bx)*(cx-bx)+(ay-by)*(cy-by); // dot product
    //double magnitude = Math.sqrt( Math.pow((ax-bx),2) + Math.pow((ay-by),2) ) *
    //                   Math.sqrt( Math.pow((cx-bx),2) + Math.pow((cy-by),2) );

    double tan = Math.abs( Math.toDegrees( Math.atan( crossProduct / dotProduct ) ) );
    //double cos = Math.abs( Math.toDegrees( Math.acos( dotProduct / magnitude ) ) );
    //double sin = Math.abs( Math.toDegrees( Math.asin( crossProduct / magnitude ) ) );
    double angle = tan;
    if ( dotProduct < 0 )
    {
      angle = 180.0 - angle;
    }
    if ( crossProduct < 0 )
    {
      angle = angle * -1;
    }
    return angle;
  }

  public void scaleBy( double minX, double minY, double xFactor, double yFactor)
  {
    double temp = xFactor * (location.doubleX() - minX);
    double tempX = location.doubleX();
    double tempY = location.doubleY();
    if ( temp > MIN_FOR_SCALE )
    {
      tempX = minX + temp;
    }
    temp = yFactor * (location.doubleY() - minY);
    if ( temp > MIN_FOR_SCALE )
    {
      tempY = minY + temp;
    }
    location = new Location( tempX, tempY );
  }

  /**
   * Sets whether or not this Node is selected.
   *
   * @param boolean: Whether or not this Node is selected.
   */
  public void setSelected(boolean state) { isSelected = state; }

  public void setSpecialSelected(boolean state) { isSpecialSelected = state; }

  /**
   * Toggles whether or not this Node is selected.
   */
  public void toggleSelected() { isSelected = !isSelected; }

  public void toggleSpecialSelected() { isSpecialSelected = !isSpecialSelected; }

  public Edge getAccessEdge() { return accessEdge; }

  public void setAccessEdge(Edge aEdge) { accessEdge = aEdge; }

  public boolean hasEdge(EdgeInterface edge) { return incidentEdges().contains(edge); }

  /**
   * Adds the given Edge as an incident Edge to this Node.
   *
   * @param Edge e: The Edge to add as incident to this Node.
   */
  public boolean addIncidentEdge(EdgeInterface edge)
  {
    Edge e = (Edge)edge;
    if (!incidentEdges().contains(e))
    {
      addIncidentEdgeNoCheck(e);
      return true;
    }
    else
    {
      return false;
    }
  }

  /**
   * Adds the given Edge as an incident Edge to this Node. Allowing duplicates.
   *
   * @param Edge e: The Edge to add as incident to this Node.
   */
  public void addIncidentEdgeNoCheck(EdgeInterface edge)
  {
    Edge e = (Edge)edge;
    numEdges++;
    if ( accessEdge == null )
    {
      accessEdge = e;
      accessEdge.setNextInOrderFrom(this, accessEdge);
      accessEdge.setPreviousInOrderFrom(this, accessEdge);
    }
    else if ( accessEdge.getNextInOrderFrom( this ) == accessEdge )
    {
      accessEdge.setNextInOrderFrom(this, e);
      accessEdge.setPreviousInOrderFrom(this, e);
      e.setNextInOrderFrom(this, accessEdge);
      e.setPreviousInOrderFrom(this, accessEdge);
    }
    else
    {
      Edge prev = (Edge)accessEdge.getPreviousInOrderFrom(this);
      accessEdge.setPreviousInOrderFrom(this, e);
      e.setNextInOrderFrom(this, accessEdge);
      e.setPreviousInOrderFrom(this, prev);
      prev.setNextInOrderFrom(this, e);
    }
  }

  public void addEdgeBetween( EdgeInterface edge, EdgeInterface prev,
                              EdgeInterface next )
  {
    numEdges++;
    prev.setNextInOrderFrom(this, edge);
    edge.setPreviousInOrderFrom(this, prev);
    edge.setNextInOrderFrom(this, next);
    next.setPreviousInOrderFrom(this, edge);
    if ( accessEdge == null )
    {
      if ( edge instanceof Edge )
      {
        accessEdge = (Edge)edge;
      }
      else
      {
        accessEdge = ((EdgeExtender)edge).getRef();
      }
    }
  }

  public void resetIncidentEdges()
  {
    numEdges = 0;
    accessEdge = null;
  }

  /**
   * Removes the given Edge from the incident Edges of this Node.
   *
   * @param Edge e: The edge to remove from the incident Edges of this Node.
   */
  public void deleteIncidentEdge(EdgeInterface edge)
  {
    Edge e = (Edge)edge;
    numEdges--;
    if ( e.getNextInOrderFrom(this) == e )
    {
      accessEdge = null;
    }
    else if ( e.getNextInOrderFrom(this).getNextInOrderFrom(this) == e )
    {
      accessEdge = (Edge)e.getNextInOrderFrom(this);
      accessEdge.setNextInOrderFrom( this, accessEdge );
      accessEdge.setPreviousInOrderFrom( this, accessEdge );
    }
    else
    {
      if ( e == accessEdge )
      {
        accessEdge = (Edge)accessEdge.getNextInOrderFrom(this);
      }
      Edge prev, next;
      prev = (Edge)e.getPreviousInOrderFrom(this);
      next = (Edge)e.getNextInOrderFrom(this);
      prev.setNextInOrderFrom(this, next);
      next.setPreviousInOrderFrom(this, prev);
    }
  }

  public double distanceSquaredFrom(Node otherNode)
  {
    double ax = location.doubleX();
    double ay = location.doubleY();
    double bx = otherNode.location.doubleX();
    double by = otherNode.location.doubleY();
    return Math.pow((bx-ax),2) + Math.pow((by-ay),2);
  }

  /**
   * Returns a String representation of this Node (ie. label, x coordinate,
   * y coordinate).
   *
   * @return String: A String representation of this Node.
   */
  public String toString()
  {
    return(label + "(" + location.intX() + "," + location.intY() + ")");
  }

  public void printAll()
  {
    Vector incidentEdges = incidentEdges();
    System.out.print("** " + this + " ** " );
    for ( int i=0; i<incidentEdges.size(); i++ )
    {
      System.out.print(incidentEdges.elementAt(i));
      if ( i < incidentEdges.size() -1 )
      {
        System.out.print(", ");
      }
      else
      {
        System.out.print("\n");
      }
    }
  }

  /**
   * Returns a Vector of all Nodes that are connected to this Node
   * by a single Edge.
   *
   * @return Vector: A Vector of all neighbouring Nodes of this Node.
   */
  public Vector neighbours()
  {
    Vector result = new Vector(numEdges);
    Enumeration edges = incidentEdges().elements();
    while(edges.hasMoreElements())
      result.addElement(((Edge)edges.nextElement()).otherEndFrom(this));
    return result;
  }

  /**
   * Draw the Node with default Colors.
   *
   * @param Graphics aPen: The Graphics object to use to draw the Node.
   */
  public void draw( Graphics2D g2, boolean drawSelected,
                    boolean showCoord, boolean showLabel )
  {
    draw( g2, 0, 0, drawSelected, showCoord, showLabel );
  }

  public void draw( Graphics2D g2, int xOffset, int yOffset,
                    boolean drawSelected, boolean showCoord, boolean showLabel )
  {
    if ( isVisible )
    {
      g2.setStroke(new BasicStroke( (float)LINE_THICKNESS ));
      g2.setColor(color);
      Location aLocation;
      aLocation = new Location(location.intX() + xOffset, location.intY() + yOffset);
      g2.fill (new Ellipse2D.Double( aLocation.intX() - RADIUS,
                                     aLocation.intY() - RADIUS,
                                     RADIUS * 2, RADIUS * 2) );
      // Draw a black border around the circle
      g2.setColor(Color.black);
      g2.draw ( new Ellipse2D.Double( aLocation.intX() - RADIUS,
                                      aLocation.intY() - RADIUS,
                                      RADIUS * 2, RADIUS * 2 ) );

      if ( isSpecialSelected )
      {
        float dash1[] = {(float)DASH_LENGTH};
        g2.setStroke(new BasicStroke( (float)LINE_THICKNESS,
                                      BasicStroke.CAP_BUTT,
                                      BasicStroke.JOIN_MITER,
                                      10.0f, dash1, 0.0f ));
        g2.setColor(SPECIAL_SELECTED_COLOR);
        g2.draw ( new Ellipse2D.Double( aLocation.intX() - (RADIUS+2),
                                        aLocation.intY() - (RADIUS+2),
                                        (RADIUS+2)*2, (RADIUS+2)*2 ) );
      }
      else if ( isSelected && drawSelected )
      {
        float dash1[] = {(float)DASH_LENGTH};
        g2.setStroke(new BasicStroke( (float)LINE_THICKNESS,
                                      BasicStroke.CAP_BUTT,
                                      BasicStroke.JOIN_MITER,
                                      10.0f, dash1, 0.0f ));
        g2.setColor(SELECTED_COLOR);
        g2.draw ( new Ellipse2D.Double( aLocation.intX() - (RADIUS+2),
                                        aLocation.intY() - (RADIUS+2),
                                        (RADIUS+2)*2, (RADIUS+2)*2 ) );
      }

      if ( drawX )
      {
        g2.setStroke(new BasicStroke( (float)LINE_THICKNESS*2.0f/3.0f ));
        g2.setColor(Color.black);
        g2.drawLine( aLocation.intX() - RADIUS, aLocation.intY() - RADIUS,
                     aLocation.intX() + RADIUS, aLocation.intY() + RADIUS );
        g2.drawLine( aLocation.intX() - RADIUS, aLocation.intY() + RADIUS,
                     aLocation.intX() + RADIUS, aLocation.intY() - RADIUS );
      }
      g2.setStroke(new BasicStroke( (float)LINE_THICKNESS ));
      if ( showLabel )
      {
        if ( label.length() > 0 )
        {
          thisTl = new TextLayout(label, drawTextFont, g2.getFontRenderContext());
          if ( OPAQUE_TEXT )
          {
            Rectangle2D bounds = thisTl.getBounds();
            g2.setColor(userInterface.GraphEditor.backgroundColor);
            g2.fill( new Rectangle2D.Double( aLocation.intX() + RADIUS+3,
                                             aLocation.intY() + RADIUS+2
                                               - bounds.getHeight() + 1,
                                             bounds.getWidth() + 2,
                                             bounds.getHeight() ) );
          }
          g2.setColor(TEXT_COLOR);
          thisTl.draw(g2, aLocation.intX() + RADIUS+3, aLocation.intY() + RADIUS+2);
        }
      }
      else if ( showCoord )
      {
        thisTl = new TextLayout( String.valueOf(location.intX()) + ", " +
                                 String.valueOf(location.intY()),
                                 drawTextFont, g2.getFontRenderContext());
        if ( OPAQUE_TEXT )
        {
          Rectangle2D bounds = thisTl.getBounds();
          g2.setColor(userInterface.GraphEditor.backgroundColor);
          g2.fill( new Rectangle2D.Double( aLocation.intX() + RADIUS+3,
                                           aLocation.intY() + RADIUS+2
                                             - bounds.getHeight() + 1,
                                           bounds.getWidth() + 2,
                                           bounds.getHeight() ) );
        }
        g2.setColor(TEXT_COLOR);
        thisTl.draw(g2, aLocation.intX() + RADIUS+3, aLocation.intY() + RADIUS+2);
      }
    }
  }

  /**
   * Save this Node to the File that is provided as a Parameter. Note that the
   * incident edges are not saved at this point.
   *
   * @param PrintWriter aFile: The file to save to that is open/ready for output.
   */
  public void saveTo(PrintWriter aFile)
  {
    aFile.println(index);
    aFile.println(location.doubleX());
    aFile.println(location.doubleY());
    aFile.println(label);
    aFile.println(color.getRGB());
    Vector incidentEdges = incidentEdges();
    for ( int i=0; i<incidentEdges.size(); i++ )
    {
      aFile.print( ((Edge)incidentEdges.elementAt(i)).getIndex() );
      if ( i < incidentEdges.size()-1 )
      {
        aFile.print( "," );
      }
    }
    aFile.println();
  }

  /**
   * Load this Node from the File that is provided. Note that the incident edges
   * are not connected.
   *
   * @param BufferedReader aFile: The file to load from that is open/ready for input.
   */
  public static Node loadFrom(BufferedReader aFile, Vector edgeIndices) throws IOException
  {
    Node aNode = new Node();
    aNode.setIndex(Integer.valueOf(aFile.readLine()).intValue());
    aNode.setLocation( new Location (
                        Double.valueOf(aFile.readLine()).doubleValue(),
                        Double.valueOf(aFile.readLine()).doubleValue()) );
    aNode.setLabel(aFile.readLine());
    aNode.setColor(new Color(Integer.valueOf(aFile.readLine()).intValue()));
    String edgeIndexString = aFile.readLine();
    StringTokenizer st = new StringTokenizer( edgeIndexString, "," );
    Vector indices = new Vector();
    while ( st.hasMoreTokens() )
    {
      indices.addElement( Integer.valueOf(st.nextToken()) );
    }
    edgeIndices.addElement(indices);
    return(aNode);
  }

  public void setExtender(NodeExtender ex)
  {
    extender = ex;
  }

  public NodeExtender getExtender()
  {
    return extender;
  }

  public Node getNode()
  {
    return this;
  }
}
