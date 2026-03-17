package graphStructure.mementos;

import graphStructure.*;

public class NodeMovementMemento implements MementoInterface
{
  private static int NO_TYPE = 0;
  private static int MOVE_TYPE = 1;

  private Node target;
  private Location location;
  private int type;

  private NodeMovementMemento(Node target)
  {
    this.target = target;
    this.location = new Location(target.getLocation());
    type = NO_TYPE;
  }

  public static NodeMovementMemento createMoveMemento(NodeInterface target)
  {
    NodeMovementMemento toReturn = new NodeMovementMemento( target.getNode() );
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
      Location aPoint = target.getLocation();
      target.setLocation(location);
      location = new Location(aPoint);
    }
  }

  public String toString()
  {
    if ( type == MOVE_TYPE )
    {
      return "moveNode: " + target + " " + location;
    }
    else
    {
      return "Unknown: " + target;
    }
  }

  public boolean isUseless()
  {
    return location.equals(target.getLocation());
  }
}