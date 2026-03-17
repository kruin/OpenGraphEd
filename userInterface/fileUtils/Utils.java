package userInterface.fileUtils;

import java.io.File;

/**
 * This class defines the Utils class that provides helper
 * methods and fields for dealing with file extensions for
 * the various file input and output dialogs.
 * 
 * @author Jon Harris (Adapted from Sun's Java Tutorial)
 */
public class Utils
{
  public final static String jpeg = "jpeg";
  public final static String jpg = "jpg";
  public final static String gif = "gif";
  public final static String tiff = "tiff";
  public final static String tif = "tif";
  public final static String bmp = "bmp";
  public final static String graph = "graph";

  /*
   * Gets the extension of the given File.
   *
   * @param File f: The File object to retrieve the extension of.
   * @return String: The extension of the given File.
   */
  public static String getExtension(File f)
  {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');

    if (i > 0 &&  i < s.length() - 1)
    {
      ext = s.substring(i+1).toLowerCase();
    }
    return ext;
  }
}
