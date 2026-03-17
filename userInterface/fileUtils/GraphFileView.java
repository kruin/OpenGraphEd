package userInterface.fileUtils;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * This class defines the FileView object for displaying a graph
 * file selector in an elegant manner.
 * 
 * @author Jon Harris (Adapted from Sun's Java Tutorial)
 */
public class GraphFileView extends FileView
{
  public String getName(File f)
  {
    return null; // let the L&F FileView figure this out
  }
    
  public String getDescription(File f)
  {
    return null; // let the L&F FileView figure this out
  }
    
  public Boolean isTraversable(File f)
  {
    return null; // let the L&F FileView figure this out
  }
    
  /**
   * Returns a String representation of the type of the given File object.
   *
   * @param File f: The File to evaluate the type of.
   * @return String: A String description of the type of the File that was provided.
   */
  public String getTypeDescription(File f)
  {
    String extension = Utils.getExtension(f);
    String type = null;

    if (extension != null)
    {
      if (extension.equalsIgnoreCase(Utils.graph) )
      {
        type = "Graph File";
      }
    }
    return type;
  }
    
  public Icon getIcon(File f)
  {
    String extension = Utils.getExtension(f);
    Icon icon = null;

    if (extension != null)
    {
    }
    return icon;
  }
}
