package graphStructure.mementos;

import graphStructure.*;

public class EdgeMemento implements MementoInterface
{
  private static int NO_TYPE = 0;
  private static int PRESERVE_GENERATED_TYPE = 7;
  private static int BECOME_GENERATED_TYPE = 8;

  private Edge target;
  private int type;

  private EdgeMemento(Edge target)
  {
    this.target = target;
    type = NO_TYPE;
  }

  public static EdgeMemento createPreserveGeneratedMemento(Edge target)
  {
    EdgeMemento toReturn = new EdgeMemento(target);
    toReturn.type = PRESERVE_GENERATED_TYPE;
    return toReturn;
  }

  public void apply(Graph graph)
  {
    if ( type == NO_TYPE )
    {
      return;
    }
    else if ( type == PRESERVE_GENERATED_TYPE )
    {
      target.setIsGenerated(true);
      type++;
    }
    else if ( type == BECOME_GENERATED_TYPE )
    {
      target.setIsGenerated(false);
      type--;
    }
  }

  public String toString()
  {
    if ( type == PRESERVE_GENERATED_TYPE )
    {
      return "makeGenerated: " + target;
    }
    else if ( type == BECOME_GENERATED_TYPE )
    {
      return "preserveGenerated: " + target;
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