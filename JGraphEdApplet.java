import java.awt.*;
import javax.swing.*;
import userInterface.*;

public class JGraphEdApplet extends JPanel
{
  public static final int WIDTH = 700;
  public static final int HEIGHT = 550;

  private GraphController controller;
  private JMenuBar menuBar;

  public JGraphEdApplet()
  {
    init();
  }

  public void init()
  {
    controller = new GraphController(false);

    setLayout(new BorderLayout());
    add(controller.getToolBar(), BorderLayout.NORTH);
    add(controller.getGraphWindow(), BorderLayout.CENTER);

    menuBar = controller.getMenuBar();
    setVisible(true);
  }

  public JMenuBar getAppletMenuBar()
  {
    return menuBar;
  }
}
