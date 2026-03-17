package userInterface;

public abstract class GraphEditorDialog extends JGraphEdInternalFrame 
{
  public GraphEditorDialog(GraphController controller, String title, boolean resizable,
                           boolean closeable, boolean maximizable, boolean iconifiable)
  {
    super(controller, title, resizable,
          closeable, maximizable, iconifiable);
  }
  
  public abstract GraphEditorWindow getOwner();
  public abstract void setOwner(GraphEditorWindow o);
  
  public abstract void enableRunButton();
  public abstract void disableRunButton();

}