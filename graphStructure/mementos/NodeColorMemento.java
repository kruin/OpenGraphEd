package graphStructure.mementos;

import java.awt.Color;
import graphStructure.*;

public class NodeColorMemento implements MementoInterface
{
  private static int NO_TYPE = 0;
  private static int COLOR_TYPE = 1;

  private Node target;
  private Color color;
  private int type;

  private NodeColorMemento(Node target)
  {
    this.target = target;
    color = target.getColor();
    type = NO_TYPE;
  }

  public static NodeColorMemento createColorMemento(NodeInterface target)
  {
    NodeColorMemento toReturn = new NodeColorMemento( target.getNode() );
    toReturn.type = COLOR_TYPE;
    return toReturn;
  }

  public void apply(Graph graph)
  {
    if ( type == NO_TYPE )
    {
      return;
    }
    else if ( type == COLOR_TYPE )
    {
      Color temp = target.getColor();
      target.setColor(color);
      color = temp;
    }
  }

  public String toString()
  {
    if ( type == COLOR_TYPE )
    {
      return "ChangeNodeColor: " + target + " " + color;
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