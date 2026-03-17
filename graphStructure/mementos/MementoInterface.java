package graphStructure.mementos;

import graphStructure.*;

public interface MementoInterface
{
  public void apply(Graph graph);
  public boolean isUseless();
}