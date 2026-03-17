package graphStructure.mementos;

import graphStructure.Graph;
import dataStructure.DoublyLinkedList;

public class MementoGrouper implements MementoInterface
{
  private static boolean TRACE = false;

  private DoublyLinkedList list;
  private String title;
  private boolean reverse;

  public MementoGrouper(String title)
  {
    if ( TRACE ) { System.out.println("\n\nnew memento grouper: " + title); }
    this.title = title;
    list = new DoublyLinkedList();
    reverse = false;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getTitle() { return title; }

  public void addMemento(MementoInterface memento)
  {
    if ( TRACE ) { System.out.println("add to memento grouper: " + memento); }
    list.enqueue(memento);
  }

  public void apply(Graph graph)
  {
    if ( TRACE ) { System.out.print("\n\napplying memento grouper: " + title); }
    MementoInterface memento;
    if ( reverse )
    {
      if ( TRACE ) { System.out.println(" (redo)"); }
      list.toHead();
    }
    else
    {
      if ( TRACE ) { System.out.println(" (undo)"); }
      list.toTail();
    }
    while ( list.getCurrent() != null )
    {
      memento = (MementoInterface)list.getCurrent();
      if ( TRACE ) { System.out.println("applying: " + memento); }
      memento.apply(graph);
      if ( reverse )
      {
        list.toNext();
      }
      else
      {
        list.toPrev();
      }
    }
    reverse = !reverse;
  }

  public int size()
  {
    return list.size();
  }

  public void removeUselessMementos()
  {
    if ( list.size() > 0 )
    {
      list.toHead();
      while ( list.getCurrent() != null )
      {
        if ( ((MementoInterface)list.getCurrent()).isUseless() )
        {
          list.removeCurrent();
        }
        else
        {
          list.toNext();
        }
      }
    }
  }
  
  public boolean isUseless()
  {
    if ( list.size() > 0 )
    {
      list.toHead();
      while ( list.getCurrent() != null )
      {
        if ( !((MementoInterface)list.getCurrent()).isUseless() )
        {
          return false;
        }
        list.toNext();
      }
      return true;
    }
    else
    {
      return true;
    }
  }

  public String toString() { return title; }
}