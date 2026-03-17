package dataStructure.binaryHeap;

/*
 * This class represents the HeapNode objects that are used as
 * positional objects (containing Nodes) within a BinaryHeap.
 */
public class HeapNode
{
  private HeapNode parent; // Parent HeapNode of this HeapNode in a BinaryHeap.
  private HeapNode left;   // Left Child HeapNode of this HeapNode in a BinaryHeap.
  private HeapNode right;  // Right Child HeapNode of this HeapNode in a BinaryHeap.
  private HeapObject element;       // The Node object contained by this HeapNode.

  /*
   * Constructor for a HeapNode that contains the given Node.
   */
  public HeapNode(HeapObject ho)
  {
    element = ho;
    parent = null;
    left = null;
    right = null;
  }

  public HeapObject getElement() { return element; }
  
  public void setElement(HeapObject ho) { element = ho; }

  public HeapNode getParent() { return parent; }
  
  public void setParent(HeapNode hn) { parent = hn; }
  
  public HeapNode getLeft() { return left; }
  
  public void setLeft(HeapNode hn) { left = hn; }
  
  public HeapNode getRight() { return right; }
  
  public void setRight(HeapNode hn) { right = hn; }

  /*
   * This method deletes all HeapNode objects that are descendants of this
   * HeapNode in a BinaryHeap.
   */
  public void deleteRecurse()
  {
    if ( left != null )
    {
      left.deleteRecurse();
      left = null;
    }
    if ( right != null )
    {
      right.deleteRecurse();
      right = null;
    }  
  }

  /*
   * The following are the accessor/modifier methods of a HeapNode object.
   */
  public boolean isLeftChild()
  {
    if ( parent == null )
    {
      return false;
    }
    else
    {
      return ( parent.left == this );
    }
  }

  public boolean isRightChild()
  {
    if ( parent == null )
    {
      return false;
    }
    else
    {
      return ( parent.right == this );
    }
  }

  public HeapNode getSibling()
  {
    if ( parent == null )
    {
      return null;
    }
    else if ( isLeftChild() )
    {
      return parent.right;
    }
    else
    {
      return parent.left;
    }
  }
}
