package userInterface;


import javax.swing.*;
import java.awt.*;
import graphStructure.*;

public class GraphEditorWindow extends JGraphEdInternalFrame
{
  public static int WIDTH = 1150;//400;//SET MAX, see below:setSize
  public static int HEIGHT = 750;//400;

  private GraphController graphController;
  private GraphEditor graphEditor;
  private static int newCount = 0;
  private GraphEditorDialog ged;
  private GraphEditorInfoWindow infoWindow;
  private GraphEditorLogWindow logWindow;

  public GraphEditorWindow(GraphController graphController, Graph graph)
  {
    super(graphController, graph.getFileName(),
          true, //resizable
          true, //closable
          true, //maximizable
          true);//iconifiable
    if ( graph.getLabel().length() == 0 )
    {
      setTitle(graph.getFileName());
    }
    init(graphController);
    graphEditor.setGraph(graph);
  }

  public GraphEditorWindow(GraphController graphController)
  {
    super(graphController, "Untitled " + ++newCount,
          true, //resizable
          true, //closable
          true, //maximizable
          true);//iconifiable
    init(graphController);
  }

  private void init(GraphController graphController)
  {
    this.graphController = graphController;
    infoWindow = new GraphEditorInfoWindow(graphController, this);
    logWindow = new GraphEditorLogWindow(graphController, this);
    graphEditor = new GraphEditor(graphController, infoWindow, logWindow);
    ged = null;

    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints layoutCons = new GridBagConstraints();
    getContentPane().setLayout(layout);

    JScrollPane scrollPane = new JScrollPane(graphEditor,
                                             JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                             JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = GridBagConstraints.REMAINDER;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(scrollPane, layoutCons);
    getContentPane().add(scrollPane);

    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addInternalFrameListener(graphController);

  //  setSize(WIDTH, HEIGHT);
    
    setSize(graphController.getGraphWindow().getWidth(), graphController.getGraphWindow().getHeight());
    setVisible(true);
  }

  public void dispose()
  {
    graphEditor.prepareForClose();
    super.dispose();
  }

  public GraphEditor getGraphEditor()
  {
    return graphEditor;
  }

  public GraphEditorDialog getDialog() { return ged; }

  public void setDialog(GraphEditorDialog d) { ged = d; }

  public GraphEditorInfoWindow getInfoWindow()
  {
    return infoWindow;
  }
  
  public GraphEditorLogWindow getLogWindow()
  {
    return logWindow;
  }  
}