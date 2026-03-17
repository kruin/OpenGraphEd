package graphStructure.mementos;

import graphStructure.*;

public class NodeDrawXMemento implements MementoInterface
{
  private static int NO_TYPE = 0;
  private static int DRAWX_TYPE = 1;

  private Node target;
  private boolean drawX;
  private int type;

  private NodeDrawXMemento(Node target)
  {
    this.target = target;
    drawX = target.getDrawX();
    type = NO_TYPE;
  }

  public static NodeDrawXMemento createDrawXMemento(NodeInterface target)
  {
    NodeDrawXMemento toReturn = new NodeDrawXMemento( target.getNode() );
    toReturn.type = DRAWX_TYPE;
    return toReturn;
  }

  public void apply(Graph graph)
  {
    if ( type == NO_TYPE )
    {
      return;
    }
    else if ( type == DRAWX_TYPE )
    {
      boolean temp = target.getDrawX();
      target.setDrawX(drawX);
      drawX = temp;
    }
  }

  public String toString()
  {
    if ( type == DRAWX_TYPE )
    {
      return "ChangeNodeDrawX: " + target + " " + drawX;
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