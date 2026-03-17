package userInterface.fileUtils;

import javax.swing.*;
import java.beans.*;
import java.awt.*;
import java.io.File;

/**
 * This class defines the ImagePreview object which allows for a thumbnail
 * or preview of an image to be displayed in the file list whenever the
 * user clicks on a filename.
 * 
 * @author Jon Harris (Adapted from Sun's Java Tutorial)
 */
public class ImagePreview extends JComponent implements PropertyChangeListener
{
  ImageIcon thumbnail = null;
  File file = null;
                             
  /**
   * Constructor for class ImagePreview specifying the JFileChooser to show the
   * preview in.
   *
   * @param JFileChooser fc: The JFileChooser to display the preview in.
   */
  public ImagePreview(JFileChooser fc)
  {
    setPreferredSize(new Dimension(100, 50));
    fc.addPropertyChangeListener(this);
  }

  /**
   * Loads the current Image selected in the FileChooser and creates
   * a thumbnail or preview of this Image.
   */
  public void loadImage()
  {
    if (file == null)
    {
      return;
    }
 
    ImageIcon tmpIcon = new ImageIcon(file.getPath());
    if (tmpIcon.getIconWidth() > 90)
    {
      thumbnail = new ImageIcon(tmpIcon.getImage().getScaledInstance(90, -1,
                                Image.SCALE_DEFAULT));
    }
    else
    {
      thumbnail = tmpIcon;
    }
  }

  /**
   * This method is triggered when the user selects a different
   * File from the FileChooser. The preview of the selected Image
   * File (if any) is updated.
   *
   * @param PropertyChangeEvent e: The PropertyChangeEvent that triggered this method.
   */
  public void propertyChange(PropertyChangeEvent e)
  {
    String prop = e.getPropertyName();
    if (prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
    {
      file = (File) e.getNewValue();
      if (isShowing())
      {
        loadImage();
        repaint();
      }
    }
  }

  /**
   * Redraws the different components of the preview.
   *
   * @param Graphics g: The Graphics object to draw with.
   */
  public void paintComponent(Graphics g)
  {
    if (thumbnail == null)
    {
      loadImage();
    }
    if (thumbnail != null)
    {
      int x = getWidth()/2 - thumbnail.getIconWidth()/2;
      int y = getHeight()/2 - thumbnail.getIconHeight()/2;

      if (y < 0)
      {
        y = 0;
      }

      if (x < 5)
      {
        x = 5;
      }
      thumbnail.paintIcon(this, g, x, y);
    }
  }
}
