package userInterface.fileUtils;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * This class defines the FileView object for displaying an image
 * file selector in an elegant manner.
 * 
 * @author Jon Harris (Adapted from Sun's Java Tutorial)
 */
public class ImageFileView extends FileView
{
  ImageIcon jpgIcon = new ImageIcon("ajpgIcon.gif");
  ImageIcon gifIcon = new ImageIcon("agifIcon.gif");
  ImageIcon tiffIcon = new ImageIcon("atiffIcon.gif");
    
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
      if (extension.equalsIgnoreCase(Utils.jpeg) ||
          extension.equalsIgnoreCase(Utils.jpg))
      {
        type = "JPEG Image";
      }
      else if (extension.equalsIgnoreCase(Utils.gif))
      {
        type = "GIF Image";
      }
      else if (extension.equalsIgnoreCase(Utils.tiff) ||
               extension.equalsIgnoreCase(Utils.tif))
      {
        type = "TIFF Image";
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
      if (extension.equalsIgnoreCase(Utils.jpeg) ||
          extension.equalsIgnoreCase(Utils.jpg))
      {
        icon = jpgIcon;
      }
      else if (extension.equalsIgnoreCase(Utils.gif))
      {
        icon = gifIcon;
      }
      else if (extension.equalsIgnoreCase(Utils.tiff) ||
               extension.equalsIgnoreCase(Utils.tif))
      {
        icon = tiffIcon;
      } 
    }
    return icon;
  }
}
