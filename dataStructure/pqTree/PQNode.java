package dataStructure.pqTree;

import java.util.Vector;
import java.awt.*;

// The secondary model component of the model/view/controller for this application
// This class represents the nodes of the PQTree.
public class PQNode
{
  public static final boolean SHOW_DEBUG_OUTPUT = false;
  public static final int DRAW_SIZE = 12;
  public static final int DRAW_BOUNDARY_SIZE = 12;
  public static final int DRAW_CONNECTOR_SIZE = 24;
  public static final Color DRAW_NORMAL_COLOR = Color.black;
  public static final Color DRAW_PARTIAL_COLOR = Color.gray;
  public static final Color DRAW_NO_PARENT_COLOR = Color.red;
  public static final Color DRAW_PSEUDO_PARENT_COLOR = Color.green;
  public static final Color DRAW_DEBUG_COLOR = Color.blue;

  private static final int LABEL_EMPTY = 0;
  private static final int LABEL_PARTIAL = 1;
  private static final int LABEL_FULL = 2;
  private static final int TYPE_PNODE = 0;
  private static final int TYPE_QNODE = 1;
  protected static final int TYPE_DNODE = 3;

  private int label; // 0 - empty, 1 - partial, 2 - full
  protected int type; // 0 - P-node, 1 - Q-Node, 2 D-Node (Direction)

  private boolean blocked;
  private boolean queued;
  private int pertinentChildCount; // The number of pertinent children before the reduction
  private int pertinentLeafCount; // The number of pertinent leaf decendants after reduction

  private Object data = null; // the data stored, only valid for leaf nodes.
  private boolean deleted; // marked interior q-node children have deleted parents
  private boolean pseudoNode; // marked interior q-node children of the root may need to be reparented to a pseudonode.

  private PQNode parent = null; // not valid for marked interior q-node children, or null for root.

  // SIBLING ACCESS VARIABLES
  protected PQNodePair siblings = null; // only valid for children of q-nodes
  private PQNode left = null; // for p-node children only
  private PQNode right = null;// for p-node children only
  private PQNode fullLeft = null; // for full children only
  private PQNode fullRight = null; // for full children only
  private PQNode partialLeft = null; // for partial children only
  private PQNode partialRight = null; // for partial children only

  // CHILDREN ACCESS VARIABLES
  private int childCount; // number of child nodes, for p-nodes only
  private int fullChildCount; // number of full child nodes.
  private int partialChildCount; // number of partial child nodes (max is 2)
  private PQNodePair endMostChildren = null; // only valid for q-nodes
  private PQNode childAccessNode = null; // child node to use to access children of p-nodes
  private PQNode fullChildAccessNode = null; // child node to use to access full children
  private PQNode partialChildAccessNode = null;  // child node to use to access partial children

  // DRAWING VARIABLES
  private int subLeafCount; // number of leaf nodes under this node, field used for drawing the tree.
  private int depth; // greatest depth of node from leaf node, field used for drawing the tree.
  private int childBounds; // used for drawing a pseudo-node
  private int leftBound; // used for drawing a pseudo-node

  // constructor for non-leaf nodes
  public PQNode()
  {
    init(true);
  }

  // constructor for leaf nodes
  public PQNode(Object data)
  {
    init(true);
    this.data = data;
  }

  // initialize this node to default values
  public void init(boolean pNode)
  {
    childAccessNode = null;
    fullChildAccessNode = null;
    partialChildAccessNode = null;
    endMostChildren = null;
    siblings = null;
    childCount = 0;
    fullChildCount = 0;
    partialChildCount = 0;
    label = LABEL_EMPTY;
    pertinentChildCount = 0;
    pertinentLeafCount = 0;
    queued = false;
    parent = null;
    left = null;
    right = null;
    fullLeft = null;
    fullRight = null;
    partialLeft = null;
    partialRight = null;
    if (pNode)
    {
      type = TYPE_PNODE;
    }
    else
    {
      type = TYPE_QNODE;
    }
    data = null;
    deleted = false;
    pseudoNode = false;
  }

  public void delete()
  {
    if( pseudoNode )
    {
      childAccessNode = null;
      partialChildAccessNode = null;
      siblings = null;
      childCount = 0;
      partialChildCount = 0;
      pertinentChildCount = 0;
      pertinentLeafCount = 0;
      queued = false;
      parent = null;
      left = null;
      right = null;
      fullLeft = null;
      fullRight = null;
      partialLeft = null;
      partialRight = null;
    }
    else
    {
      init(false);
    }
    deleted = true;
  }


  // accessor and modifier methods...
  public int getPertinentChildCount() { return pertinentChildCount; }

  public void setPertinentChildCount(int value) { pertinentChildCount = value; }

  public int getPertinentLeafCount() { return pertinentLeafCount; }

  public void setPertinentLeafCount(int value) { pertinentLeafCount = value; }

  public boolean isPseudoNode() { return pseudoNode; }

  public void pseudoNode() { pseudoNode = true; childBounds = 0; leftBound = Integer.MAX_VALUE; subLeafCount = pertinentChildCount; }

  public boolean isDeleted() { return deleted; }

  public PQNode getParent() { return parent; }

  public void setParent(PQNode theParent) { parent = theParent; }

  public PQNodePair getSiblings() { return siblings; }

  public int getNumFullChildren() { return fullChildCount; }

  public int getNumPartialChildren() { return partialChildCount; }

  public boolean isQNode() { return type == TYPE_QNODE; }

  public boolean isPNode() { return type == TYPE_PNODE; }

  public boolean isDNode() { return type == TYPE_DNODE; }

  public boolean isFull() { return label == LABEL_FULL; }

  public boolean isFullOrDirectedFull(PQNode aNode) throws Exception
  {
    return ( ( !isDNode() && isFull() ) ||
             ( isDNode() &&
               siblings.otherPQNode(aNode) != null &&
               siblings.otherPQNode(aNode).isFullOrDirectedFull(this) ) );
  }

  public boolean isPartial() { return label == LABEL_PARTIAL; }

  public boolean isPartialOrDirectedPartial(PQNode aNode) throws Exception
  {
    return ( ( !isDNode() && isPartial() ) ||
             ( isDNode() &&
               siblings.otherPQNode(aNode) != null &&
               siblings.otherPQNode(aNode).isPartialOrDirectedPartial(this) ) );
  }

  public boolean isEmpty() { return label == LABEL_EMPTY; }

  public boolean isEmptyOrDirectedEmpty(PQNode aNode) throws Exception
  {
    return ( ( !isDNode() && isEmpty() ) ||
             ( isDNode() &&
               siblings.otherPQNode(aNode) != null &&
               siblings.otherPQNode(aNode).isEmptyOrDirectedEmpty(this) ) );
  }

  public boolean isBlocked() { return blocked; }

  public void setBlocked() { blocked = true; }

  public void setUnblocked() { blocked = false; }

  public void setQueued() { queued = true; }

  public void setUnqueued() { queued = false; }

  public boolean isQueued() { return queued; }

  public void setData(Object someData) { data = someData; }

  public Object getData() { return data; }

  public int getLabel() { return label; }

  public PQNodePair getEndMostChildren() { return endMostChildren; }

  public void becomeRoot() { parent = null; }

  public int getDepth() { return depth; }

  public int getNumChildren() throws Exception
  {
    if ( isQNode() )
    {
      throw new Exception("*** Warning, Qnodes do not store num children");
    }
    return childCount;
  }

  public int getNumEmptyChildren() throws Exception
  {
    if ( isQNode() )
    {
      throw new Exception("*** Warning, Qnodes do not store num (empty) children");
    }
    return childCount-fullChildCount-partialChildCount;
  }

  // convert this node to a q-node.
  public void convertToQNode() throws Exception
  {
    type = TYPE_QNODE;
    endMostChildren = new PQNodePair();
    if ( childCount > 0 )
    {
      throw new Exception("*** ERROR cannot convert to qnode unless no children present!");
    }
  }

  // convert this node to a p-node.
  public void convertToPNode() throws Exception
  {
    if (isQNode())
    {
      PQNode aNode, bNode;
      aNode = endMostChildren.PQNodeAt(0);
      bNode = endMostChildren.PQNodeAt(1);
      if ( hasOnlyTwoChildren() )
      {
        type = TYPE_PNODE;
        childAccessNode = aNode;
        childCount = 2;
        aNode.siblings = null;
        aNode.left = bNode;
        aNode.right = bNode;
        bNode.siblings = null;
        bNode.left = aNode;
        bNode.right = aNode;
        endMostChildren = null;
      }
      else
      {
        throw new Exception("*** ERROR convert to pnode was only designed for cases when a qnode has 2 children!");
      }
    }
  }

  public void convertToDNode() throws Exception
  {
    if (isPNode())
    {
      if ( childCount == 0 )
      {
        type = TYPE_DNODE;
      }
      else
      {
        throw new Exception("*** ERROR convert to dnode is only allowed for child-less pnodes!");
      }
    }
    else
    {
      throw new Exception("*** ERROR convert to dnode is only allowed for pnodes!");
    }
  }

  // label this node as a full node, updating its parent.
  public void labelAsFull() throws Exception
  {
    if ( !isFull() )
    {
      if ( parent != null )
      {
        parent.removeChild(this, false);
      }
      label = LABEL_FULL;
      if ( parent != null )
      {
        parent.addChild(this, false);
      }
    }
  }

  // label this node as a partial node, updating its parent.
  public void labelAsPartial() throws Exception
  {
    if ( !isPartial() )
    {
      if ( parent != null )
      {
        parent.removeChild(this, false);
      }
      label = LABEL_PARTIAL;
      if ( parent != null )
      {
        parent.addChild(this, false);
      }
    }
  }

  // label this node as a partial node, updating its parent.
  public void labelAsEmpty() throws Exception
  {
    if ( !isEmpty() )
    {
      if ( parent != null )
      {
        parent.removeChild(this, false);
      }
      label = LABEL_EMPTY;
      if ( parent != null )
      {
        parent.addChild(this, false);
      }
    }
  }

  // returns whether or not the current node has any children.
  public boolean hasChildren()
  {
    if ( isPNode() )
    {
      return ( childCount > 0 );
    }
    else if ( isQNode() )
    {
      return ( endMostChildren.size() > 0 );
    }
    else
    {
      return false;
    }
  }

  // returns all the children of this node in a vector, used only for displaying the tree.
  private Vector getAllChildren() throws Exception
  {
    Vector allVector = new Vector();
    if ( isPNode() )
    {
      if ( hasChildren() )
      {
        PQNode currentNode = childAccessNode;
        do
        {
          allVector.addElement(currentNode);
          currentNode = currentNode.right;
        }
        while ( currentNode != childAccessNode );
      }
    }
    else if ( isQNode() )
    {
      if ( hasChildren() )
      {
        PQNode previousNode = null;
        PQNode currentNode = endMostChildren.PQNodeAt(0);
        PQNode nextNode;
        PQNode lastNode = null;

        if ( isPseudoNode() )
        {
          if (currentNode.siblings.PQNodeAt(0) != null && currentNode.siblings.PQNodeAt(0).parent != this)
          {
            previousNode = currentNode.siblings.PQNodeAt(0);
          }
          else if (currentNode.siblings.PQNodeAt(1) != null && currentNode.siblings.PQNodeAt(1).parent != this)
          {
            previousNode = currentNode.siblings.PQNodeAt(1);
          }

          if ( endMostChildren.size() > 1 )
          {
            PQNode tempNode = endMostChildren.PQNodeAt(1);
            if (tempNode.siblings.PQNodeAt(0) != null && tempNode.siblings.PQNodeAt(0).parent != this)
            {
              lastNode = tempNode.siblings.PQNodeAt(0);
            }
            else if (tempNode.siblings.PQNodeAt(1) != null && tempNode.siblings.PQNodeAt(1).parent != this)
            {
              lastNode = tempNode.siblings.PQNodeAt(1);
            }
          }
        }
        do
        {
          allVector.addElement(currentNode);
          nextNode = currentNode.siblings.otherPQNode(previousNode);
          previousNode = currentNode;
          currentNode = nextNode;
        }
        while ( currentNode != lastNode );
      }
    }
    return allVector;
  }

  public boolean isSiblingOf(PQNode aNode) throws Exception
  {
    if ( siblings != null && aNode.siblings != null )
    {
      if( siblings.contains(aNode) && aNode.siblings.contains(this) )
      {
        return true;
      }
      else
      {
        if ( siblings.PQNodeAt(0) != null && siblings.PQNodeAt(0).isDNode() &&
             siblings.PQNodeAt(0).siblings.otherPQNode(this) == aNode )
        {
          return true;
        }
        if ( siblings.PQNodeAt(1) != null && siblings.PQNodeAt(1).isDNode() &&
             siblings.PQNodeAt(1).siblings.otherPQNode(this) == aNode )
        {
          return true;
        }
      }
      return false;
    }
    else
    {
      throw new Exception("*** ERROR isSiblingOf was used on non Q-Node children!");
    }
  }

  public PQNode getFullLeavesFrom() throws Exception
  {
    if ( isQNode() )
    {
      PQNode aFullChild = fullChildAccessNode;
      PQNode prevFullChild = null;
      PQNode firstFullChild = null;

      do
      {
        if ( aFullChild.siblings.PQNodeAt(0) == null ||
             ( aFullChild.siblings.PQNodeAt(0) != null &&
               !(aFullChild.siblings.PQNodeAt(0).isFullOrDirectedFull(aFullChild)) ) )
        {
          prevFullChild = aFullChild.siblings.PQNodeAt(0);
          firstFullChild = aFullChild;
          break;
        }
        if ( aFullChild.siblings.PQNodeAt(1) == null ||
             ( aFullChild.siblings.PQNodeAt(1) != null &&
               !(aFullChild.siblings.PQNodeAt(1).isFullOrDirectedFull(aFullChild)) ) )
        {
          prevFullChild = aFullChild.siblings.PQNodeAt(1);
          firstFullChild = aFullChild;
          break;
        }
        aFullChild = aFullChild.fullRight;
      }
      while ( aFullChild != fullChildAccessNode );

      if ( prevFullChild != null && prevFullChild.isDNode() )
      {
        PQNode nextNode, tempNode, prevNode;
        nextNode = prevFullChild;
        prevNode = firstFullChild;
        while ( nextNode != null && nextNode.isDNode() )
        {
          tempNode = nextNode;
          nextNode = nextNode.siblings.otherPQNode(prevNode);
          prevNode = tempNode;
        }
        prevFullChild = nextNode;
      }
      return prevFullChild;
    }
    else
    {
      throw new Exception("*** ERROR getFullLeavesFrom() is only meant for Q-Nodes!");
    }
  }

  public PQNode getFullLeavesTo() throws Exception
  {
    PQNode prevFullChild = getFullLeavesFrom();
    PQNode nextFullChild = null;
    PQNode aFullChild, aPrevFullChild, tempFullChild;
    if ( prevFullChild != null )
    {
      if ( prevFullChild.siblings.PQNodeAt(0) != null &&
           prevFullChild.siblings.PQNodeAt(0).isFullOrDirectedFull(prevFullChild) )
      {
        aFullChild = prevFullChild.siblings.PQNodeAt(0);
      }
      else if ( prevFullChild.siblings.PQNodeAt(1) != null &&
                prevFullChild.siblings.PQNodeAt(1).isFullOrDirectedFull(prevFullChild) )
      {
        aFullChild = prevFullChild.siblings.PQNodeAt(1);
      }
      else
      {
        throw new Exception("*** ERROR getFullLeavesTo() failed to get a valid fullLeaveFrom!");
      }

      nextFullChild = aFullChild;
      aPrevFullChild = prevFullChild;

      do
      {
        tempFullChild = nextFullChild;
        nextFullChild = nextFullChild.siblings.otherPQNode(aPrevFullChild);
        aPrevFullChild = tempFullChild;
      }
      while ( nextFullChild != null &&
              ( nextFullChild.isFullOrDirectedFull(aPrevFullChild) ||
                nextFullChild.isDNode() ) );
    }
    else
    {
      aFullChild = fullChildAccessNode;
      do
      {
        if ( ( aFullChild.siblings.PQNodeAt(0) != null &&
               !(aFullChild.siblings.PQNodeAt(0).isFullOrDirectedFull(aFullChild)) ) )
        {
          nextFullChild = aFullChild.siblings.PQNodeAt(0);
          prevFullChild = aFullChild;
          break;
        }
        if ( ( aFullChild.siblings.PQNodeAt(1) != null &&
               !(aFullChild.siblings.PQNodeAt(1).isFullOrDirectedFull(aFullChild)) ) )
        {
          nextFullChild = aFullChild.siblings.PQNodeAt(1);
          prevFullChild = aFullChild;
          break;
        }
        aFullChild = aFullChild.fullRight;
      }
      while ( aFullChild != fullChildAccessNode );

      if ( nextFullChild.isDNode() )
      {
        do
        {
          tempFullChild = nextFullChild;
          nextFullChild = nextFullChild.siblings.otherPQNode(prevFullChild);
          prevFullChild = tempFullChild;
        }
        while ( nextFullChild != null && nextFullChild.isDNode() );
      }
    }
    return nextFullChild;
  }

  public Vector getFullLeaves() throws Exception
  {
    Vector fullLeafVector = new Vector();
    Vector subLeafVector;
    if ( isQNode() )
    {
      if ( hasChildren() )
      {
        PQNode firstFullChild = null;
        PQNode aFullChild = fullChildAccessNode;
        PQNode prevFullChild = null;
        PQNode tempFullChild;
        do
        {
          if ( aFullChild.siblings.PQNodeAt(0) == null ||
               ( aFullChild.siblings.PQNodeAt(0) != null &&
                 !(aFullChild.siblings.PQNodeAt(0).isFullOrDirectedFull(aFullChild)) ) )
          {
            prevFullChild = aFullChild.siblings.PQNodeAt(0);
            firstFullChild = aFullChild;
            break;
          }
          if ( aFullChild.siblings.PQNodeAt(1) == null ||
               ( aFullChild.siblings.PQNodeAt(1) != null &&
                 !(aFullChild.siblings.PQNodeAt(1).isFullOrDirectedFull(aFullChild)) ) )
          {
            prevFullChild = aFullChild.siblings.PQNodeAt(1);
            firstFullChild = aFullChild;
            break;
          }
          aFullChild = aFullChild.fullRight;
        }
        while ( aFullChild != fullChildAccessNode );

        // add all consecutive dNodes adjacent (and before) the firstFullChild.
        if ( prevFullChild != null && prevFullChild.isDNode() )
        {
          PQNode nextNode, tempNode, prevNode;
          Vector directedNodes = new Vector();
          nextNode = prevFullChild;
          prevNode = firstFullChild;
          while ( nextNode != null && nextNode.isDNode() )
          {
            if ( prevNode == ((PQDNode)nextNode).getDirection() )
            {
              ((PQDNode)nextNode).setReadInReverseDirection(false);
            }
            else if ( nextNode.siblings.otherPQNode(prevNode) == ((PQDNode)nextNode).getDirection() )
            {
              ((PQDNode)nextNode).setReadInReverseDirection(true);
            }
            else
            {
              throw new Exception("*** ERROR: Could not verify DNode read direction!");
            }
            directedNodes.addElement(nextNode);
            tempNode = nextNode;
            nextNode = nextNode.siblings.otherPQNode(prevNode);
            prevNode = tempNode;
          }
          for ( int i=directedNodes.size(); i>0; i-- )
          {
            fullLeafVector.addElement(directedNodes.elementAt(i-1));
          }
        }

        aFullChild = firstFullChild;
        do
        {
          if ( aFullChild.isDNode() )
          {
            if ( prevFullChild == ((PQDNode)aFullChild).getDirection() )
            {
              ((PQDNode)aFullChild).setReadInReverseDirection(true);
            }
            else if ( aFullChild.siblings.otherPQNode(prevFullChild) == ((PQDNode)aFullChild).getDirection() )
            {
              ((PQDNode)aFullChild).setReadInReverseDirection(false);
            }
            else
            {
              throw new Exception("Could not verify DNode read direction!");
            }
          }
          subLeafVector = aFullChild.getFullLeaves();
          fullLeafVector.addAll(subLeafVector);
          tempFullChild = aFullChild;
          aFullChild = aFullChild.siblings.otherPQNode(prevFullChild);
          prevFullChild = tempFullChild;
        }
        while ( aFullChild != null &&
                ( aFullChild.isFullOrDirectedFull(prevFullChild) ||
                  aFullChild.isDNode() ) );
      }
      else
      {
        //fullLeafVector.addElement(this);
        throw new Exception("*** ERROR: QNode with no full children to get!");
      }
    }
    else
    {
      if ( hasChildren() )
      {
        PQNode aFullChild = fullChildAccessNode;
        do
        {
          subLeafVector = aFullChild.getFullLeaves();
          fullLeafVector.addAll(subLeafVector);
          aFullChild = aFullChild.fullRight;
        }
        while ( aFullChild != fullChildAccessNode );
      }
      else
      {
        fullLeafVector.addElement(this);
      }
    }
    return fullLeafVector;
  }

  // reparents the full children of this node to the specified node.
  public void moveFullChildrenTo(PQNode newNode) throws Exception
  {
    if ( isPNode() )
    {
      if ( fullChildCount > 0)
      {
        PQNode currentNode = fullChildAccessNode;
        PQNode nextNode;
        do
        {
          nextNode = currentNode.fullRight;
          removeChild(currentNode);
          newNode.addChild(currentNode);
          currentNode = nextNode;
        }
        while ( fullChildAccessNode != null );
      }
    }
    else
    {
      throw new Exception("*** ERROR move full children method not meant for children of q nodes!");
    }
  }

  // returns the partial child of this node at index 0 or 1 (max 2 partial children per node)
  public PQNode getPartialChild(int index) throws Exception
  {
    if ( index+1 > partialChildCount )
    {
      throw new Exception("*** ERROR tried to get a partial child that does not exist! [" + index + "]");
    }
    if ( index == 0)
    {
      return partialChildAccessNode;
    }
    else if ( index == 1 )
    {
      return partialChildAccessNode.partialRight;
    }
    else
    {
      throw new Exception("*** ERROR tried to get a partial child that does not exist! [" + index + "]");
    }
  }

  // removes and returns the only full child of a node.
  public PQNode removeOnlyFullChild() throws Exception
  {
    if ( isPNode() )
    {
      if ( fullChildCount != 1 )
      {
        throw new Exception("*** ERROR not exactly one full child to remove! " + fullChildCount);
      }
      PQNode returnNode = fullChildAccessNode;
      removeChild(returnNode);
      return returnNode;
    }
    else
    {
      throw new Exception("*** ERROR remove only full child is only meant for p nodes!" );
    }
  }

  public PQNode getOnlyFullChild() throws Exception
  {
    if ( isPNode() )
    {
      if ( fullChildCount != 1 )
      {
        throw new Exception("*** ERROR not exactly one full child to retrieve! " + fullChildCount);
      }

      return fullChildAccessNode;
    }
    else
    {
      throw new Exception("*** ERROR retrieve only full child is only meant for p nodes!" );
    }
  }

  // removes and returns the only empty child of a node.
  public PQNode removeOnlyEmptyChild() throws Exception
  {
    if ( isPNode() )
    {
      if ( getNumEmptyChildren() != 1 )
      {
        throw new Exception("*** ERROR not exactly one empty child to remove! " + getNumEmptyChildren());
      }
      PQNode returnNode = childAccessNode;
      do
      {
        if ( returnNode.isEmpty() )
        {
          break;
        }
        returnNode = returnNode.right;
      }
      while ( returnNode != childAccessNode );
      removeChild(returnNode);
      return returnNode;
    }
    else
    {
      throw new Exception("*** ERROR remove only empty child is only meant for p nodes!" );
    }
  }

  // the default for addChild method is to modify all related settings.
  public void addChild(PQNode pq) throws Exception
  {
    addChild(pq, true);
  }

  // adds the given pq node to this pq nodes children lists, setting
  // all of its sibling and parent references.
  // a false modify value will only modify the full/partial structure.
  public void addChild(PQNode pq, boolean modify) throws Exception
  {
    if ( pq.isFull() )
    {
      fullChildCount++;
      pq.fullLeft = null;
      pq.fullRight = null;
      if ( fullChildAccessNode == null )
      {
        fullChildAccessNode = pq;
        fullChildAccessNode.fullLeft = fullChildAccessNode;
        fullChildAccessNode.fullRight = fullChildAccessNode;
      }
      else
      {
        pq.fullLeft = fullChildAccessNode.fullLeft;
        pq.fullLeft.fullRight = pq;
        fullChildAccessNode.fullLeft = pq;
        pq.fullRight = fullChildAccessNode;
        fullChildAccessNode = pq;
      }
    }
    else if ( pq.isPartial() )
    {
      partialChildCount++;
      pq.partialLeft = null;
      pq.partialRight = null;
      if ( partialChildAccessNode == null )
      {
        partialChildAccessNode = pq;
        partialChildAccessNode.partialLeft = partialChildAccessNode;
        partialChildAccessNode.partialRight = partialChildAccessNode;
      }
      else
      {
        pq.partialLeft = partialChildAccessNode.partialLeft;
        pq.partialLeft.partialRight = pq;
        partialChildAccessNode.partialLeft = pq;
        pq.partialRight = partialChildAccessNode;
        partialChildAccessNode = pq;
      }
    }
    // store p-node children in an arbitrary order doubly linked circular list.
    if ( isPNode() && modify )
    {
      pq.parent = this;
      childCount++;
      pq.left = null;
      pq.right = null;
      pq.siblings = null;
      if ( childAccessNode == null )
      {
        childAccessNode = pq;
        childAccessNode.left = childAccessNode;
        childAccessNode.right = childAccessNode;
      }
      else
      {
        pq.left = childAccessNode.left;
        pq.left.right = pq;
        childAccessNode.left = pq;
        pq.right = childAccessNode;
        childAccessNode = pq;
      }
    }
    // store q-node children in a fixed order doubly linked list, the siblings of the childen
    // are an unordered set
    else if ( isQNode() && !isPseudoNode() && modify )
    {
      pq.parent = this;
      PQNode sibling = null;
      if ( pq.siblings != null )
      {
        if ( pq.siblings.PQNodeAt(0) != null && endMostChildren.contains(pq.siblings.PQNodeAt(0)) )
        {
          sibling = pq.siblings.PQNodeAt(0);
        }
        else if ( pq.siblings.PQNodeAt(1) != null && endMostChildren.contains(pq.siblings.PQNodeAt(1)) )
        {
          sibling = pq.siblings.PQNodeAt(1);
        }
      }
      else
      {
        pq.siblings = new PQNodePair();
      }

      if ( sibling == null )
      {
        for (int i=0; i<endMostChildren.size(); i++)
        {
          if ( ((PQNode)endMostChildren.PQNodeAt(i)).label == pq.label )
          {
            sibling = (PQNode)endMostChildren.PQNodeAt(i);
            break;
          }
          else if ( ((PQNode)endMostChildren.PQNodeAt(i)).isFull() && pq.isPartial() )
          {
            sibling = (PQNode)endMostChildren.PQNodeAt(i);
            break;
          }
          else if ( ((PQNode)endMostChildren.PQNodeAt(i)).isPartial() && pq.isFull() )
          {
            sibling = (PQNode)endMostChildren.PQNodeAt(i);
            break;
          }

        }
      }
      if ( sibling == null && endMostChildren.size() > 0 )
      {
        sibling = (PQNode)endMostChildren.PQNodeAt(0);
      }
      if ( sibling != null )
      {
        if ( endMostChildren.size() > 1 )
        {
          endMostChildren.removePQNode(sibling);
        }
        endMostChildren.addPQNode(pq);
        sibling.siblings.addPQNode(pq);
        pq.siblings.addPQNode(sibling);
      }
      else
      {
        endMostChildren.addPQNode(pq);
      }
    }
    else if ( isQNode() && isPseudoNode() && modify )
    {
      pq.parent = this;
      if ( childAccessNode == null )
      {
        childAccessNode = pq;
      }
      if ( pq.siblings != null && pq.siblings.size() == 2 )
      {
        if ( !(pq.siblings.PQNodeAt(0).parent == this && pq.siblings.PQNodeAt(1).parent == this) )
        {
          endMostChildren.addPQNode(pq);
        }
      }
      else
      {
        throw new Exception("*** ERROR invalid child being added to pseudonode!");
      }
    }
  }

  // absorbs the children of the given partial q-node as children of this q-node, placing
  // the new children at the position of the given partial q-node child in the sibling list.
  public void absorbPartialChild(PQNode partialChild) throws Exception
  {
    if ( isQNode() && partialChild.isQNode() && partialChild.isPartial() ) // partialChild.parent == this???
    {
      PQNode fullConnectChild = partialChild.siblings.PQNodeAt(0);
      if ( !fullConnectChild.isFullOrDirectedFull(partialChild) &&
           !fullConnectChild.isPartialOrDirectedPartial(partialChild) )
      {
        fullConnectChild = partialChild.siblings.PQNodeAt(1);
        if ( fullConnectChild != null &&
             !fullConnectChild.isFullOrDirectedFull(partialChild) &&
             !fullConnectChild.isPartialOrDirectedPartial(partialChild) )
        {
          fullConnectChild = null;
        }
      }
      PQNode emptyConnectChild = partialChild.siblings.PQNodeAt(0);
      if ( !emptyConnectChild.isEmptyOrDirectedEmpty(partialChild) )
      {
        emptyConnectChild = partialChild.siblings.PQNodeAt(1);
        if ( emptyConnectChild != null &&
             !emptyConnectChild.isEmptyOrDirectedEmpty(partialChild) )
        {
          emptyConnectChild = null;
        }
      }
      PQNode fullJoinChild = (PQNode)partialChild.endMostChildren.PQNodeAt(0);
      if ( !fullJoinChild.isFullOrDirectedFull(null) )
      {
        fullJoinChild = null;
        if ( partialChild.endMostChildren.size() > 1 )
        {
          fullJoinChild = (PQNode)partialChild.endMostChildren.PQNodeAt(1);
          if ( !fullJoinChild.isFullOrDirectedFull(null) )
          {
            fullJoinChild = null;
          }
        }
      }
      PQNode emptyJoinChild = (PQNode)partialChild.endMostChildren.PQNodeAt(0);
      if ( !emptyJoinChild.isEmptyOrDirectedEmpty(null) )
      {
        emptyJoinChild = null;
        if ( partialChild.endMostChildren.size() > 1 )
        {
          emptyJoinChild = (PQNode)partialChild.endMostChildren.PQNodeAt(1);
          if ( !emptyJoinChild.isEmptyOrDirectedEmpty(null) )
          {
            emptyJoinChild = null;
          }
        }
      }
      if ( fullJoinChild == null || emptyJoinChild == null )
      {
        throw new Exception("*** ERROR invalid partial child in absorb partial child!");
      }
      if ( fullConnectChild != null )
      {
        fullJoinChild.siblings.addPQNode(fullConnectChild);
        fullConnectChild.siblings.replacePQNode(partialChild, fullJoinChild);
      }
      else
      {
        if ( !endMostChildren.removePQNode(partialChild) )
        {
          throw new Exception("*** ERROR could not absorb partial child!");
        }
        fullJoinChild.parent = this;
        endMostChildren.addPQNode(fullJoinChild);
      }
      if ( emptyConnectChild != null )
      {
        emptyJoinChild.siblings.addPQNode(emptyConnectChild);
        emptyConnectChild.siblings.replacePQNode(partialChild, emptyJoinChild);
      }
      else
      {
        if ( !endMostChildren.removePQNode(partialChild) )
        {
          throw new Exception("*** ERROR could not absorb partial child!");
        }
        emptyJoinChild.parent = this;
        endMostChildren.addPQNode(emptyJoinChild);
      }
      if ( partialChild.fullChildCount > 0)
      {
        PQNode currentNode = partialChild.fullChildAccessNode;
        PQNode nextNode;
        do
        {
          nextNode = currentNode.fullRight;
          partialChild.removeChild(currentNode, false);
          addChild(currentNode, false);
          currentNode.parent = this;
          if ( nextNode != currentNode )
          {
            currentNode = nextNode;
          }
          else
          {
            break;
          }
        }
        while ( partialChild.fullChildAccessNode != null );
      }
      removeChild(partialChild, false);
      partialChild.delete();
    }
  }

  // the default for removeChild method is to modify all related settings.
  public void removeChild(PQNode pq)
  {
    removeChild(pq, true);
  }

  // removes the given pq node from this pq nodes children lists, setting
  // all of its sibling and parent references to null.
  // a false modify value will only modify the full/partial structure.
  public void removeChild(PQNode pq, boolean modify)
  {
    if ( pq.isFull() )
    {
      fullChildCount--;
      if ( pq.fullRight == pq ) // only 1 node left...
      {
        pq.fullRight = null;
        pq.fullLeft = null;
        fullChildAccessNode = null;
      }
      else
      {
        if ( pq == fullChildAccessNode )
        {
          fullChildAccessNode = fullChildAccessNode.fullRight;
        }
        pq.fullRight.fullLeft = pq.fullLeft;
        pq.fullLeft.fullRight = pq.fullRight;
        pq.fullLeft = null;
        pq.fullRight = null;
      }
    }
    else if ( pq.isPartial() )
    {
      partialChildCount--;
      if ( pq.partialRight == pq ) // only 1 node left...
      {
        pq.partialRight = null;
        pq.partialLeft = null;
        partialChildAccessNode = null;
      }
      else
      {
        if ( pq == partialChildAccessNode )
        {
          partialChildAccessNode = partialChildAccessNode.partialRight;
        }
        pq.partialRight.partialLeft = pq.partialLeft;
        pq.partialLeft.partialRight = pq.partialRight;
        pq.partialLeft = null;
        pq.partialRight = null;
      }
    }

    if ( isPNode() && modify )
    {
      pq.parent = null;
      childCount--;
      if ( pq == childAccessNode )
      {
        if ( pq.right == pq ) // only 1 node left...
        {
          pq.right = null;
          pq.left = null;
          childAccessNode = null;
        }
        else
        {
          childAccessNode = childAccessNode.right;
          pq.right.left = pq.left;
          pq.left.right = pq.right;
          pq.left = null;
          pq.right = null;
        }
      }
      else
      {
        pq.right.left = pq.left;
        pq.left.right = pq.right;
        pq.left = null;
        pq.right = null;
      }
    }
    else if ( isQNode() && modify )
    {
      pq.parent = null;
      if ( pq.siblings.PQNodeAt(1) == null )
      {
        endMostChildren.removePQNode(pq);
        if ( pq.siblings.PQNodeAt(0) != null )
        {
          if ( !endMostChildren.contains(pq.siblings.PQNodeAt(0)) )
          {
            try
            {
              endMostChildren.addPQNode(pq.siblings.PQNodeAt(0));
            }
            catch (Exception e)
            {
              e.printStackTrace();
            }
          }
          pq.siblings.PQNodeAt(0).siblings.removePQNode(pq);
          pq.siblings = null;
        }
      }
      else
      {
        // remove child lead to a gap in the siblings...
        pq.siblings.PQNodeAt(0).siblings.removePQNode(pq);
        pq.siblings.PQNodeAt(1).siblings.removePQNode(pq);
        pq.siblings = null;
      }
    }
  }

  // the default for replaceChild method is to modify all related settings.
  public boolean replaceChild(PQNode oldPQNode, PQNode newPQNode) throws Exception
  {
    return replaceChild(oldPQNode, newPQNode, true);
  }

  // replaces a given pq node in this pq nodes children lists, with a second given pq node.
  // the new pq node will assume the sibling and parent references of the old node.
  // a false modify value will not modify the values of the old pq node.
  public boolean replaceChild(PQNode oldPQNode, PQNode newPQNode, boolean modify) throws Exception
  {
    if ( isQNode() )
    {
      newPQNode.siblings = oldPQNode.siblings;
      if ( oldPQNode.siblings.PQNodeAt(0) != null )
      {
        oldPQNode.siblings.PQNodeAt(0).siblings.replacePQNode(oldPQNode, newPQNode);
      }
      if ( oldPQNode.siblings.PQNodeAt(1) != null )
      {
        oldPQNode.siblings.PQNodeAt(1).siblings.replacePQNode(oldPQNode, newPQNode);
      }
      if ( endMostChildren.removePQNode(oldPQNode) )
      {
        endMostChildren.addPQNode(newPQNode);
      }
    }
    else
    {
      newPQNode.left = oldPQNode.left;
      if ( oldPQNode.left != null )
      {
        oldPQNode.left.right = newPQNode;
      }
      newPQNode.right = oldPQNode.right;
      if ( oldPQNode.right != null )
      {
        oldPQNode.right.left = newPQNode;
      }
    }

    newPQNode.parent = this; // do i need this?
    removeChild(oldPQNode, false);
    addChild(newPQNode, false);
    
    if ( childAccessNode == oldPQNode )
    {
      childAccessNode = newPQNode;
    }
    
    if ( modify )
    {
      oldPQNode.fullLeft = null;
      oldPQNode.fullRight = null;
      oldPQNode.partialLeft = null;
      oldPQNode.partialRight = null;
      oldPQNode.parent = null;
      if ( isQNode() )
      {
        oldPQNode.siblings = null;
      }
      else
      {
        oldPQNode.left = null;
        oldPQNode.right = null;
      }
    }
    return true;
  }

  public void replaceFullChildrenWith(PQNode pq) throws Exception
  {
    if (isQNode())
    {
      if ( fullChildCount > 0 )
      {
        PQNode tempNode;
        Vector newSiblings = new Vector();

        tempNode = getFullLeavesFrom();
        if ( tempNode != null )
        {
          newSiblings.addElement(tempNode);
        }
        tempNode = getFullLeavesTo();
        if ( tempNode != null )
        {
          newSiblings.addElement(tempNode);
        }
        tempNode = null;

        if ( newSiblings.size() == 0 )
        {
          endMostChildren = new PQNodePair();
          endMostChildren.addPQNode(pq);
        }
        else if ( newSiblings.size() == 1 )
        {
          PQNode endNode = null;
          for ( int i=0; i<endMostChildren.size(); i++ )
          {
            endNode = null;
            if ( isPseudoNode() )
            {
              endNode = (PQNode)endMostChildren.PQNodeAt(i);
              if ( endNode.siblings.PQNodeAt(0) != null &&
                   endNode.siblings.PQNodeAt(0).parent != this )
              {
                endNode = endNode.siblings.PQNodeAt(0);
              }
              else if ( endNode.siblings.PQNodeAt(1) != null &&
                        endNode.siblings.PQNodeAt(1).parent != this )
              {
                endNode = endNode.siblings.PQNodeAt(1);
              }
              else
              {
                throw new Exception("*** ERROR could not find ends of pseudonode for checkPartialAreAtEnds!");
              }
            }

            if ( ((PQNode)endMostChildren.PQNodeAt(i)).isFullOrDirectedFull(endNode) ) // dfull didn't work
            {
              endMostChildren.removePQNodeAt(i);
              break;
            }
          }
          endMostChildren.addPQNode(pq);
        }

        pq.siblings = new PQNodePair();
        for (int i=0; i<newSiblings.size(); i++)
        {
          tempNode = (PQNode)newSiblings.elementAt(i);
          if ( tempNode.siblings.PQNodeAt(0) != null &&
               tempNode.siblings.PQNodeAt(0).isFullOrDirectedFull(tempNode) )
          {
            tempNode.siblings.replacePQNode(tempNode.siblings.PQNodeAt(0), pq);
            pq.siblings.addPQNode(tempNode);
          }
          else if ( tempNode.siblings.PQNodeAt(1) != null &&
                    tempNode.siblings.PQNodeAt(1).isFullOrDirectedFull(tempNode) )
          {
            tempNode.siblings.replacePQNode(tempNode.siblings.PQNodeAt(1), pq);
            pq.siblings.addPQNode(tempNode);
          }
        }

        pq.setParent(this);

        while ( fullChildAccessNode != null )
        {
          tempNode = fullChildAccessNode;
          removeChild(tempNode, false);
          tempNode.label = LABEL_EMPTY;
        }
      }
      else
      {
        throw new Exception("*** ERROR Could not replace full children since none existed!");
      }
    }
  }

  // makes this node assume all the children and properties (except label)
  // of the given child node, deleting the child node afterwards.
  public void becomeChild(PQNode theChild) throws Exception
  {
    childAccessNode = theChild.childAccessNode;
    fullChildAccessNode = theChild.fullChildAccessNode;
    partialChildAccessNode = theChild.partialChildAccessNode;
    endMostChildren = theChild.endMostChildren;
    childCount = theChild.childCount;
    fullChildCount = theChild.fullChildCount;
    partialChildCount = theChild.partialChildCount;
    pertinentChildCount = theChild.pertinentChildCount;
    pertinentLeafCount = theChild.pertinentLeafCount;
    type = theChild.type;
    data = theChild.data;
    deleted = theChild.deleted;
    pseudoNode = theChild.pseudoNode;
    if ( isPNode() )
    {
      throw new Exception("*** ERROR Nodes are only meant to assume the identity of one of their Q-Node children!");
    }
    else if ( isQNode() )
    {
      for (int i=0; i<endMostChildren.size(); i++)
      {
        ((PQNode)endMostChildren.PQNodeAt(i)).parent = this;
      }
      if ( fullChildCount > 0 )
      {
        PQNode currentNode = fullChildAccessNode;
        PQNode nextNode;
        do
        {
          nextNode = currentNode.fullRight;
          currentNode.parent = this;
          if ( nextNode != currentNode )
          {
            currentNode = nextNode;
          }
          else
          {
            break;
          }
        }
        while ( currentNode != fullChildAccessNode );
      }
    }
    theChild.delete();
  }

  // merges two partial q-node children of this node, reparenting all of the children of one
  // partial child node to the other partial child node.
  public void mergePartialChildren( PQNode partialChild1, PQNode partialChild2 ) throws Exception
  {
    if ( partialChild1.isPartial() && partialChild2.isPartial() )
    {
      PQNodePair endMostChildren1 = partialChild1.getEndMostChildren();
      PQNodePair endMostChildren2 = partialChild2.getEndMostChildren();
      if ( endMostChildren1.size() == 2 && endMostChildren2.size() == 2 )
      {
        PQNode tempNode;
        PQNode fullEndMostNode1 = null;
        PQNode emptyEndMostNode1 = null;
        for ( int i=0; i<endMostChildren1.size(); i++ )
        {
          tempNode = endMostChildren1.PQNodeAt(i);
          if ( tempNode.isFullOrDirectedFull(null) )
          {
            fullEndMostNode1 = tempNode;
          }
          else if ( tempNode.isEmptyOrDirectedEmpty(null) )
          {
            emptyEndMostNode1 = tempNode;
          }
        }
        PQNode fullEndMostNode2 = null;
        PQNode emptyEndMostNode2 = null;
        for ( int i=0; i<endMostChildren2.size(); i++ )
        {
          tempNode = endMostChildren2.PQNodeAt(i);
          if ( tempNode.isFullOrDirectedFull(null) )
          {
            fullEndMostNode2 = tempNode;
          }
          else if ( tempNode.isEmptyOrDirectedEmpty(null) )
          {
            emptyEndMostNode2 = tempNode;
          }
        }
        if ( fullEndMostNode1 != null && emptyEndMostNode1 != null &&
             fullEndMostNode2 != null && emptyEndMostNode2 != null )
        {
          fullEndMostNode1.parent = partialChild2;
          emptyEndMostNode1.parent = partialChild2;

          fullEndMostNode1.siblings.addPQNode(fullEndMostNode2);
          fullEndMostNode2.siblings.addPQNode(fullEndMostNode1);

          endMostChildren2.removePQNode(fullEndMostNode2);
          endMostChildren2.addPQNode(emptyEndMostNode1);

          endMostChildren1.removePQNode(fullEndMostNode1);
          endMostChildren1.removePQNode(emptyEndMostNode1);

          if ( partialChild1.fullChildCount > 0)
          {
            PQNode currentNode = partialChild1.fullChildAccessNode;
            PQNode nextNode;
            do
            {
              nextNode = currentNode.fullRight;
              partialChild1.removeChild(currentNode, false);
              partialChild2.addChild(currentNode, false);
              currentNode.parent = partialChild2;
              if ( nextNode != currentNode )
              {
                currentNode = nextNode;
              }
              else
              {
                break;
              }
            }
            while ( partialChild1.fullChildAccessNode != null );
          }
          removeChild(partialChild1);
        }
        else
        {
          throw new Exception("*** ERROR merge children were not partial (null)!");
        }
      }
      else
      {
        throw new Exception("*** ERROR merge children were not partial!");
      }
    }
    else
    {
      throw new Exception("*** ERROR merge only meant for partial children!");
    }
  }

  // Returns a vector containing the maximal consecutive group of siblings of this node
  // that are marked as blocked. (only valid for q-node children)
  public Vector getMaximalConsecutiveBlockedSiblings() throws Exception
  {
    Vector aVector = new Vector();
    PQNode previousNode = null;
    PQNode currentNode = null;
    PQNode nextNode = null;

    for ( int i=0; i<siblings.size(); i++ )
    {
      previousNode = this;
      currentNode = siblings.PQNodeAt(i);
      while ( currentNode != null && ( currentNode.isBlocked() || currentNode.isDNode() ) )
      {
        if ( !currentNode.isDNode() )
        {
          aVector.addElement(currentNode);
        }
        nextNode = currentNode.siblings.otherPQNode(previousNode);
        previousNode = currentNode;
        currentNode = nextNode;
      }
    }
    return aVector;
  }

  // returns the end most children of a q-node which are full.
  public Vector getFullEndMostChildren()
  {
    Vector aVector = new Vector();
    PQNode aNode;
    for ( int i=0; i<endMostChildren.size(); i++ )
    {
      aNode = (PQNode)endMostChildren.PQNodeAt(i);
      if ( aNode.isFull() )
      {
        aVector.addElement(aNode);
      }
    }
    return aVector;
  }

  // returns the end most children of a q-node which are empty.
  public Vector getEmptyEndMostChildren()
  {
    Vector aVector = new Vector();
    PQNode aNode;
    for ( int i=0; i<endMostChildren.size(); i++ )
    {
      aNode = (PQNode)endMostChildren.PQNodeAt(i);
      if ( aNode.isEmpty() )
      {
        aVector.addElement(aNode);
      }
    }
    return aVector;
  }

  // returns the siblings of this node which are blocked. (only valid for q-node children)
  public Vector getBlockedSiblings() throws Exception
  {
    Vector aVector = new Vector();
    if ( siblings != null )
    {
      PQNode checkNode;
      checkNode = getNonDirectedSibling(siblings.PQNodeAt(1));
      if ( checkNode != null && checkNode.isBlocked() )
      {
        aVector.addElement(checkNode);
      }
      checkNode = getNonDirectedSibling(siblings.PQNodeAt(0));
      if ( checkNode != null && checkNode.isBlocked() )
      {
        aVector.addElement(checkNode);
      }
    }
    return aVector;
  }

  // returns the siblings of this node which are unblocked. (only valid for q-node children)
  public Vector getUnblockedSiblings() throws Exception
  {
    Vector aVector = new Vector();
    if ( siblings != null )
    {
      PQNode checkNode;
      checkNode = getNonDirectedSibling(siblings.PQNodeAt(1));
      // if no nonDirectedSibling exists in one direction, but directedSiblings do exist,
      // we can use the parent pointer of the endMost directedSibling.
      // BUBBLE MUST NOT DO ANYTHING EXCEPT USE ITS PARENT POINTER!!!
      if ( checkNode == null )
      {
        checkNode = getEndMostDirectedSibling(siblings.PQNodeAt(1));
        if ( checkNode != null )
        {
          aVector.addElement(checkNode);
        }
      }
      else if ( checkNode != null && (!checkNode.isBlocked() && checkNode.parent != null && !checkNode.parent.isDeleted()) )
      {
        aVector.addElement(checkNode);
      }
      checkNode = getNonDirectedSibling(siblings.PQNodeAt(0));
      if ( checkNode == null )
      {
        checkNode = getEndMostDirectedSibling(siblings.PQNodeAt(0));
        if ( checkNode != null )
        {
          aVector.addElement(checkNode);
        }
      }
      else if ( checkNode != null && (!checkNode.isBlocked() && checkNode.parent != null && !checkNode.parent.isDeleted()) )
      {
        aVector.addElement(checkNode);
      }
    }
    return aVector;
  }

  public PQNode getEndMostDirectedSibling(PQNode otherSide) throws Exception
  {
    PQNode returnSibling = null;
    if ( siblings.otherPQNode(otherSide) != null )
    {
      returnSibling = siblings.otherPQNode(otherSide).getEndMostDirectedSibling(this);
    }
    else if ( isDNode() )
    {
      returnSibling = this;
    }
    return returnSibling;
  }

  public PQNode getNonDirectedSibling(PQNode otherSide) throws Exception
  {
    PQNode returnSibling = null;
    if ( siblings.otherPQNode(otherSide) != null )
    {
      if ( !siblings.otherPQNode(otherSide).isDNode() )
      {
        returnSibling = siblings.otherPQNode(otherSide);
      }
      else
      {
        returnSibling = siblings.otherPQNode(otherSide).getNonDirectedSibling(this);
      }
    }
    return returnSibling;
  }

  // returns whether or not the full children of a qnode are all adjacent
  public boolean checkFullAreAdjacent() throws Exception
  {
    if ( fullChildCount == 0 )
    {
      return true;
    }
    else
    {
      PQNode previousChild = fullChildAccessNode;
      PQNode currentChild;
      int fullCount = 1;
      PQNode nextChild;

      for ( int i=0; i<fullChildAccessNode.siblings.size(); i++ )
      {
        currentChild = fullChildAccessNode.siblings.PQNodeAt(i);
        previousChild = fullChildAccessNode;
        while ( fullCount < fullChildCount && currentChild != null &&
                ( currentChild.isFull() || currentChild.isDNode() ) )
        {
          if ( !currentChild.isDNode() )
          {
            fullCount++;
          }
          nextChild = currentChild.siblings.otherPQNode(previousChild);
          previousChild = currentChild;
          currentChild = nextChild;
        }
      }
      return ( fullCount == fullChildCount );
    }
  }

  // returns whether a qNode has all its full children adjacent to the specified child.
  public boolean checkFullAreAdjacentTo(PQNode aNode) throws Exception
  {
    if ( fullChildCount == 0 )
    {
      return true;
    }
    else
    {
      return ( checkFullAreAdjacent() &&
               ( ( aNode.siblings.PQNodeAt(0) != null &&
                   aNode.siblings.PQNodeAt(0).isFullOrDirectedFull(aNode) ) ||
                 ( aNode.siblings.PQNodeAt(1) != null &&
                   aNode.siblings.PQNodeAt(1).isFullOrDirectedFull(aNode) ) ) );
    }
  }

  //returns whether qNode has all its full children adjacent to one end of the child list.
  public boolean checkFullAreEndMost() throws Exception
  {
    if ( fullChildCount == 0 )
    {
      return true;
    }
    else
    {
      if ( !checkFullAreAdjacent() )
      {
        return false;
      }
      for ( int i=0; i<endMostChildren.size(); i++ )
      {
        if ( isPseudoNode() )
        {
          PQNode endNode = (PQNode)endMostChildren.PQNodeAt(i);
          if ( endNode.siblings.PQNodeAt(0) != null &&
               endNode.siblings.PQNodeAt(0).parent != this )
          {
            endNode = endNode.siblings.PQNodeAt(0);
          }
          else if ( endNode.siblings.PQNodeAt(1) != null &&
                    endNode.siblings.PQNodeAt(1).parent != this )
          {
            endNode = endNode.siblings.PQNodeAt(1);
          }
          else
          {
            throw new Exception("*** ERROR could not find ends of pseudonode for checkPartialAreAtEnds!");
          }
          if ( ((PQNode)endMostChildren.PQNodeAt(i)).isFullOrDirectedFull(endNode) )
          {
            return true;
          }
        }
        else
        {
          if ( ((PQNode)endMostChildren.PQNodeAt(i)).isFullOrDirectedFull(null) )
          {
            return true;
          }
        }
      }
      return false;
    }
  }

  // returns whether or not all partial children of this qnode are endmost.
  public boolean checkPartialAreAtEnds() throws Exception
  {
    if ( partialChildCount == 0 )
    {
      return true;
    }
    else if ( partialChildCount == 1 )
    {
      for ( int i=0; i<endMostChildren.size(); i++ )
      {
        if ( isPseudoNode() )
        {
          PQNode endNode = (PQNode)endMostChildren.PQNodeAt(i);
          if ( endNode.siblings.PQNodeAt(0) != null &&
               endNode.siblings.PQNodeAt(0).parent != this )
          {
            endNode = endNode.siblings.PQNodeAt(0);
          }
          else if ( endNode.siblings.PQNodeAt(1) != null &&
                    endNode.siblings.PQNodeAt(1).parent != this )
          {
            endNode = endNode.siblings.PQNodeAt(1);
          }
          else
          {
            throw new Exception("*** ERROR could not find ends of pseudonode for checkPartialAreAtEnds!");
          }
          if ( ((PQNode)endMostChildren.PQNodeAt(i)).isPartialOrDirectedPartial(endNode) )
          {
            return true;
          }
        }
        else
        {
          if ( ((PQNode)endMostChildren.PQNodeAt(i)).isPartialOrDirectedPartial(null) )
          {
            return true;
          }
        }
      }
      return false;
    }
    else
    {
      for ( int i=0; i<endMostChildren.size(); i++ )
      {
        if ( isPseudoNode() )
        {
          PQNode endNode = (PQNode)endMostChildren.PQNodeAt(i);
          if ( endNode.siblings.PQNodeAt(0) != null &&
               endNode.siblings.PQNodeAt(0).parent != this )
          {
            endNode = endNode.siblings.PQNodeAt(0);
          }
          else if ( endNode.siblings.PQNodeAt(1) != null &&
                    endNode.siblings.PQNodeAt(1).parent != this )
          {
            endNode = endNode.siblings.PQNodeAt(1);
          }
          else
          {
            throw new Exception("*** ERROR could not find ends of pseudonode for checkPartialAreAtEnds!");
          }
          if ( !((PQNode)endMostChildren.PQNodeAt(i)).isPartialOrDirectedPartial(endNode) )
          {
            return false;
          }
        }
        else
        {
          if ( !((PQNode)endMostChildren.PQNodeAt(i)).isPartialOrDirectedPartial(null) )
          {
            return false;
          }
        }
      }
      return true;
    }
  }

  // returns whether or not both endmost children of this node are empty or partial (not full)
  public boolean checkEndMostAreEmptyOrPartial() throws Exception
  {
    for ( int i=0; i<endMostChildren.size(); i++ )
    {
      if ( ((PQNode)endMostChildren.PQNodeAt(i)).isFullOrDirectedFull(null) )
      {
        return false;
      }
    }
    return true;
  }

  // returns whether or not all the children of this node are full.
  public boolean childrenAreFull() throws Exception
  {
    if ( isQNode() )
    {
      int countFullEndMost = 0;
      for ( int i=0; i<endMostChildren.size(); i++ )
      {
        if ( ((PQNode)endMostChildren.PQNodeAt(i)).isFullOrDirectedFull(null) )
        {
          countFullEndMost++;
        }
      }
      return ( countFullEndMost == endMostChildren.size() && checkFullAreAdjacent() );
    }
    else
    {
      return ( fullChildCount == childCount );
    }
  }

  // returns whether or not this node has only one child.
  public boolean hasOnlyOneChild()
  {
    if ( isQNode() )
    {
      return ( endMostChildren.size() == 1 );
    }
    else
    {
      return ( childCount == 1 );
    }
  }

  public boolean hasOnlyTwoChildren()
  {
    if ( isQNode() )
    {
      if ( endMostChildren.size() == 2 )
      {
        return ((PQNode)endMostChildren.PQNodeAt(0)).siblings.PQNodeAt(0) ==
               endMostChildren.PQNodeAt(1);
      }
      else
      {
        return false;
      }
    }
    else
    {
      return childCount == 2;
    }
  }

  // the default behaviour of the clear method is to clear the parent after this node.
  public void clear() throws Exception
  {
    clear(true);
  }

  // clears all values in this node that were used during the reduction process.
  // a false recurse value prevents the parent of this node from being cleared...
  public void clear(boolean recurse) throws Exception
  {
    labelAsEmpty();
    queued = false;
    blocked = false;
    pertinentChildCount = 0;
    pertinentLeafCount = 0;

    if ( recurse )
    {
      if ( parent != null && (parent.label != LABEL_EMPTY ||
           parent.queued || parent.blocked || parent.pertinentChildCount != 0 ||
           parent.pertinentLeafCount != 0 || parent.fullChildCount != 0 || parent.partialChildCount != 0 ) )
      {
        parent.clear();
      }
    }
  }

  // returns an informative (debug) String representation of this node's values.
  public String infoString()
  {
    String outString = new String("[");
    if (isQNode())
    {
      outString+= "Q";
      if ( isPseudoNode() )
      {
        outString+= "P";
      }
    }
    else if (isPNode())
    {
      outString+= "P";
    }
    else if (isDNode())
    {
      outString+= "D";
    }

    if (parent != null)
    {
      outString = outString + " p: " + parent.hashCode();
    }
    else
    {
      outString = outString + " p: null";
    }

    if (isDeleted())
    {
      outString+=" DELETED]";
      return outString;
    }


    if (data != null)
    {
      outString = outString + " " + hashCode() + " " + data.toString();
    }
    else
    {
      outString = outString + " " + hashCode() + " null ";
    }
    if ( isFull() )
    {
      outString = outString + " f ";
    }
    else if ( isPartial() )
    {
      outString = outString + " p ";
    }
    else if ( isEmpty() )
    {
      outString = outString + " e ";
    }
    outString = outString + " fc: " + fullChildCount;
    outString = outString + " pc: " + partialChildCount;
    if (parent != null)
    {
      outString = outString + " p: " + parent.hashCode();
      if ( parent.isPNode() )
      {
        if ( left != null )
        {
          outString = outString + " l: " + left.hashCode();
        }
        else
        {
          outString = outString + " l: " + "null";
        }
        if ( right != null )
        {
          outString = outString + " r: " + right.hashCode();
        }
        else
        {
          outString = outString + " r: " + "null";
        }
      }
      else if ( parent.isQNode() )
      {
        if ( siblings == null )
        {
          outString = outString + " siblings are null!";
        }
        else
        {
          if ( siblings.PQNodeAt(0) != null )
          {
            outString = outString + " s1: " + siblings.PQNodeAt(0).hashCode();
          }
          else
          {
            outString = outString + " s1: " + "null";
          }
          if ( siblings.PQNodeAt(1) != null )
          {
            outString = outString + " s2: " + siblings.PQNodeAt(1).hashCode();
          }
          else
          {
            outString = outString + " s2: " + "null";
          }
        }
      }
      if ( fullLeft != null )
      {
        outString = outString + " fl: " + fullLeft.hashCode();
      }
      else
      {
        outString = outString + " fl: " + "null";
      }
      if ( fullRight != null )
      {
        outString = outString + " fr: " + fullRight.hashCode();
      }
      else
      {
        outString = outString + " fr: " + "null";
      }
      if ( partialLeft != null )
      {
        outString = outString + " pl: " + partialLeft.hashCode();
      }
      else
      {
        outString = outString + " pl: " + "null";
      }
      if ( partialRight != null )
      {
        outString = outString + " pr: " + partialRight.hashCode();
      }
      else
      {
        outString = outString + " pr: " + "null";
      }
    }
    if ( isQNode() )
    {
      outString = outString + " e:";
      for ( int i=0; i<endMostChildren.size(); i++ )
      {
        outString = outString + " " + ((PQNode)endMostChildren.PQNodeAt(i)).infoString();
      }
    }
    outString = outString + " perl: " + pertinentLeafCount;
    outString = outString + " perc: " + pertinentChildCount;
    if ( fullChildAccessNode == null )
    {
      outString = outString + " fcan: null";
    }
    else
    {
      outString = outString + " fcan: " + fullChildAccessNode;
    }
    outString+= " " + queued + " " + blocked;
    outString = outString + "]";
    return outString;
  }

  // returns a String representation of this node.
  public String toString()
  {
    String returnString = new String();
    if (isQNode())
    {
      returnString+="Q";
    }
    else if (isPNode())
    {
      returnString+="P";
    }
    if ( data != null )
    {
      return returnString + data.toString();
    }
    else
    {
      return returnString + "Interior Node";
    }
  }

  // print the informative String of this node, and recursively call this method
  // on all child nodes. (for debug purposes).
  public void printStructure() throws Exception
  {
    System.out.print(infoString());
    if ( isDeleted() )
    {
      System.out.println(" DELETED");
    }
    else
    {
      System.out.println();
      Vector children = getAllChildren();
      for (int i=0; i<children.size(); i++)
      {
        ((PQNode)children.elementAt(i)).printStructure();
      }
    }
  }

  // returns a count of the number of leaves stored underneath this node.
  // this method also calculates the greatest depth of this node from a leaf node.
  public int countSubLeaves(int parent_depth) throws Exception
  {
    subLeafCount = 0;
    depth = parent_depth+1;
    int tempDepth = depth;
    if ( hasChildren() )
    {
      PQNode childNode;
      Vector children = getAllChildren();
      for (int i=0; i<children.size(); i++)
      {
        childNode = (PQNode)children.elementAt(i);
        subLeafCount += childNode.countSubLeaves(tempDepth);
        if (childNode.depth > depth)
        {
          depth = childNode.depth;
        }
      }
      return subLeafCount;
    }
    else
    {
      return 1;
    }
  }

  // The first time we call count subDeletedNodes, no deleted nodes have been
  // encountered, so initialize the Vector of all deleted nodes to 0 size.
  public int countSubDeletedNodes() throws Exception
  {
    return countSubDeletedNodes(new Vector());
  }

  // returns the number of deleted nodes that are pointed to by children in the
  // subtree rooted at this node (for debug/mem consumption analysis purposes only)
  public int countSubDeletedNodes(Vector deletedNodes) throws Exception
  {
    int subDeletedNodeCount = 0;
    if ( hasChildren() )
    {
      PQNode childNode;
      Vector children = getAllChildren();
      for (int i=0; i<children.size(); i++)
      {
        childNode = (PQNode)children.elementAt(i);
        if ( childNode.parent.isDeleted() )
        {
          if ( !deletedNodes.contains(childNode.parent) )
          {
            deletedNodes.addElement(childNode.parent);
            subDeletedNodeCount++;
          }
        }
        subDeletedNodeCount += childNode.countSubDeletedNodes(deletedNodes);
      }
    }
    return subDeletedNodeCount;
  }

  // returns the number of nodes that in the subtree rooted at this node
  // (for debug purposes only)
  public int countSubNodes() throws Exception
  {
    int subNodeCount = 1;
    if ( hasChildren() )
    {
      Vector children = getAllChildren();
      for (int i=0; i<children.size(); i++)
      {
        subNodeCount += ((PQNode)children.elementAt(i)).countSubNodes();
      }
    }
    return subNodeCount;
  }
}
