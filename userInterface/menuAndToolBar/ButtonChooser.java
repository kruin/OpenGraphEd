package userInterface.menuAndToolBar;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class ButtonChooser extends JButton implements ActionListener, MouseListener, FocusListener, AncestorListener
{
  private int selectedIndex;
  private Vector buttons;
  private JWindow chooserPopupWindow;
  private boolean popupInited;
  private Action action;

  public ButtonChooser(Action action, Vector actions)
  {
    super( (Icon)((Action)actions.firstElement()).getValue(Action.SMALL_ICON) );
    this.action = action;
    setToolTipText( (String)action.getValue(Action.SHORT_DESCRIPTION) );
    selectedIndex = 0;
    buttons = new Vector(actions.size());
    popupInited = false;
    JButton button;

    for ( int i=0; i<actions.size(); i++ )
    {
      buttons.addElement(((MenuAndToolBarAction)actions.elementAt(i)).getButton());
    }

    addFocusListener(this);
    addMouseListener(this);
    addAncestorListener(this);
  }

  public Vector getButtons() { return buttons; }

  public int getSelectedIndex() { return selectedIndex; }

  public void setSelectedIndex(int index)
  {
    if ( index >= 0 && index < buttons.size() )
    {
      selectedIndex = index;
      setIcon( ((JButton)buttons.elementAt(selectedIndex)).getIcon() );
    }
  }

  public JButton getSelected()
  {
    if ( buttons == null || selectedIndex >= buttons.size() )
    {
      return null;
    }
    return (JButton)buttons.elementAt(selectedIndex);
  }

  public void setSelected(JButton button)
  {
    int index = buttons.indexOf(button);
    if ( index != -1 )
    {
      selectedIndex = index;
      setIcon( ((JButton)buttons.elementAt(selectedIndex)).getIcon() );
    }
  }

  public void showPopup()
  {
    if ( !popupInited )
    {
      popupInited = true;
      chooserPopupWindow = new JWindow(getParentWindow(this));
      JPanel popupPanel = new JPanel();

      GridLayout layout = new GridLayout(buttons.size(), 1);
      popupPanel.setLayout(layout);

      JButton button;
      for ( int i=0; i<buttons.size(); i++ )
      {
        button = (JButton)buttons.elementAt(i);
        button.setText("");
        button.addActionListener(this);
        popupPanel.add(button);
      }

      Dimension dim = popupPanel.getPreferredSize();
      dim.width = getWidth();
      popupPanel.setSize(dim);
      popupPanel.setLocation(0,0);
      popupPanel.setVisible(true);
      chooserPopupWindow.setFocusableWindowState(false);
      chooserPopupWindow.addFocusListener(this);
      chooserPopupWindow.setSize(popupPanel.getSize());
      chooserPopupWindow.getLayeredPane().add(popupPanel, JLayeredPane.POPUP_LAYER);
    }

    Point location = new Point(0, getHeight() + getInsets().bottom);
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Rectangle screenBounds;
    Insets screenInsets;
    GraphicsConfiguration gc = null;
    // Try to find GraphicsConfiguration, that includes mouse
    // pointer position
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gd = ge.getScreenDevices();
    for(int i = 0; i < gd.length; i++)
    {
      if(gd[i].getType() == GraphicsDevice.TYPE_RASTER_SCREEN)
      {
        GraphicsConfiguration dgc = gd[i].getDefaultConfiguration();
        if(dgc.getBounds().contains(location))
        {
          gc = dgc;
          break;
        }
      }
    }

    if(gc != null)
    {
      // If we have GraphicsConfiguration use it to get
      // screen bounds and insets
      screenInsets = toolkit.getScreenInsets(gc);
      screenBounds = gc.getBounds();
    }
    else
    {
      // If we don't have GraphicsConfiguration use primary screen
      // and empty insets
      screenInsets = new Insets(0, 0, 0, 0);
      screenBounds = new Rectangle(toolkit.getScreenSize());
    }

    int scrWidth = screenBounds.width - Math.abs(screenInsets.left+screenInsets.right);
    int scrHeight = screenBounds.height - Math.abs(screenInsets.top+screenInsets.bottom);

    Dimension size;

    size = chooserPopupWindow.getPreferredSize();


    SwingUtilities.convertPointToScreen(location, this);

    if( (location.x + size.width) > screenBounds.x + scrWidth )
    {
      location.x = screenBounds.x + scrWidth - size.width;
    }
    if( (location.y + size.height) > screenBounds.y + scrHeight)
    {
      location.y = screenBounds.y + scrHeight - size.height;
    }
    if( location.x < screenBounds.x )
    {
      location.x = screenBounds.x;
    }
    if( location.y < screenBounds.y )
    {
      location.y = screenBounds.y;
    }
    chooserPopupWindow.setLocation(location.x, location.y);
    chooserPopupWindow.setVisible(true);
  }

  public void hidePopup()
  {
    if ( chooserPopupWindow != null )
    {
      chooserPopupWindow.setVisible(false);
    }
  }

  public void focusGained(FocusEvent e) { }

  public void focusLost(FocusEvent e)
  {
    hidePopup();
  }

  public void actionPerformed(ActionEvent e)
  {
    JButton source = (JButton)e.getSource();
    selectedIndex = buttons.indexOf(source);
    hidePopup();
  }

  public void mouseClicked(MouseEvent e) { }
  public void mouseReleased(MouseEvent e) { }
  public void mouseEntered(MouseEvent e) { }

  public void mouseExited(MouseEvent e)
  {
    if ( e.getY() < 5 ||
         ( ( e.getX() < 5 || e.getX() > getWidth()-5 ) &&
           e.getY() < getHeight() -5 ) )
    {
      hidePopup();
    }
  }

  public void mousePressed(MouseEvent e)
  {
    if ( e.getSource() == this )
    {
      if ( chooserPopupWindow != null && chooserPopupWindow.isVisible() )
      {
        hidePopup();
      }
      else
      {
        showPopup();
      }
    }
  }

  public void ancestorMoved(AncestorEvent event)
  {
    hidePopup();
  }

  public void ancestorAdded(AncestorEvent event) { }
  public void ancestorRemoved(AncestorEvent event) { }

  private Window getParentWindow(Component owner)
  {
    Window window = null;

    if (owner instanceof Window)
    {
      window = (Window)owner;
    }
    else if (owner != null)
    {
      window = SwingUtilities.getWindowAncestor(owner);
    }
    if (window == null)
    {
      window = new Frame();
    }
    return window;
  }

  public void setAction(Action action)
  {
    this.action = action;
  }

  public Action getAction()
  {
    return action;
  }
}