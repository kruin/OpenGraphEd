 package userInterface;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import dataStructure.DoublyLinkedList;

public class GraphEditorHelpWindow extends JGraphEdInternalFrame implements HyperlinkListener, TreeSelectionListener, ActionListener
{
  public static int WIDTH = 700;
  public static int HEIGHT = 400;

  private JTree contentsTree;
  private JEditorPane contentPane;
  private JToolBar toolBar;
  private DoublyLinkedList pageList;
  private JButton backButton;
  private JButton upButton;
  private JButton forwardButton;
  private boolean navigation;

  public GraphEditorHelpWindow(GraphController controller)
  {
    super(controller, "JGraphEd Help",
          true, //resizable
          true, //closable
          true, //maximizable
          false);//iconifiable

    toolBar = new JToolBar();
    toolBar.setFloatable(false);
    GridBagLayout tlayout = new GridBagLayout();
    GridBagConstraints tlayoutCons = new GridBagConstraints();
    toolBar.setLayout(tlayout);
    navigation = false;

    backButton = new JButton(new ImageIcon(GraphEditorHelpWindow.class.getResource("/images/Back.gif")));
    backButton.setEnabled(false);
    backButton.setToolTipText("Move Back to the Previously Viewed Page");
    backButton.addActionListener(this);
    tlayoutCons.gridx = GridBagConstraints.RELATIVE;
    tlayoutCons.gridy = GridBagConstraints.RELATIVE;
    tlayoutCons.gridwidth = 1;
    tlayoutCons.gridheight = 1;
    tlayoutCons.fill = GridBagConstraints.NONE;
    tlayoutCons.insets = new Insets(1,1,1,1);
    tlayoutCons.anchor = GridBagConstraints.NORTH;
    tlayoutCons.weightx = 0.0;
    tlayoutCons.weighty = 0.0;
    tlayout.setConstraints(backButton, tlayoutCons);
    toolBar.add(backButton);

    upButton = new JButton(new ImageIcon(GraphEditorHelpWindow.class.getResource("/images/Up.gif")));
    upButton.setToolTipText("Move Up One Page in the Contents Tree");
    upButton.setEnabled(false);
    upButton.addActionListener(this);
    tlayoutCons.gridx = GridBagConstraints.RELATIVE;
    tlayoutCons.gridy = GridBagConstraints.RELATIVE;
    tlayoutCons.gridwidth = 1;
    tlayoutCons.gridheight = 1;
    tlayoutCons.fill = GridBagConstraints.NONE;
    tlayoutCons.insets = new Insets(1,1,1,1);
    tlayoutCons.anchor = GridBagConstraints.NORTH;
    tlayoutCons.weightx = 0.0;
    tlayoutCons.weighty = 0.0;
    tlayout.setConstraints(upButton, tlayoutCons);
    toolBar.add(upButton);

    forwardButton = new JButton(new ImageIcon(GraphEditorHelpWindow.class.getResource("/images/Forward.gif")));
    forwardButton.setEnabled(false);
    forwardButton.setToolTipText("Move Forward to a Recently Viewed Page");
    forwardButton.addActionListener(this);
    tlayoutCons.gridx = GridBagConstraints.RELATIVE;
    tlayoutCons.gridy = GridBagConstraints.RELATIVE;
    tlayoutCons.gridwidth = 1;
    tlayoutCons.gridheight = 1;
    tlayoutCons.fill = GridBagConstraints.NONE;
    tlayoutCons.insets = new Insets(1,1,1,1);
    tlayoutCons.anchor = GridBagConstraints.NORTH;
    tlayoutCons.weightx = 0.0;
    tlayoutCons.weighty = 0.0;
    tlayout.setConstraints(forwardButton, tlayoutCons);
    toolBar.add(forwardButton);

    pageList = new DoublyLinkedList();

    getContentPane().setLayout(new BorderLayout());

    getContentPane().add(toolBar, BorderLayout.NORTH);

    buildContentsTree();
    contentsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    contentsTree.addTreeSelectionListener(this);

    contentPane = new JEditorPane();
    contentPane.setEditable(false);
    contentPane.addHyperlinkListener(this);
    try
    {
      contentPane.setPage(GraphEditorHelpWindow.class.getResource("/help/index.htm"));
    }
    catch (Exception e)
    {
      System.out.println("Couldn't create help URL: " + "/help/index.htm");
    }
    pageList.enqueue((LinkAndDescription)((DefaultMutableTreeNode)contentsTree.
      getModel().getRoot()).getUserObject());

    JScrollPane scrollPane1 = new JScrollPane(contentsTree,
                                             JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                             JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    JScrollPane scrollPane2 = new JScrollPane(contentPane,
                                             JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                             JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    JSplitPane hSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
      scrollPane1, scrollPane2 );
    hSplitPane.setOneTouchExpandable(true);
    hSplitPane.setResizeWeight(0.3);

    getContentPane().add(hSplitPane, BorderLayout.CENTER);

    addInternalFrameListener(controller);

    setSize(WIDTH, HEIGHT);
    setVisible(true);
  }

  private void buildContentsTree()
  {
    try
    {
      DefaultMutableTreeNode root, nodeOne, nodeTwo, nodeThree;
      root = new DefaultMutableTreeNode(new LinkAndDescription(GraphEditorHelpWindow.class.getResource("/help/index.htm"), "JGraphEd Help Contents"));

      nodeOne = new DefaultMutableTreeNode(new LinkAndDescription(GraphEditorHelpWindow.class.getResource("/help/using.htm"), "Using JGraphEd"));
      root.add(nodeOne);
      nodeTwo = new DefaultMutableTreeNode(new LinkAndDescription(GraphEditorHelpWindow.class.getResource("/help/commands.htm"), "Commands - ToolBar Button and Menu Items"));
      nodeOne.add(nodeTwo);
      nodeTwo = new DefaultMutableTreeNode(new LinkAndDescription(GraphEditorHelpWindow.class.getResource("/help/modes.htm"), "Modes of Operation"));
      nodeOne.add(nodeTwo);
      nodeThree = new DefaultMutableTreeNode(new LinkAndDescription(GraphEditorHelpWindow.class.getResource("/help/edit.htm"), "Edit Mode"));
      nodeTwo.add(nodeThree);
      nodeThree = new DefaultMutableTreeNode(new LinkAndDescription(GraphEditorHelpWindow.class.getResource("/help/move.htm"), "Move Mode"));
      nodeTwo.add(nodeThree);
      nodeThree = new DefaultMutableTreeNode(new LinkAndDescription(GraphEditorHelpWindow.class.getResource("/help/rotate.htm"), "Rotate Mode"));
      nodeTwo.add(nodeThree);
      nodeThree = new DefaultMutableTreeNode(new LinkAndDescription(GraphEditorHelpWindow.class.getResource("/help/resize.htm"), "Resize Mode"));
      nodeTwo.add(nodeThree);
      nodeTwo = new DefaultMutableTreeNode(new LinkAndDescription(GraphEditorHelpWindow.class.getResource("/help/preferences.htm"), "Preferences"));
      nodeOne.add(nodeTwo);

      nodeOne = new DefaultMutableTreeNode(new LinkAndDescription(GraphEditorHelpWindow.class.getResource("/help/about.htm"), "About JGraphEd"));
      // more to come here...
      root.add(nodeOne);

      contentsTree = new JTree(root);
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
  }

  public void hyperlinkUpdate(HyperlinkEvent e)
  {
    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
    {
      try
      {
        contentsTree.getSelectionModel().setSelectionPath(pathForLink(e.getURL()));
        // what about outside links?
      }
      catch ( Exception ex )
      {
        ex.printStackTrace(); // FIXME display error message
      }
    }
  }

  public void valueChanged(TreeSelectionEvent e)
  {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
      contentsTree.getLastSelectedPathComponent();
    if (node != null)
    {
      LinkAndDescription linkAndDesc = (LinkAndDescription)node.getUserObject();
      if ( linkAndDesc.getURL() != null )
      {
        try
        {
          if ( !navigation )
          {
            pageList.enqueueAfterCurrent(linkAndDesc);
          }
          else
          {
            navigation = false;
          }
          contentPane.setPage(linkAndDesc.getURL());
          update();
        }
        catch ( java.io.IOException ioe )
        {
          ioe.printStackTrace();
        }
      }
    }
  }

  private TreePath pathForLink(URL url)
  {
    TreeNode treeNode = nodeForLink(url);
    return new TreePath(((DefaultTreeModel)contentsTree.getModel()).getPathToRoot(treeNode));
  }

  private TreeNode nodeForLink(URL url)
  {
    return findNodeWithLink((DefaultMutableTreeNode)contentsTree.getModel().getRoot(), url);
  }

  private TreeNode findNodeWithLink(DefaultMutableTreeNode parentNode, URL url)
  {
    if ( parentNode.getUserObject().equals(url) )
    {
      return parentNode;
    }
    else
    {
      TreeNode childNode;
      for ( int i=0; i<parentNode.getChildCount(); i++ )
      {
        childNode = findNodeWithLink( (DefaultMutableTreeNode)parentNode.getChildAt(i), url );
        if ( childNode != null )
        {
          return childNode;
        }
      }
      return null;
    }
  }

  public void actionPerformed(ActionEvent e)
  {
    if ( e.getSource() == backButton )
    {
      if ( pageList.hasPrev() )
      {
        navigation = true;
        pageList.toPrev();
        LinkAndDescription lad = (LinkAndDescription)pageList.getCurrent();
        contentsTree.getSelectionModel().setSelectionPath(pathForLink(lad.getURL()));
      }
    }
    else if ( e.getSource() == forwardButton )
    {
      if ( pageList.hasNext() )
      {
        navigation = true;
        pageList.toNext();
        LinkAndDescription lad = (LinkAndDescription)pageList.getCurrent();
        contentsTree.getSelectionModel().setSelectionPath(pathForLink(lad.getURL()));
      }
    }
    else if ( e.getSource() == upButton )
    {
      LinkAndDescription lad = (LinkAndDescription)
        ((DefaultMutableTreeNode)((DefaultMutableTreeNode)contentsTree.
        getLastSelectedPathComponent()).getParent()).getUserObject();
      contentsTree.getSelectionModel().setSelectionPath(
        pathForLink(lad.getURL()));
    }
  }

  private void update()
  {
    backButton.setEnabled ( pageList.hasPrev() );
    forwardButton.setEnabled( pageList.hasNext() );
    upButton.setEnabled( !((DefaultMutableTreeNode)
      contentsTree.getLastSelectedPathComponent()).isRoot() );
  }
}

class LinkAndDescription
{
  private String title;
  private URL url;

  public LinkAndDescription(URL url, String title)
  {
    this.title = title;
    this.url = url;
  }

  public String getTitle() { return title; }
  public URL getURL() { return url; }

  public boolean equals(Object o)
  {
    if ( url == null )
    {
      return false;
    }
    return url.equals(o);
  }

  public String toString() { return title; }
}