package graphStructure.mementos;

import graphStructure.Graph;

public class GridSizeMemento implements MementoInterface
{
  private static int NO_TYPE = 0;
  private static int CHANGE_GRID_SIZE_TYPE = 1;
  
  private int gridRows;
  private int gridCols;
  private int gridRowHeight;
  private int gridColWidth;
  private Graph target;
  private int type;

  private GridSizeMemento(Graph target)
  {
    this.target = target;
    gridRows = target.getGridRows();
    gridCols = target.getGridCols();
    gridRowHeight = target.getGridRowHeight();
    gridColWidth = target.getGridColWidth();
    type = NO_TYPE;
  }

  public static GridSizeMemento createGridSizeMemento(Graph target)
  {
    GridSizeMemento toReturn = new GridSizeMemento(target);
    toReturn.type = CHANGE_GRID_SIZE_TYPE;
    return toReturn;
  }

  public void apply(Graph graph)
  {
    if ( type == NO_TYPE )
    {
      return;
    }
    else if ( type == CHANGE_GRID_SIZE_TYPE )
    {
      int tempGridRows = target.getGridRows();
      int tempGridCols = target.getGridCols();
      int tempGridRowHeight = target.getGridRowHeight();
      int tempGridColWidth = target.getGridColWidth();
      target.setGrid(gridRows, gridRowHeight, gridCols, gridColWidth, false);
      gridRows = tempGridRows;
      gridCols = tempGridCols;
      gridRowHeight = tempGridRowHeight;
      gridColWidth = tempGridColWidth;
    }
  }

  public String toString()
  {
    if ( type == CHANGE_GRID_SIZE_TYPE )
    {
      return "ChangeGridSize: " + target + " " + gridRows + " " + gridCols +
             " " + gridRowHeight + " " + gridColWidth;
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
