package dataStructure.pqTree;

public class PQNodePairDirected extends PQNodePair
{
  private int directedTowardsIndex;

  public PQNodePairDirected(int directionIndex)
  {
    super();
    directedTowardsIndex = directionIndex;
  }

  public void setDirectedTowards(PQNode directedTowards)
  {
    if ( directedTowards == null )
    {
      directedTowardsIndex = -1;
    }
    else if ( contains(directedTowards) )
    {
      directedTowardsIndex = indexOf(directedTowards);
    }
    else
    {
      directedTowardsIndex = -1;
    }
  }

  public PQNode getDirectedTowards()
  {
    if ( directedTowardsIndex == -1 )
    {
      return null;
    }
    else
    {
      return PQNodeAt(directedTowardsIndex);
    }
  }

  public void addPQNode(PQNode pqNode) throws Exception
  {
    super.addPQNode(pqNode);
    if ( directedTowardsIndex == -1 )
    {
      directedTowardsIndex = indexOf(pqNode);
    }
  }

  public boolean removePQNode(PQNode pqNode)
  {
    if ( directedTowardsIndex == 1 && indexOf(pqNode) == 0 )
    {
      directedTowardsIndex = 0;
    }
    else if ( indexOf(pqNode) == directedTowardsIndex )
    {
      directedTowardsIndex = -1;
    }
    return super.removePQNode(pqNode);
  }
}