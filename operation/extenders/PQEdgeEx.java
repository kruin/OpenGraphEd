package operation.extenders;

import graphStructure.*;
import dataStructure.pqTree.PQNode;

public class PQEdgeEx extends EdgeExtender
{
  protected PQNode pqNode;
  
  public void setPQNode(PQNode pq) { pqNode = pq; }
  
  public PQNode getPQNode() { return pqNode; }
}