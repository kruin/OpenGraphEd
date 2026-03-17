package dataStructure.pqTree;

public class PQNodePair
{
  protected static int MAX_SIZE = 2;
  protected int size;
  protected PQNode firstPQNode;
  protected PQNode secondPQNode;

  public PQNodePair()
  {
    size = 0;
    firstPQNode = null;
    secondPQNode = null;
  }

  public int size() { return size; }

  public void addPQNode(PQNode pqNode) throws Exception
  {
    if ( size < MAX_SIZE )
    {
      if ( size == 0 )
      {
        firstPQNode = pqNode;
      }
      else
      {
        secondPQNode = pqNode;
      }
      size++;
    }
    else
    {
      throw new Exception("*** ERROR cannot add more than " + MAX_SIZE + " PQNodes to a pair!");
    }
  }

  public boolean removePQNode(PQNode pqNode)
  {
    if ( pqNode == firstPQNode )
    {
      firstPQNode = secondPQNode;
      secondPQNode = null;
      size--;
      return true;
    }
    else if ( pqNode == secondPQNode )
    {
      secondPQNode = null;
      size--;
      return true;
    }
    return false;
  }

  public boolean removePQNodeAt(int index)
  {
    return removePQNode(PQNodeAt(index));
  }

  public PQNode PQNodeAt(int index)
  {
    if ( index < size )
    {
      if ( index == 0 )
      {
        return firstPQNode;
      }
      else
      {
        return secondPQNode;
      }
    }
    else
    {
      return null;
    }
  }

  public PQNode otherPQNode(PQNode pqNode) throws Exception
  {
    if ( size == 1 )
    {
      if ( pqNode == null )
      {
        return firstPQNode;
      }
      else if ( pqNode == firstPQNode )
      {
        return null;
      }
      else
      {
        throw new Exception("*** ERROR cannot return other PQNode of " + pqNode);
      }
    }
    else if ( size == 2 )
    {
      if ( pqNode == firstPQNode )
      {
        return secondPQNode;
      }
      else if ( pqNode == secondPQNode )
      {
        return firstPQNode;
      }
      else
      {
        throw new Exception("*** ERROR cannot return other PQNode of " + pqNode);
      }
    }
    else
    {
      throw new Exception("*** ERROR cannot return other PQNode of " + pqNode);
    }
  }

  public void replacePQNode(PQNode oldPQNode, PQNode newPQNode) throws Exception
  {
    if ( firstPQNode == oldPQNode )
    {
      firstPQNode = newPQNode;
    }
    else if ( secondPQNode == oldPQNode )
    {
      secondPQNode = newPQNode;
    }
    else
    {
      throw new Exception("*** ERROR cannot replace nonexistant PQNode: " + oldPQNode);
    }
  }

  public int indexOf(PQNode pqNode)
  {
    if ( pqNode == firstPQNode )
    {
      return 0;
    }
    else if ( pqNode == secondPQNode )
    {
      return 1;
    }
    else
    {
      return -1;
    }
  }

  public boolean contains(PQNode pqNode)
  {
    return indexOf(pqNode) != -1;
  }
}