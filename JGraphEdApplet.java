import java.awt.*;
import javax.swing.*;
import userInterface.*;

public class JGraphEdApplet extends JApplet
{
  public static final int WIDTH = 700;
  public static final int HEIGHT = 550;

  private GraphController controller;

  public void init()
  {
    controller = new GraphController(false);

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(controller.getToolBar(), BorderLayout.NORTH);
    getContentPane().add(controller.getGraphWindow(), BorderLayout.CENTER);

    setJMenuBar(controller.getMenuBar());
    setVisible(true);
  }
}