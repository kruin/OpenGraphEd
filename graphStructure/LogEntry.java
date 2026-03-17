package graphStructure;

import java.util.Vector;
import java.util.Enumeration;
import java.text.DecimalFormat;
import javax.swing.tree.TreeNode;

public class LogEntry implements TreeNode
{
  private static DecimalFormat decimalFormat = new DecimalFormat("000");
  private static String PREFIX = "  ";
  private String operationName;
  private int numNodes;
  private int numEdges;
  private long timeTaken;
  private Vector subEntries;
  private LogEntry parent;
  private String data;
  
  public LogEntry(String opName, Graph g, long startTime)
  {
    operationName = opName;
    numNodes = g.getNumNodes();
    numEdges = g.getNumEdges();
    timeTaken = startTime;
    subEntries = new Vector();
    parent = null;
    data = null;
  }
  
  public LogEntry()
  {
    operationName = "None";
    numNodes = -1;
    numEdges = -1;
    timeTaken = 0;
    subEntries = new Vector();
    parent = null;
    data = null;
  }
  
  public LogEntry getParentEntry()
  {
    return parent;
  }
  
  public void updateTimeTaken(long endTime)
  {
    timeTaken = endTime - timeTaken;
  }
  
  public void addSubEntry(LogEntry logEntry)
  {
    logEntry.parent = this;
    subEntries.addElement(logEntry);
  }
  
  public void setSubEntries(Vector subEntries)
  {
    this.subEntries = subEntries;
  }
  
  public void setData(String newData)
  {
    data = newData;
  }
  
  public String toString()
  {
    String toReturn = operationName + " run on graph with " + numNodes +
                      " nodes and " + numEdges + " edges in " +
                      formatTime(timeTaken);
    if ( data != null )
    {
      toReturn+= " (" + data + ")";
    }
    return toReturn;
  }
  
  public String infoString()
  {
    return infoString("");
  }
  
  public String infoString(String prefix)
  {
    String toReturn = prefix + operationName + " run on graph with " + numNodes +
                      " nodes and " + numEdges + " edges in " +
                      formatTime(timeTaken);
    if ( data == null )
    {
      toReturn+="\n";
    }
    else
    {
      toReturn+= " (" + data + ")\n";
    }
    for ( int i=0; i < subEntries.size(); i++ )
    {
      toReturn+= ((LogEntry)subEntries.elementAt(i)).infoString(prefix + PREFIX);
    }
    return toReturn;
  }
  
  private static String formatTime(long time)
  {
    int millis = (int)(time % 1000);
    time = (time - millis) / 1000;
    
    return time + "." + decimalFormat.format(millis) + " s";
    
    /*int secs = (int)(time % 60);
    time = (time - secs) / 60;
    int mins = (int)(time % 60);
    time = (time - mins) / 60;
    int hours = (int)time;
    if ( hours > 0 )
    {
      return hours + " h, " + mins + " m, " + secs + "." + decimalFormat.format(millis) + " s";
    }
    if ( mins > 0 )
    {
      return mins + " m, " + secs + "." + decimalFormat.format(millis) + " s";
    }
    return secs + "." + decimalFormat.format(millis) + " s";*/
  }
  
  public Enumeration children()
  {
    return subEntries.elements();
  }
  
  public boolean getAllowsChildren()
  {
    return true;
  }
  
  public TreeNode getChildAt(int index)
  {
    return (TreeNode)subEntries.elementAt(index);
  }
  
  public int getChildCount()
  {
    return subEntries.size();
  }
  
  public int getIndex(TreeNode node)
  {
    return subEntries.indexOf(node);
  }
  
  public TreeNode getParent()
  {
    return parent;
  }

  public boolean isLeaf()
  {
    return subEntries.size() == 0;
  }

}