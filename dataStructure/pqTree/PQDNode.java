package dataStructure.pqTree;

public class PQDNode extends PQNode
{
  private boolean readInReverseDirection;

  public PQDNode(Object data)
  {
    super(data);
    siblings = new PQNodePairDirected(-1);
    type = TYPE_DNODE;
    readInReverseDirection = false;
  }

  public void setReadInReverseDirection( boolean readInReverse )
  {
    readInReverseDirection = readInReverse;
  }

  public boolean readInReverseDirection()
  {
    return readInReverseDirection;
  }

  public void toggleReadInReverseDirection()
  {
    readInReverseDirection = !readInReverseDirection;
  }

  public void setDirection(PQNode directedNode) throws Exception
  {
    if ( siblings.contains(directedNode) )
    {
      ((PQNodePairDirected)siblings).setDirectedTowards(directedNode);
    }
  }

  public PQNode getDirection()
  {
    return ((PQNodePairDirected)siblings).getDirectedTowards();
  }

  public String infoString()
  {
    return super.infoString() + " ==> " + ((PQNodePairDirected)siblings).getDirectedTowards().infoString();
  }

  public String toString()
  {
    return "D"+super.toString() + " ==> " + ((PQNodePairDirected)siblings).getDirectedTowards();
  }
}