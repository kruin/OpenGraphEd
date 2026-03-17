package userInterface.menuAndToolBar;

import java.lang.reflect.Method;
import java.awt.event.ActionEvent;
import javax.swing.*;
import userInterface.GraphController; 

public class MenuAndToolBarAction extends AbstractAction implements java.io.Serializable
{
  private GraphController controller;
  protected CompoundMenuAndToolBarAction parentAction;
  private int width;
  protected JMenuItem menuItem;
  protected JButton button;
  private Method method;
  
  public MenuAndToolBarAction( String text, ImageIcon icon, String desc,
                               Integer mnemonic, boolean enabled, int width,
                               Method method, boolean isChild,
                               GraphController controller )
  {
    super(text, icon);
    setEnabled(enabled);
    putValue(SHORT_DESCRIPTION, desc);
    putValue(MNEMONIC_KEY, mnemonic);
    this.width = width;
    this.method = method;
    button = new JButton(this);
    button.setText("");
    if ( isChild )
    {  
      menuItem = new JRadioButtonMenuItem(this);
    }
    else
    {
      menuItem = new JMenuItem(this);
    }
    this.controller = controller;
    menuItem.setIcon(null);
  }

  public void actionPerformed(ActionEvent e)
  {
    if ( method != null )
    {
      try
      {
        method.invoke(controller, null);
        controller.update();
      }
      catch ( Exception ex )
      {
        ex.printStackTrace();
        // error
      }
    }
  }
  
  public boolean equalsText(String text)
  {
    return getText().equals(text);
  }
  
  public MenuAndToolBarAction getActionWithText(String text)
  {
    if ( equalsText(text) )
    {
      return this;
    }
    else
    {
      return null;
    }
  }
  
  /*public void actionPerformed(ActionEvent e)
  {
    if ( parentAction != null )
    {
      ((ButtonChooser)parentAction.getButton()).setSelected(button);
      menuItem.setSelected(true);
    }
  }*/

  public JButton getButton() { return button; }
  
  public JMenuItem getMenuItem() { return menuItem; }
  
  public String getText() { return (String)getValue(Action.NAME); }
  
  public CompoundMenuAndToolBarAction getParentAction() { return parentAction; }
  
  public void setParentAction(CompoundMenuAndToolBarAction parentAction)
  {
    this.parentAction = parentAction;
  }
  
  public int getWidth() { return width; }
  
  public boolean isCompound() { return false; }
  
  public void putValue(String key, Object newValue)
  {
    if ( menuItem == null && button == null )
    {
      super.putValue(key, newValue);
    }
    else
    {
      if ( key.equals( NAME ) )
      {
        menuItem.setText((String)newValue);
      }
      else if ( key.equals( SMALL_ICON ) )
      {
        button.setIcon((Icon)newValue);
      }
      else
      {
        super.putValue(key, newValue);
      }
    }
  }
}