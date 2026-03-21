package dataStructure.binaryHeap;

/*
 * This class represents a standard binary heap.
 */
public class BinaryHeap
{
 private HeapNode min;  // The root HeapNode in the BinaryHeap.
 private HeapNode last; // The last HeapNode in the BinaryHeap.
 private int size;      // The number of HeapNodes in the BinaryHeap.

  /*
   * Default Constructor for a BinaryHeap.
   */
  public BinaryHeap()
  {
    min = null;
    last = null;
    size = 0;
  }

  /*
   * Clears the BinaryHeap and releases its internal node references.
   */
  public void clear()
  {
    if ( !isEmpty() )
    {
      min.deleteRecurse();
      min = null;
      last = null;
      size = 0;
    }
  }

  /* 
   * This method inserts a new HeapNode object that contains the given Node
   * object into the BinaryHeap.
   * The Node object's HeapNode instance variable is set to the new HeapNode.
   */
  public void insert(HeapObject ho)
  {
    size++;
    if ( isEmpty() )
    {
      min = new HeapNode(ho);
      ho.setHeapNode(min);
      last = min;
    }
    else
    {
      HeapNode newNode = new HeapNode(ho);
      ho.setHeapNode(newNode);
      findLastParentForInsert();
      newNode.setParent(last);
      if ( last.getLeft() != null )
      {
         last.setRight(newNode);
      }
      else
      {
         last.setLeft(newNode);
      }
      last = newNode;
      upHeap(newNode);
    }
  }

  /* 
   * This method extracts the Node object with the minimum cost from
   * the BinaryHeap.
   * The Node object's HeapNode instance variable is reset to null.
   */
  public HeapObject extractMin()
  {
    if ( !isEmpty() )
    {
      size--;
      HeapObject minNode = min.getElement();
      minNode.setHeapNode(null);
      min.setElement(last.getElement());
      min.getElement().setHeapNode(min);
      downHeap(min);
      if ( last.isLeftChild() )
      {
        last = last.getParent();
        last.setLeft(null);
      }
      else if ( last.isRightChild() )
      {
        last = last.getParent();
        last.setRight(null);
      }
      else
      {
        last = null;
        min = null;
      }
      if ( last != null )
      {
        updateLastAfterDelete();
      }
      return minNode;
    }
    else
    {
      return null;
    }
  }

  /* 
   * This method decreases the cost of the given Node object, using the current
   * value of its cost instance variable..
   * The HeapNode object containing the Node is moved towards the root of the
   * BinaryHeap until it satisfies the heap order property.
   */
  public void decreaseKey(HeapObject ho)
  {
    upHeap(ho.getHeapNode());
  }
  
  /* 
   * Returns whether or not the BinaryHeap is empty.
   */
  public boolean isEmpty()
  {
    return min == null;
  }

  /*
   * Returns the size of the BinaryHeap.
   */
  public int size()
  {
    return size;
  }

  /*
   * This method prints the preorder traversal of the BinaryHeap starting with
   * the root (min elemennt) of the heap. (For debugging purposes).
   */
  public void printHeap()
  {
    System.out.println("PRINTHEAP");
    if ( isEmpty() )
    {
      System.out.println("  EMPTY HEAP");
    }
    else
    {
      printHeap(min);
      System.out.println("\nmin is: " + min.getElement());
      System.out.println("\nlast is: " + last.getElement());
    }
    System.out.println("\nEND PRINTHEAP");
  }

  /* 
   * This method serves as a utility method to move a HeapNode towards the
   * root of the BinaryHeap until it satisfies the heap order property.
   */
  private void upHeap(HeapNode aNode)
  {
    if ( aNode.getParent() != null &&
         aNode.getElement().getCost() < aNode.getParent().getElement().getCost() )
    {
      HeapObject temp = aNode.getElement();
      aNode.setElement(aNode.getParent().getElement());
      aNode.getParent().setElement(temp);
      aNode.getElement().setHeapNode(aNode);
      aNode.getParent().getElement().setHeapNode(aNode.getParent());
      upHeap(aNode.getParent());
    }
  }

  /* 
   * This method serves as a utility method to move a HeapNode away from the
   * root of the BinaryHeap until it satisfies the heap order property.
   */
  private void downHeap(HeapNode aNode)
  {
    HeapNode minNode = null;
    if ( aNode.getLeft() != null )
    {
      minNode = aNode.getLeft();
    }
    if ( aNode.getRight() != null )
    {
      if ( minNode == null ||
           aNode.getRight().getElement().getCost() < minNode.getElement().getCost() )
      {
        minNode = aNode.getRight();
      }
    }
  
    if ( minNode != null &&
         minNode.getElement().getCost() < aNode.getElement().getCost() )
    {
      HeapObject temp = aNode.getElement();
      aNode.setElement(minNode.getElement());
      minNode.setElement(temp);
      aNode.getElement().setHeapNode(aNode);
      minNode.getElement().setHeapNode(minNode);
      downHeap(minNode);
    }
  }

  /* 
   * This method serves as a utility method to set the last position in the
   * BinaryHeap to be the HeapNode that will be the parent of the next HeapNode
   * to be inserted.
   */
  private void findLastParentForInsert()
  {
    if ( last.isLeftChild() )
    {
      last = last.getParent();
    }
    else
    {
      while ( last.isRightChild() )
      {
        last = last.getParent();
      }
      if ( last.getSibling() != null )
      {
        last = last.getSibling();
      }
      while ( last.getLeft() != null )
      {
        last = last.getLeft();
      }
    }
  }

  /* 
   * This method serves as a utility method to set the last position in the
   * BinaryHeap to be the HeapNode that will be deleted next.
   */
  private void updateLastAfterDelete()
  {
    if ( last.getLeft() != null )
    {
      last = last.getLeft();
    }
    else
    {
      while ( last.isLeftChild() )
      {
        last = last.getParent();
      }
      if ( last.getSibling() != null )
      {
        last = last.getSibling();
      }
      while ( last.getRight() != null )
      {
        last = last.getRight();
      }
    }
  }

  /* 
   * This method serves as a utility method that prints the preorder
   * traversal of the BinaryHeap starting at the given HeapNode.
   */
  private void printHeap(HeapNode node)
  {
    if (node != null)
    {
      System.out.println(node.getElement() +
        " " +  (node.getLeft() == null) +
        " " + (node.getRight() == null) +
        " " + (node.getParent() == null));
      printHeap(node.getLeft());
      printHeap(node.getRight());
    }
  }
}