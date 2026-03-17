package userInterface;

import javax.swing.*;

import userInterface.modes.*;

import java.awt.*;
import java.awt.event.*;

public class GraphEditorPreferencesWindow extends JGraphEdInternalFrame implements ActionListener
{
  public static int WIDTH = 400;
  public static int HEIGHT = 400;

  private GraphController controller;
  private JCheckBox singleClickAddNodeBox;
  private JCheckBox addNodeOnEdgeDropBox;
  private JCheckBox drawOnEmbeddingBox;
  private JCheckBox clearGeneratedBox;
  private JCheckBox opaqueTextBox;

  public GraphEditorPreferencesWindow(GraphController controller)
  {
    super(controller, "JGraphEd Preferences",
          true, //resizable
          true, //closable
          true, //maximizable
          false);//iconifiable
    this.controller = controller;

    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints layoutCons = new GridBagConstraints();
    getContentPane().setLayout(layout);

    singleClickAddNodeBox = new JCheckBox("Allow Single (as well as Double) Click To Add Nodes");
    singleClickAddNodeBox.setSelected(EditListener.SINGLE_CLICK_ADD_NODE);
    singleClickAddNodeBox.addActionListener(this);

    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.HORIZONTAL;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 0.0;
    layout.setConstraints(singleClickAddNodeBox, layoutCons);
    getContentPane().add(singleClickAddNodeBox);

    addNodeOnEdgeDropBox = new JCheckBox("Create a New End Node if a new Edge has only one Node");
    addNodeOnEdgeDropBox.setSelected(EditListener.ADD_NODE_ON_EDGE_DROP);
    addNodeOnEdgeDropBox.addActionListener(this);

    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.HORIZONTAL;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 0.0;
    layout.setConstraints(addNodeOnEdgeDropBox, layoutCons);
    getContentPane().add(addNodeOnEdgeDropBox);

    drawOnEmbeddingBox = new JCheckBox("Default for Draw Canonical Order and Normal Label on Embedding");
    drawOnEmbeddingBox.setSelected(controller.getDrawOnEmbedding());
    drawOnEmbeddingBox.addActionListener(this);

    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.HORIZONTAL;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 0.0;
    layout.setConstraints(drawOnEmbeddingBox, layoutCons);
    getContentPane().add(drawOnEmbeddingBox);

    clearGeneratedBox = new JCheckBox("Clear Generated Edges after Straight Line Embedding");
    clearGeneratedBox.setSelected(controller.getClearGenerated());
    clearGeneratedBox.addActionListener(this);

    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.HORIZONTAL;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 0.0;
    layout.setConstraints(clearGeneratedBox, layoutCons);
    getContentPane().add(clearGeneratedBox);
    
    opaqueTextBox = new JCheckBox("Draw Text Background on Top of Edges");
    opaqueTextBox.setSelected(graphStructure.Node.OPAQUE_TEXT);
    opaqueTextBox.addActionListener(this);

    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.HORIZONTAL;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 0.0;
    layout.setConstraints(opaqueTextBox, layoutCons);
    getContentPane().add(opaqueTextBox);

    addInternalFrameListener(controller);

    setSize(WIDTH, HEIGHT);
    setVisible(true);
  }

  public void actionPerformed(ActionEvent e)
  {
    if ( e.getSource() == singleClickAddNodeBox )
    {
      EditListener.SINGLE_CLICK_ADD_NODE = !EditListener.SINGLE_CLICK_ADD_NODE;
    }
    else if ( e.getSource() == addNodeOnEdgeDropBox )
    {
      EditListener.ADD_NODE_ON_EDGE_DROP = !EditListener.ADD_NODE_ON_EDGE_DROP;
    }
    else if ( e.getSource() == drawOnEmbeddingBox )
    {
      controller.toggleDrawOnEmbedding();
    }
    else if ( e.getSource() == clearGeneratedBox )
    {
      controller.toggleClearGenerated();
    }
    else if ( e.getSource() == opaqueTextBox )
    {
      graphStructure.Node.OPAQUE_TEXT = !graphStructure.Node.OPAQUE_TEXT;
      controller.getGraphWindow().forceGraphRepaints();
    }
  }
}