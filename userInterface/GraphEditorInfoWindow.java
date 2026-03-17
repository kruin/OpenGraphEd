package userInterface;

import javax.swing.*;
import java.awt.*;
import graphStructure.Graph;
import operation.*;

public class GraphEditorInfoWindow extends JGraphEdInternalFrame
{
  public static int WIDTH = 400;
  public static int HEIGHT = 400;

  private GraphController controller;
  private GraphEditorWindow editorWindow;

  private JLabel totalNodesLabel;
  private JLabel totalEdgesLabel;
  private JLabel generatedEdgesLabel;
  private JLabel curvedEdgesLabel;
  private JLabel planarLabel;
  private JLabel maximalPlanarLabel;
  private JLabel connectedCountLabel;
  private JLabel biconnectedCountLabel;

  public GraphEditorInfoWindow( GraphController controller,
                                GraphEditorWindow editorWindow)
  {
    super(controller, editorWindow.getTitle() + " - Info",
          true, //resizable
          true, //closable
          true, //maximizable
          true);//iconifiable
    this.controller = controller;
    this.editorWindow = editorWindow;

    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints layoutCons = new GridBagConstraints();
    getContentPane().setLayout(layout);

    totalNodesLabel = new JLabel();
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(totalNodesLabel, layoutCons);
    getContentPane().add(totalNodesLabel);

    totalEdgesLabel = new JLabel();
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(totalEdgesLabel, layoutCons);
    getContentPane().add(totalEdgesLabel);

    generatedEdgesLabel = new JLabel();
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(generatedEdgesLabel, layoutCons);
    getContentPane().add(generatedEdgesLabel);

    curvedEdgesLabel = new JLabel();
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(curvedEdgesLabel, layoutCons);
    getContentPane().add(curvedEdgesLabel);

    planarLabel = new JLabel();
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(planarLabel, layoutCons);
    getContentPane().add(planarLabel);

    maximalPlanarLabel = new JLabel();
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(maximalPlanarLabel, layoutCons);
    getContentPane().add(maximalPlanarLabel);

    connectedCountLabel = new JLabel();
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(connectedCountLabel, layoutCons);
    getContentPane().add(connectedCountLabel);

    biconnectedCountLabel = new JLabel();
    layoutCons.gridx = GridBagConstraints.RELATIVE;
    layoutCons.gridy = GridBagConstraints.RELATIVE;
    layoutCons.gridwidth = GridBagConstraints.REMAINDER;
    layoutCons.gridheight = 1;
    layoutCons.fill = GridBagConstraints.BOTH;
    layoutCons.insets = new Insets(3,3,3,3);
    layoutCons.anchor = GridBagConstraints.NORTH;
    layoutCons.weightx = 1.0;
    layoutCons.weighty = 1.0;
    layout.setConstraints(biconnectedCountLabel, layoutCons);
    getContentPane().add(biconnectedCountLabel);

    update();

    addInternalFrameListener(controller);

    setSize(WIDTH, HEIGHT);
  }

  public void update()
  {
    if ( isVisible() )
    {
      Graph g = editorWindow.getGraphEditor().getGraph();
      totalNodesLabel.setText("Total Nodes: " + g.getNumNodes());
      totalEdgesLabel.setText("Total Edges: " + g.getNumEdges());
      generatedEdgesLabel.setText("Generated Edges: " + g.getNumGeneratedEdges());
      curvedEdgesLabel.setText("Curved Edges: " + g.getNumCurvedEdges());
      boolean planar = PlanarityOperation.isPlanar(g);
      planarLabel.setText("Planar?: " + planar);
      maximalPlanarLabel.setText("Maximal Planar?: " + (planar &&
        g.getNumEdges() == g.getNumNodes() * 3 - 6));
      connectedCountLabel.setText("Num Connected Components: " +
        ConnectivityOperation.getConnectedComponents(g).size());
      biconnectedCountLabel.setText("Num Biconnected Components: " +
        BiconnectivityOperation.getBiconnectedComponents(g).size());
    }
  }
}