package dataStructure;

// This class represents a Queue data structure used for storing nodes
// that are used during the reduction process of the PQTree.
public class Queue
{
  private QueueNode head;
  private QueueNode tail;
  private int size;

  public Queue()
  {
    head = null;
    tail = null;
    size = 0;
  }

  public void enqueue(Object obj)
  {
    QueueNode node = new QueueNode(obj, null);
    if (size == 0)
    {
      head = node;
    }
    else
    {
      tail.setNext(node);
    }
    tail = node;
    size++;
  }
  
  public Object dequeue()
  {
    if (size == 0)
    {
      return null;
    }
    else
    {
      Object obj = head.getElement();
      head = head.getNext();
      size--;
      if (size == 0)
      {
        tail = null;
      }
      return obj;
    }
  }
  
  public int size()
  {
    return size;
  }
}

//This class represents the sequence of objects that are contained within the Queue.
class QueueNode
{
  private Object element;
  private QueueNode next;
  
  QueueNode()
  {
    this(null,null);
  }
  
  public QueueNode(Object e, QueueNode n)
  {
    element = e;
    next = n;
  }
  
  void setElement(Object newElem) { element = newElem; }
  
  void setNext(QueueNode newNext) { next = newNext; }
  
  Object getElement() { return element; }
  
  QueueNode getNext() { return next; }
}
