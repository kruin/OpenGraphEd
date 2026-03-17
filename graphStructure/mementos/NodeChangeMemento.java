package graphStructure.mementos;

import graphStructure.*;

public class NodeChangeMemento implements MementoInterface
{
  private static int NO_TYPE = 0;
  private static int CHANGE_TYPE = 1;

  private Node target;
  private Edge accessEdge;
  private int type;
  
  private NodeChangeMemento(Node target)
  {
    this.target = target;
    this.accessEdge = target.getAccessEdge();
    type = NO_TYPE;
  }
  
  public static NodeChangeMemento createChangeMemento(Node target)
  {
    NodeChangeMemento toReturn = new NodeChangeMemento(target);
    
    toReturn.type = CHANGE_TYPE;
    return toReturn;
  }
  
  public void apply(Graph graph)
  {
    if ( type == NO_TYPE )
    {
      return;
    }
    else if ( type == CHANGE_TYPE )
    {
      Edge temp = target.getAccessEdge();
      target.setAccessEdge(accessEdge);
      accessEdge = temp;
    }
  }
  
  public String toString()
  {
    if ( type == CHANGE_TYPE )
    {
      return "changeNode: " + target + " " + accessEdge;
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