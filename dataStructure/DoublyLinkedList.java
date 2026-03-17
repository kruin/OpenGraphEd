package dataStructure;

// This class represents a Queue data structure used for storing nodes
// that are used during the reduction process of the PQTree.
public class DoublyLinkedList
{
  private DoublyLinkedListNode head;
  private DoublyLinkedListNode tail;
  private int size;
  private DoublyLinkedListNode current;

  public DoublyLinkedList()
  {
    head = new DoublyLinkedListNode();
    tail = new DoublyLinkedListNode();
    current = tail;
    size = 0;
  }

  public Object dequeue()
  {
    if (size == 0)
    {
      return null;
    }
    else
    {
      Object obj = head.getNext().getElement();
      head.setNext(head.getNext().getNext());
      head.getNext().setPrev(head);
      size--;
      return obj;
    }
  }

  public void enqueue(Object obj)
  {
    DoublyLinkedListNode node = new DoublyLinkedListNode(obj);
    if (size == 0)
    {
      head.setNext(node);
      node.setPrev(head);
      tail.setPrev(node);
      node.setNext(tail);
    }
    else
    {
      node.setPrev(tail.getPrev());
      tail.getPrev().setNext(node);
      node.setNext(tail);
      tail.setPrev(node);
    }
    current = node;
    size++;
  }
  
  public void enqueueAfterCurrent(Object obj)
  {
    DoublyLinkedListNode node = new DoublyLinkedListNode(obj);
    if (size == 0)
    {
      head.setNext(node);
      node.setPrev(head);
      tail.setPrev(node);
      node.setNext(tail);
    }
    else
    {
      if ( current != tail )
      {
        DoublyLinkedListNode next = current.getNext();
        while ( next != tail )
        {
          size--;
          next = next.getNext();
        }
        current.setNext(node);
        node.setPrev(current);
        node.setNext(tail);
        tail.setPrev(node);
      }
      else
      {
        node.setPrev(tail.getPrev());
        tail.getPrev().setNext(node);
        node.setNext(tail);
        tail.setPrev(node);
      }
    }
    current = node;
    size++;
  }
  
  public Object getCurrent()
  {
    if ( current == head || current == tail )
    {
      return null;
    }
    else
    {
      return current.getElement();
    }
  }
  
  public Object removeCurrent()
  {
    if ( current == head || current == tail )
    {
      return null;
    }
    else
    {
      current.getNext().setPrev(current.getPrev());
      current.getPrev().setNext(current.getNext());
      current = current.getNext();
      size--;
      return current.getElement();
    }
  }
  
  public void toHead()
  {
    current = head.getNext();
  }
  
  public void toTail()
  {
    current = tail.getPrev();
  }
  
  public void toNext()
  {
    if ( current != tail )
    {
      current = current.getNext();
    }
  }
  
  public void toPrev()
  {
    if ( current != head )
    {
      current = current.getPrev();
    }
  }
  
  public boolean hasNext()
  {
    return current != tail && current.getNext() != tail;
  }
  
  public boolean hasPrev()
  {
    return current.getPrev() != head;
  }
  
  public boolean isEmpty()
  {
    return size == 0;
  }
  
  public int size()
  {
    return size;
  }
}

//This class represents the sequence of objects that are contained within the Queue.
class DoublyLinkedListNode
{
  private Object element;
  private DoublyLinkedListNode prev, next;
  
  DoublyLinkedListNode()
  {
    this(null,null,null);
  }
  
  public DoublyLinkedListNode(Object e)
  {
    this(e, null, null);
  }
  
  public DoublyLinkedListNode(Object e, DoublyLinkedListNode p, DoublyLinkedListNode n)
  {
    element = e;
    prev = p;
    next = n;
  }
  
  void setElement(Object newElem) { element = newElem; }
  
  void setPrev(DoublyLinkedListNode newPrev) { prev = newPrev; }
  
  void setNext(DoublyLinkedListNode newNext) { next = newNext; }
  
  Object getElement() { return element; }
  
  DoublyLinkedListNode getPrev() { return prev; }
  
  DoublyLinkedListNode getNext() { return next; }
}