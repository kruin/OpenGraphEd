package operation.extenders;

import graphStructure.*;

public class NormalEdgeEx extends EdgeExtender
{
  protected int normalLabel;
  
  public NormalEdgeEx() { super(); }
  
  public NormalEdgeEx(NormalNodeEx start, NormalNodeEx end)
  {
    super(start, end);
  }
  
  public void setNormalLabel(int label) { normalLabel = label; }
  
  public int getNormalLabel() { return normalLabel; }
  
  public NormalNodeEx getNormalLabelSourceNode()
  {
    if ( normalLabel == 1 )
    {
      if ( ((NormalNodeEx)getStartNode()).getR1Parent() == getEndNode() )
      {
        return (NormalNodeEx)getStartNode();
      }
      else if ( ((NormalNodeEx)getEndNode()).getR1Parent() == getStartNode() )
      {
        return (NormalNodeEx)getEndNode();
      }
      else
      {
        return null;
      }
    }
    else if ( normalLabel == 2 )
    {
      if ( ((NormalNodeEx)getStartNode()).getR2Parent() == getEndNode() )
      {
        return (NormalNodeEx)getStartNode();
      }
      else if ( ((NormalNodeEx)getEndNode()).getR2Parent() == getStartNode() )
      {
        return (NormalNodeEx)getEndNode();
      }
      else
      {
        return null;
      }
    }
    else if ( normalLabel == 3 )
    {
      if ( ((NormalNodeEx)getStartNode()).getR3Parent() == getEndNode() )
      {
        return (NormalNodeEx)getStartNode();
      }
      else if ( ((NormalNodeEx)getEndNode()).getR3Parent() == getStartNode() )
      {
        return (NormalNodeEx)getEndNode();
      }
      else
      {
        return null;
      }
    }
    else
    {
      return null;
    }
  }
}