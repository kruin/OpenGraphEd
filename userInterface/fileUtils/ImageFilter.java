package userInterface.fileUtils;

import java.io.File;
import javax.swing.filechooser.*;

/**
 * This class defines the FileFilter object for filtering the files
 * that are displayed when the user is saving an image or loading an
 * image from a file.
 *
 * @author Jon Harris (Adapted from Sun's Java Tutorial)
 */
public class ImageFilter extends FileFilter {

  /**
   * Determines whether or not the given file is of an acceptable
   * format for display (.Gif, .Jpg, .Tif, .Bmp files will return true)
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
      if (extension.equalsIgnoreCase(Utils.gif) || extension.equalsIgnoreCase(Utils.jpeg) || extension.equalsIgnoreCase(Utils.jpg))
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
    return "Image Files (GIF & JPG)";
  }
}
