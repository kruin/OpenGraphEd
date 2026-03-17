package graphStructure.mementos;

import graphStructure.*;

public class NodeMemento implements MementoInterface
{
  private static int NO_TYPE = 0;
  private static int DELETE_TYPE = 1;
  private static int CREATE_TYPE = 2;

  private Node target;
  private int type;
  
  private NodeMemento(Node target)
  {
    this.target = target;
    type = NO_TYPE;
  }
  
  public static NodeMemento createDeleteMemento(Node target)
  {
    NodeMemento toReturn = new NodeMemento(target);
    toReturn.type = DELETE_TYPE;
    return toReturn;
  }
  
  public static NodeMemento createCreateMemento(Node target)
  {
    NodeMemento toReturn = new NodeMemento(target);
    toReturn.type = CREATE_TYPE;
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
      graph.addNode(target, false);
      type++;
    }
    else if ( type == CREATE_TYPE )
    {
      graph.deleteNode(target, false);
      type--;
    }
  }
  
  public String toString()
  {
    if ( type == DELETE_TYPE )
    {
      return "addNode: " + target;
    }
    else if ( type == CREATE_TYPE )
    {
      return "deleteNode: " + target;
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