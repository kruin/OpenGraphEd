package userInterface.fileUtils;

import java.io.File;
import javax.swing.filechooser.*;

/**
 * This class defines the FileFilter object for filtering the files
 * that are displayed when the user is saving a graph or loading a
 * graph from a file.
 *
 * @author Jon Harris (Adapted from Sun's Java Tutorial)
 */
public class GraphFilter extends FileFilter {

  /**
   * Determines whether or not the given file is of an acceptable
   * format for display (.graph files will return true)
   *
   * @param File f: The File to verify.
   * @return boolean: Whether or not the given File is acceptable.
   */
  public boolean accept(File f)
  {
    if (f.isDirectory())
    {
      return true;
    }

    String extension = Utils.getExtension(f);
    if (extension != null)
    {
      if (extension.equalsIgnoreCase(Utils.graph) )
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Determines whether or not the given file is of an acceptable
   * format for saving the graph (.graph files will return true)
   *
   * @param File f: The File to verify.
   * @return boolean: Whether or not the given File is acceptable for saving.
   */
  public static boolean acceptForSave(File f)
  {
    String extension = Utils.getExtension(f);
    if (extension != null)
    {
      if (extension.equalsIgnoreCase(Utils.graph) )
      {
        return true;
      }
      else
      {
        return false;
      }
    }
    return false;
  }

  /**
   * Returns the description of this FileFilter
   *
   * @return String: A String description of this FileFilter.
   */
  public String getDescription()
  {
    return "GRAPH files";
  }
}
