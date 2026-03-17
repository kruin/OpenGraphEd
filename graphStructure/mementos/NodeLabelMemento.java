package graphStructure.mementos;

import graphStructure.*;

public class NodeLabelMemento implements MementoInterface
{
  private static int NO_TYPE = 0;
  private static int LABEL_TYPE = 1;

  private Node target;
  private String label;
  private int type;

  private NodeLabelMemento(Node target)
  {
    this.target = target;
    label = target.getLabel();
    type = NO_TYPE;
  }

  public static NodeLabelMemento createLabelMemento(NodeInterface target)
  {
    NodeLabelMemento toReturn = new NodeLabelMemento( target.getNode() );
    toReturn.type = LABEL_TYPE;
    return toReturn;
  }

  public void apply(Graph graph)
  {
    if ( type == NO_TYPE )
    {
      return;
    }
    else if ( type == LABEL_TYPE )
    {
      String temp = target.getLabel();
      target.setLabel(label);
      label = temp;
    }
  }

  public String toString()
  {
    if ( type == LABEL_TYPE )
    {
      return "ChangeNodeLabel: " + target + " " + label;
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