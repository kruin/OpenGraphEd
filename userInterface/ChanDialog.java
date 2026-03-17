package userInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChanDialog extends GraphEditorDialog implements ActionListener
{
  public static int WIDTH = 400;
  public static int HEIGHT = 200;

  private ButtonGroup buttonGroup;
  private JRadioButton button1;
  private JRadioButton button2;
  private JRadioButton button3;
  private GraphEditorWindow owner;
  private JButton runButton;

  public ChanDialog( GraphController controller,GraphEditorWindow owner,
                     String title, String message )
  {
    super(controller, owner.getTitle() + " - " + title,
          true, //resizable
          true, //closable
          true, //maximizable
          false);//iconifiable
    this.owner = owner;

    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints layoutCons = new GridBagConstraints();
    getContentPane().setLayout(layout);

    JLabel messageLabel = new JLabel(message);
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(messageLabel, layoutCons);
    getContentPane().add(messageLabel);

    button1 = new JRadioButton("Method 1");
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(button1, layoutCons);
    getContentPane().add(button1);
    
    button2 = new JRadioButton("Method 2");
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(button2, layoutCons);
    getContentPane().add(button2);
    
    button3 = new JRadioButton("Method 3");
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(button3, layoutCons);
    getContentPane().add(button3);
    
    buttonGroup = new ButtonGroup();
    button1.setSelected(true);
    buttonGroup.add(button1);
    buttonGroup.add(button2);
    buttonGroup.add(button3);
    
    runButton = new JButton("Run");
    runButton.addActionListener(this);
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(runButton, layoutCons);
    getContentPane().add(runButton);

    addInternalFrameListener(controller);

    setSize(WIDTH, HEIGHT);
    setVisible(true);
  }

  public int getSelectedMethodNumber()
  {
    if ( button1.isSelected() )
    {
      return 1;
    }
    else if ( button2.isSelected() )
    {
      return 2;
    }
    else if ( button3.isSelected() )
    {
      return 3;
    }
    else
    {
      // this case can never happen if a button is initially selected...
      return -1;
    }
  }
  
  public void enableRunButton() { runButton.setEnabled(true); }

  public void disableRunButton() { runButton.setEnabled(false); }

  public GraphEditorWindow getOwner() { return owner; }

  public void setOwner(GraphEditorWindow o) { owner = o; }

  public void actionPerformed(ActionEvent e)
  {
    // do nothing, let this be overriden...
  }
}
