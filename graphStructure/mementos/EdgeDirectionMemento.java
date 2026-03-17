package graphStructure.mementos;

import graphStructure.*;

public class EdgeDirectionMemento implements MementoInterface
{
  private static int NO_TYPE = 0;
  private static int DIRECTION_TYPE = 1;

  private Edge target;
  private Node directedSource;
  private int type;

  private EdgeDirectionMemento(Edge target)
  {
    this.target = target;
    directedSource = target.getDirectedSourceNode();
    type = NO_TYPE;
  }

  public static EdgeDirectionMemento createDirectionMemento(EdgeInterface target)
  {
    EdgeDirectionMemento toReturn = new EdgeDirectionMemento( target.getEdge() );
    toReturn.type = DIRECTION_TYPE;
    return toReturn;
  }

  public void apply(Graph graph)
  {
    if ( type == NO_TYPE )
    {
      return;
    }
    else if ( type == DIRECTION_TYPE )
    {
      Node temp = target.getDirectedSourceNode();
      target.setDirectedFrom(directedSource);
      directedSource = temp;
    }
  }

  public String toString()
  {
    if ( type == DIRECTION_TYPE )
    {
      return "ChangeEdgeDirection: " + target + " " + directedSource;
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