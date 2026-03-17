package userInterface;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

public class KruinDialog extends GraphEditorDialog implements ActionListener
{
  public static int WIDTH = 400;
  public static int HEIGHT = 200;

  private ButtonGroup buttonGroup;
  private JRadioButton button1;
  private JRadioButton button2;
  private JRadioButton button3;
  private JRadioButton button4;
  private JRadioButton button5;
  private JRadioButton button6;
  private JRadioButton button7;



  private GraphEditorWindow owner;
  private JButton runButton;

  public KruinDialog( GraphController controller,GraphEditorWindow owner,
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

    button1 = new JRadioButton("1 - Domain Tree");
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

    button2 = new JRadioButton("2 - Inward Tree");
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


    button3 = new JRadioButton("3 - Compact Tree");
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

    button4 = new JRadioButton("4 - Vertical Tree");
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(button4, layoutCons);
    getContentPane().add(button4);
    
    button5 = new JRadioButton("5 - Drawn Tree");
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(button5, layoutCons);
    getContentPane().add(button5);


    
    buttonGroup = new ButtonGroup();
    button1.setSelected(true);
    buttonGroup.add(button1);
    buttonGroup.add(button2);
    buttonGroup.add(button3);
    buttonGroup.add(button4);
    buttonGroup.add(button5);
   
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
    setBounds(750, 0, 400, 200);
//?? setBounds(graphController.getGraphWindow().getWidth()-420,graphController.getGraphWindow().getHeight()-220,400, 200);
//    setSize(WIDTH, HEIGHT);
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
    else if ( button4.isSelected() )
    {
      return 4;
    }
    else if ( button5.isSelected() )
    {
      return 5;
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
