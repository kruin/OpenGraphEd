package userInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SchnyderDialog extends GraphEditorDialog implements ActionListener
{
  public static int WIDTH = 400;
  public static int HEIGHT = 200;

  private boolean useEmbeddingBox;
  private boolean useRandomButton;
  private GraphEditorWindow owner;
  private JButton runButton;
  private JCheckBox embeddingBox;
  private JButton randomButton;

  public SchnyderDialog( GraphController controller,GraphEditorWindow owner,
                         String title, String message )
  {
    this( controller, owner, title, message, false, false );
  }

  public SchnyderDialog( GraphController controller,GraphEditorWindow owner,
                         String title, String message,
                         boolean useEmbeddingBox, boolean useRandomButton )
  {
    super(controller, owner.getTitle() + " - " + title,
          true, //resizable
          true, //closable
          true, //maximizable
          false);//iconifiable
    this.owner = owner;
    this.useEmbeddingBox = useEmbeddingBox;
    this.useRandomButton = useRandomButton;

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

    if ( useEmbeddingBox )
    {
      embeddingBox = new JCheckBox("Display on Embedding");
      embeddingBox.setSelected(controller.getDrawOnEmbedding());
      layoutCons.gridx = GridBagConstraints.RELATIVE;
      layoutCons.gridy = GridBagConstraints.RELATIVE;
      layoutCons.gridwidth = GridBagConstraints.REMAINDER;
      layoutCons.gridheight = 1;
      layoutCons.fill = GridBagConstraints.BOTH;
      layoutCons.insets = new Insets(3,3,3,3);
      layoutCons.anchor = GridBagConstraints.NORTH;
      layoutCons.weightx = 1.0;
      layoutCons.weighty = 1.0;
      layout.setConstraints(embeddingBox, layoutCons);
      getContentPane().add(embeddingBox);
    }

    if ( useRandomButton )
    {
      randomButton = new JButton("Run With Random Outer Face");
      randomButton.addActionListener(this);
      layoutCons.gridx = GridBagConstraints.RELATIVE;
      layoutCons.gridy = GridBagConstraints.RELATIVE;
      layoutCons.gridwidth = GridBagConstraints.REMAINDER;
      layoutCons.gridheight = 1;
      layoutCons.fill = GridBagConstraints.BOTH;
      layoutCons.insets = new Insets(3,3,3,3);
      layoutCons.anchor = GridBagConstraints.NORTH;
      layoutCons.weightx = 1.0;
      layoutCons.weighty = 1.0;
      layout.setConstraints(randomButton, layoutCons);
      getContentPane().add(randomButton);
    }

    if ( useRandomButton )
    {
      runButton = new JButton("Run With Selected Outer Face");
    }
    else
    {
      runButton = new JButton("Run");
    }
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

  public JButton getRandomButton() { return randomButton; }

  public boolean getOnEmbedding() { return embeddingBox.isSelected(); }

  public void enableRunButton() { runButton.setEnabled(true); }

  public void disableRunButton() { runButton.setEnabled(false); }

  public GraphEditorWindow getOwner() { return owner; }

  public void setOwner(GraphEditorWindow o) { owner = o; }

  public void actionPerformed(ActionEvent e)
  {
    // do nothing, let this be overriden...
  }
}
