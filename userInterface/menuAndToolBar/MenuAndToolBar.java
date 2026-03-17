package userInterface.menuAndToolBar;

import java.util.StringTokenizer;
import java.lang.reflect.Method;
import javax.swing.*;
import userInterface.GraphController;
import userInterface.GraphEditor;
import graphStructure.Graph;
import java.awt.*;
import java.util.Vector;
import java.util.HashMap;

public class MenuAndToolBar
{
  private GraphController controller;
  private StringTokenizer tok;
  
  private static int NO_MODE = 0;
  private static int EDIT_MODE = 1;
  private static int MOVE_MODE = 2;
  private static int ROTATE_MODE = 3;
  private static int RESIZE_MODE = 4;

  private Vector actions;
  private HashMap groups;
  
  private JMenuBar menuBar;
  private JToolBar toolBar;
  private JLabel cursorLocationLabel;
  private Point cursorPoint;
  private JInternalFrame lastWindow;
  private int lastShownIndex;
  /////////////////////////////////////////
  private JMenu windowMenu;
  private JMenuItem noWindowItem;
  /////////////////////////////////////////

  public MenuAndToolBar(GraphController controller)
  {
    this.controller = controller;
    actions = new Vector();
    groups = new HashMap();
    cursorPoint = null;
    createControls(loadControls());
    hideControls();
  }

  public JToolBar getToolBar() { return toolBar; }

  public JMenuBar getMenuBar() { return menuBar; }

  public void updateCursorLocation(Point cursorPoint)
  {
    this.cursorPoint = cursorPoint;
  }

  public Point getCursorPoint() { return cursorPoint; }

  public void setCursorLocationText( String text )
  {
    cursorLocationLabel.setText(text);
  }
    
  private Vector loadControls()
  {
    Vector v = new Vector();

    v.add("M,File,F,Load & Save Graphs");
    v.add("S,File,New Graph,/images/New.gif,Create a New Graph,N,true,1,newGraph");
    v.add("S,File,Load Graph,/images/Open.gif,Load a Graph From a File,L,isApplication,1,loadGraph");
    v.add("S,File,Save Graph,/images/Save.gif,Save the Current Graph To a File,S,false,1,saveGraph");
    v.add("S,File,Close Graph,/images/Delete.gif,Close the Current Graph Editor,C,false,1,closeGraph");
    v.add("S,File,Preferences,/images/Preferences.gif,JGraphEd Preferences,P,true,1,preferences");
    v.add("separator,space");

    v.add("M,Modes,M,Change Editor Mode");
    v.add("P,Modes,Choose an Operation Mode,/images/DefaultDisplay.gif,Choose an Operation Mode,Z,false,1,6,"  
        + "C,Modes,Edit,/images/Edit.gif,Graph Edit Mode,E,false,1,editMode,"
        + "C,Modes,Grid,/images/Grid.gif,Graph Grid Mode,M,false,1,gridMode,"
        + "C,Modes,KruinGrid,/images/Grid.gif,Graph KruinGrid Mode,M,false,1,kruinGridMode," /* do increment (P,Modes) 5 to 6 */
        + "C,Modes,Move,/images/Move.gif,Graph Move Mode,M,false,1,moveMode,"
        + "C,Modes,Rotate,/images/Rotate.gif,Graph Rotate Mode,R,false,1,rotateMode,"
        + "C,Modes,Resize,/images/Resize.gif,Graph Resize Mode,Z,false,1,resizeMode");
    //v.add("separator,none");
    
    v.add("M,Show,S,Coordinate or Label Showing Options");
    v.add("P,Show,Show,/images/DefaultDisplay.gif,Choose to Show Coordinates or Labels,Z,false,1,3,"
        + "C,Show,Show Coordinates,/images/ShowCoords.gif,Show Coordinates,C,false,1,showCoords,"
        + "C,Show,Show Labels,/images/ShowLabels.gif,Show Labels,L,false,1,showLabels,"
        + "C,Show,Show Nothing,/images/ShowNothing.gif,Show Nothing,T,false,1,showNothing");
    //v.add("separator,none");
    
    v.add("M,Info,I,Info about the current graph");
    v.add("S,Info,Info,/images/Info.gif,Info about the current graph,I,false,1,info");
    v.add("S,Info,Log,/images/Log.gif,Log of Operations run on the current graph,L,false,1,log");
    //v.add("separator,none");
    
    v.add("M,Help,H,JGraphEd Help");
    v.add("S,Help,Help,/images/Help.gif,JGraphEd Help,H,true,1,help");
    v.add("separator,space"); 
    
    v.add("M,Undo,U,Undo or Redo Actions");
    v.add("S,Undo,Undo,/images/Undo.gif,Undo Last Command,U,false,1,undo");
    v.add("S,Undo,Redo,/images/Redo.gif,Redo Last Un-Done Command,R,false,1,redo");
    v.add("S,Undo,Toggle Undos,/images/ToggleUndos.gif,Disable Undos,D,true,1,toggleUndo");
    v.add("separator,space");
    
    v.add("M,Edit,E,Edit Options");
    v.add("S,Edit,Unselect All,/images/UnselectAll.gif,Unselect All Nodes and Edges,U,false,1,unselectAll");
    v.add("S,Edit,Remove Selected,/images/RemoveSelected.gif,Remove Selected Nodes and Edges,S,false,1,removeSelected");
    v.add("S,Edit,Remove All,/images/RemoveAll.gif,Remove All Nodes and Edges,R,false,1,removeAll");
    v.add("S,Edit,Remove Generated,/images/RemoveGenerated.gif,Remove Generated Edges,E,false,1,removeGenerated");
    v.add("S,Edit,Preserve Generated,/images/PreserveGenerated.gif,Preserve Generated Edges,P,false,1,preserveGenerated");
    v.add("separator,newline");
    
    v.add("M,Test,T,Test the Current Graph");
    v.add("S,Test,Connectivity Test,/images/TestConnectivity.gif,Is Current Graph Connected?,C,false,1,testConnectivity");
    v.add("S,Test,Biconnectivity Test,/images/TestBiconnectivity.gif,Is Current Graph Biconnected?,B,false,1,testBiconnectivity");
    v.add("S,Test,Planarity Test,/images/TestPlanarity.gif,Is Current Graph Planar?,P,false,1,testPlanarity");
    v.add("separator,space");
    
    v.add("M,Operation,O,Operate on Current Graph");
    v.add("S,Operation,Create Random,/images/CreateRandom.gif,Create X Random Nodes,R,false,1,createRandom");
    v.add("S,Operation,Embedding,/images/Embed.gif,Embed the Current Graph,E,false,1,embedding");
    v.add("S,Operation,Make Connected,/images/MakeConnected.gif,Connect the Current Graph,C,false,1,makeConnected");
    v.add("S,Operation,Make Biconnected,/images/MakeBiconnected.gif,Biconnect the Current Graph,B,false,1,makeBiconnected");
    v.add("S,Operation,Make Maximal,/images/MakeMaximal.gif,Triangulate the Current Graph,M,false,1,makeMaximal");
    v.add("S,Operation,Straight Line Embed,/images/StraightLineEmbed.gif,Straight Line Embed the Current Graph,S,false,1,straightLineEmbed");
    v.add("separator,space");

    v.add("M,Display,D,Graph Display Options");
    v.add("S,Display,Default,/images/DefaultDisplay.gif,Reset to Default Display,F,false,1,resetDisplay");
    v.add("S,Display,Depth First Search,/images/DFSDisplay.gif,Display Depth First Search,D,false,1,displayDFS");
    v.add("S,Display,Biconnected Components,/images/BiconnectedDisplay.gif,Display Biconnected Components,B,false,1,displayBiconnected");
    v.add("S,Display,ST Numbering,/images/STDisplay.gif,Display ST Numbering,S,false,1,displayST");
    v.add("S,Display,Canonical Ordering,/images/CanonicalDisplay.gif,Display Canonical Ordering,O,false,1,displayCanonical");
    v.add("S,Display,Normal Labeling,/images/NormalDisplay.gif,Display Normal Labeling,N,false,1,displayNormal");
    v.add("S,Display,Chan Tree Drawing,/images/ChanTreeDisplay.gif,Display Chan Tree Drawing,C,false,1,displayChanTree");
    v.add("S,Display,Minimum Spanning Tree,/images/MSTDisplay.gif,Display Minimum Spanning Tree,M,false,1,displayMST");
    v.add("S,Display,Dijkstra Shortest Path,/images/SPDisplay.gif,Display Shortest Path For Two Nodes,P,false,1,displayDijkstra");
    v.add("S,Display,Kruin Domain Tree Drawing,/images/ChanTreeDisplay.gif,Display Kruin Tree Drawing,C,false,1,displayKruinTree");
    
  v.add("separator,space");
    
    v.add("M,Windows,W,List of Open Windows");
    v.add("S,Windows,None,null,No Windows Are Open, ,true,1, ");
    
    v.add("cursorLocationLabel");
    
    return v;
  }
  
  private void createControls(Vector controls)
  {
    toolBar = new JToolBar();
    toolBar.setFloatable(false);
    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints layoutCons = new GridBagConstraints();
    toolBar.setLayout(layout);
    menuBar = new JMenuBar();
    
    String current;
    boolean firstOfGroup = true;
    JMenu menu = null;
    JButton button;
    JMenuItem menuItem;
    JRadioButtonMenuItem radioMenuItem;
    ButtonGroup buttonGroup;
    MenuAndToolBarAction action;
    CompoundMenuAndToolBarAction cAction;
    for ( int i=0; i<controls.size(); i++ )
    {
      current = (String)controls.elementAt(i);
      if ( isSeparator(current) )
      {
        JPanel panel = new JPanel();
        layoutCons.gridx = GridBagConstraints.RELATIVE;
        layoutCons.gridy = GridBagConstraints.RELATIVE;
        if ( isNewLineSeparator(current) )
        {
          layoutCons.gridwidth = GridBagConstraints.REMAINDER;
        }
        else
        {  
          layoutCons.gridwidth = 1;
        }
        layoutCons.gridheight = 1;
        layoutCons.fill = GridBagConstraints.NONE;
        layoutCons.insets = new Insets(0,0,0,0);
        layoutCons.anchor = GridBagConstraints.NORTH;
        layoutCons.weightx = 0.0;
        layoutCons.weighty = 0.0;
        layout.setConstraints(panel, layoutCons);
        toolBar.add(panel);
      }
      else
      {
        if ( isMenu(current) )
        {
          menu = createMenu(current);
          menuBar.add(menu);
          if ( menu.getText().equals("Windows") )
          {
            windowMenu = menu;
          }
          firstOfGroup = false;
        }
        else if ( isAction(current) )
        {
          action = createMenuAndToolbarAction(current);
          actions.addElement(action);
          
          if ( !action.isCompound() )
          {
            if ( action.getText().equals("None") )
            {
              noWindowItem = action.getMenuItem();
            }
            else
            {
              button = action.getButton();             
              layoutCons.gridx = GridBagConstraints.RELATIVE;
              layoutCons.gridy = GridBagConstraints.RELATIVE;
              layoutCons.gridwidth = action.getWidth();
              layoutCons.gridheight = 1;
              layoutCons.fill = GridBagConstraints.NONE;
              layoutCons.insets = new Insets(1,1,1,1);
              layoutCons.anchor = GridBagConstraints.NORTH;
              layoutCons.weightx = 0.0;
              layoutCons.weighty = 0.0;
              layout.setConstraints(button, layoutCons);
              toolBar.add(button);
            }
            menuItem = action.getMenuItem();
            menu.add(menuItem);
          }
          else
          {
            ButtonChooser bc = ((CompoundMenuAndToolBarAction)action).getButtonChooser();
            Vector menuItems = ((CompoundMenuAndToolBarAction)action).getMenuItems();
            for ( int j=0; j<menuItems.size(); j++ )
            {
              menu.add((JRadioButtonMenuItem)menuItems.elementAt(j));
            }
      
            layoutCons.gridx = GridBagConstraints.RELATIVE;
            layoutCons.gridy = GridBagConstraints.RELATIVE;
            layoutCons.gridwidth = action.getWidth();
            layoutCons.gridheight = 1;
            layoutCons.fill = GridBagConstraints.NONE;
            layoutCons.insets = new Insets(1,1,1,1);
            layoutCons.anchor = GridBagConstraints.NORTH;
            layoutCons.weightx = 0.0;
            layoutCons.weighty = 0.0;
            layout.setConstraints(bc, layoutCons);
            toolBar.add(bc);
          }
        }
        else if ( isCursorLocationLabel(current) )
        {
          cursorLocationLabel = new JLabel();
          layoutCons.gridx = GridBagConstraints.RELATIVE;
          layoutCons.gridy = GridBagConstraints.RELATIVE;
          layoutCons.gridwidth = 2;
          layoutCons.gridheight = 1;
          layoutCons.fill = GridBagConstraints.NONE;
          layoutCons.insets = new Insets(1,1,1,1);
          layoutCons.anchor = GridBagConstraints.NORTH;
          layoutCons.weightx = 0.01;
          layoutCons.weighty = 0.01;
          layout.setConstraints(cursorLocationLabel, layoutCons);
          toolBar.add(cursorLocationLabel);
        }
        else
        {
          // error
        }
      }
    }
  }
  
  private boolean isSeparator(String s)
  {
    return s.startsWith("separator");
  }
  
  private boolean isNewLineSeparator(String s)
  {
    return isSeparator(s) && s.endsWith(",newline");
  }
  
  private boolean isMenu(String s)
  {
    return s.startsWith("M");
  }
  
  private boolean isAction(String s)
  {
    return s.startsWith("S") || s.startsWith("P");
  }
  
  private boolean isCursorLocationLabel(String s)
  {
    return s.equals("cursorLocationLabel");
  }
  
  private JMenu createMenu(String s)
  {
    tok = new StringTokenizer(s,",");
    JMenu menu = null;
    if ( tok.countTokens() == 4 )
    {  
      tok.nextToken();
      menu = new JMenu(tok.nextToken());
      menu.setMnemonic(getKeyCode(tok.nextToken()));
      menu.getAccessibleContext().setAccessibleDescription(tok.nextToken());
    }
    else
    {
      //error
    }
    return menu;
  }
  
  private MenuAndToolBarAction createMenuAndToolbarAction(String s)
  {
    return createMenuAndToolbarAction(new StringTokenizer(s,","));
  }
    
  private MenuAndToolBarAction createMenuAndToolbarAction(StringTokenizer tok)
  {
    MenuAndToolBarAction action = null;
    String type = tok.nextToken();
    if ( ( type.equals("S") && tok.countTokens() == 8 ) ||
         ( type.equals("C") && tok.countTokens() >= 8 ) )
    {
      String group = tok.nextToken();
      String text = tok.nextToken();
      String iconString = tok.nextToken();
      ImageIcon icon = null;
      if ( !iconString.equals("null") )
      {  
        icon = new ImageIcon(MenuAndToolBar.class.getResource(iconString));
      }
      String desc = tok.nextToken();
      Integer mnemonic = new Integer(getKeyCode(tok.nextToken()));
      String enabledString = tok.nextToken();
      boolean enabled;
      if ( enabledString.equals("isApplication") )
      {
        enabled = controller.isApplication();
      }
      else
      {  
        enabled = Boolean.valueOf(enabledString).booleanValue();
      }
      int width = Integer.parseInt(tok.nextToken());
      Method method = null;
      try
      {
        method = GraphController.class.getDeclaredMethod(tok.nextToken(), null);
      }
      catch (NoSuchMethodException nsme)
      {
        // error
      }
      action = new MenuAndToolBarAction( text, icon, desc, mnemonic, enabled,
                                         width, method, type.equals("C"),
                                         controller );
      addToGroup(group, action);
    }
    else if ( type.equals("P") )
    {
      if ( tok.countTokens() > 8 )
      {
        String group = tok.nextToken();
        String text = tok.nextToken();
        ImageIcon icon = new ImageIcon(MenuAndToolBar.class.getResource(tok.nextToken()));
        String desc = tok.nextToken();
        Integer mnemonic = new Integer(getKeyCode(tok.nextToken()));
        boolean enabled = Boolean.valueOf(tok.nextToken()).booleanValue();
        int width = Integer.parseInt(tok.nextToken());
        int numChildren = Integer.parseInt(tok.nextToken());
        if ( tok.countTokens() == numChildren * 9 )
        {
          Vector v = new Vector(numChildren);
          for ( int j=0; j<numChildren; j++ )
          {
            v.addElement(createMenuAndToolbarAction(tok));
          }
          action = new CompoundMenuAndToolBarAction( text, icon, desc, mnemonic,
                                                     enabled, width, v,
                                                     controller );
          addToGroup(group, action);
        }
        else
        {
          // error
        }
      }
      else
      {
        // error
      }
    }
    else
    { 
      // error
    }
    return action;
  }
  
  private int getKeyCode(String ch)
  {
    if ( ch.length() != 1 )
    {
      return -1;
    }
    else
    {
      return (int)ch.charAt(0);
    }
  }
  
  public void removeWindow(JMenuItem menuItem)
  {
    windowMenu.remove(menuItem);
    if ( windowMenu.getItemCount() == 0 )
    {
      windowMenu.add(noWindowItem);
    }
  }
  
  public void addWindow(JMenuItem menuItem)
  {
    if ( !windowContainsMenuItem(menuItem) )
    {  
      if ( windowMenu.getItemCount() > 0 &&
           windowMenu.getItem(0) == noWindowItem )
      {
        windowMenu.remove(noWindowItem);
      }
      windowMenu.add(menuItem);
    }
  }
  
  public void showControls( GraphEditor graphEditor )
  {
    MenuAndToolBarAction action = getActionWithText(graphEditor.getModeString());
    CompoundMenuAndToolBarAction compoundAction = action.getParentAction();
    
    compoundAction.getButtonChooser().setSelected(action.getButton());
    action.getMenuItem().setSelected(true);

    action = getActionWithText(graphEditor.getShowString());
    compoundAction = action.getParentAction();
    
    compoundAction.getButtonChooser().setSelected(action.getButton());
    action.getMenuItem().setSelected(true);
    
    enableAllControls();
    
    updateUndo( graphEditor.getGraph() );
  }
  
  public void hideControls()
  {
    disableAllControls();
  }
  
  private void enableAllControls()
  {
    for ( int i=0; i<actions.size(); i++ )
    {
      ((MenuAndToolBarAction)actions.elementAt(i)).setEnabled(true);
    }
    getActionWithText("Load Graph").setEnabled(controller.isApplication());
    getActionWithText("Save Graph").setEnabled(controller.isApplication());
  }
  
  private void disableAllControls()
  {
    for ( int i=0; i<actions.size(); i++ )
    {
      ((MenuAndToolBarAction)actions.elementAt(i)).setEnabled(false);
    }
    getActionWithText("New Graph").setEnabled(true);
    getActionWithText("Load Graph").setEnabled(controller.isApplication());
    getActionWithText("Preferences").setEnabled(true);
    setEnabled("Help", true);
    setEnabled("Window", true);
    
  }
  
  private void setEnabled(String group, boolean enabled)
  {
    Vector controls = getGroup(group);
    if ( controls != null )
    {
      for ( int i=0; i<controls.size(); i++ )
      {
        ((MenuAndToolBarAction)controls.elementAt(i)).setEnabled(enabled);
      }
    }
  }
  
  private MenuAndToolBarAction getActionWithText(String text)
  {
    for ( int i=0; i<actions.size(); i++ )
    {
      if ( ((MenuAndToolBarAction)actions.elementAt(i)).equalsText(text) )
      {
        return ((MenuAndToolBarAction)actions.elementAt(i)).getActionWithText(text);
      }
    }
    return null;
  }
  
  private boolean windowContainsMenuItem( JMenuItem item )
  {
    boolean found = false;
    if ( item == null )
    {
      return true;
    }
    for ( int i=0; i<windowMenu.getItemCount(); i++ )
    {
      if ( windowMenu.getItem(i) == item )
      {
        found = true;
        break;
      }
    }
    return found;
  }
  
  private void addToGroup(String group, MenuAndToolBarAction action)
  {
    if ( !groups.containsKey(group) )
    {
      groups.put(group, new Vector());
    }
    getGroup(group).add(action);
  }
  
  public Vector getGroup(String group)
  {
    if ( groups.containsKey(group) )
    {
      return (Vector)groups.get(group);
    }
    else
    {
      return null;
    }
  }

  public void updateUndo(Graph g)
  {
    MenuAndToolBarAction action;
    JMenuItem menuItem;
    if ( g.getTrackUndos() )
    {      
      action = (MenuAndToolBarAction)getActionWithText("Redo");
      menuItem = action.getMenuItem();
      if ( !g.hasMoreRedos() )
      {
        action.putValue(Action.SHORT_DESCRIPTION, "No more commands to Redo" );
        menuItem.setText("Redo");
        action.setEnabled(false);
      }
      else
      {
        action.putValue(Action.SHORT_DESCRIPTION, "Redo " + g.peekRedo() );
        menuItem.setText("Redo " + g.peekRedo());
        action.setEnabled(true);
      }
      action = (MenuAndToolBarAction)getActionWithText("Undo");
      menuItem = action.getMenuItem();
      if ( !g.hasMoreUndos() )
      {
        action.putValue(Action.SHORT_DESCRIPTION, "No more commands to Undo" );
        menuItem.setText("Undo");
        action.setEnabled(false);
      }
      else
      {
        action.putValue(Action.SHORT_DESCRIPTION, "Undo " + g.peekUndo() );
        menuItem.setText("Undo " + g.peekUndo());
        action.setEnabled(true);
      }
      
      action = getActionWithText("Toggle Undos");
      action.putValue(Action.SHORT_DESCRIPTION, "Disable Undos" );
      action.setEnabled(true);
      action.getButton().setIcon(new ImageIcon(GraphController.class.getResource("/images/DisableUndos.gif")));
      action.getMenuItem().setText("Disable Undos");
    }
    else
    {
      action = getActionWithText("Redo");
      menuItem = action.getMenuItem();
      action.putValue(Action.SHORT_DESCRIPTION, "Redo" );
      menuItem.setText("Redo");
      action.setEnabled(false);
      
      action = getActionWithText("Undo");
      menuItem = action.getMenuItem();
      action.putValue(Action.SHORT_DESCRIPTION, "Undo" );
      menuItem.setText("Undo");
      action.setEnabled(false);
      
      action = getActionWithText("Toggle Undos");
      action.putValue(Action.SHORT_DESCRIPTION, "Enable Undos" );
      action.setEnabled(true);
      action.getButton().setIcon(new ImageIcon(GraphController.class.getResource("/images/EnableUndos.gif")));
      action.getMenuItem().setText("Enable Undos");
    }
  }    
}
  
