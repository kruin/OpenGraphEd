package userInterface;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.Vector;
import graphStructure.LogEntry;

public class GraphEditorLogWindow extends JGraphEdInternalFrame
{
  public static int WIDTH = 400;
  public static int HEIGHT = 400;

  private GraphController controller;
  private GraphEditorWindow editorWindow;

  private JTree logEntryTree;

  public GraphEditorLogWindow( GraphController controller,
                               GraphEditorWindow editorWindow )
  {
    super(controller, editorWindow.getTitle() + " - Log",
          true, //resizable
          true, //closable
          true, //maximizable
          true);//iconifiable
    this.controller = controller;
    this.editorWindow = editorWindow;

    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints layoutCons = new GridBagConstraints();
    getContentPane().setLayout(layout);

    logEntryTree = new JTree();
    logEntryTree.setShowsRootHandles(true);
    JScrollPane scrollPane = new JScrollPane(logEntryTree,
                               JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                               JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(scrollPane, layoutCons);
    getContentPane().add(scrollPane);

    update();

    addInternalFrameListener(controller);

    setSize(WIDTH, HEIGHT);
  }

  public void update()
  {
    if ( isVisible() )
    {
      Vector logEntries = editorWindow.getGraphEditor().getGraph().getLogEntries();
      if ( logEntries.size() == 0 )
      {
        logEntryTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("No Log Entries")));
      }
      else
      {
        LogEntry root = new LogEntry();
        root.setSubEntries(logEntries);
        logEntryTree.setModel(new DefaultTreeModel(root));
        logEntryTree.setRootVisible(false);
      }
      logEntryTree.repaint();
      logEntryTree.validate();
    }
  }
}