package userInterface;


import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.event.*;
import java.io.*;
import java.util.Vector;
import graphStructure.*;
import graphException.*;
import operation.*;
import userInterface.fileUtils.*;
import userInterface.menuAndToolBar.*;

public class GraphController implements InternalFrameListener
{
  private static String DEFAULT_RANDOM_NODES = "10";

  private GraphWindow graphWindow;
  private GraphEditorWindow activeGraphEditorWindow;
  private GraphEditorDialog activeGraphEditorDialog;
  private MenuAndToolBar menuAndToolBar;
  private boolean otsResultAvailable = false;
  private GraphEditorWindow otsResultWindow = null;
  private Graph otsResultGraph = null;
  private boolean isApplication;
  private JInternalFrame lastWindow;
  private int lastShownIndex;
  private boolean drawOnEmbedding;
  private boolean clearGenerated;

  public GraphController(boolean isApplication)
  {
    this.isApplication = isApplication;
    activeGraphEditorWindow = null;
    activeGraphEditorDialog = null;
    lastWindow = null;
    lastShownIndex = 0;
    drawOnEmbedding = true;
    clearGenerated = false;
    menuAndToolBar = new MenuAndToolBar(this);
    graphWindow = new GraphWindow();
  }

  public void setDrawOnEmbedding(boolean drawEmbed) { drawOnEmbedding = drawEmbed; }

  public boolean getDrawOnEmbedding() { return drawOnEmbedding; }

  public void toggleDrawOnEmbedding() { drawOnEmbedding = !drawOnEmbedding; }

  public void setClearGenerated(boolean clearGen) { clearGenerated = clearGen; }

  public boolean getClearGenerated() { return clearGenerated; }

  public void toggleClearGenerated() { clearGenerated = !clearGenerated; }

  public GraphWindow getGraphWindow() { return graphWindow; }

  public GraphEditorWindow getActiveGraphEditor() { return activeGraphEditorWindow; }

  public boolean isApplication() { return isApplication; }

  public void updateCursorLocation(Point cursorPoint)
  {
    menuAndToolBar.updateCursorLocation(cursorPoint);
  }

  public JToolBar getToolBar()
  {
    return menuAndToolBar.getToolBar();
  }

  public JMenuBar getMenuBar()
  {
    return menuAndToolBar.getMenuBar();
  }

  private void refreshOTSMenuVisibility()
  {
    if ( menuAndToolBar == null )
    {
      return;
    }

    if ( otsResultAvailable &&
         activeGraphEditorWindow != null &&
         activeGraphEditorWindow == otsResultWindow &&
         otsResultGraph != null )
    {
      menuAndToolBar.showSaveOTS();
      menuAndToolBar.setSaveOTSEnabled(true);
    }
    else
    {
      menuAndToolBar.setSaveOTSEnabled(false);
      menuAndToolBar.hideSaveOTS();
    }
  }

  public void internalFrameOpened(InternalFrameEvent e)
  {
    menuAndToolBar.addWindow(((JGraphEdInternalFrame)e.getSource()).getMenuItem());
  }

  public void internalFrameClosed(InternalFrameEvent e)
  {
    menuAndToolBar.removeWindow(((JGraphEdInternalFrame)e.getSource()).getMenuItem());
    lastWindow = null;

    if ( e.getSource() instanceof GraphEditorWindow )
    {
      GraphEditorWindow gew = (GraphEditorWindow)e.getSource();
      if ( gew.getDialog() != null )
      {
        GraphEditorDialog ged = gew.getDialog();
        gew.setDialog(null);
        ged.setOwner(null);
        graphWindow.close(ged);
      }
      if ( gew.getInfoWindow().isVisible() )
      {
        graphWindow.closeInfo(gew.getInfoWindow());
      }
      if ( gew.getLogWindow().isVisible() )
      {
        graphWindow.closeLog(gew.getLogWindow());
      }
    }
    else if ( e.getSource() instanceof GraphEditorDialog )
    {
      GraphEditorDialog ged = (GraphEditorDialog)e.getSource();
      if ( ged.getOwner() != null )
      {
        ged.getOwner().getGraphEditor().allowNodeSelection(0);
        ged.getOwner().setDialog(null);
        graphWindow.activate(ged.getOwner());
      }
    }
  }

  public void internalFrameActivated(InternalFrameEvent e)
  {
    if ( e.getSource() instanceof GraphEditorWindow )
    {
      activeGraphEditorWindow = (GraphEditorWindow)e.getSource();
      if ( activeGraphEditorWindow.getDialog() != null &&
           activeGraphEditorWindow.getDialog() != lastWindow )
      {
        lastWindow = activeGraphEditorWindow;
        graphWindow.activateDialog(activeGraphEditorWindow.getDialog());
      }
      else
      {
        menuAndToolBar.showControls( activeGraphEditorWindow.getGraphEditor() );
        refreshOTSMenuVisibility();
        lastWindow = activeGraphEditorWindow;
      }
    }
    else
    {
      if ( e.getSource() instanceof GraphEditorDialog )
      {
        activeGraphEditorDialog = (GraphEditorDialog)e.getSource();
        if ( lastWindow != activeGraphEditorDialog.getOwner() )
        {
          graphWindow.activate(activeGraphEditorDialog.getOwner());
          activeGraphEditorDialog = (GraphEditorDialog)e.getSource();
          graphWindow.activateDialog(activeGraphEditorDialog);
        }
        lastWindow = activeGraphEditorDialog;
      }
      menuAndToolBar.hideControls();
      refreshOTSMenuVisibility();
    }
  }

  public void internalFrameDeactivated(InternalFrameEvent e)
  {
    menuAndToolBar.hideControls();
    refreshOTSMenuVisibility();
    if ( e.getSource() instanceof GraphEditorWindow &&
         activeGraphEditorWindow == (GraphEditorWindow)e.getSource() )
    {
      activeGraphEditorWindow = null;
    }
    if ( e.getSource() instanceof GraphEditorDialog &&
         activeGraphEditorDialog == (GraphEditorDialog)e.getSource() )
    {
      activeGraphEditorDialog = null;
    }
  }

  public void internalFrameClosing(InternalFrameEvent e)
  {
    if ( e.getSource() instanceof GraphEditorWindow )
    {
      GraphEditorWindow editorWindow = (GraphEditorWindow)e.getSource();
      if ( editorWindow != null )
      {
        if ( editorWindow.getGraphEditor().getGraph().hasChangedSinceLastSave() )
        {
          int returnInt = JOptionPane.showConfirmDialog(graphWindow,
                          "Are you sure you want to discard all changes to " +
                          editorWindow.getTitle() + "?",
                          "Discard Changes", JOptionPane.YES_NO_OPTION);
          if ( returnInt == JOptionPane.YES_OPTION )
          {
            editorWindow.dispose();
          }
        }
        else
        {
          editorWindow.dispose();
        }
      }
    }
  }

  public void internalFrameDeiconified(InternalFrameEvent e) { }

  public void internalFrameIconified(InternalFrameEvent e) { }

  public MenuAndToolBar getMenuAndToolBar() { return menuAndToolBar; }

  public void showWindow( JGraphEdInternalFrame intFrame )
  {
    graphWindow.activate( intFrame );
  }

  public boolean hasUnsavedGraphs()
  {
    Vector graphEditorWindows = graphWindow.getAllGraphEditorWindows();
    for ( int i=0; i<graphEditorWindows.size(); i++ )
    {
      if ( ((GraphEditorWindow)graphEditorWindows.elementAt(i)).getGraphEditor().getGraph().hasChangedSinceLastSave() )
      {
        return true;
      }
    }
    return false;
  }

  public void nodesSelectedByEditor(int numNodes, int maxNodes)
  {
    if ( activeGraphEditorWindow != null &&
         activeGraphEditorWindow.getDialog() != null )
    {
      GraphEditorDialog ged = (GraphEditorDialog)activeGraphEditorWindow.getDialog();
      if ( numNodes == maxNodes )
      {
        ged.enableRunButton();
        activeGraphEditorWindow.getGraphEditor().repaint();
        graphWindow.activateDialog(ged);
      }
      else
      {
        ged.disableRunButton();
        activeGraphEditorWindow.getGraphEditor().repaint();
      }
    }
  }

  public void update()
  {
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().updateShapes();
    }
    else if ( activeGraphEditorDialog != null )
    {
      activeGraphEditorDialog.getOwner().getGraphEditor().updateShapes();
    }
    graphWindow.repaint();
  }

  public void newGraph()
  {
    resetOTSCommandState();
    graphWindow.addGraphEditorWindow(this);
  }

  public void loadGraph()
  {
    resetOTSCommandState();
    JFileChooser fc = new JFileChooser();
    fc.addChoosableFileFilter(new GraphFilter());
    fc.setFileView(new GraphFileView());
    fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
    int returnVal = fc.showDialog(graphWindow, "Load GRAPH File");

    if (returnVal == JFileChooser.APPROVE_OPTION)
    {
      String loadGraphPath = "";
      try
      {
        loadGraphPath = fc.getSelectedFile().getCanonicalPath();
        BufferedReader aReader = new BufferedReader(new FileReader(loadGraphPath));
        Graph graph = Graph.loadFrom(aReader);
        graph.setFilePath(loadGraphPath);
        aReader.close();
        graphWindow.addGraphEditorWindow(this, graph);
      }
      catch(java.io.IOException ioe)
      {
        JOptionPane.showMessageDialog(graphWindow, "The file you selected does not appear to be a valid GRAPH file.",
                                      "Unable to load graph file", JOptionPane.ERROR_MESSAGE);
        //ioe.printStackTrace();
      }
      catch(Exception ex)
      {
        JOptionPane.showMessageDialog(graphWindow, loadGraphPath + " does not appear to be a valid GRAPH file.",
                                      "Unable to load graph file", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
      }
    }
  }

  public void saveGraph()
  {
    if ( activeGraphEditorWindow != null )
    {
      JFileChooser fc = new JFileChooser();
      GraphFilter graphFilter = new GraphFilter();
      ImageFilter imageFilter = new ImageFilter();
      fc.addChoosableFileFilter(graphFilter);
      fc.addChoosableFileFilter(imageFilter);
      fc.setFileFilter(graphFilter);
      fc.setFileView(new GraphFileView());
      fc.setCurrentDirectory(new File(System.getProperty("user.dir")));

      int returnVal = fc.showDialog(graphWindow, "Save Current Graph to File");

      if (returnVal == JFileChooser.APPROVE_OPTION)
      {
        String savePath = "";
        try
        {
          savePath = fc.getSelectedFile().getCanonicalPath();
          String fileExt = Utils.getExtension(fc.getSelectedFile());
          if ( fc.getFileFilter() == graphFilter )
          {
            if ( fileExt == null )
            {
              savePath = savePath + ".graph";
              fileExt = Utils.graph;
            }
            if ( fileExt != null && fileExt.equalsIgnoreCase(Utils.graph) )
            {
              File aFile = new File(savePath);
              if ( aFile.exists() )
              {
                int returnInt = JOptionPane.showConfirmDialog(graphWindow,
                  "Do you wish to Overwrite the file " + savePath + "?",
                  "File Already Exists", JOptionPane.YES_NO_OPTION);
                if ( returnInt == JOptionPane.YES_OPTION )
                {
                  PrintWriter aWriter = new PrintWriter(new FileWriter(savePath));
                  activeGraphEditorWindow.getGraphEditor().getGraph().saveTo(aWriter);
                  aWriter.close();
                }
              }
              else
              {
                PrintWriter aWriter = new PrintWriter(new FileWriter(savePath));
                activeGraphEditorWindow.getGraphEditor().getGraph().saveTo(aWriter);
                aWriter.close();
              }
            }
            else
            {
              JOptionPane.showMessageDialog(graphWindow, "Graphs may only be saved as .graph files.",
                                            "Unable to Save Graph", JOptionPane.ERROR_MESSAGE);
            }
          }
          else if ( fc.getFileFilter() == imageFilter )
          {
            if (fileExt != null && (fileExt.equalsIgnoreCase(Utils.gif) || fileExt.equalsIgnoreCase(Utils.jpg) || fileExt.equalsIgnoreCase(Utils.jpeg)))
            {
              File aFile = new File(savePath);
              if ( aFile.exists() )
              {
                int returnInt = JOptionPane.showConfirmDialog(graphWindow,
                  "Do you wish to Overwrite the file " + savePath + "?",
                  "File Already Exists", JOptionPane.YES_NO_OPTION);
                if ( returnInt == JOptionPane.YES_OPTION )
                {
                  activeGraphEditorWindow.getGraphEditor().saveImage(savePath);
                }
              }
              else
              {
                activeGraphEditorWindow.getGraphEditor().saveImage(savePath);
              }
            }
            else
            {
              JOptionPane.showMessageDialog(graphWindow, "Images may only be saved as .gif or .jpg or .jpeg files.",
                                            "Unable to Save Graph Image", JOptionPane.ERROR_MESSAGE);
            }
          }
        }
        catch(java.io.IOException ioe)
        {
          JOptionPane.showMessageDialog(graphWindow, "Unable to write to the selected file.",
                                        "Unable to Save Graph", JOptionPane.ERROR_MESSAGE);
          //ioe.printStackTrace();
        }
      }
    }
  }

  public void closeGraph()
  {
    resetOTSCommandState();
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.doDefaultCloseAction();
    }
  }

  public void preferences()
  {
    graphWindow.show(new GraphEditorPreferencesWindow(this));
  }

  public void help()
  {
    graphWindow.show(new GraphEditorHelpWindow(this));
  }

  public void info()
  {
    graphWindow.showInfo(activeGraphEditorWindow);
  }

  public void log()
  {
    graphWindow.showLog(activeGraphEditorWindow);
  }

  public void editMode()
  {
    resetOTSCommandState();
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().changeToEditMode();
      menuAndToolBar.showControls(activeGraphEditorWindow.getGraphEditor());
    }
  }

  public void gridMode()
  {
    resetOTSCommandState();
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().changeToGridMode();
      menuAndToolBar.showControls(activeGraphEditorWindow.getGraphEditor());
    }
  }
  public void kruinGridMode()
  {
    resetOTSCommandState();
    if ( activeGraphEditorWindow != null )
    {
   activeGraphEditorWindow.getGraphEditor().changeToKruinGridMode(40/*rows*/,20/*height*/,30,20);



      menuAndToolBar.showControls(activeGraphEditorWindow.getGraphEditor());
    }
  }
  public void moveMode()
  {
    resetOTSCommandState();
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().changeToMoveMode();
      menuAndToolBar.showControls(activeGraphEditorWindow.getGraphEditor());
      graphWindow.repaint();
    }
  }

  public void rotateMode()
  {
    resetOTSCommandState();
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().changeToRotateMode();
      menuAndToolBar.showControls(activeGraphEditorWindow.getGraphEditor());
    }
  }

  public void resizeMode()
  {
    resetOTSCommandState();
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().changeToResizeMode();
      menuAndToolBar.showControls(activeGraphEditorWindow.getGraphEditor());
    }
  }

  public void toggleUndo()
  {
    if ( activeGraphEditorWindow != null )
    {
      Graph graph = activeGraphEditorWindow.getGraphEditor().getGraph();
      if ( graph.getTrackUndos() )
      {
        graph.setTrackUndos(false);
      }
      else
      {
        graph.setTrackUndos(true);
      }
      menuAndToolBar.updateUndo(activeGraphEditorWindow.getGraphEditor().getGraph());
    }
  }

  public void undo()
  {
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().undo();
      newUndo();
      activeGraphEditorWindow.getGraphEditor().update();
    }
  }

  public void redo()
  {
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().redo();
      newUndo();
      activeGraphEditorWindow.getGraphEditor().update();
    }
  }

  public void newUndo()
  {
    if ( activeGraphEditorWindow != null )
    {
      menuAndToolBar.updateUndo(activeGraphEditorWindow.getGraphEditor().getGraph());
    }
  }

  public void unselectAll()
  {
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().getGraph().unselectAll();
      activeGraphEditorWindow.getGraphEditor().repaint();
    }
  }

  public void removeSelected()
  {
    resetOTSCommandState();
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Remove Selected");
      activeGraphEditorWindow.getGraphEditor().getGraph().deleteSelected();
      activeGraphEditorWindow.getGraphEditor().getGraph().doneMemento();
      newUndo();
      activeGraphEditorWindow.getGraphEditor().update();
    }
  }

  public void removeAll()
  {
    resetOTSCommandState();
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Remove All");
      activeGraphEditorWindow.getGraphEditor().getGraph().deleteAll();
      activeGraphEditorWindow.getGraphEditor().getGraph().doneMemento();
      newUndo();
      activeGraphEditorWindow.getGraphEditor().update();
    }
  }

  public void removeGenerated()
  {
    resetOTSCommandState();
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Remove Generated Edges");
      activeGraphEditorWindow.getGraphEditor().getGraph().deleteGeneratedEdges();
      activeGraphEditorWindow.getGraphEditor().getGraph().doneMemento();
      newUndo();
      activeGraphEditorWindow.getGraphEditor().update();
    }
  }

  public void preserveGenerated()
  {
    resetOTSCommandState();
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Make Generated Edges Permanent");
      activeGraphEditorWindow.getGraphEditor().getGraph().makeGeneratedEdgesPermanent();
      activeGraphEditorWindow.getGraphEditor().getGraph().doneMemento();
      newUndo();
      activeGraphEditorWindow.getGraphEditor().update();
    }
  }

  public void testConnectivity()
  {
    if ( activeGraphEditorWindow != null )
    {
      if ( !activeGraphEditorWindow.getGraphEditor().getGraph().hasNodes() )
      {
        JOptionPane.showMessageDialog(graphWindow, "The Graph Has No Nodes",
                                      "Connectivity Test Results", JOptionPane.INFORMATION_MESSAGE);
      }
      else if ( ConnectivityOperation.isConnected(activeGraphEditorWindow.getGraphEditor().getGraph()) )
      {
        JOptionPane.showMessageDialog(graphWindow, "The Graph Is Connected",
                                      "Connectivity Test Results", JOptionPane.INFORMATION_MESSAGE);
      }
      else
      {
        JOptionPane.showMessageDialog(graphWindow, "The Graph Is Not Connected",
                                      "Connectivity Test Results", JOptionPane.INFORMATION_MESSAGE);
      }
    }
  }

  public void testBiconnectivity()
  {
    if ( activeGraphEditorWindow != null )
    {
      if ( !activeGraphEditorWindow.getGraphEditor().getGraph().hasNodes() )
      {
        JOptionPane.showMessageDialog(graphWindow, "The Graph Has No Nodes",
                                      "Biconnectivity Test Results", JOptionPane.INFORMATION_MESSAGE);
      }
      else if ( BiconnectivityOperation.isBiconnected(activeGraphEditorWindow.getGraphEditor().getGraph()) )
      {
        JOptionPane.showMessageDialog(graphWindow, "The Graph Is Biconnected",
                                      "Biconnectivity Test Results", JOptionPane.INFORMATION_MESSAGE);
      }
      else
      {
        JOptionPane.showMessageDialog(graphWindow, "The Graph Is Not Biconnected",
                                      "Biconnectivity Test Results", JOptionPane.INFORMATION_MESSAGE);
      }
    }
  }

  public void testPlanarity()
  {
    if ( activeGraphEditorWindow != null )
    {
      if ( !activeGraphEditorWindow.getGraphEditor().getGraph().hasNodes() )
      {
        JOptionPane.showMessageDialog(graphWindow, "The Graph Has No Nodes.",
                                      "Planarity Test Results", JOptionPane.INFORMATION_MESSAGE);
      }
      else if ( PlanarityOperation.isPlanar(activeGraphEditorWindow.getGraphEditor().getGraph()) )
      {
        JOptionPane.showMessageDialog(graphWindow, "The Graph Is Planar",
                                      "Planarity Test Results", JOptionPane.INFORMATION_MESSAGE);
      }
      else
      {
        JOptionPane.showMessageDialog(graphWindow, "The Graph Is Not Planar",
                                      "Planarity Test Results", JOptionPane.INFORMATION_MESSAGE);
      }
    }
  }

  public void createRandom()
  {
    if ( activeGraphEditorWindow != null )
    {
      try
      {
        String numberString = (String)JOptionPane.showInputDialog(
                                      graphWindow,
                                      "Enter the number of random nodes to create",
                                      "Create X Random Nodes",
                                      JOptionPane.PLAIN_MESSAGE,
                                      null,
                                      null,
                                      DEFAULT_RANDOM_NODES);
        try
        {
          if ( numberString != null )
          {
            int number = Integer.parseInt(numberString);
            if ( number < 0 )
            {
              throw new NumberFormatException("Value must be positive");
            }
            activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Create Random");
            CreateRandomGraphOperation.createRandomNodes(activeGraphEditorWindow.getGraphEditor().getGraph(),
              number,
              activeGraphEditorWindow.getGraphEditor().getDrawWidth(),
              activeGraphEditorWindow.getGraphEditor().getDrawHeight());
            activeGraphEditorWindow.getGraphEditor().getGraph().doneMemento();
            newUndo();
            activeGraphEditorWindow.getGraphEditor().update();
          }
        }
        catch ( NumberFormatException nfe )
        {
          JOptionPane.showMessageDialog(graphWindow,
                                        "Please enter a positive whole number for the number of random nodes to create",
                                        "Invalid Input",
                                        JOptionPane.ERROR_MESSAGE);
        }
      }
      catch( Exception e )
      {
        JOptionPane.showMessageDialog(graphWindow, e.getMessage(),
                                      "Error During Create Random Operation", JOptionPane.ERROR_MESSAGE);
        if ( !(e instanceof GraphException) )
        {
          e.printStackTrace();
        }
      }
    }
  }

  public void embedding()
  {
    if ( activeGraphEditorWindow != null )
    {
      try
      {
        activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Embed");
        EmbedOperation.embed(activeGraphEditorWindow.getGraphEditor().getGraph());
        activeGraphEditorWindow.getGraphEditor().getGraph().doneMemento();
        newUndo();
        activeGraphEditorWindow.getGraphEditor().repaint();
        JOptionPane.showMessageDialog(graphWindow, "Hold down Control (or Control-Shift) and click on a Node or Edge",
                                      "Embedding", JOptionPane.INFORMATION_MESSAGE);
      }
      catch( Exception e )
      {
        activeGraphEditorWindow.getGraphEditor().getGraph().abortMemento();
        JOptionPane.showMessageDialog(graphWindow, e.getMessage(),
                                      "Error During Embedding Operation", JOptionPane.ERROR_MESSAGE);
        if ( !(e instanceof GraphException) )
        {
          e.printStackTrace();
        }
      }
    }
  }

  public void makeConnected()
  {
    if ( activeGraphEditorWindow != null )
    {
      try
      {
        activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Make Connected");
        ConnectivityOperation.makeConnected(activeGraphEditorWindow.getGraphEditor().getGraph());
        activeGraphEditorWindow.getGraphEditor().getGraph().doneMemento();
        newUndo();
        activeGraphEditorWindow.getGraphEditor().update();
      }
      catch( Exception e )
      {
        activeGraphEditorWindow.getGraphEditor().getGraph().abortMemento();
        JOptionPane.showMessageDialog(graphWindow, e.getMessage(),
                                      "Error During Make Connected Operation", JOptionPane.ERROR_MESSAGE);
        if ( !(e instanceof GraphException) )
        {
          e.printStackTrace();
        }
      }
    }
  }

  public void makeBiconnected()
  {
    if ( activeGraphEditorWindow != null )
    {
      try
      {
        activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Make Biconnected");
        BiconnectivityOperation.makeBiconnected(activeGraphEditorWindow.getGraphEditor().getGraph());
        activeGraphEditorWindow.getGraphEditor().getGraph().doneMemento();
        newUndo();
        activeGraphEditorWindow.getGraphEditor().update();
      }
      catch( Exception e )
      {
        activeGraphEditorWindow.getGraphEditor().getGraph().abortMemento();
        JOptionPane.showMessageDialog(graphWindow, e.getMessage(),
                                      "Error During Make Biconnected Operation", JOptionPane.ERROR_MESSAGE);
        if ( !(e instanceof GraphException) )
        {
          e.printStackTrace();
        }
      }
    }
  }

  public void makeMaximal()
  {
    if ( activeGraphEditorWindow != null )
    {
      try
      {
        activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Make Maximal");
        MakeMaximalOperation.makeMaximal(activeGraphEditorWindow.getGraphEditor().getGraph());
        activeGraphEditorWindow.getGraphEditor().getGraph().doneMemento();
        newUndo();
        activeGraphEditorWindow.getGraphEditor().update();
      }
      catch( Exception e )
      {
        activeGraphEditorWindow.getGraphEditor().getGraph().abortMemento();
        JOptionPane.showMessageDialog(graphWindow, e.getMessage(),
                                      "Error During Make Maximal Operation", JOptionPane.ERROR_MESSAGE);
        if ( !(e instanceof GraphException) )
        {
          e.printStackTrace();
        }
      }
    }
  }

  public void straightLineEmbed()
  {
    if ( activeGraphEditorWindow != null )
    {
      try
      {
        activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Straight Line Embed");
        SchnyderEmbeddingOperation.straightLineGridEmbed(
          activeGraphEditorWindow.getGraphEditor().getGraph(),
          activeGraphEditorWindow.getGraphEditor().getDrawWidth(),
          activeGraphEditorWindow.getGraphEditor().getDrawHeight() );
        if ( clearGenerated )
        {
          activeGraphEditorWindow.getGraphEditor().getGraph().deleteGeneratedEdges();
        }
        activeGraphEditorWindow.getGraphEditor().getGraph().doneMemento();
        newUndo();
        activeGraphEditorWindow.getGraphEditor().setPreferredSize();
        activeGraphEditorWindow.getGraphEditor().update();
      }
      catch( Exception e )
      {
        activeGraphEditorWindow.getGraphEditor().getGraph().abortMemento();
        JOptionPane.showMessageDialog(graphWindow, e.getMessage(),
                                      "Error During Straight Line Embedding Operation", JOptionPane.ERROR_MESSAGE);
        if ( !(e instanceof GraphException) )
        {
          e.printStackTrace();
        }
      }
    }
  }

  public void showCoords()
  {
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().getGraph().setShowCoords(true);
      activeGraphEditorWindow.getGraphEditor().getGraph().setShowLabels(false);
      menuAndToolBar.showControls( activeGraphEditorWindow.getGraphEditor() );
      activeGraphEditorWindow.repaint();
    }
  }

  public void showLabels()
  {
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().getGraph().setShowCoords(false);
      activeGraphEditorWindow.getGraphEditor().getGraph().setShowLabels(true);
      menuAndToolBar.showControls( activeGraphEditorWindow.getGraphEditor() );
      activeGraphEditorWindow.repaint();
    }
  }

  public void showNothing()
  {
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().getGraph().setShowCoords(false);
      activeGraphEditorWindow.getGraphEditor().getGraph().setShowLabels(false);
      menuAndToolBar.showControls( activeGraphEditorWindow.getGraphEditor() );
      activeGraphEditorWindow.repaint();
    }
  }

  public void resetDisplay()
  {
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Reset To Default Display");
      activeGraphEditorWindow.getGraphEditor().getGraph().resetColors(true);
      activeGraphEditorWindow.getGraphEditor().getGraph().doneMemento();
      newUndo();
      activeGraphEditorWindow.getGraphEditor().update();
    }
  }

  public void displayDFS()
  {
    if ( activeGraphEditorWindow != null )
    {
      showLabels();
      activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Display Depth First Search");
      DepthFirstSearchOperation.displayDepthFirstSearch(activeGraphEditorWindow.getGraphEditor().getGraph());
      activeGraphEditorWindow.getGraphEditor().getGraph().doneMemento();
      newUndo();
      activeGraphEditorWindow.getGraphEditor().update();
    }
  }

  public void displayBiconnected()
  {
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Display Biconnected Components");
      BiconnectivityOperation.displayBiconnectedComponents(activeGraphEditorWindow.getGraphEditor().getGraph());
      activeGraphEditorWindow.getGraphEditor().getGraph().doneMemento();
      newUndo();
      activeGraphEditorWindow.getGraphEditor().update();
    }
  }

  public void displayST()
  {
    if ( activeGraphEditorWindow != null )
    {
      try
      {
        showLabels();
        activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Display ST Numbering");
        STNumberOperation.displayStNumbering(activeGraphEditorWindow.getGraphEditor().getGraph());
        activeGraphEditorWindow.getGraphEditor().getGraph().doneMemento();
        newUndo();
        activeGraphEditorWindow.getGraphEditor().update();
      }
      catch ( Exception e )
      {
        activeGraphEditorWindow.getGraphEditor().getGraph().abortMemento();
        JOptionPane.showMessageDialog(graphWindow, e.getMessage(),
                                      "Error During ST Number Display", JOptionPane.ERROR_MESSAGE);
        if ( !(e instanceof GraphException) )
        {
          e.printStackTrace();
        }
      }
    }
  }

  public void displayCanonical()
  {
    if ( activeGraphEditorWindow != null )
    {
      try
      {
        activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Make Maximal");
        if ( ! MakeMaximalOperation.makeMaximal(activeGraphEditorWindow.getGraphEditor().getGraph()) )
        {
          EmbedOperation.embed(activeGraphEditorWindow.getGraphEditor().getGraph(), false);
        }
        activeGraphEditorWindow.getGraphEditor().update();

        if ( activeGraphEditorWindow.getDialog() != null )
        {
          graphWindow.close(activeGraphEditorWindow.getDialog());
        }
        SchnyderDialog ged = new SchnyderDialog( this,
        activeGraphEditorWindow,
        "Canonical Order Display",
        "Please Select 3 Nodes to Bound the Outer-Face/Triangle",
        true, true )
        {
          public void actionPerformed(ActionEvent e)
          {
              getOwner().getGraphEditor().getGraph().renameMemento("Display Canonical Ordering");
              displayCanonicalHelper(getOwner(), e.getSource() == getRandomButton(), getOnEmbedding());
          }

          public void dispose()
          {
            if ( getOwner() != null )
            {
              getOwner().getGraphEditor().getGraph().doneMemento();
              newUndo();
            }
            super.dispose();
          }
        };
        ged.disableRunButton();
        activeGraphEditorWindow.getGraphEditor().allowTriangleSelection();
        activeGraphEditorWindow.setDialog(ged);
        graphWindow.showDialog(ged);
      }
      catch ( Exception e )
      {
        if ( !(e instanceof GraphException) )
        {
          e.printStackTrace();
        }
        activeGraphEditorWindow.getGraphEditor().getGraph().abortMemento();
        JOptionPane.showMessageDialog(graphWindow, e.getMessage(),
                                      "Error During Canonical Order Display", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void displayCanonicalHelper(GraphEditorWindow editorWindow, boolean useRandom, boolean onEmbedding)
  {
    try
    {
      showLabels();
      Node triangleNodes[];
      if ( useRandom )
      {
        triangleNodes = editorWindow.getGraphEditor().getGraph().getRandomTriangularFace();
      }
      else
      {
        triangleNodes = editorWindow.getGraphEditor().getSpecialNodeSelections();
      }
      if ( onEmbedding )
      {
        int gridNum = editorWindow.getGraphEditor().getGraph().getNumNodes()-1;
        SchnyderEmbeddingOperation.displayCanonicalOrdering(
          editorWindow.getGraphEditor().getGraph(),
          triangleNodes[0], triangleNodes[1], triangleNodes[2],
          editorWindow.getGraphEditor().getDrawWidth(),
          editorWindow.getGraphEditor().getDrawHeight() );
      }
      else
      {
        CanonicalOrderOperation.displayCanonicalOrdering(
          editorWindow.getGraphEditor().getGraph(),
          triangleNodes[0], triangleNodes[1], triangleNodes[2]);
      }

      editorWindow.getGraphEditor().update();
      editorWindow.getGraphEditor().allowNodeSelection(0);
      graphWindow.close(editorWindow.getDialog());
      editorWindow.setDialog(null);
    }
    catch ( Exception e )
    {
      if ( !(e instanceof GraphException) )
      {
        e.printStackTrace();
      }
      activeGraphEditorWindow.getGraphEditor().getGraph().abortMemento();
      JOptionPane.showMessageDialog(graphWindow, e.getMessage(),
                                    "Error During Canonical Order Display", JOptionPane.ERROR_MESSAGE);
    }
  }

  public void displayNormal()
  {
    if ( activeGraphEditorWindow != null )
    {
      try
      {
        activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Make Maximal");
        if ( ! MakeMaximalOperation.makeMaximal(activeGraphEditorWindow.getGraphEditor().getGraph()) )
        {
          EmbedOperation.embed(activeGraphEditorWindow.getGraphEditor().getGraph(), false);
        }
        activeGraphEditorWindow.getGraphEditor().update();

        if ( activeGraphEditorWindow.getDialog() != null )
        {
          graphWindow.close(activeGraphEditorWindow.getDialog());
        }
        SchnyderDialog ged = new SchnyderDialog( this,
        activeGraphEditorWindow,
        "Normal Labeling Display",
        "Please Select 3 Nodes to Bound the Outer-Face/Triangle",
        true, true )
        {
          public void actionPerformed(ActionEvent e)
          {
            getOwner().getGraphEditor().getGraph().renameMemento("Display Normal Labeling");
            displayNormalHelper(getOwner(), e.getSource() == getRandomButton(), getOnEmbedding());
          }

          public void dispose()
          {
            if ( getOwner() != null )
            {
              getOwner().getGraphEditor().getGraph().doneMemento();
              newUndo();
            }
            super.dispose();
          }
        };
        ged.disableRunButton();
        activeGraphEditorWindow.getGraphEditor().allowTriangleSelection();
        activeGraphEditorWindow.setDialog(ged);
        graphWindow.showDialog(ged);
      }
      catch ( Exception e )
      {
        if ( !(e instanceof GraphException) )
        {
          e.printStackTrace();
        }
        activeGraphEditorWindow.getGraphEditor().getGraph().abortMemento();
        JOptionPane.showMessageDialog(graphWindow, e.getMessage(),
                                      "Error During Normal Label Display", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void displayNormalHelper(GraphEditorWindow editorWindow, boolean useRandom, boolean onEmbedding)
  {
    try
    {
      Node triangleNodes[];
      if ( useRandom )
      {
        triangleNodes = editorWindow.getGraphEditor().getGraph().getRandomTriangularFace();
      }
      else
      {
        triangleNodes = editorWindow.getGraphEditor().getSpecialNodeSelections();
      }
      if ( onEmbedding )
      {
        SchnyderEmbeddingOperation.displayNormalLabeling(
          editorWindow.getGraphEditor().getGraph(),
          triangleNodes[0], triangleNodes[1], triangleNodes[2],
          editorWindow.getGraphEditor().getDrawWidth(),
          editorWindow.getGraphEditor().getDrawHeight() );
      }
      else
      {
        NormalLabelOperation.displayNormalLabeling(
          editorWindow.getGraphEditor().getGraph(),
          triangleNodes[0], triangleNodes[1], triangleNodes[2]);
      }

      editorWindow.getGraphEditor().update();
      editorWindow.getGraphEditor().allowNodeSelection(0);
      graphWindow.close(editorWindow.getDialog());
      editorWindow.setDialog(null);
    }
    catch ( Exception e )
    {
      if ( !(e instanceof GraphException) )
      {
        e.printStackTrace();
      }
      activeGraphEditorWindow.getGraphEditor().getGraph().abortMemento();
      JOptionPane.showMessageDialog(graphWindow, e.getMessage(),
                                    "Error During Normal Label Display", JOptionPane.ERROR_MESSAGE);
    }
  }

  public void displaySchnyder()
  {
    if ( activeGraphEditorWindow != null )
    {
      try
      {
        activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Make Maximal");
        if ( ! MakeMaximalOperation.makeMaximal(activeGraphEditorWindow.getGraphEditor().getGraph()) )
        {
          EmbedOperation.embed(activeGraphEditorWindow.getGraphEditor().getGraph(), false);
        }
        activeGraphEditorWindow.getGraphEditor().update();

        if ( activeGraphEditorWindow.getDialog() != null )
        {
          graphWindow.close(activeGraphEditorWindow.getDialog());
        }
        SchnyderDialog ged = new SchnyderDialog( this,
        activeGraphEditorWindow,
        "Schnyder Embedding Display",
        "Please Select 3 Nodes to Bound the Outer-Face/Triangle",
        false, true )
        {
          public void actionPerformed(ActionEvent e)
          {
            getOwner().getGraphEditor().getGraph().renameMemento("Display Schnyder Embedding");
            displaySchnyderHelper(getOwner(), e.getSource() == getRandomButton());
          }

          public void dispose()
          {
            if ( getOwner() != null )
            {
              getOwner().getGraphEditor().getGraph().doneMemento();
              newUndo();
            }
            super.dispose();
          }
        };
        ged.disableRunButton();
        activeGraphEditorWindow.getGraphEditor().allowTriangleSelection();
        activeGraphEditorWindow.setDialog(ged);
        graphWindow.showDialog(ged);
      }
      catch ( Exception e )
      {
        if ( !(e instanceof GraphException) )
        {
          e.printStackTrace();
        }
        activeGraphEditorWindow.getGraphEditor().getGraph().abortMemento();
        JOptionPane.showMessageDialog(graphWindow, e.getMessage(),
                                      "Error During Schnyder Embedding Display", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void displaySchnyderHelper(GraphEditorWindow editorWindow, boolean useRandom)
  {
    try
    {
      Node triangleNodes[];
      if ( useRandom )
      {
        triangleNodes = editorWindow.getGraphEditor().getGraph().getRandomTriangularFace();
      }
      else
      {
        triangleNodes = editorWindow.getGraphEditor().getSpecialNodeSelections();
      }
      int gridNum = editorWindow.getGraphEditor().getGraph().getNumNodes()-2;
      SchnyderEmbeddingOperation.displayStraightLineGridEmbedding(
        editorWindow.getGraphEditor().getGraph(),
        triangleNodes[0], triangleNodes[1], triangleNodes[2],
        editorWindow.getGraphEditor().getDrawWidth(),
        editorWindow.getGraphEditor().getDrawHeight() );
      editorWindow.getGraphEditor().setPreferredSize();
      editorWindow.getGraphEditor().update();
      editorWindow.getGraphEditor().allowNodeSelection(0);
      graphWindow.close(editorWindow.getDialog());
      editorWindow.setDialog(null);
    }
    catch ( Exception e )
    {
      if ( !(e instanceof GraphException) )
      {
        e.printStackTrace();
      }
      activeGraphEditorWindow.getGraphEditor().getGraph().abortMemento();
      JOptionPane.showMessageDialog(graphWindow, e.getMessage(),
                                    "Error During Schnyder Embedding Display", JOptionPane.ERROR_MESSAGE);
    }
  }

  public void displayChanTree()
  {
    if ( activeGraphEditorWindow != null )
    {
      if ( activeGraphEditorWindow.getDialog() != null )
      {
        graphWindow.close(activeGraphEditorWindow.getDialog());
      }
      ChanDialog ged = new ChanDialog( this,
      activeGraphEditorWindow,
      "Chan Tree Drawing Display",
      "<html>Please Select which Drawing Method to Use<br>and Select the Root Node of the Tree</html>" )
      {
        public void actionPerformed(ActionEvent e)
        {
          displayChanTreeHelper(getOwner(), this);
        }
      };
      ged.disableRunButton();
      activeGraphEditorWindow.getGraphEditor().allowNodeSelection(1);
      activeGraphEditorWindow.setDialog(ged);
      graphWindow.showDialog(ged);
    }
  }

  private void displayChanTreeHelper(GraphEditorWindow editorWindow, ChanDialog dialog)
  {
    try
    {
      editorWindow.getGraphEditor().getGraph().newMemento("Display Chan Tree Drawing");
      ChanTreeDrawOperation.displayChanTreeDrawing(
        editorWindow.getGraphEditor().getGraph(),
        editorWindow.getGraphEditor().getSpecialNodeSelections()[0],
        dialog.getSelectedMethodNumber(),
        editorWindow.getGraphEditor().getDrawWidth(),
        editorWindow.getGraphEditor().getDrawHeight() );
      editorWindow.getGraphEditor().setPreferredSize();
      editorWindow.getGraphEditor().update();
      editorWindow.getGraphEditor().allowNodeSelection(0);
      graphWindow.close(editorWindow.getDialog());
      editorWindow.setDialog(null);
      editorWindow.getGraphEditor().getGraph().doneMemento();
      newUndo();
    }
    catch ( Exception e )
    {
      if ( !(e instanceof GraphException) )
      {
        e.printStackTrace();
      }
      editorWindow.getGraphEditor().getGraph().abortMemento();
      JOptionPane.showMessageDialog(graphWindow, e.getMessage(),
                                    "Error During Chan Tree Drawing Display", JOptionPane.ERROR_MESSAGE);
    }
  }


  private void invalidateOTSResult()
  {
    otsResultAvailable = false;
    otsResultWindow = null;
    otsResultGraph = null;
    refreshOTSMenuVisibility();
  }

  private void resetOTSCommandState()
  {
    otsResultAvailable = false;
    otsResultWindow = null;
    otsResultGraph = null;
    refreshOTSMenuVisibility();
  }

  private void makeOTSResultAvailable(GraphEditorWindow window, Graph drawnGraph) throws Exception
  {
    otsResultAvailable = true;
    otsResultWindow = window;
    otsResultGraph = cloneGraph(drawnGraph);
    refreshOTSMenuVisibility();
  }

  public void saveOTS()
  {
    if ( activeGraphEditorWindow == null ||
         !otsResultAvailable ||
         activeGraphEditorWindow != otsResultWindow ||
         otsResultGraph == null )
    {
      JOptionPane.showMessageDialog(graphWindow,
                                    "Save OTS is only available immediately after a Kruin draw.",
                                    "Save OTS",
                                    JOptionPane.INFORMATION_MESSAGE);
      invalidateOTSResult();
      return;
    }
    try
    {
      Graph currentGraph = activeGraphEditorWindow.getGraphEditor().getGraph();
      saveOTSGraph(currentGraph, otsResultGraph);
      invalidateOTSResult();
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      JOptionPane.showMessageDialog(graphWindow, e.getMessage(),
                                    "Error During Save OTS", JOptionPane.ERROR_MESSAGE);
    }
  }

  public void displayKruinTree()
  {
    if ( activeGraphEditorWindow != null )
    {
      invalidateOTSResult();
      if ( activeGraphEditorWindow.getDialog() != null )
      {
        graphWindow.close(activeGraphEditorWindow.getDialog());
      }
      KruinDialog ged = new KruinDialog( this,
      activeGraphEditorWindow,
      "Kruin Tree Drawing Display",
      "<html>Select structure type, then Run.</html>" )
      {
        public void actionPerformed(ActionEvent e)
        {
          displayKruinTreeHelper(getOwner(), this);
        }
      };
      activeGraphEditorWindow.getGraphEditor().allowNodeSelection(0);
      activeGraphEditorWindow.setDialog(ged);
      graphWindow.showDialog(ged);
    }
  }

  private void displayKruinTreeHelper(GraphEditorWindow editorWindow, KruinDialog dialog)
  {
    try
    {
      Graph graph = editorWindow.getGraphEditor().getGraph();
      int rootIndex = getKruinRootIndex(editorWindow);
      graph.newMemento("Display Kruin Drawing");
      KruinTreeDrawOperation.displayKruinTreeDrawing(
        graph,
        graph.getNodeAt(rootIndex),
        dialog.getSelectedMethodNumber(),
        editorWindow.getGraphEditor().getDrawWidth(),
        editorWindow.getGraphEditor().getDrawHeight() );
      editorWindow.getGraphEditor().update();
      editorWindow.getGraphEditor().allowNodeSelection(0);
      graphWindow.close(editorWindow.getDialog());
      editorWindow.setDialog(null);
      graph.doneMemento();
      newUndo();
      makeOTSResultAvailable(editorWindow, graph);
    }
    catch ( Exception e )
    {
      if ( !(e instanceof GraphException) )
      {
        e.printStackTrace();
      }
      editorWindow.getGraphEditor().getGraph().abortMemento();
      JOptionPane.showMessageDialog(graphWindow, e.getMessage(),
                                    "Error During Kruin Drawing Display", JOptionPane.ERROR_MESSAGE);
    }
  }

  private int getKruinRootIndex(GraphEditorWindow editorWindow)
  {
    Graph graph = editorWindow.getGraphEditor().getGraph();
    Node[] selectedRoots = editorWindow.getGraphEditor().getSpecialNodeSelections();
    if ( selectedRoots != null && selectedRoots.length > 0 && selectedRoots[0] != null )
    {
      int selectedIndex = findNodeIndex(graph, selectedRoots[0]);
      if ( selectedIndex >= 0 )
      {
        return selectedIndex;
      }
    }
    return 0;
  }

  private int findNodeIndex(Graph graph, Node target)
  {
    for ( int i=0; i<graph.getNumNodes(); i++ )
    {
      if ( graph.getNodeAt(i) == target )
      {
        return i;
      }
    }
    return -1;
  }

  private Graph cloneGraph(Graph sourceGraph) throws Exception
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    sourceGraph.saveTo(pw);
    pw.close();

    BufferedReader reader = new BufferedReader(new StringReader(sw.toString()));
    Graph cloned = Graph.loadFrom(reader);
    reader.close();
    return cloned;
  }


private void saveOTSGraph(Graph sourceGraph, Graph otsGraph) throws Exception
{
  String sourcePath = tryGetGraphFilePath(sourceGraph);
  String suggestedPath = null;
  String savePath = null;

  if ( sourcePath != null && sourcePath.length() > 0 )
  {
    suggestedPath = deriveOTSPath(sourcePath);
  }
  else
  {
    suggestedPath = new File(System.getProperty("user.dir"), "OTS_graph.ots").getCanonicalPath();
  }

  JFileChooser fc = new JFileChooser();

  File suggestedFile = new File(suggestedPath);
  File parentDir = suggestedFile.getParentFile();
  if ( parentDir != null && parentDir.exists() )
  {
    fc.setCurrentDirectory(parentDir);
  }
  else
  {
    fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
  }
  fc.setSelectedFile(new File(suggestedFile.getName()));
  fc.setDialogTitle("Save OTS");
  fc.setApproveButtonText("Save OTS");
  fc.setApproveButtonToolTipText("OTS = Open Tree Structure");

  JPanel accessory = new JPanel(new BorderLayout());
  accessory.add(new JLabel("<html><b>OTS</b> = Open Tree Structure<br>Suggested result name starts with <b>OTS_</b> and ends with <b>.ots</b>.</html>"),
                BorderLayout.NORTH);
  fc.setAccessory(accessory);

  int returnVal = fc.showSaveDialog(graphWindow);
  if ( returnVal != JFileChooser.APPROVE_OPTION )
  {
    return;
  }

  savePath = fc.getSelectedFile().getCanonicalPath();
  String fileExt = Utils.getExtension(fc.getSelectedFile());
  if ( fileExt == null )
  {
    savePath = savePath + ".ots";
    fileExt = "ots";
  }
  if ( fileExt == null || !fileExt.equalsIgnoreCase("ots") )
  {
    JOptionPane.showMessageDialog(graphWindow,
                                  "OTS files may only be saved as .ots files.",
                                  "Unable to Save OTS",
                                  JOptionPane.ERROR_MESSAGE);
    return;
  }
  if ( !confirmOverwriteIfNeeded(savePath) )
  {
    return;
  }

  PrintWriter writer = new PrintWriter(new FileWriter(savePath));
  otsGraph.saveTo(writer);
  writer.close();

  JOptionPane.showMessageDialog(graphWindow,
                                "Open Tree Structure saved to:\n" + savePath,
                                "Save OTS",
                                JOptionPane.INFORMATION_MESSAGE);
}

private boolean confirmOverwriteIfNeeded(String savePath)
  {
    File aFile = new File(savePath);
    if ( aFile.exists() )
    {
      int returnInt = JOptionPane.showConfirmDialog(graphWindow,
                        "Do you wish to Overwrite the file " + savePath + "?",
                        "File Already Exists",
                        JOptionPane.YES_NO_OPTION);
      return returnInt == JOptionPane.YES_OPTION;
    }
    return true;
  }


private String deriveOTSPath(String sourcePath)
{
  File sourceFile = new File(sourcePath);
  String name = sourceFile.getName();
  File parent = sourceFile.getParentFile();

  int dot = name.lastIndexOf('.');
  if ( dot > 0 )
  {
    name = name.substring(0, dot);
  }
  if ( !name.startsWith("OTS_") )
  {
    name = "OTS_" + name;
  }
  name = name + ".ots";

  if ( parent != null )
  {
    return new File(parent, name).getPath();
  }
  return name;
}

private String tryGetGraphFilePath(Graph graph)
  {
    try
    {
      java.lang.reflect.Method method = graph.getClass().getMethod("getFilePath", new Class[0]);
      Object value = method.invoke(graph, new Object[0]);
      if ( value instanceof String )
      {
        return (String)value;
      }
    }
    catch ( Exception e )
    {
    }
    return null;
  }



  public void displayMST()
  {
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().getGraph().newMemento("Display Minimum Spanning Tree");
      MinimumSpanningTreeOperation.drawMinimumSpanningTree(activeGraphEditorWindow.getGraphEditor().getGraph());
      activeGraphEditorWindow.getGraphEditor().getGraph().doneMemento();
      newUndo();
      activeGraphEditorWindow.getGraphEditor().update();
    }
  }

  public void displayDijkstra()
  {
    if ( activeGraphEditorWindow != null )
    {
      activeGraphEditorWindow.getGraphEditor().update();

      // disable controls here?
      SchnyderDialog ged = new SchnyderDialog( this,
        activeGraphEditorWindow,
        "Dijkstra Shortest Path",
        "Please Select a Source and Destination Node" )
      {
        public void actionPerformed(ActionEvent e)
        {
          getOwner().getGraphEditor().getGraph().newMemento("Display Dijkstra Shortest Path");
          displayDijkstraHelper(getOwner());
          getOwner().getGraphEditor().getGraph().doneMemento();
          newUndo();
        }
      };
      ged.disableRunButton();
      activeGraphEditorWindow.getGraphEditor().allowNodeSelection(2);
      activeGraphEditorWindow.setDialog(ged);
      graphWindow.showDialog(ged);
    }
  }

  private void displayDijkstraHelper(GraphEditorWindow editorWindow)
  {
    Node nodeSelections[] = editorWindow.getGraphEditor().getSpecialNodeSelections();
    DijkstraShortestPathOperation.drawShortestPath(editorWindow.getGraphEditor().getGraph(),
      nodeSelections[0], nodeSelections[1]);
    editorWindow.getGraphEditor().update();
    editorWindow.getGraphEditor().allowNodeSelection(0);
    graphWindow.close(editorWindow.getDialog());
    editorWindow.setDialog(null);
  }
}