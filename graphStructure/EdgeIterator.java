package graphStructure;

public class EdgeIterator
{
  private Node target;
  private Edge currentEdge;
  private Edge accessEdge;
  private boolean started;

  public EdgeIterator(Node target, Edge accessEdge)
  {
    this.target = target;
    this.accessEdge = accessEdge;
    if ( accessEdge != null )
    {
      currentEdge = accessEdge;
    }
    else
    {
      currentEdge = null;
    }
    started = false;
  }

  public void reset()
  {
    if ( accessEdge != null )
    {
      currentEdge = accessEdge;
    }
    else
    {
      currentEdge = null;
    }
    started = false;
  }

  public Edge nextEdge()
  {
    if ( accessEdge != null )
    {
      if ( !started )
      {
        started = true;
        return accessEdge;
      }
      else
      {
        currentEdge = (Edge)currentEdge.getNextInOrderFrom(target);
        if ( currentEdge == accessEdge )
        {
          return null;
        }
        else
        {
          return currentEdge;
        }
      }
    }
    return null;
  }

  public Edge currentEdge()
  {
    if ( accessEdge != null )
    {
      if ( currentEdge == accessEdge )
      {
        if ( started )
        {
          return null;
        }
        else
        {
          return accessEdge;
        }
      }
      else
      {
        return currentEdge;
      }
    }
    return null;
  }

  public boolean hasMoreEdges()
  {
    return accessEdge != null &&
           (!started || currentEdge.getNextInOrderFrom(target) != accessEdge );
  }
}