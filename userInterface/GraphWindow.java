package userInterface;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import graphStructure.*;

public class GraphWindow extends JPanel
{
  private JDesktopPane desktopPane;

  public GraphWindow()
  {
    desktopPane = new JDesktopPane();

    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints layoutCons = new GridBagConstraints();
    setLayout(layout);

    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = GridBagConstraints.REMAINDER;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(1,1,1,1);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 120.0;
    layoutCons.weighty = 120.0;
    layout.setConstraints(desktopPane, layoutCons);
    add(desktopPane);
  }

  public void addGraphEditorWindow(GraphController graphController)
  {
    GraphEditorWindow gew = new GraphEditorWindow(graphController);
    desktopPane.add( gew );
    try {
    gew.setSelected(true);
    } catch (java.beans.PropertyVetoException pve) { pve.printStackTrace(); }
    desktopPane.getDesktopManager().activateFrame( gew );
  }

  public void addGraphEditorWindow(GraphController graphController, Graph graph)
  {
    GraphEditorWindow gew = new GraphEditorWindow(graphController, graph);
    desktopPane.add( gew );
    try {
    gew.setSelected(true);
    } catch (java.beans.PropertyVetoException pve) { pve.printStackTrace(); }
    desktopPane.getDesktopManager().activateFrame( gew );
  }

  public void close(JGraphEdInternalFrame intFrame)
  {
    intFrame.dispose();
  }

  public void activate(JGraphEdInternalFrame intFrame)
  {
    try {
    if ( intFrame.isIcon() )
    {
      intFrame.setIcon(false);
    }
    else
    {
      intFrame.setSelected(true);      
    }
    } catch (java.beans.PropertyVetoException pve) { pve.printStackTrace(); }
    if ( intFrame.isSelected() ) // dialog may have been shown selected on top
    {  
      desktopPane.getDesktopManager().activateFrame( intFrame );
    }
  }

  public void show(JGraphEdInternalFrame intFrame)
  {
    if ( !intFrame.isIcon() && desktopPane.getIndexOf( intFrame ) == -1 )
    {
      intFrame.setVisible(true);
      desktopPane.add( intFrame );
    }
    try {
    if ( intFrame.isIcon() )
    {
      intFrame.setIcon(false);
    }
    intFrame.setSelected(true);
    } catch (java.beans.PropertyVetoException pve) { pve.printStackTrace(); }
    desktopPane.getDesktopManager().activateFrame( intFrame );
  }


  public void showInfo(GraphEditorWindow window)
  {
    if ( desktopPane.getIndexOf( window.getInfoWindow() ) == -1 )
    {
      window.getInfoWindow().setVisible(true);
      desktopPane.add( window.getInfoWindow() );
    }
    window.getInfoWindow().update();
    try {
    window.getInfoWindow().setClosed(false);
    window.getInfoWindow().setSelected(true);
    } catch (java.beans.PropertyVetoException pve) { pve.printStackTrace(); }
    desktopPane.getDesktopManager().activateFrame( window.getInfoWindow() );
  }

  public void closeInfo(GraphEditorInfoWindow window)
  {
    try {
    window.setClosed(true);
    } catch (java.beans.PropertyVetoException pve) { pve.printStackTrace(); }
  }

  public void showLog(GraphEditorWindow window)
  {
    if ( desktopPane.getIndexOf( window.getLogWindow() ) == -1 )
    {
      window.getLogWindow().setVisible(true);
      desktopPane.add( window.getLogWindow() );
    }
    window.getLogWindow().update();
    try {
    window.getLogWindow().setClosed(false);
    window.getLogWindow().setSelected(true);
    } catch (java.beans.PropertyVetoException pve) { pve.printStackTrace(); }
    desktopPane.getDesktopManager().activateFrame( window.getLogWindow() );
  }

  public void closeLog(GraphEditorLogWindow window)
  {
    try {
    window.setClosed(true);
    } catch (java.beans.PropertyVetoException pve) { pve.printStackTrace(); }
  }

  public void showDialog(GraphEditorDialog dialog)
  {
    if ( desktopPane.getIndexOf( dialog ) == -1 )
    {
      dialog.setVisible(true);
      desktopPane.add( dialog );
    }
    try {
    dialog.setClosed(false);
    dialog.setSelected(true);
    } catch (java.beans.PropertyVetoException pve) { pve.printStackTrace(); }
    desktopPane.getDesktopManager().activateFrame( dialog );
  }

  public void activateDialog(GraphEditorDialog dialog)
  {
    try {
    if ( dialog.getOwner().isSelected() )
    {
      dialog.getOwner().setSelected(false);
    }
    dialog.setClosed(false);
    dialog.setSelected(true);
    } catch (java.beans.PropertyVetoException pve) { pve.printStackTrace(); }
    desktopPane.getDesktopManager().activateFrame( dialog );
  }

  public void forceGraphRepaints()
  {
    JInternalFrame frames[] = desktopPane.getAllFrames();
    for ( int i=0; i<frames.length; i++ )
    {
      if ( frames[i] instanceof GraphEditorWindow )
      {
        ((GraphEditorWindow)frames[i]).getGraphEditor().getGraph().markForRepaint();
      }
    }
    repaint();
  }
  
  public Vector getAllGraphEditorWindows()
  {
    Vector toReturn = new Vector();
    JInternalFrame frames[] = desktopPane.getAllFrames();
    for ( int i=0; i<frames.length; i++ )
    {
      if ( frames[i] instanceof GraphEditorWindow )
      {
        toReturn.add(frames[i]);
      }
    }
    return toReturn;
  }
}