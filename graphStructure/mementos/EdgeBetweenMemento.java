package graphStructure.mementos;

import graphStructure.*;

public class EdgeBetweenMemento implements MementoInterface
{
  private static int NO_TYPE = 0;
  private static int DELETE_TYPE = 1;
  private static int CREATE_TYPE = 2;
  private static int CHANGE_TYPE = 3;

  private Edge target;
  private Edge startPrevious;
  private Edge endPrevious;
  private int type;

  private EdgeBetweenMemento(Edge target, Edge sourcePrevious, Edge endPrevious)
  {
    this.target = target;
    this.startPrevious = sourcePrevious;
    this.endPrevious = endPrevious;
    type = NO_TYPE;
  }

  public static EdgeBetweenMemento createCreateMemento( Edge target,
                                                        Edge previous, Edge next)
  {
    EdgeBetweenMemento toReturn = new EdgeBetweenMemento(target, previous, next);
    toReturn.type = CREATE_TYPE;
    return toReturn;
  }

  public static EdgeBetweenMemento createDeleteMemento( Edge target,
                                                        Edge previous, Edge next)
  {
    EdgeBetweenMemento toReturn = new EdgeBetweenMemento(target, previous, next);
    toReturn.type = DELETE_TYPE;
    return toReturn;
  }

  public static EdgeBetweenMemento createChangeMemento( Edge target )
  {
    Edge previous = (Edge)target.getPreviousInOrderFrom((Node)target.getStartNode());
    Edge next = (Edge)target.getPreviousInOrderFrom((Node)target.getEndNode());
    EdgeBetweenMemento toReturn = new EdgeBetweenMemento(target, previous, next);
    toReturn.type = CHANGE_TYPE;
    return toReturn;
  }

  public void apply(Graph graph)
  {
    if ( type == NO_TYPE )
    {
      return;
    }
    else if ( type == DELETE_TYPE )
    {
      graph.addEdge(target, startPrevious, endPrevious, false);
      type++;
    }
    else if ( type == CREATE_TYPE )
    {
      graph.deleteEdge(target, false);
      type--;
    }
    else if ( type == CHANGE_TYPE )
    {
      Edge temp;
      temp = (Edge)target.getPreviousInOrderFrom((Node)target.getStartNode());
      target.setPreviousInOrderFrom(target.getStartNode(), startPrevious);
      startPrevious.setNextInOrderFrom(target.getStartNode(), target);
      startPrevious = temp;

      temp = (Edge)target.getPreviousInOrderFrom((Node)target.getEndNode());
      target.setPreviousInOrderFrom(target.getEndNode(), endPrevious);
      endPrevious.setNextInOrderFrom(target.getEndNode(), target);
      endPrevious = temp;
    }
  }

  public String toString()
  {
    if ( type == DELETE_TYPE )
    {
      return "addEdge: " + target + " Between: " + startPrevious + ", " + endPrevious;
    }
    else if ( type == CREATE_TYPE )
    {
      return "deleteEdge: " + target + " Between: " + startPrevious + ", " + endPrevious;
    }
    else if ( type == CHANGE_TYPE )
    {
      return "changeOrder: " + target + " Start Prev: " + startPrevious + ", End Prev: " + endPrevious;
    }
    else
    {
      return "Unknown: " + target;
    }
  }

  public boolean isUseless()
  {
    return false;
  }
}