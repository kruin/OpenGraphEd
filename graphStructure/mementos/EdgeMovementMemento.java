package graphStructure.mementos;

import graphStructure.*;

public class EdgeMovementMemento implements MementoInterface
{
  private static int NO_TYPE = 0;
  private static int MOVE_TYPE = 1;

  private Edge target;
  private Location location;
  private boolean curved;
  private boolean orthogonal;
  private int type;

  private EdgeMovementMemento(Edge target)
  {
    this.target = target;
    this.location = new Location(target.getCenterLocation());
    curved = target.isCurved();
    orthogonal = target.isOrthogonal();
    type = NO_TYPE;
  }

  public static EdgeMovementMemento createMoveMemento(EdgeInterface target)
  {
    EdgeMovementMemento toReturn = new EdgeMovementMemento( target.getEdge() );
    toReturn.type = MOVE_TYPE;
    return toReturn;
  }

  public void apply(Graph graph)
  {
    if ( type == NO_TYPE )
    {  
      return;
    }
    else if ( type == MOVE_TYPE )
    {
      Location aPoint = target.getCenterLocation();
      target.setCenterLocation(location);
      location = new Location(aPoint);
      boolean curve = target.isCurved();
      target.setIsCurved(curved);
      curved = curve;
      boolean orth = target.isOrthogonal();
      target.setIsOrthogonal(orthogonal);
      orthogonal = orth;
      
    }
  }

  public String toString()
  {
    if ( type == MOVE_TYPE )
    {
      return "moveEdge: " + target + " " + location;
    }
    else
    {
      return "Unknown: " + target;
    }
  }

  public boolean isUseless()
  {
    return location.equals(target.getCenterLocation());
  }
}