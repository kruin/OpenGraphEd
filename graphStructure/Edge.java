package graphStructure;

import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.geom.*;

/**
 * This class represents an edge object that links two nodes to each other.
 * Edges can be added, deleted, blocked or made directional by using the
 * interface of the editor component.
 *
 * @author Jon Harris
 */

public class Edge implements EdgeInterface
{
  /** The width in pixels to use when drawing this Edge in the UI */
  public static int THICKNESS = 4;
  public static int GENERATED_DASH_LENGTH = 10;
  public static int SELECTED_DASH_LENGTH = 2;
  public static int ARROW_WIDTH =6 ;
  public static int ARROW_HEIGHT = 20;
  public static double CURVE_INTERVAL = 0.01;
  /** The Color to use when drawing this Edge in the UI */
  public static Color DEFAULT_COLOR = Color.blue;
  public static Color SELECTED_COLOR = Color.black;
  /** The Color to use when drawing this Edge as selected Edge in the UI */
  private Color color;
  private boolean isSelected;
  private boolean isAdded;
  private boolean isGenerated;
  private boolean isCurved;
  private boolean isOrthogonal;
  private boolean isOrthogonalLeftFromStart;
  private HalfEdge startEdge;
  private HalfEdge endEdge;
  private EdgeInterface copy;
  private Node directedSourceNode;
  private Location centerLocation;
  private double startControlAngle;
  private double endControlAngle;
  private int index;
  private EdgeExtender extender;
  private boolean isVisible;

  public Color getColor() { return color; }

  public void setColor(Color aColor) { color = aColor; }

  public void setIsAdded(boolean aAdded) { isAdded = aAdded; }

  public boolean isAdded() { return isAdded; }

  public boolean isGenerated() { return isGenerated; }

  public void setIsGenerated(boolean generated) { isGenerated = generated; }

  public int getIndex() { return index; }

  public void setIndex(int index) { this.index = index; }

  public boolean isCurved() { return isCurved; }

  public void setIsCurved(boolean curved) { isCurved = curved; }
  
  public void makeCurved()
  {
    isCurved = true;
    isOrthogonal = false;
    initCurveAngles();
  }
  
  public boolean isOrthogonal() { return isOrthogonal; }

  public void setIsOrthogonal(boolean orth) { isOrthogonal = orth; }

  public void makeOrthogonal()
  {
    isOrthogonal = true;
    isCurved = false;
    initOrthogonalBendLocation();
  }
  
  private void initOrthogonalBendLocation()
  {
    Location center = getOrthogonalLocation();
    if ( center == null )
    {
      makeStraight();
    }
    else
    {
      centerLocation = center;
    }
  }
  
  public void setCopy(EdgeInterface aCopy) { copy = aCopy; }

  public EdgeInterface getCopy() { return copy; }

  public EdgeInterface getMasterCopy()
  {
    if ( copy == null )
    {
      return null;
    }
    Edge cp = this;
    while ( cp.copy != null )
    {
      cp = (Edge)cp.copy;
    };
    return cp;
  }
  
  public void setIsVisible(boolean v) { isVisible = v; }

  /**
   * Constructor for class Edge that constructs an edge based on the
   * start and end nodes.
   *
   * @param Node start: The start node of the new Edge.
   * @param Node end: The end node of the new Edge.
   */
  protected Edge(NodeInterface start, NodeInterface end)
  {
    initialize(start, end);
    color = DEFAULT_COLOR;
    centerLocation = new Location( ( (start.getX() + end.getX())/2 ),
                                   ( (start.getY() + end.getY())/2 ) );
  }

  public Edge(Edge anEdge, NodeInterface dNode, NodeInterface start, NodeInterface end)
  {
    initialize(start, end);
    if ( anEdge.color != null )
    {
      color = new Color(anEdge.color.getRGB());
    }
    else
    {
      color = DEFAULT_COLOR;
    }
    isSelected = anEdge.isSelected;
    isGenerated = anEdge.isGenerated;
    isCurved = anEdge.isCurved;
    isOrthogonal = anEdge.isOrthogonal;
    isOrthogonalLeftFromStart = anEdge.isOrthogonalLeftFromStart;
    centerLocation = new Location( anEdge.getCenterLocation() );
    startControlAngle = anEdge.startControlAngle;
    endControlAngle = anEdge.endControlAngle;
    directedSourceNode = (Node)dNode;
    isVisible = anEdge.isVisible;
  }

  /**
   * Returns whether or not this Edge is equal to the given Object.<br>
   * To be equal, the Object must be an edge and have the same start and end nodes.
   *
   * @return boolean: Whether or not this Edge is equal to the given Object.
   */
  public boolean equals(Object object)
  {
    try
    {
      Edge compareEdge = (Edge)object;
      if ((((Node)getStartNode()).equals(compareEdge.getStartNode()) && ((Node)getEndNode()).equals(compareEdge.getEndNode())) ||
          (((Node)getEndNode()).equals(compareEdge.getStartNode()) && ((Node)getStartNode()).equals(compareEdge.getEndNode())))
      {
        return true;
      }
      else
      {
        return false;
      }
    }
    catch (ClassCastException cce)
    {
      return false;
    }
  }

  public Node getDirectedSourceNode()
  {
    return directedSourceNode;
  }

  public void setDirectedFrom(NodeInterface directedSourceNode)
  {
    if ( directedSourceNode == null )
    {
      this.directedSourceNode = null;
    }
    else if ( getStartNode() == directedSourceNode ||
              getEndNode() == directedSourceNode )
    {
      this.directedSourceNode = (Node)directedSourceNode;
    }
  }

  public boolean isBetween(NodeInterface firstNode, NodeInterface secondNode)
  {
    return ( (((Node)getStartNode()).equals(firstNode) && ((Node)getEndNode()).equals(secondNode)) ||
             (((Node)getStartNode()).equals(secondNode) && ((Node)getEndNode()).equals(firstNode)) );
  }

  // initialize instance variables...
  private void initialize(NodeInterface start, NodeInterface end)
  {
    isSelected = false;
    isGenerated = false;
    isCurved = false;
    isOrthogonal = false;
    isOrthogonalLeftFromStart = false;
    isAdded = false;
    startEdge = null;
    endEdge = null;
    startControlAngle = 0;
    endControlAngle = 0;
    startEdge = new HalfEdge((Node)start, this, null);
    endEdge = new HalfEdge((Node)end, this, startEdge);
    startEdge.setTwinEdge(endEdge);
    isVisible = true;
  }

  /**
   * Returns the start Node of this Edge.
   *
   * @return Node: The start Node of this Edge.
   */
  public NodeInterface getStartNode() { return startEdge.getSourceNode(); }

  /**
   * Returns the end Node of this Edge.
   *
   * @return Node: The end Node of this Edge.
   */
  public NodeInterface getEndNode() { return endEdge.getSourceNode(); }

  public HalfEdge getStartHalfEdge() { return startEdge; }

  public HalfEdge getEndHalfEdge() { return endEdge; }

  public HalfEdge getHalfEdgeFrom(Node aNode)
  {
    if ( aNode == getStartNode() )
    {
      return startEdge;
    }
    else if ( aNode == getEndNode() )
    {
      return endEdge;
    }
    else
    {
      return null;
    }
  }

  public HalfEdge getHalfEdgeTo(Node aNode)
  {
    if ( aNode == getEndNode() )
    {
      return startEdge;
    }
    else if ( aNode == getStartNode() )
    {
      return endEdge;
    }
    else
    {
      return null;
    }
  }

  // set the next edge in counter clockwise order around the given node.
  public void setNextInOrderFrom( NodeInterface sourceNode, EdgeInterface nextEdge )
  {
    getHalfEdgeFrom( (Node)sourceNode ).setPrevious( ((Edge)nextEdge).getHalfEdgeTo( (Node)sourceNode ) );
  }

  public void setPreviousInOrderFrom( NodeInterface sourceNode, EdgeInterface prevEdge )
  {
    getHalfEdgeTo( (Node)sourceNode ).setNext( ((Edge)prevEdge).getHalfEdgeFrom( (Node)sourceNode ) );
  }

  public EdgeInterface getNextInOrderFrom( NodeInterface sourceNode )
  {
    return getHalfEdgeFrom( (Node)sourceNode ).getPrevious().getParentEdge();
  }

  public EdgeInterface getPreviousInOrderFrom( NodeInterface sourceNode )
  {
    return getHalfEdgeTo( (Node)sourceNode ).getNext().getParentEdge();
  }

  public int getLowerIndex()
  {
    return Math.min(((Node)getStartNode()).getIndex(), ((Node)getEndNode()).getIndex());
  }

  public int getHigherIndex()
  {
    return Math.max(((Node)getStartNode()).getIndex(), ((Node)getEndNode()).getIndex());
  }

  /**
   * Returns whether or not this Edge is selected.
   *
   * @return boolean: Whether or not this Edge is selected.
   */
  public boolean isSelected() { return isSelected; }

  /**
   * Sets whether or not this Edge is selected.
   *
   * @param boolean state: Whether or not this Edge is selected.
   */
  public void setSelected(boolean state) { isSelected = state; }

  /**
   * Toggles whether or not this Edge is selected.
   */
  public void toggleSelected() { isSelected = !isSelected; }

  /**
   * Returns a String representation of this Edge.
   *
   * @return String: A String representation of this Edge.
   */
  public String toString()
  {
    return(((Node)getStartNode()).toString() + " --> " + ((Node)getEndNode()).toString());
  }

/*  public String infoString()
  {
    return "used: " + used + " added: " + added + " backEdge: " + backEdge +
    "old: " + isOld + " isGeneratedEdge: " + isGeneratedEdge +
    "startNode: " + getStartNode() + " endNode: " + getEndNode() + "hash: " + hashCode() +
    "\ns he: " + startEdge.infoString() + "\ne he: " + endEdge.infoString();
  }*/

  /**
   * Returns the Node at the other end of this Edge from the given Node.
   *
   * @param Node aNode: The Node to use to find the Node at the other end of this Edge.
   * @return Node: The Node at the other end of this Edge from the given Node.
   */
  public NodeInterface otherEndFrom(NodeInterface aNode)
  {
    if (getStartNode() == aNode)
      return getEndNode();
    else
      return getStartNode();
  }

  public Vector edgesFromSameCycle()
  {
    Vector edgeVector = new Vector();
    HalfEdge he = startEdge;
    do
    {
      edgeVector.addElement(he.getParentEdge());
      he = he.getNext();
    }
    while (he != startEdge);
    return edgeVector;
  }

  public Vector edgesFromSameCycleOnOtherSide()
  {
    Vector edgeVector = new Vector();
    HalfEdge he = endEdge;
    do
    {
      edgeVector.addElement(he.getParentEdge());
      he = he.getNext();
    }
    while (he != endEdge);
    return edgeVector;
  }

  public boolean isDirected() { return directedSourceNode != null; }

  public QuadCurve2D.Double getQuadCurve()
  {
    Location sLocation, eLocation;
    sLocation = ((Node)getStartNode()).getLocation();
    eLocation = ((Node)getEndNode()).getLocation();
    return new QuadCurve2D.Double( sLocation.intX(), sLocation.intY(),
                                   centerLocation.intX()*2 - sLocation.intX()/2 - eLocation.intX()/2,
                                   centerLocation.intY()*2 - sLocation.intY()/2 - eLocation.intY()/2,
                                   eLocation.intX(), eLocation.intY() );
  }

  public void draw(Graphics2D g2, boolean drawSelected)
  {
    draw(g2, 0, 0, drawSelected);
  }

  public void draw( Graphics2D g2, int xOffset, int yOffset,
                    boolean drawSelected )
  {
    if ( isVisible &&
         !getStartNode().getLocation().equals(getEndNode().getLocation()) )
    {
      Location sLocation, eLocation, cLocation;
      sLocation = ((Node)getStartNode()).getLocation();
      eLocation = ((Node)getEndNode()).getLocation();
      cLocation = centerLocation;
      sLocation = new Location( sLocation.intX() + xOffset, sLocation.intY() + yOffset );
      eLocation = new Location( eLocation.intX() + xOffset, eLocation.intY() + yOffset );
      cLocation = new Location( cLocation.intX() + xOffset, cLocation.intY() + yOffset );

      float dash1[] = {(float)SELECTED_DASH_LENGTH};
      if ( isSelected && drawSelected )
      {
        g2.setStroke(new BasicStroke( (float)2*THICKNESS,
                                      BasicStroke.CAP_BUTT,
                                      BasicStroke.JOIN_MITER,
                                      10.0f, dash1, 0.0f ));
        g2.setColor(SELECTED_COLOR);
        if ( isCurved )
        {
          drawCurved( g2, sLocation, eLocation, cLocation );
        }
        else if ( isOrthogonal )
        {
          drawOrthogonal( g2, sLocation, eLocation, cLocation );
        }
        else
        {
          drawStraight( g2, sLocation, eLocation );
        }
      }
      g2.setColor(color);
      if ( isGenerated )
      {
        dash1[0] = (float)GENERATED_DASH_LENGTH;
        g2.setStroke(new BasicStroke( (float)THICKNESS,
                                      BasicStroke.CAP_BUTT,
                                      BasicStroke.JOIN_MITER,
                                      10.0f, dash1, 0.0f ));
      }
      else
      {
        g2.setStroke(new BasicStroke( (float)THICKNESS ));
      }
      if ( isCurved )
      {
        drawCurved( g2, sLocation, eLocation, cLocation );
      }
      else if ( isOrthogonal )
      {
        drawOrthogonal( g2, sLocation, eLocation, cLocation );
      }
      else
      {
        drawStraight( g2, sLocation, eLocation );
      }
      g2.setStroke(new BasicStroke( (float)THICKNESS ));
      g2.setColor(color);
      if ( isDirected() )
      {
        Location dLocation = directedSourceNode.getLocation();
        dLocation = new Location( dLocation.intX() + xOffset, dLocation.intY() + yOffset );
        drawDirected( g2, xOffset, yOffset );
      }
      else
      {
        if ( isSelected && drawSelected )
        {
          g2.setStroke(new BasicStroke( 1.0f ));
          g2.setColor(SELECTED_COLOR);
          g2.fill( new Ellipse2D.Double( cLocation.intX()-(THICKNESS+1),
                                         cLocation.intY()-(THICKNESS+1),
                                         (THICKNESS+1) * 2, (THICKNESS+1) * 2 ) );
          g2.setColor(color);
        }
        g2.fill( new Ellipse2D.Double( cLocation.intX()-THICKNESS,
                                       cLocation.intY()-THICKNESS,
                                       THICKNESS * 2, THICKNESS * 2 ) );
      }
    }
  }

  /**
   * Draw the Edge, directed from the given source node, allowing it to be drawn
   * with a different Color.
   * Directed Edges are drawn with a triangle indicating their
   * direction at their centre point.
   *
   * @param Graphics aPen: The Graphics object to use to draw the Edge.
   */
  private void drawDirected( Graphics2D g2, int xOffset, int yOffset )
  {
    g2.fill(getDirectionArrow(directedSourceNode, xOffset, yOffset));
  }

  public Polygon getDirectionArrow(Node directionSource, int xOffset, int yOffset)
  {
    return getDirectionArrow(directionSource, xOffset, yOffset, 0, 0);
  }

  public Polygon getDirectionArrow(Node directionSource, int xOffset, int yOffset,
                                   int extraWidth, int extraHeight)
  {
    double edgeAngle = -1.0 * getDirectedAngle(directionSource);
    double midPointX = centerLocation.intX() + xOffset;
    double midPointY = centerLocation.intY() + yOffset;
    int width = ARROW_WIDTH + extraWidth;
    int height = ARROW_HEIGHT + extraHeight;
    midPointX = (int)Math.round( midPointX - height/2.0 * Math.cos( Math.toRadians( edgeAngle ) ) );
    midPointY = (int)Math.round( midPointY - height/2.0 * Math.sin( Math.toRadians( edgeAngle ) ) );

    int triX[] = new int[3];
    int triY[] = new int[3];
    triX[0] = (int)Math.round( midPointX + width * Math.cos( Math.toRadians( edgeAngle+90.0 ) ) );
    triX[1] = (int)Math.round( midPointX + width * Math.cos( Math.toRadians( edgeAngle-90.0 ) ) );
    triX[2] = (int)Math.round( midPointX + height * Math.cos( Math.toRadians( edgeAngle ) ) );
    triY[0] = (int)Math.round( midPointY + width * Math.sin( Math.toRadians( edgeAngle+90.0 ) ) );
    triY[1] = (int)Math.round( midPointY + width * Math.sin( Math.toRadians( edgeAngle-90.0 ) ) );
    triY[2] = (int)Math.round( midPointY + height * Math.sin( Math.toRadians( edgeAngle ) ) );
    return new Polygon(triX, triY, 3);
  }

  private void drawCurved( Graphics2D g2, Location sLocation,
                           Location eLocation, Location cLocation )
  {
    g2.draw( new QuadCurve2D.Double( sLocation.intX(), sLocation.intY(),
                                     cLocation.intX()*2 - sLocation.intX()/2 - eLocation.intX()/2,
                                     cLocation.intY()*2 - sLocation.intY()/2 - eLocation.intY()/2,
                                     eLocation.intX(), eLocation.intY() ) );
  }

  public QuadCurve2D.Double getCurve()
  {
    return getCurve(0,0);
  }

  public QuadCurve2D.Double getCurve(int xOffset, int yOffset)
  {
    int cPoint[] = new int[6];
    cPoint[0] = getStartNode().getLocation().intX() + xOffset;
    cPoint[1] = getStartNode().getLocation().intY() + yOffset;
    cPoint[4] = getEndNode().getLocation().intX() + xOffset;
    cPoint[5] = getEndNode().getLocation().intY() + yOffset;
    cPoint[2] = (centerLocation.intX() + xOffset)*2 - cPoint[0]/2 - cPoint[4]/2;
    cPoint[3] = (centerLocation.intY() + yOffset)*2 - cPoint[1]/2 - cPoint[5]/2;
    return new QuadCurve2D.Double( cPoint[0], cPoint[1], cPoint[2],
                                   cPoint[3], cPoint[4], cPoint[5] );
  }

  public Polygon getBend()
  {
    return getBend(0,0);
  }
  
  public Polygon getBend(int xOffset, int yOffset)
  {
    int triX[] = new int[3];
    int triY[] = new int[3];
    triX[0] = getStartNode().getLocation().intX() + xOffset;
    triX[1] = centerLocation.intX() + xOffset;
    triX[2] = getEndNode().getLocation().intX() + xOffset;
    triY[0] = getStartNode().getLocation().intY() + yOffset;
    triY[1] = centerLocation.intY() + yOffset;
    triY[2] = getEndNode().getLocation().intY() + yOffset;
    return new Polygon(triX, triY, 3);
  }
  
  private void drawStraight( Graphics2D g2, Location sLocation,
                             Location eLocation )
  {
    g2.draw( new Line2D.Double( sLocation.intX(), sLocation.intY(),
                                eLocation.intX(), eLocation.intY() ) );
  }
  
  private void drawOrthogonal( Graphics2D g2, Location sLocation,
                               Location eLocation, Location cLocation )
  {
    g2.draw( new Line2D.Double( sLocation.intX(), sLocation.intY(),
                                cLocation.intX(), cLocation.intY() ) );
    g2.draw( new Line2D.Double( cLocation.intX(), cLocation.intY(),
                                eLocation.intX(), eLocation.intY() ) );
  }

  /**
   * Save this Edge to the File that is provided as a Parameter. The Nodes that
   * are the end points of the Edge are not saved since we assume that Node locations
   * are unique identifiers for the Nodes.
   *
   * @param PrintWriter aFile: The file to save to that is open/ready for output.
   */
  public void saveTo(PrintWriter aFile)
  {
    aFile.println(index);
    aFile.println(((Node)getStartNode()).getIndex());
    aFile.println(((Node)getEndNode()).getIndex());
    if ( isDirected() )
    {
      aFile.println(directedSourceNode.getIndex());
    }
    else
    {
      aFile.println("-1");
    }
    aFile.println(centerLocation.doubleX());
    aFile.println(centerLocation.doubleY());
    aFile.println(isCurved);
    aFile.println(isOrthogonal);
    aFile.println(isGenerated);
    aFile.println(color.getRGB());
  }

  /**
   * Load this Edge from the File that is provided. Note that the nodes themselves are
   * not loaded. We are actually making temporary nodes here that do not correspond to
   * the actual graph nodes that this edge connects.  We'll have to throw out these
   * TEMP nodes later and replace them with the actual graph nodes that connect to this
   * edge.
   *
   * @param BufferedReader aFile: The file to load from that is open/ready for input.
   */
  public static Edge loadFrom(BufferedReader aFile, Vector nodeVector) throws IOException
  {
    Edge anEdge;
    int index = Integer.valueOf(aFile.readLine()).intValue();
    int startIndex = Integer.valueOf(aFile.readLine()).intValue();
    int endIndex = Integer.valueOf(aFile.readLine()).intValue();
    anEdge = new Edge( (Node)nodeVector.elementAt(startIndex-1),
                       (Node)nodeVector.elementAt(endIndex-1) );
    anEdge.setIndex(index);
    int directedSourceIndex = Integer.valueOf(aFile.readLine()).intValue();
    if ( directedSourceIndex != -1 )
    {
      if ( directedSourceIndex == startIndex )
      {
        anEdge.setDirectedFrom(anEdge.getStartNode());
      }
      else if ( directedSourceIndex == endIndex )
      {
        anEdge.setDirectedFrom(anEdge.getEndNode());
      }
      else
      {
        throw new IOException("Direction source was not an end node of an edge");
      }
    }
    
    anEdge.setCenterLocation( new Location (
                                Double.valueOf(aFile.readLine()).doubleValue(),
                                Double.valueOf(aFile.readLine()).doubleValue()) );
    anEdge.isCurved = Boolean.valueOf(aFile.readLine()).booleanValue();
    anEdge.isOrthogonal = Boolean.valueOf(aFile.readLine()).booleanValue();
    if ( anEdge.isCurved )
    {
      anEdge.initCurveAngles();
    }
    else if ( anEdge.isOrthogonal )
    {
      
    }
    anEdge.setIsGenerated( Boolean.valueOf(aFile.readLine()).booleanValue() );
    anEdge.setColor( new Color(Integer.valueOf(aFile.readLine()).intValue()) );
    return (anEdge);
  }

  /**
   * Returns the slope of this Edge.
   *
   * @return double: The slope of this Edge.
   */
  public double getSlope()
  {
    double rise = Math.abs(((Node)getStartNode()).getLocation().intY() - ((Node)getEndNode()).getLocation().intY());
    double run = Math.abs(((Node)getStartNode()).getLocation().intX() - ((Node)getEndNode()).getLocation().intX());
    if (run == 0)
      return Double.MAX_VALUE;
    return (rise / run);
  }

  /**
   * Returns the length of this Edge.
   *
   * @return double: The length of this Edge.
   */
  public double getStraightLength()
  {
    int ax, ay, bx, by;
    ax = ((Node)getStartNode()).getLocation().intX();
    ay = ((Node)getStartNode()).getLocation().intY();
    bx = ((Node)getEndNode()).getLocation().intX();
    by = ((Node)getEndNode()).getLocation().intY();
    return Math.sqrt( (bx-ax)*(bx-ax) + (by-ay)*(by-ay) );
  }

  public double getLength()
  {
    if ( isCurved )
    {
      double length = 0;
      Location sLocation = ((Node)getStartNode()).getLocation();
      Location cLocation = centerLocation;
      Location eLocation = ((Node)getEndNode()).getLocation();
      // convert from center point to bezier control point
      cLocation = new Location( (2*cLocation.doubleX() -
                                 sLocation.doubleX()/2 - eLocation.doubleX()/2),
                                (2*cLocation.doubleY() -
                                 sLocation.doubleY()/2 - eLocation.doubleY()/2) );

      double nextX, currentX = sLocation.doubleX();
      double nextY, currentY = sLocation.doubleY();;

      for ( double step = 0; step < 1.0 ; step+=CURVE_INTERVAL )
      {
        nextX = ((sLocation.doubleX() - 2*cLocation.doubleX() + eLocation.doubleX()) *
                 Math.pow(step, 2.0)) +
                ((2*cLocation.doubleX() - 2*sLocation.doubleX()) * step) +
                sLocation.doubleX();
        nextY = ((sLocation.doubleY() - 2*cLocation.doubleY() + eLocation.doubleY()) *
                 Math.pow(step, 2.0)) +
                ((2*cLocation.doubleY() - 2*sLocation.doubleY()) * step) +
                sLocation.doubleY();
        length+= Math.sqrt( (nextX-currentX)*(nextX-currentX) +
                            (nextY-currentY)*(nextY-currentY) );
        currentX = nextX;
        currentY = nextY;
      }
      return length;
    }
    else if ( isOrthogonal )
    {
      int ax, ay, bx, by;
      ax = getStartNode().getLocation().intX();
      ay = getStartNode().getLocation().intY();
      bx = centerLocation.intX();
      by = centerLocation.intY();
      double length = Math.sqrt( (bx-ax)*(bx-ax) + (by-ay)*(by-ay) );
      ax = centerLocation.intX();
      ay = centerLocation.intY();
      bx = getEndNode().getLocation().intX();
      by = getEndNode().getLocation().intY();
      return length + Math.sqrt( (bx-ax)*(bx-ax) + (by-ay)*(by-ay) );
    }
    else
    {
      return getStraightLength();
    }
  }
  public void setCenterLocation(Location aLocation)
  {
    centerLocation = new Location(aLocation);
  }

  public void initCurveAngles()
  {
    startControlAngle = Node.angleBetween( centerLocation,
                                           getStartNode().getLocation(),
                                           getEndNode().getLocation() );
    endControlAngle = Node.angleBetween( centerLocation,
                                         getEndNode().getLocation(),
                                         getStartNode().getLocation() );
  }
  
  public void translate(int transX, int transY)
  {
    centerLocation = new Location( centerLocation.intX() + transX,
                                   centerLocation.intY() + transY );
  }

  public void rotate(Location referencePoint, double angle)
  {
    double cos = Math.cos(Math.toRadians(angle));
    double sin = Math.sin(Math.toRadians(angle));
    double tempX = centerLocation.doubleX() - referencePoint.doubleX();
    double tempY = centerLocation.doubleY() - referencePoint.doubleY();
    centerLocation.setX( (cos * tempX - sin * tempY) + referencePoint.doubleX() );
    centerLocation.setY( (sin * tempX + cos * tempY) + referencePoint.doubleY() );
  }

  public void scaleBy( double minX, double minY, double xFactor, double yFactor)
  {
    double temp = xFactor * (centerLocation.doubleX() - minX);
    double tempX = centerLocation.doubleX();
    double tempY = centerLocation.doubleY();
    if ( temp > Node.MIN_FOR_SCALE )
    {
      tempX = minX + temp;
    }
    temp = yFactor * (centerLocation.doubleY() - minY);
    if ( temp > Node.MIN_FOR_SCALE )
    {
      tempY = minY + temp;
    }
    centerLocation = new Location( tempX, tempY );
  }

  public void update()
  {
    if ( isCurved )
    {
      Location s, e;
      s = getLocationAtAngleFrom( (Node)getStartNode(), startControlAngle );
      e = getLocationAtAngleFrom( (Node)getEndNode(), endControlAngle );
      centerLocation = getIntersectionLocation( getStartNode().getLocation(), s,
                                                getEndNode().getLocation(), e );
    }
    else if ( isOrthogonal )
    {
      centerLocation = getOrthogonalLocation();
      if ( centerLocation == null )
      {
        makeStraight();
      }
    }
    else
    {
      centerLocation = getNormalLocation();
    }
  }

  public static Location getIntersectionLocation( Location l1, Location l2,
                                                  Location l3, Location l4 )
  {
    double x43 = l4.doubleX() - l3.doubleX();
    double y43 = l4.doubleY() - l3.doubleY();
    double x21 = l2.doubleX() - l1.doubleX();
    double y21 = l2.doubleY() - l1.doubleY();
    double u = ( ( x43 * (l1.doubleY() - l3.doubleY()) ) -
                 ( y43 * (l1.doubleX() - l3.doubleX()) ) ) /
               ( ( y43 * x21 ) - ( x43 * y21 ) );
    return new Location( (l1.doubleX() + u*x21), (l1.doubleY() + u*y21) );
  }
  
  public Location getNormalLocation()
  {
    return new Location( ( (getStartNode().getX() + getEndNode().getX())/2 ),
                         ( (getStartNode().getY() + getEndNode().getY())/2 ) );
  }
  
  public Location getOrthogonalLocation()
  {
    double turnOrientation = getTurnOrientation();
    Location s = getStartNode().getLocation();
    Location e = getEndNode().getLocation();
    Location c = getCenterLocation();
    if ( s.intX() == e.intX() || s.intY() == e.intY() )
    {
      return null;
    }
    else if ( isLeftTurn(turnOrientation) )
    {
      isOrthogonalLeftFromStart = true;
      if ( (s.intX() < e.intX() && s.intY() < e.intY()) || 
           (s.intX() > e.intX() && s.intY() > e.intY()) )
      {
        return new Location( e.intX(), s.intY() );
      }
      else
      {
        return new Location( s.intX(), e.intY() );
      }
    }
    else if ( isRightTurn(turnOrientation) )
    {
      isOrthogonalLeftFromStart = false;
      if ( (s.intX() < e.intX() && s.intY() > e.intY()) || 
           (s.intX() > e.intX() && s.intY() < e.intY()) )
      {
        return new Location( e.intX(), s.intY() );
      }
      else
      {
        return new Location( s.intX(), e.intY() );
      }
    }
    else
    {
      return null;
    }
  }
  
  // determines the turn orientation of going from s to e and then to c.
  public double getTurnOrientation()
  {
    Location s = getStartNode().getLocation();
    Location e = getEndNode().getLocation();
    Location c = getCenterLocation();
    return (e.doubleX()-s.doubleX())*(c.doubleY()-s.doubleY())-
           (e.doubleY()-s.doubleY())*(c.doubleX()-s.doubleX());
  }
  
  public boolean isLeftTurn( double turnOrientation )
  {
    return turnOrientation < 0;
  }
  
  public boolean isRightTurn( double turnOrientation )
  {
    return turnOrientation > 0;
  }

  public Location getCenterLocation() { return centerLocation; }

  public void makeStraight()
  {
    isCurved = false;
    isOrthogonal = false;
    update();
  }

  public void setExtender(EdgeExtender ex)
  {
    extender = ex;
  }

  public EdgeExtender getExtender()
  {
    return extender;
  }

  public boolean hasZeroLength()
  {
    return getStartNode().getLocation().equals(getEndNode().getLocation());
  }

  public boolean intersects(Edge edge)
  {
    if ( hasZeroLength() )
    {
      if ( edge.hasZeroLength() &&
           getStartNode().getLocation().equals(edge.getStartNode().getLocation()) )
      {
        return true;
      }
      else if ( getStartNode().getLocation().equals(edge.getStartNode().getLocation()) ||
                getStartNode().getLocation().equals(edge.getEndNode().getLocation()) )
      {
        return true;
      }
    }
    int x1 = getStartNode().getX();
    int y1 = getStartNode().getY();
    int x2 = getEndNode().getX();
    int y2 = getEndNode().getY();
    int x3 = edge.getStartNode().getX();
    int y3 = edge.getStartNode().getY();
    int x4 = edge.getEndNode().getX();
    int y4 = edge.getEndNode().getY();

    double x21 = x2-x1;
    double y21 = y2-y1;
    double x43 = x4-x3;
    double y43 = y4-y3;
    double x31 = x3-x1;
    double y31 = y3-y1;
    double denominator = x21*y43-y21*x43;
    double numerator1 = x31*y43-y31*x43;
    double numerator2 = x31*y21-y31*x21;

    if ( denominator != 0 ) // edges are not parallel
    {
      double determinant1 = numerator1/denominator;
      double determinant2 = numerator2/denominator;
      if ( determinant1 >= 0 && determinant1 <= 1 &&
           determinant2 >= 0 && determinant2 <= 1 )
      {
        if ( (x1 == x3 && y1 == y3) || (x1 == x4 && y1 == y4) ||
             (x2 == x3 && y2 == y3) || (x2 == x4 && y2 == y4) )
        {
          return false; // edges intersect at shared end point.
        }
        else
        {
          return true; // normal intersection exists
        }
      }
      else
      {
        return false; // no intersection exists
      }
    }
    else // edges are parallel
    {
      if ( numerator1 != 0 && numerator2 != 0 )
      {
        return false; // edges are parallel but not overlapping
      }

      if ( x1 != x2 ) // this edge is not vertical (project to x axis)
      {
        double minX1 = Math.min(x1, x2);
        double maxX1 = Math.max(x1, x2);
        double minX2 = Math.min(x3, x4);
        double maxX2 = Math.max(x3, x4);
        if ( maxX2 < minX1 || minX2 > maxX1 )
        {
          return false; // edges are collinear but don't overlap
        }
        else if ( maxX2 == minX1 || minX2 == maxX1 )
        {
          return false; // edges are collinear and share one endpoint
        }
        else
        {
          return true; // edges are collinear and overlap
        }
      }
      else // this edge is vertical (project to y axis)
      {
        double minY1 = Math.min(y1, y2);
        double maxY1 = Math.max(y1, y2);
        double minY2 = Math.min(y3, y4);
        double maxY2 = Math.max(y3, y4);
        if ( maxY2 < minY1 || minY2 > maxY1 )
        {
          return false; // edges are collinear but don't overlap
        }
        else if ( maxY2 == minY1 || minY2 == maxY1 )
        {
          return false; // edges are collinear and share one endpoint
        }
        else
        {
          return true; // edges are collinear and overlap
        }
      }
    }
  }

  public Location getLocationAtAngleFrom(Node pivotEndNode, double angle)
  {
    double totalAngle = getAngleFrom(pivotEndNode);
    if ( totalAngle == -1 )
    {
      return null;
    }
    totalAngle+= angle;
    double slope = -1.0 * Math.tan( Math.toRadians( totalAngle ) );
    double intercept = pivotEndNode.getLocation().doubleY() -
                       slope * pivotEndNode.getLocation().doubleX();
    return new Location( pivotEndNode.getLocation().doubleX() + 5000.0,
                         slope * (pivotEndNode.getLocation().doubleX() + 5000.0 ) +
                         intercept );
  }

  public double getDirectedAngle(Node directionSource)
  {
    if ( directionSource == getStartNode() )
    {
      return getAngleFrom((Node)getStartNode());
    }
    else if ( directionSource == getEndNode() )
    {
      return getAngleFrom((Node)getEndNode());
    }
    else
    {
      return -1.0;
    }
  }

  public double getAngleFrom(Node aNode)
  {
    if ( aNode != getStartNode() && aNode != getEndNode() )
    {
      return -1.0;
    }

    Node otherNode = (Node)otherEndFrom(aNode);
    return getAngleFrom( otherNode.getLocation().intX(),
                         otherNode.getLocation().intY(),
                         aNode.getLocation().intX(),
                         aNode.getLocation().intY(),
                         aNode.getLocation().intX() + 100.0,
                         aNode.getLocation().intY() );
  }

  private double getAngleFrom( double ax, double ay, double bx,
                               double by, double cx, double cy )
  {
    double crossProduct = (ax-bx)*(cy-by)-(ay-by)*(cx-bx); // cross product
    double dotProduct = (ax-bx)*(cx-bx)+(ay-by)*(cy-by); // dot product

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
    if ( angle < 0 )
    {
      angle = 360.0 + angle;
    }
    return angle;
  }

  public Edge getEdge()
  {
    return this;
  }
}