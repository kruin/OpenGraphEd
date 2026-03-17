package userInterface;

import javax.swing.*;
import java.awt.event.*;

public abstract class JGraphEdInternalFrame extends JInternalFrame
{
  protected JMenuItem menuItem;

  public JGraphEdInternalFrame( final GraphController controller, String title,
                                boolean resizable, boolean closable,
                                boolean maximizable, boolean iconifiable )
  {
    super(title, resizable, closable, maximizable, iconifiable);
    final JGraphEdInternalFrame thisCopy = this;
    menuItem = new JMenuItem(new JGraphEdInternalFrameAction( title, null,
                                   title, null, true )
    {
      public void actionPerformed(ActionEvent event)
      {
        controller.showWindow(thisCopy);
      }
    } );
    
    
  }

  public JMenuItem getMenuItem() { return menuItem; }
}

class JGraphEdInternalFrameAction extends AbstractAction
{

  public JGraphEdInternalFrameAction( String text, ImageIcon icon, String desc,
                                      Integer mnemonic, boolean enabled )
  {
    super(text, icon);
    setEnabled(enabled);
    putValue(SHORT_DESCRIPTION, desc);
    putValue(MNEMONIC_KEY, mnemonic);
  }

  public void actionPerformed(ActionEvent e)
  {
    // do nothing, overriden
  }
}