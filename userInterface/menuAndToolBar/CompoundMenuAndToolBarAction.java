package userInterface.menuAndToolBar;

import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JRadioButtonMenuItem;
import userInterface.GraphController;

public class CompoundMenuAndToolBarAction extends MenuAndToolBarAction
{
  private Vector subActions;
  private ButtonChooser bc;
  
  public CompoundMenuAndToolBarAction( String text, ImageIcon icon, String desc,
																			Integer mnemonic, boolean enabled,
                                      int width, Vector subActions,
                                      GraphController controller )
  {
    super(text, icon, desc, mnemonic, enabled, width, null, false, controller );
    this.subActions = subActions;
    ButtonGroup buttonGroup = new ButtonGroup();
    
    ((JRadioButtonMenuItem)((MenuAndToolBarAction)subActions.firstElement()).getMenuItem()).setSelected(true);
    MenuAndToolBarAction subAction;
    for ( int i=0; i<subActions.size(); i++ )
    {
      subAction = (MenuAndToolBarAction)subActions.elementAt(i);
      buttonGroup.add(subAction.getMenuItem());
      subAction.setParentAction(this);
    }
    button = bc = new ButtonChooser(this, subActions);
  }

  public void setEnabled(boolean enabled)
  {
    if ( bc != null )
    {  
      bc.setEnabled(enabled);
    }
    super.setEnabled(enabled);
    if ( subActions != null )
    {
      for ( int i=0; i<subActions.size(); i++ )
      {
        ((MenuAndToolBarAction)subActions.elementAt(i)).setEnabled(enabled);
      }
    }
  }
  
  public Vector getButtons()
  {
    Vector buttons = new Vector(subActions.size());
    for ( int i=0; i<subActions.size(); i++ )
    {
      buttons.add(((MenuAndToolBarAction)subActions.get(i)).getButton());
    }
    return buttons;
  }
  
  public Vector getMenuItems()
  {
    Vector menuItems = new Vector(subActions.size());
    for ( int i=0; i<subActions.size(); i++ )
    {
      menuItems.add(((MenuAndToolBarAction)subActions.get(i)).getMenuItem());
    }
    return menuItems;
  }
  
  public boolean equalsText(String text)
  {
    if ( super.equalsText(text) )
    {
      return true;
    }
    for ( int i=0; i<subActions.size(); i++ )
    {
      if ( ((MenuAndToolBarAction)subActions.elementAt(i)).equalsText(text) )
      {
        return true;
      }
    }
    return false;
  }
  
  public MenuAndToolBarAction getActionWithText(String text)
  {
    if ( getText().equals(text) )
    {
      return this;
    }
    else
    {
      for ( int i=0; i<subActions.size(); i++ )
      {
        if ( ((MenuAndToolBarAction)subActions.elementAt(i)).equalsText(text) )
        {
          return (MenuAndToolBarAction)subActions.elementAt(i);
        }
      }
      return null;
    }
  }
  
  public boolean isCompound() { return true; }
  
  public ButtonChooser getButtonChooser()
  {
    return bc;
  }
}
