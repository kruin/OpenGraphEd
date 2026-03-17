package graphStructure;

public class HalfEdge
{
  private Node sourceNode;
  private HalfEdge twinEdge; // could be eliminated by using parentEdge.
  private HalfEdge nextEdge;
  private HalfEdge previousEdge;
  private Edge parentEdge;

  public HalfEdge(Node aSourceNode, Edge aParentEdge, HalfEdge aTwin)
  {
    sourceNode = aSourceNode;
    parentEdge = aParentEdge;
    twinEdge = aTwin;
  }
  
  public boolean equals(Object o)
  {
    if ( o instanceof HalfEdge )
    {
      return ( ((HalfEdge)o).getSourceNode().equals( getSourceNode() ) && 
               ((HalfEdge)o).getDestNode().equals( getDestNode() ) );
    }
    else
    {
      return false;
    }
  }
  
  public void setTwinEdge(HalfEdge aTwin) { twinEdge = aTwin; }
  
  public HalfEdge getTwinEdge() { return twinEdge; }
  
  public Node getSourceNode() { return sourceNode; }
  
  public Node getDestNode() { return twinEdge.sourceNode; }
  
  public Edge getParentEdge() { return parentEdge; }
  
  public void setNext(HalfEdge next) { nextEdge = next; }
  
  public HalfEdge getNext() { return nextEdge; }
  
  public void setPrevious(HalfEdge previous) { previousEdge = previous; }
  
  public HalfEdge getPrevious() { return previousEdge; }
  
  public String infoString()
  {
    return "h: " + hashCode() + " sn: " + sourceNode + " te: " + twinEdge.hashCode() + " ne: " + nextEdge.hashCode() + 
    " pe: " + previousEdge.hashCode() + " par: " + parentEdge;
  }
}