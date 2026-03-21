package dataStructure.pqTree;

import java.util.Vector;
import java.util.Iterator;
import dataStructure.Queue;

// This class represents the PQTree and the template operations that are performed
// on the nodes it contains during the reduction process.
public class PQTree
{
  public static final boolean SHOW_DEBUG_OUTPUT = false;
  private PQNode root;
  private Vector leaves = null; // the leaf PQNodes of the PQTree
  private Queue queue = null; // nodes currently being processed by reduction
  private Queue clearQueue = null; // nodes to clear after a reduction.
  private int drawWidth;
  private int drawHeight;
  private boolean hasChanged;
  private String templateMatchString;
  private String templateTimeString;
  private String reduceString;
  private boolean doneReduction;
  private boolean cleared;
  private Vector constraints = null;
  private boolean flaggedAsNull;
  private PQNode lastPertRoot = null;

  // Constructor for creating an empty(Null) PQTree.
  public PQTree() throws Exception
  {
    init(true);
  }

  // Constructor for creating the universal PQTree which has P-Node as the root
  // and leaf P-Nodes as children of the root which have as data the objects
  // provided in the data vector.
  public PQTree(Vector data) throws Exception
  {
    init(true);
    boolean save_memory = false;
    boolean run_fast = true;
    PQNode aNode = null;
    try
    {
      leaves = new Vector(data.size());
    }
    catch (OutOfMemoryError e)
    {
      System.out.println("Insufficient memory to store " + data.size() + " leaf nodes");
      throw e;
    }
    if ( save_memory )
    {
      if ( data.size() == 1 )
      {
        root.setData(data.firstElement());
        leaves.addElement(root);
      }
      else
      {
        Iterator iter = data.iterator();
        int count = 0;
        try
        {
          while (iter.hasNext())
          {
            aNode = new PQNode(iter.next());
            root.addChild(aNode);
            leaves.addElement(aNode);
            iter.remove();
            count++;
          }
        }
        catch (OutOfMemoryError e)
        {
          System.out.println("Insufficient memory while creating leaf node #" + count);
          throw e;
        }
      }
    }
    else if ( run_fast )
    {
      int i = 0;
      try
      {
        if ( data.size() == 1 )
        {
          root.setData(data.firstElement());
          leaves.addElement(root);
        }
        else
        {
          for ( i=0; i<data.size(); i++ )
          {
            aNode = new PQNode(data.elementAt(i));
            root.addChild(aNode);
            leaves.addElement(aNode);
          }
        }
      }
      catch (OutOfMemoryError e)
      {
        System.out.println("Insufficient memory while creating leaf node #" + i);
        throw e;
      }
    }
  }

  // Constructor for creating the universal PQTree which has P-Node as the root
  // and leaf P-Nodes as children of the root which have as data the values
  // between 0 and universeSize -1
  public PQTree(int universeSize) throws Exception
  {
    init(true);
    PQNode aNode = null;
    try
    {
      leaves = new Vector(universeSize);
    }
    catch (OutOfMemoryError e)
    {
      System.out.println("Insufficient memory to store " + universeSize + " leaf nodes");
      throw e;
    }
    int i=0;
    try
    {
      if ( universeSize == 1 )
      {
        root.setData(Integer.valueOf(0));
        leaves.addElement(root);
      }
      else
      {
        for ( i=0; i<universeSize; i++ )
        {
          aNode = new PQNode(Integer.valueOf(i));
          root.addChild(aNode);
          leaves.addElement(aNode);
        }
      }
    }
    catch (OutOfMemoryError e)
    {
      System.out.println("Insufficient memory while creating leaf node #" + i);
      throw e;
    }
  }

  private void init(boolean initLeaves) throws Exception
  {
    hasChanged = true;
    templateMatchString = new String();
    templateTimeString = new String();
    reduceString = new String();
    doneReduction = true;
    flaggedAsNull = false;
    if (constraints != null)
    {
      clear();
      constraints = null;
    }
    cleared = true;
    queue = null;
    clearQueue = new Queue();
    root = new PQNode();
    if (initLeaves)
    {
      leaves = new Vector();
    }
  }

  // Accessor and modifier methods...
  public PQNode getRoot() { return root; }

  public void setRoot(PQNode rootNode) { root = rootNode; }

  public Vector getLeaves() { return leaves; }

  public boolean isNullTree() { return !root.hasChildren(); }

  public int getWidth() { return drawWidth; }

  public int getHeight() { return drawHeight; }

  public String getTemplateMatchString() { return templateMatchString; }

  private void setTemplateTimeString(String newString) { templateTimeString = newString; }

  public String getReduceString() { return reduceString; }

  public void setConstraints(Vector c) { constraints = c; }

  public Vector getConstraints() { return constraints; }

  public boolean isDoneReduction() { return doneReduction && cleared; }

  public boolean isReduced() { return doneReduction; }

  // Returns a leaf node with the specified index relative the the order in which
  // they were originally added when the PQTree was built.
  public PQNode getLeafAt(int index)
  {
    return (PQNode)leaves.elementAt(index);
  }

  // Resets the tree back to the universal tree with the leaves as children of the root.
  public void resetTree() throws Exception
  {
    init(false);
    for ( int i=0; i<leaves.size(); i++ )
    {
      PQNode aNode = (PQNode)leaves.elementAt(i);
      aNode.clear(false);
      root.addChild(aNode);
    }
    System.gc(); // run the garbage collector...
  }

  // Performs a reduction on the PQTree based on the data provided in the data vector
  public void reductionByValue(Vector data) throws Exception
  {
    Vector s = new Vector();
    for ( int i=0; i<leaves.size(); i++ )
    {
      if ( data.contains( ((PQNode)leaves.elementAt(i)).getData() ) )
      {
        s.addElement(leaves.elementAt(i));
      }
    }
    reduction(s, 0);
  }

  public void reduction(Vector s, int numSteps) throws Exception
  {
    constraints = s;
    reduction(numSteps);
  }

  public PQNode reduction(Vector s) throws Exception
  {
    constraints = s;
    return reduction(0);
  }

  // Performs a reduction on the PQTree using the PQNodes provided in the s vector.
  // returns the root of the pertinent subtree...
  public PQNode reduction(int numSteps) throws Exception
  {
    PQNode pertRoot = null;
    if ( !cleared )
    {
      clear();
    }
    long timeTaken = System.currentTimeMillis();
    boolean previouslyDoneReduction = doneReduction;
    if (queue == null)
    {
      queue = new Queue();
      Iterator iterator = constraints.iterator();
      Object obj;
      reduceString = "Reduced: {";
      while (iterator.hasNext())
      {
        obj = iterator.next();
        queue.enqueue(obj);
        reduceString = reduceString + obj.toString();
        if ( iterator.hasNext() )
        {
          reduceString = reduceString + ",";
        }
        else
        {
          reduceString = reduceString + "}\n";
        }
      }
      bubble(queue);
      if ( SHOW_DEBUG_OUTPUT ) { printTree(); }
      queue = null;
    }

    if ( !flaggedAsNull )
    {
      if ( doneReduction && cleared )
      {
        queue = new Queue();
        Iterator iterator = constraints.iterator();
        while (iterator.hasNext())
        {
          queue.enqueue(iterator.next());
        }
        pertRoot = reduce(queue, constraints.size(), numSteps);
        if (queue == null || queue.size() == 0)
        {
          doneReduction = true;
          queue = null;
        }
        cleared = false;
        if ( SHOW_DEBUG_OUTPUT ) { printTree(); }
      }
      else if ( doneReduction && !cleared )
      {
        templateMatchString = "";
      }
      else if ( !doneReduction )
      {
        pertRoot = reduce(queue, constraints.size(), numSteps);
        if (queue == null || queue.size() == 0)
        {
          doneReduction = true;
          queue = null;
        }
        if ( SHOW_DEBUG_OUTPUT ) { printTree(); }
      }
    }

    if ( previouslyDoneReduction && doneReduction && !cleared || numSteps == 0 || flaggedAsNull)
    {
      if ( flaggedAsNull )
      {
        String rString = "Could Not Reduce: " + reduceString.substring(reduceString.indexOf('{'), reduceString.length());
        String tString = new String(templateMatchString);
        init(false);
        reduceString = rString;
        templateMatchString = tString;
      }
      if ( SHOW_DEBUG_OUTPUT ) { System.out.println("$$$ Cleaning up $$$"); }
      //clear();
    }

    hasChanged = true;
    if ( SHOW_DEBUG_OUTPUT ) { printTree(); }
    setTemplateTimeString("Time taken in milliseconds: " + (System.currentTimeMillis() - timeTaken) + "\n");
    return pertRoot;
  }

  // Preliminary pass of the reduction process which ensures that each pertinent PQNode
  // (that will be processed during reduction) has a valid parent pointer and ha a count of the
  // number of pertinent children below them.
  // If children of the root node have invalid parent pointers, a pseudoNode may be assigned
  // as their parent, if this is not possible, the Null PQTree is returned.
  public void bubble(Queue queue) throws Exception
  {
    if ( SHOW_DEBUG_OUTPUT ) { System.out.println("$$$ Bubble start $$$"); }

    Vector blockedNodeVector = new Vector();
    Vector blockedSiblings;
    Vector unblockedSiblings;
    int blockCount = 0;
    int blockedNodes = 0;
    int offTheTop = 0;
    PQNode currentNode;
    while ( queue.size() + blockCount + offTheTop > 1 )
    {
      if ( queue.size() == 0 )
      {
        break;
      }
      currentNode = (PQNode)queue.dequeue();
      if ( !currentNode.isQueued() )
      {
        currentNode.setPertinentLeafCount(1);
      }
      if ( SHOW_DEBUG_OUTPUT ) { System.out.println("Checking Bubble for: " + currentNode.infoString()); }
      currentNode.setBlocked();
      blockedSiblings = currentNode.getBlockedSiblings();
      unblockedSiblings = currentNode.getUnblockedSiblings();
      if (unblockedSiblings.size() > 0)
      {
        currentNode.setParent(((PQNode)unblockedSiblings.elementAt(0)).getParent());
        currentNode.setUnblocked();
      }
      else if ( currentNode.getSiblings() == null || currentNode.getSiblings().size() < 2 )
      {
        currentNode.setUnblocked();
      }
      if ( !currentNode.isBlocked() )
      {
        PQNode parentNode = currentNode.getParent();
        Vector list = new Vector(0);
        if ( blockedSiblings.size() > 0 )
        {
          list = currentNode.getMaximalConsecutiveBlockedSiblings();
          Iterator iterator = list.iterator();
          while ( iterator.hasNext() )
          {
            PQNode blockedSibling = (PQNode)iterator.next();
            blockedSibling.setUnblocked();
            blockedSibling.setParent(parentNode);
            parentNode.setPertinentChildCount(parentNode.getPertinentChildCount()+1);
          }
        }
        if ( parentNode == null )
        {
          offTheTop = 1;
        }
        else
        {
          parentNode.setPertinentChildCount(parentNode.getPertinentChildCount()+1);
          if ( !parentNode.isBlocked() && !parentNode.isQueued() )
          {
            queue.enqueue(parentNode);
            parentNode.setQueued();

            if ( SHOW_DEBUG_OUTPUT ) { System.out.println("  Adding this to the end of queue: " + parentNode.infoString()); }
          }
        }
        blockCount-= blockedSiblings.size();
        blockedNodes-= list.size();
      }
      else
      {
        blockedNodeVector.addElement(currentNode);
        blockCount-= (blockedSiblings.size()-1);
        blockedNodes++;
      }
    }
    if ( blockCount == 1 && blockedNodes > 1 )
    {
      PQNode pseudoNode = new PQNode();
      pseudoNode.convertToQNode();
      pseudoNode.setPertinentChildCount(blockedNodes);
      pseudoNode.pseudoNode();
      PQNode aBlockedNode = null;

      PQNode startNode = null;
      PQNode lastNode = null;
      PQNode prevNode = null;
      PQNode tempNode;
      for ( int i=0; i<blockedNodeVector.size(); i++ )
      {
        aBlockedNode = (PQNode)blockedNodeVector.elementAt(i);
        aBlockedNode.setParent(pseudoNode);
        if ( !aBlockedNode.getNonDirectedSibling(aBlockedNode.siblings.PQNodeAt(0)).isBlocked() )
        {
          if ( startNode == null )
          {
            startNode = aBlockedNode;
            prevNode = aBlockedNode.siblings.PQNodeAt(1);
          }
          else
          {
            lastNode = aBlockedNode;
          }
        }
        else if ( !aBlockedNode.getNonDirectedSibling(aBlockedNode.siblings.PQNodeAt(1)).isBlocked() )
        {
          if ( startNode == null )
          {
            startNode = aBlockedNode;
            prevNode = aBlockedNode.siblings.PQNodeAt(0);
          }
          else
          {
            lastNode = aBlockedNode;
          }
        }
      }

      if ( startNode != null && prevNode != null && lastNode != null )
      {
        // do a traversal to get all blocked nodes, and dNodes in between in order.
        Vector pseudoVector = new Vector();

        aBlockedNode = startNode;
        aBlockedNode.setParent(pseudoNode);
        pseudoVector.addElement(aBlockedNode);
        while ( aBlockedNode != lastNode )
        {
          tempNode = aBlockedNode;
          aBlockedNode = aBlockedNode.siblings.otherPQNode(prevNode);
          prevNode = tempNode;
          aBlockedNode.setParent(pseudoNode);
          pseudoVector.addElement(aBlockedNode);
        }

        for ( int p=0; p<pseudoVector.size(); p++ )
        {
          pseudoNode.addChild((PQNode)pseudoVector.elementAt(p), false);
        }
        pseudoNode.getEndMostChildren().addPQNode((PQNode)pseudoVector.firstElement());
        pseudoNode.getEndMostChildren().addPQNode((PQNode)pseudoVector.lastElement());
      }
      else
      {
        throw new Exception("*** ERROR no starting blocked node could be found to add to a new pseudonode!");
      }

    }
    else if ( blockCount > 1 )
    {
      flaggedAsNull = true;
      templateMatchString = "Templates Matched: {NONE}\n";
      reduceString = "Could Not educed: {";
    }
    hasChanged = true;
    if ( SHOW_DEBUG_OUTPUT ) { System.out.println("$$$ Bubble end $$$"); }
  }

  // Main pass of the reduction process where PQ Nodes are matched with templates and an
  // appropriate replacement is made. If no template is matched by a PQNode, the PQTree
  // becomes the Null PQTree.
  // the root of the pertinent subtree is returned.
  public PQNode reduce(Queue queue, int pertinentCount, int numSteps) throws Exception
  {
    if ( SHOW_DEBUG_OUTPUT ) { System.out.println("$$$ Reduce start $$$"); }
    PQNode currentNode;
    int count = 0;
    if ( doneReduction )
    {
      templateMatchString = new String("Templates Matched: {");
      doneReduction = false;
    }
    else
    {
      templateMatchString = "";
    }
    while ( queue.size() > 0)
    {
      if (numSteps != 0 )
      {
        if (numSteps == count++ )
        {
          break;
        }
      }
      currentNode = (PQNode)queue.dequeue();
      clearQueue.enqueue(currentNode);

      if ( SHOW_DEBUG_OUTPUT ) { System.out.println("  Checking Template matches for: " + currentNode.infoString()); }
      if ( currentNode.getPertinentLeafCount() < pertinentCount && pertinentCount != 1 )
      {
        // node is not the root of the pertinent subtree
        PQNode parentNode = currentNode.getParent();
        parentNode.setPertinentLeafCount(parentNode.getPertinentLeafCount()+currentNode.getPertinentLeafCount());
        parentNode.setPertinentChildCount(parentNode.getPertinentChildCount()-1);
        if ( parentNode.getPertinentChildCount() == 0 )
        {
          queue.enqueue(parentNode);
          if ( SHOW_DEBUG_OUTPUT ) { System.out.println("  Adding this to the end of queue: " + parentNode.infoString()); }
        }
        if ( !templateL1(currentNode) )
        {
          if ( !templateP1(currentNode) )
          {
            if ( !templateP3(currentNode) )
            {
              if ( !templateP5(currentNode) )
              {
                if ( !templateQ1(currentNode) )
                {
                  if ( !templateQ2(currentNode) )
                  {
                    if ( SHOW_DEBUG_OUTPUT ) { System.out.println("    *** NO MATCH FOUND1 ***"); }
                    String tempString = templateMatchString;
                    flaggedAsNull = true;
                    templateMatchString = tempString + "NONE";
                    break;
                  }
                  else
                  {
                    templateMatchString = templateMatchString + "Q2, ";
                  }
                }
                else
                {
                  templateMatchString = templateMatchString + "Q1, ";
                }
              }
              else
              {
                templateMatchString = templateMatchString + "P5, ";
              }
            }
            else
            {
              templateMatchString = templateMatchString + "P3, ";
            }
          }
          else
          {
            templateMatchString = templateMatchString + "P1, ";
          }
        }
        else
        {
          templateMatchString = templateMatchString + "L1, ";
        }
      }
      else // node is the root of pertinent subtree
      {
        lastPertRoot = currentNode;
        if ( !templateL1(currentNode) )
        {
          if ( !templateP1(currentNode) )
          {
            if ( !templateP2(currentNode) )
            {
              if ( !templateP4(currentNode) )
              {
                if ( !templateP6(currentNode) )
                {
                  if ( !templateQ1(currentNode) )
                  {
                    if ( !templateQ2(currentNode) )
                    {
                      if ( !templateQ3(currentNode) )
                      {
                        if ( SHOW_DEBUG_OUTPUT ) { System.out.println("    *** NO MATCH FOUND2 ***"); }
                        String tempString = templateMatchString;
                        flaggedAsNull = true;
                        templateMatchString = tempString + "NONE";
                        break;
                      }
                      else
                      {
                        templateMatchString = templateMatchString + "Q3";
                      }
                    }
                    else
                    {
                      templateMatchString = templateMatchString + "Q2";
                    }
                  }
                  else
                  {
                    templateMatchString = templateMatchString + "Q1";
                  }
                }
                else
                {
                  templateMatchString = templateMatchString + "P6";
                }
              }
              else
              {
                templateMatchString = templateMatchString + "P4";
              }
            }
            else
            {
              templateMatchString = templateMatchString + "P2";
            }
          }
          else
          {
            templateMatchString = templateMatchString + "P1";
          }
        }
        else
        {
          templateMatchString = templateMatchString + "L1";
        }
      }
      hasChanged = true;
      if ( SHOW_DEBUG_OUTPUT ) { printTree(); }
    }
    if ( SHOW_DEBUG_OUTPUT ) { System.out.println("$$$ Reduce end $$$"); }
    if ( queue.size() == 0 || flaggedAsNull )
    {
      templateMatchString = templateMatchString + "}\n";
    }
    return lastPertRoot;
  }

  public void clear() throws Exception
  {
    cleared = true;
    if ( clearQueue != null )
    {
      PQNode currentNode;
      while ( clearQueue.size() > 0)
      {
        currentNode = (PQNode)clearQueue.dequeue();
        currentNode.clear();
      }
      if (constraints != null)
      {
        for ( int j=0; j<constraints.size(); j++ )
        {
          ((PQNode)constraints.elementAt(j)).clear();
        }
      }
    }
  }

  public void clear( Queue queueToClear ) throws Exception
  {
    cleared = true;
    if ( queueToClear != null )
    {
      PQNode currentNode;
      while ( queueToClear.size() > 0)
      {
        currentNode = (PQNode)clearQueue.dequeue();
        currentNode.clear(false);
      }
    }
  }

  // full leaf nodes need not be replaced.
  public boolean templateL1(PQNode currentNode) throws Exception
  {
    if ( SHOW_DEBUG_OUTPUT ) { System.out.println("Trying Template L1"); }
    if ( !currentNode.hasChildren() )
    {
      currentNode.labelAsFull();
      if ( SHOW_DEBUG_OUTPUT ) { System.out.println("    Matched Template L1"); }
      return true;
    }
    return false;
  }

  // A p-node with all full children needs only to be made full.
  public boolean templateP1(PQNode currentNode) throws Exception
  {
    if ( SHOW_DEBUG_OUTPUT ) { System.out.println("Trying Template P1"); }
    if ( currentNode.isPNode() )
    {
      if ( currentNode.getNumChildren() == currentNode.getNumFullChildren() )
      {
        currentNode.labelAsFull();
        if ( SHOW_DEBUG_OUTPUT ) { System.out.println("    Matched Template P1"); }
        return true;
      }
    }
    return false;
  }

  // a p-node that is the root of the pertinent subtree with no partial children
  // will have its full children reparented to a new full child that is added to
  // this node.
  public boolean templateP2(PQNode currentNode) throws Exception
  {
    if ( SHOW_DEBUG_OUTPUT ) { System.out.println("Trying Template P2"); }
    if ( currentNode.isPNode() && currentNode.getNumPartialChildren() == 0 &&
         currentNode.getNumFullChildren() > 0 )
    {
      if ( currentNode.getNumFullChildren() > 1  && currentNode.getNumEmptyChildren() > 0 )
      {
        PQNode newNode = new PQNode();
        newNode.labelAsFull();
        currentNode.moveFullChildrenTo(newNode);
        currentNode.addChild(newNode);
        lastPertRoot = newNode; // update the pert root reference.
      }
      else
      {
        lastPertRoot = currentNode.getOnlyFullChild(); // update the pert root reference.
      }
      if ( SHOW_DEBUG_OUTPUT ) { System.out.println("    Matched Template P2"); }
      return true;
    }
    return false;
  }

  // A p-node that is not the root of the pertinent subtree with no partial children
  // will have its full children reparented to a new full child. The P-node is then
  // relabelled as empty and both it and the new full child are added to a new partial
  // q-node which replaces the original P-Node.
  public boolean templateP3(PQNode currentNode) throws Exception
  {
    if ( SHOW_DEBUG_OUTPUT ) { System.out.println("Trying Template P3"); }
    if ( currentNode.isPNode() && currentNode.getNumPartialChildren() == 0 &&
         currentNode.getNumFullChildren() > 0 )
    {
      PQNode newNode;
      newNode = new PQNode();
      newNode.convertToQNode();
      newNode.labelAsPartial();

      if ( currentNode.getNumFullChildren() > 1 )
      {
        PQNode groupNode = new PQNode();
        groupNode.labelAsFull();
        currentNode.moveFullChildrenTo(groupNode);
        newNode.addChild(groupNode);
      }
      else
      {
        newNode.addChild(currentNode.removeOnlyFullChild());
      }
      currentNode.getParent().replaceChild(currentNode, newNode);
      if ( currentNode.getNumEmptyChildren() > 1 )
      {
        currentNode.clear(false);
        newNode.addChild(currentNode);
      }
      else
      {
        newNode.addChild(currentNode.removeOnlyEmptyChild());
      }
      if ( SHOW_DEBUG_OUTPUT ) { System.out.println("    Matched Template P3"); }
      return true;
    }
    return false;
  }

  // A p-node which is the root of the pertinent subtree that has exactly one partial
  // child will have its full children reparented to a new full child, which is in turn
  // added as a child of the partial child.
  public boolean templateP4(PQNode currentNode) throws Exception
  {
    if ( SHOW_DEBUG_OUTPUT ) { System.out.println("Trying Template P4"); }
    if ( currentNode.isPNode() && currentNode.getNumPartialChildren() == 1 )
    {
      PQNode partialChild = currentNode.getPartialChild(0);

      if ( currentNode.getNumFullChildren() > 0 )
      {
        PQNode newNode;
        if ( currentNode.getNumFullChildren() > 1 )
        {
          newNode = new PQNode();
          newNode.labelAsFull();
          currentNode.moveFullChildrenTo(newNode);
        }
        else
        {
          newNode = currentNode.removeOnlyFullChild();
        }
        partialChild.addChild(newNode);
      }

      if ( currentNode.hasOnlyOneChild() )
      {
        if ( currentNode.getParent() != null )
        {
          if ( currentNode.getParent().isDeleted() )
          {
            currentNode.becomeChild(partialChild);
          }
          else
          {
            currentNode.getParent().replaceChild(currentNode, partialChild);
            currentNode.delete();
            lastPertRoot = partialChild; // update pert root reference.
          }
        }
        else
        {
          partialChild.becomeRoot();
          setRoot(partialChild);
          lastPertRoot = partialChild; // update pert root reference.
        }
      }
      else
      {
        lastPertRoot = partialChild; // update pert root reference.
      }
      if ( SHOW_DEBUG_OUTPUT ) { System.out.println("    Matched Template P4"); }
      return true;
    }
    return false;
  }

  // A p-node which is not the root of the pertinent subtree that has exactly one partial
  // child will have its full children reparented to a new full child. The P-node is then
  // relabelled as empty and both it and the new full child are added to the partial
  // q-node which replaces the original P-Node.
  public boolean templateP5(PQNode currentNode) throws Exception
  {
    if ( SHOW_DEBUG_OUTPUT ) { System.out.println("Trying Template P5"); }
    if ( currentNode.isPNode() && currentNode.getNumPartialChildren() == 1 )
    {
      PQNode partialChild = currentNode.getPartialChild(0);
      currentNode.removeChild(partialChild);

      if ( currentNode.getNumFullChildren() > 0 )
      {
        PQNode newNode;
        if ( currentNode.getNumFullChildren() > 1 )
        {
          newNode = new PQNode();
          newNode.labelAsFull();
          currentNode.moveFullChildrenTo(newNode);
        }
        else
        {
          newNode = currentNode.removeOnlyFullChild();
        }
        partialChild.addChild(newNode);
      }

      if ( currentNode.getNumEmptyChildren() > 0 )
      {
        PQNode newNode;
        if ( currentNode.getNumEmptyChildren() == 1 )
        {
          newNode = currentNode.removeOnlyEmptyChild();
          currentNode.getParent().replaceChild(currentNode, partialChild);
          currentNode.delete();
        }
        else
        {
          currentNode.getParent().replaceChild(currentNode, partialChild);
          currentNode.clear(false);
          newNode = currentNode;
        }
        partialChild.addChild(newNode);
      }
      else
      {
        currentNode.getParent().replaceChild(currentNode, partialChild);
        currentNode.delete();
      }

      if ( SHOW_DEBUG_OUTPUT ) { System.out.println("    Matched Template P5"); }
      return true;
    }
    return false;
  }

  // A p-node which is the root of the pertinent subtree that has exactly two partial
  // children will reparent its full children to a new full p-node which is in turn added
  // to one of the partial children. The two partial children are then merged.
  public boolean templateP6(PQNode currentNode) throws Exception
  {
    if ( SHOW_DEBUG_OUTPUT ) { System.out.println("Trying Template P6"); }
    if ( currentNode.isPNode() && currentNode.getNumPartialChildren() == 2 ) //fixme?
    {
      PQNode partialChild1 = currentNode.getPartialChild(0);
      PQNode partialChild2 = currentNode.getPartialChild(1);

      if ( !(partialChild1.checkFullAreEndMost() && partialChild2.checkFullAreEndMost()) )
      {
        return false;
      }

      partialChild1.setPertinentLeafCount(currentNode.getPertinentLeafCount());

      if ( currentNode.getNumFullChildren() > 0 )
      {
        PQNode newNode = null;
        if ( currentNode.getNumFullChildren() > 1 )
        {
          newNode = new PQNode();
          newNode.labelAsFull();
          currentNode.moveFullChildrenTo(newNode);
        }
        else
        {
          newNode = currentNode.removeOnlyFullChild();
        }
        partialChild1.addChild(newNode);
      }

      currentNode.mergePartialChildren(partialChild2, partialChild1 );

      partialChild2.delete();

      if ( currentNode.hasOnlyOneChild() )
      {
        if ( currentNode.getParent() != null )
        {
          if ( currentNode.getParent().isDeleted() )
          {
            currentNode.becomeChild(partialChild1);
          }
          else
          {
            currentNode.getParent().replaceChild(currentNode, partialChild1);
            currentNode.delete();
            lastPertRoot = partialChild1; // update pert root reference.
          }
        }
        else
        {
          partialChild1.becomeRoot();
          setRoot(partialChild1);
          lastPertRoot = partialChild1; // update pert root reference.
        }
      }
      else
      {
        lastPertRoot = partialChild1; // update pert root reference.
      }
      if ( SHOW_DEBUG_OUTPUT ) { System.out.println("    Matched Template P6"); }
      return true;
    }
    return false;
  }

  // A q-node with all full children needs only to be made full.
  public boolean templateQ1(PQNode currentNode) throws Exception
  {
    if ( SHOW_DEBUG_OUTPUT ) { System.out.println("Trying Template Q1"); }
    if ( currentNode.isQNode() && !currentNode.isPseudoNode() )
    {
      if ( currentNode.childrenAreFull() )
      {
        currentNode.labelAsFull();
        if ( SHOW_DEBUG_OUTPUT ) { System.out.println("    Matched Template Q1"); }
        return true;
      }
    }
    return false;
  }

  // A q-node with zero or one partial child will absorb the children of the partial child
  // as children and be labelled as partial.
  public boolean templateQ2(PQNode currentNode) throws Exception
  {
    if ( SHOW_DEBUG_OUTPUT ) { System.out.println("Trying Template Q2"); }
    if ( currentNode.isQNode() && currentNode.getNumPartialChildren() <= 1 && !currentNode.isPseudoNode() )
    {
      if ( !currentNode.checkFullAreAdjacent() )
      {
        return false;
      }
      if ( currentNode.checkEndMostAreEmptyOrPartial() && currentNode.getNumFullChildren() != 0 )
      {
        return false;
      }
      currentNode.labelAsPartial();
      if ( currentNode.getNumPartialChildren() == 1 )
      {
        PQNodePair endMostChildren = currentNode.getEndMostChildren();
        PQNode partialChild = currentNode.getPartialChild(0);
        if ( !currentNode.checkFullAreAdjacentTo(partialChild) )
        {
          return false;
        }
        if ( currentNode.getNumFullChildren() == 0 && !endMostChildren.contains(partialChild) )
        {
          return false;
        }
        if ( !partialChild.checkFullAreEndMost() )
        {
          return false;
        }
        currentNode.absorbPartialChild(partialChild);
      }
      if ( SHOW_DEBUG_OUTPUT ) { System.out.println("    Matched Template Q2"); }
      return true;
    }
    return false;
  }

  // A q-node which is the root of the pertinent subtree with 0, 1 or 2 partial children
  // that has its full children in the centre of its child list will absorb and children
  // of the partial children as its own children and then be labelled as partial
  public boolean templateQ3(PQNode currentNode) throws Exception
  {
    if ( SHOW_DEBUG_OUTPUT ) { System.out.println("Trying Template Q3"); }
    if ( currentNode.isQNode() && currentNode.getNumPartialChildren() <= 2 )
    {
      if ( !currentNode.isPseudoNode() && !currentNode.checkEndMostAreEmptyOrPartial() )
      {
        //System.out.println("funny case happened!");
        //return false;
      }
      currentNode.labelAsPartial();

      if ( currentNode.getNumPartialChildren() == 1 )
      {
        PQNode partialChild = currentNode.getPartialChild(0);
        if ( !currentNode.checkFullAreAdjacentTo(partialChild) )
        {
          return false;
        }
        if ( !partialChild.checkFullAreEndMost() )
        {
          return false;
        }
        if ( currentNode.isPseudoNode() && !currentNode.checkPartialAreAtEnds() )
        {
          return false;
        }
        currentNode.absorbPartialChild(partialChild);
      }
      else if ( currentNode.getNumPartialChildren() == 2 )
      {
        PQNode partialChild1 = currentNode.getPartialChild(0);
        PQNode partialChild2 = currentNode.getPartialChild(1);

        if ( currentNode.getNumFullChildren() == 0 &&
             !partialChild1.isSiblingOf(partialChild2) )
        {
          return false;
        }
        if ( !partialChild1.checkFullAreEndMost() || !partialChild2.checkFullAreEndMost())
        {
          return false;
        }
        if ( !( currentNode.checkFullAreAdjacentTo(partialChild1) &&
                currentNode.checkFullAreAdjacentTo(partialChild2) ) )
        {
          return false;
        }
        if ( currentNode.isPseudoNode() && !currentNode.checkPartialAreAtEnds() )
        {
          return false;
        }
        currentNode.absorbPartialChild(partialChild1);
        currentNode.absorbPartialChild(partialChild2);
      }
      if ( currentNode.isPseudoNode() )
      {
        currentNode.delete();
        // pertRoot will be a deleted node, but fullChild access will be left active.
      }

      if ( SHOW_DEBUG_OUTPUT ) { System.out.println("    Matched Template Q3"); }
      return true;
    }
    return false;
  }

  // Prints the frontier of the PQTree to the console.
  public void printFrontier()
  {
    Vector leaves = getLeaves();
    for ( int i=0; i<leaves.size(); i++ )
    {
      System.out.println(leaves.elementAt(i));
    }
  }

  // Prints a preorder traversal of the PQTree to the console.
  public void printTree() throws Exception
  {
    System.out.println("$$$ PRINT TREE START $$$");
    root.printStructure();
    System.out.println("$$$ PRINT TREE END $$$");
  }

  // Prepares the PQTree to be drawn by the GUI by calculating the width and height
  // needed to draw the tree, and calculated the width and height required to draw
  // each node of the tree.
  public void prepareToDrawTree() throws Exception
  {
    if (hasChanged)
    {
      int depth = 0;
      int width = root.countSubLeaves(depth);
      drawWidth = width*(PQNode.DRAW_SIZE+PQNode.DRAW_BOUNDARY_SIZE)+50;
      drawHeight = root.getDepth()*(PQNode.DRAW_SIZE+PQNode.DRAW_CONNECTOR_SIZE)+50;
    }
  }

  public int getNumNodes() throws Exception
  {
    return root.countSubNodes();
  }

  public int getNumDeletedNodes() throws Exception
  {
    return root.countSubDeletedNodes();
  }
}