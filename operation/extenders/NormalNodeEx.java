package operation.extenders;

import java.util.Vector;
import graphStructure.*;

public class NormalNodeEx extends NodeExtender
{
  protected NormalNodeEx r1Parent;
  protected NormalNodeEx r2Parent;
  protected NormalNodeEx r3Parent;
  protected int canonicalNumber;
  
  public NormalNodeEx()
  {
    super();
  }
  
  public NormalNodeEx(NormalNodeEx node)
  {
    super(node);
  }
  
  public void setR1Parent(NormalNodeEx r1) { r1Parent = r1; }
  
  public NormalNodeEx getR1Parent() { return r1Parent; }
  
  public void setR2Parent(NormalNodeEx r2) { r2Parent = r2; }
  
  public NormalNodeEx getR2Parent() { return r2Parent; }
  
  public void setR3Parent(NormalNodeEx r3) { r3Parent = r3; }
  
  public NormalNodeEx getR3Parent() { return r3Parent; }
  
  public void setCanonicalNumber(int canon) { canonicalNumber = canon; }
  
  public int getCanonicalNumber() { return canonicalNumber; }
  
  public Vector incidentEdgesToSmallerCanonicalNumber()
  {
    Vector incidentEdges = incidentEdges();
    int startIndex = -1;
    NormalEdgeEx currentEdge = (NormalEdgeEx)incidentEdges.firstElement();
    if ( ((NormalNodeEx)currentEdge.otherEndFrom(this)).getCanonicalNumber() < 
         canonicalNumber )
    {
      for ( int i=incidentEdges.size()-1; i>=0; i-- )
      {
        currentEdge = (NormalEdgeEx)incidentEdges.elementAt(i);
        if ( ((NormalNodeEx)currentEdge.otherEndFrom(this)).getCanonicalNumber() > 
             canonicalNumber )
        {
          if ( i < incidentEdges.size()-1 )
          {
            startIndex = i+1;
          }
          else
          {
            startIndex = 0;
          }
          break;
        }
      }
    }
    else
    {
      for ( int i=0; i<incidentEdges.size(); i++ )
      {
        currentEdge = (NormalEdgeEx)incidentEdges.elementAt(i);
        if ( ((NormalNodeEx)currentEdge.otherEndFrom(this)).getCanonicalNumber() < 
             canonicalNumber )
        {
          startIndex = i;
          break;
        }
      }
    }
    
    Vector edgeVector = new Vector(incidentEdges.size());
    
    if ( startIndex != -1 )
    {
      for ( int j=0; j<incidentEdges.size(); j++ )
      {
        currentEdge = (NormalEdgeEx)incidentEdges.elementAt((startIndex+j)%incidentEdges.size());
        if ( ((NormalNodeEx)currentEdge.otherEndFrom(this)).getCanonicalNumber() < 
             canonicalNumber )
        {
          edgeVector.addElement(currentEdge);
        }
        else
        {
          break;
        }
      }
    }
    
   return edgeVector;
  }
}