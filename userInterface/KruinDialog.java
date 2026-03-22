
package userInterface;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class KruinDialog extends GraphEditorDialog implements ActionListener
{
  public static int WIDTH = 430;
  public static int HEIGHT = 320;

  public static final int STRUCTURE_SIMPLE_TREE = 1;
  public static final int STRUCTURE_LANGUAGE_TREE = 2;
  public static final int STRUCTURE_ANAPHOR_TREE = 3;
  public static final int STRUCTURE_OTHER = 4;

  private static final String DEFAULTS_PATH = "config/kruin_defaults.properties";
  private static final String USER_SETTINGS_PATH = "config/kruin_user.properties";

  private GraphEditorWindow owner;
  private JButton runButton;
  private JButton kruinDefaultsButton;
  private JButton userSettingsButton;

  private ButtonGroup structureTypeGroup;
  private JRadioButton simpleTreeButton;
  private JRadioButton languageTreeButton;
  private JRadioButton anaphorTreeButton;
  private JRadioButton otherTreeButton;

  private JCheckBox showProjectionsCheckBox;
  private JCheckBox leftProjectionCheckBox;
  private JCheckBox rightProjectionCheckBox;
  private JCheckBox topProjectionCheckBox;
  private JCheckBox bottomProjectionCheckBox;

  private JSpinner structureLeftOffsetSpinner;
  private JSpinner structureRightOffsetSpinner;
  private JSpinner structureTopOffsetSpinner;
  private JSpinner structureBottomOffsetSpinner;

  private JSpinner leftProjectionPositionSpinner;
  private JSpinner rightProjectionPositionSpinner;
  private JSpinner topProjectionPositionSpinner;
  private JSpinner bottomProjectionPositionSpinner;

  private JSpinner gridColumnWidthSpinner;
  private JSpinner gridRowHeightSpinner;

  private boolean loadUserSettingsRequested = false;

  public KruinDialog(GraphController controller, GraphEditorWindow owner,
                     String title, String message)
  {
    super(controller, owner.getTitle() + " - " + title,
          true,
          true,
          true,
          false
    );
    this.owner = owner;

    initialiseFields();
    buildUi();
    loadAtStartup();

    addInternalFrameListener(controller);
    setVisible(true);
  }

  private void buildUi()
  {
    getContentPane().setLayout(new BorderLayout(8, 8));
    ((Container)getContentPane()).setBackground(getBackground());

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    getContentPane().add(mainPanel, BorderLayout.CENTER);

    JLabel line1 = new JLabel("Select structure type, then Run.");
    line1.setAlignmentX(Component.LEFT_ALIGNMENT);
    line1.setHorizontalAlignment(SwingConstants.LEFT);
    mainPanel.add(line1);

    JLabel line2 = new JLabel("Kruin defaults load a type profile. User settings edit details.");
    line2.setAlignmentX(Component.LEFT_ALIGNMENT);
    line2.setHorizontalAlignment(SwingConstants.LEFT);
    mainPanel.add(line2);

    mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

    runButton = createMainButton("Run");
    runButton.addActionListener(this);
    mainPanel.add(runButton);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));

    kruinDefaultsButton = createMainButton("Kruin defaults");
    kruinDefaultsButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        applyKruinDefaultsForCurrentType();
        saveUserPreferencesSilently();
      }
    });
    mainPanel.add(kruinDefaultsButton);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));

    userSettingsButton = createMainButton("User settings");
    userSettingsButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        showUserSettingsDialog();
      }
    });
    mainPanel.add(userSettingsButton);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));

    JPanel structurePanel = new JPanel();
    structurePanel.setLayout(new BoxLayout(structurePanel, BoxLayout.Y_AXIS));
    structurePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    structurePanel.setBorder(BorderFactory.createTitledBorder("Structure Type"));

    structurePanel.add(simpleTreeButton);
    structurePanel.add(languageTreeButton);
    structurePanel.add(anaphorTreeButton);
    structurePanel.add(otherTreeButton);

    mainPanel.add(structurePanel);

    setPreferredSize(new Dimension(WIDTH, HEIGHT));
    pack();
    fitWithinOwner();
  }

  private JButton createMainButton(String text)
  {
    JButton button = new JButton(text);
    button.setAlignmentX(Component.LEFT_ALIGNMENT);
    button.setMaximumSize(new Dimension(340, 28));
    button.setPreferredSize(new Dimension(340, 28));
    return button;
  }

  private void initialiseFields()
  {
    simpleTreeButton = new JRadioButton("Simple");
    languageTreeButton = new JRadioButton("Language");
    anaphorTreeButton = new JRadioButton("Anaphor");
    otherTreeButton = new JRadioButton("Other");

    simpleTreeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    languageTreeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    anaphorTreeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    otherTreeButton.setAlignmentX(Component.LEFT_ALIGNMENT);

    structureTypeGroup = new ButtonGroup();
    structureTypeGroup.add(simpleTreeButton);
    structureTypeGroup.add(languageTreeButton);
    structureTypeGroup.add(anaphorTreeButton);
    structureTypeGroup.add(otherTreeButton);

    showProjectionsCheckBox = new JCheckBox("Show projections");
    leftProjectionCheckBox = new JCheckBox("Left");
    rightProjectionCheckBox = new JCheckBox("Right");
    topProjectionCheckBox = new JCheckBox("Top");
    bottomProjectionCheckBox = new JCheckBox("Bottom");

    structureLeftOffsetSpinner = createSpinner(2, 0, 999, 1);
    structureRightOffsetSpinner = createSpinner(2, 0, 999, 1);
    structureTopOffsetSpinner = createSpinner(2, 0, 999, 1);
    structureBottomOffsetSpinner = createSpinner(2, 0, 999, 1);

    leftProjectionPositionSpinner = createSpinner(2, 0, 999, 1);
    rightProjectionPositionSpinner = createSpinner(30, 0, 999, 1);
    topProjectionPositionSpinner = createSpinner(0, 0, 999, 1);
    bottomProjectionPositionSpinner = createSpinner(30, 0, 999, 1);

    gridColumnWidthSpinner = createSpinner(20, 1, 999, 1);
    gridRowHeightSpinner = createSpinner(20, 1, 999, 1);

    simpleTreeButton.setSelected(true);
    applyKruinDefaultsForCurrentType();
  }

  private JSpinner createSpinner(int value, int min, int max, int step)
  {
    JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));
    spinner.setPreferredSize(new Dimension(70, 24));
    spinner.setMaximumSize(new Dimension(70, 24));
    return spinner;
  }

  private void fitWithinOwner()
  {
    Rectangle bounds = null;
    if (owner != null)
    {
      bounds = owner.getBounds();
    }

    int x = 40;
    int y = 40;
    if (bounds != null)
    {
      x = bounds.x + Math.max(15, (bounds.width - getWidth()) / 2);
      y = bounds.y + Math.max(15, (bounds.height - getHeight()) / 2);
    }

    setLocation(x, y);
  }

  private void showUserSettingsDialog()
  {
    final JDialog dialog = new JDialog((Frame)null, "User settings", true);
    dialog.getContentPane().setLayout(new BorderLayout(8, 8));

    JPanel top = new JPanel();
    top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
    top.setBorder(new EmptyBorder(10, 10, 0, 10));

    JLabel header1 = new JLabel(getSettingsHeaderForCurrentType());
    header1.setAlignmentX(Component.LEFT_ALIGNMENT);
    JLabel header2 = new JLabel(getSettingsSubHeaderForCurrentType());
    header2.setAlignmentX(Component.LEFT_ALIGNMENT);

    top.add(header1);
    top.add(header2);

    dialog.getContentPane().add(top, BorderLayout.NORTH);

    JTabbedPane tabs = new JTabbedPane();
    if (getStructureType() == STRUCTURE_SIMPLE_TREE)
    {
      tabs.addTab("Tree", createSimpleTreeSettingsTab());
      tabs.addTab("Grid", createGridTab());
    }
    else if (getStructureType() == STRUCTURE_LANGUAGE_TREE)
    {
      tabs.addTab("Projections", createProjectionsTab());
      tabs.addTab("Positions", createPositionsTab());
      tabs.addTab("Grid", createGridTab());
    }
    else if (getStructureType() == STRUCTURE_ANAPHOR_TREE)
    {
      tabs.addTab("Tree", createSimpleTreeSettingsTab());
      tabs.addTab("Projections", createProjectionsTab());
      tabs.addTab("Grid", createGridTab());
    }
    else
    {
      tabs.addTab("Tree", createSimpleTreeSettingsTab());
      tabs.addTab("Grid", createGridTab());
    }
    dialog.getContentPane().add(tabs, BorderLayout.CENTER);

    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton loadUser = new JButton("Load user settings");
    loadUser.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        if (!loadUserSettingsFromFile())
        {
          JOptionPane.showMessageDialog(dialog,
            "No saved user settings found.",
            "User settings",
            JOptionPane.INFORMATION_MESSAGE);
        }
      }
    });

    JButton save = new JButton("Save");
    save.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        saveUserPreferences();
      }
    });

    JButton close = new JButton("Close");
    close.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        dialog.dispose();
      }
    });

    bottom.add(loadUser);
    bottom.add(save);
    bottom.add(close);

    dialog.getContentPane().add(bottom, BorderLayout.SOUTH);
    dialog.pack();
    dialog.setResizable(false);
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
  }

  private String getSettingsHeaderForCurrentType()
  {
    if (getStructureType() == STRUCTURE_LANGUAGE_TREE)
    {
      return "Language settings";
    }
    if (getStructureType() == STRUCTURE_ANAPHOR_TREE)
    {
      return "Anaphor settings";
    }
    if (getStructureType() == STRUCTURE_OTHER)
    {
      return "Other settings";
    }
    return "Simple tree settings";
  }

  private String getSettingsSubHeaderForCurrentType()
  {
    if (getStructureType() == STRUCTURE_LANGUAGE_TREE)
    {
      return "Language uses projections and positions.";
    }
    if (getStructureType() == STRUCTURE_SIMPLE_TREE)
    {
      return "Simple uses only the open tree, without projections.";
    }
    if (getStructureType() == STRUCTURE_ANAPHOR_TREE)
    {
      return "Anaphor is reserved for later expansion.";
    }
    return "Other is reserved for later expansion.";
  }

  private JPanel createSimpleTreeSettingsTab()
  {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    GridBagConstraints c = baseConstraints();
    addRow(panel, c, 0, "Tree left", structureLeftOffsetSpinner);
    addRow(panel, c, 1, "Tree right", structureRightOffsetSpinner);
    addRow(panel, c, 2, "Tree top", structureTopOffsetSpinner);
    addRow(panel, c, 3, "Tree bottom", structureBottomOffsetSpinner);

    return panel;
  }

  private JPanel createProjectionsTab()
  {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    GridBagConstraints c = baseConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.anchor = GridBagConstraints.WEST;
    c.gridwidth = 2;
    panel.add(showProjectionsCheckBox, c);

    c.gridy++;
    c.gridwidth = 1;
    panel.add(leftProjectionCheckBox, c);
    c.gridx = 1;
    panel.add(rightProjectionCheckBox, c);

    c.gridx = 0;
    c.gridy++;
    panel.add(topProjectionCheckBox, c);
    c.gridx = 1;
    panel.add(bottomProjectionCheckBox, c);

    return panel;
  }

  private JPanel createPositionsTab()
  {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    GridBagConstraints c = baseConstraints();
    addRow(panel, c, 0, "Tree left", structureLeftOffsetSpinner);
    addRow(panel, c, 1, "Tree right", structureRightOffsetSpinner);
    addRow(panel, c, 2, "Tree top", structureTopOffsetSpinner);
    addRow(panel, c, 3, "Tree bottom", structureBottomOffsetSpinner);
    addRow(panel, c, 4, "Left projection", leftProjectionPositionSpinner);
    addRow(panel, c, 5, "Right projection", rightProjectionPositionSpinner);
    addRow(panel, c, 6, "Top projection", topProjectionPositionSpinner);
    addRow(panel, c, 7, "Bottom projection", bottomProjectionPositionSpinner);

    return panel;
  }

  private JPanel createGridTab()
  {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    GridBagConstraints c = baseConstraints();
    addRow(panel, c, 0, "Column width", gridColumnWidthSpinner);
    addRow(panel, c, 1, "Row height", gridRowHeightSpinner);

    return panel;
  }

  private GridBagConstraints baseConstraints()
  {
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(4, 4, 4, 4);
    c.anchor = GridBagConstraints.WEST;
    return c;
  }

  private void addRow(JPanel panel, GridBagConstraints c, int row, String label, Component field)
  {
    c.gridx = 0;
    c.gridy = row;
    c.weightx = 0.0;
    c.fill = GridBagConstraints.NONE;
    panel.add(new JLabel(label), c);

    c.gridx = 1;
    c.weightx = 1.0;
    panel.add(field, c);
  }

  private void loadAtStartup()
  {
    if (!loadUserSettingsFromFile())
    {
      loadDefaultsFromFileOrFallback();
    }
  }

  private void loadDefaultsFromFileOrFallback()
  {
    if (!loadPropertiesFromPath(DEFAULTS_PATH))
    {
      applyKruinDefaultsForCurrentType();
    }
    else
    {
      applyTypeRulesAfterLoad();
    }
  }

  private boolean loadUserSettingsFromFile()
  {
    boolean ok = loadPropertiesFromPath(USER_SETTINGS_PATH);
    if (ok)
    {
      loadUserSettingsRequested = true;
      applyTypeRulesAfterLoad();
    }
    return ok;
  }

  private boolean loadPropertiesFromPath(String path)
  {
    File file = new File(path);
    if (!file.exists())
    {
      return false;
    }

    FileInputStream in = null;
    try
    {
      Properties props = new Properties();
      in = new FileInputStream(file);
      props.load(in);
      applyProperties(props);
      return true;
    }
    catch (IOException ioe)
    {
      return false;
    }
    finally
    {
      if (in != null)
      {
        try { in.close(); } catch (IOException ioe) { }
      }
    }
  }

  private void applyProperties(Properties props)
  {
    int structureType = parseInt(props.getProperty("structure.type"), STRUCTURE_SIMPLE_TREE);
    if (structureType == STRUCTURE_LANGUAGE_TREE)
    {
      languageTreeButton.setSelected(true);
    }
    else if (structureType == STRUCTURE_ANAPHOR_TREE)
    {
      anaphorTreeButton.setSelected(true);
    }
    else if (structureType == STRUCTURE_OTHER)
    {
      otherTreeButton.setSelected(true);
    }
    else
    {
      simpleTreeButton.setSelected(true);
    }

    showProjectionsCheckBox.setSelected(parseBoolean(props.getProperty("projections.show"), true));
    leftProjectionCheckBox.setSelected(parseBoolean(props.getProperty("projection.left.enabled"), true));
    rightProjectionCheckBox.setSelected(parseBoolean(props.getProperty("projection.right.enabled"), false));
    topProjectionCheckBox.setSelected(parseBoolean(props.getProperty("projection.top.enabled"), true));
    bottomProjectionCheckBox.setSelected(parseBoolean(props.getProperty("projection.bottom.enabled"), false));

    structureLeftOffsetSpinner.setValue(Integer.valueOf(parseInt(props.getProperty("structure.offset.left"), 2)));
    structureRightOffsetSpinner.setValue(Integer.valueOf(parseInt(props.getProperty("structure.offset.right"), 2)));
    structureTopOffsetSpinner.setValue(Integer.valueOf(parseInt(props.getProperty("structure.offset.top"), 2)));
    structureBottomOffsetSpinner.setValue(Integer.valueOf(parseInt(props.getProperty("structure.offset.bottom"), 2)));

    leftProjectionPositionSpinner.setValue(Integer.valueOf(parseInt(props.getProperty("projection.left.position"), 2)));
    rightProjectionPositionSpinner.setValue(Integer.valueOf(parseInt(props.getProperty("projection.right.position"), 30)));
    topProjectionPositionSpinner.setValue(Integer.valueOf(parseInt(props.getProperty("projection.top.position"), 0)));
    bottomProjectionPositionSpinner.setValue(Integer.valueOf(parseInt(props.getProperty("projection.bottom.position"), 30)));

    gridColumnWidthSpinner.setValue(Integer.valueOf(parseInt(props.getProperty("grid.column.width"), 20)));
    gridRowHeightSpinner.setValue(Integer.valueOf(parseInt(props.getProperty("grid.row.height"), 20)));
  }

  private void applyKruinDefaultsForCurrentType()
  {
    if (getStructureType() == STRUCTURE_LANGUAGE_TREE)
    {
      applyLanguageDefaults();
    }
    else if (getStructureType() == STRUCTURE_ANAPHOR_TREE)
    {
      applyAnaphorDefaults();
    }
    else if (getStructureType() == STRUCTURE_OTHER)
    {
      applyOtherDefaults();
    }
    else
    {
      applySimpleDefaults();
    }
  }

  private void applyTypeRulesAfterLoad()
  {
    if (getStructureType() == STRUCTURE_SIMPLE_TREE)
    {
      showProjectionsCheckBox.setSelected(false);
      leftProjectionCheckBox.setSelected(false);
      rightProjectionCheckBox.setSelected(false);
      topProjectionCheckBox.setSelected(false);
      bottomProjectionCheckBox.setSelected(false);
    }
  }

  private void applySimpleDefaults()
  {
    showProjectionsCheckBox.setSelected(false);
    leftProjectionCheckBox.setSelected(false);
    rightProjectionCheckBox.setSelected(false);
    topProjectionCheckBox.setSelected(false);
    bottomProjectionCheckBox.setSelected(false);

    structureLeftOffsetSpinner.setValue(Integer.valueOf(2));
    structureRightOffsetSpinner.setValue(Integer.valueOf(2));
    structureTopOffsetSpinner.setValue(Integer.valueOf(2));
    structureBottomOffsetSpinner.setValue(Integer.valueOf(2));

    leftProjectionPositionSpinner.setValue(Integer.valueOf(2));
    rightProjectionPositionSpinner.setValue(Integer.valueOf(30));
    topProjectionPositionSpinner.setValue(Integer.valueOf(0));
    bottomProjectionPositionSpinner.setValue(Integer.valueOf(30));

    gridColumnWidthSpinner.setValue(Integer.valueOf(20));
    gridRowHeightSpinner.setValue(Integer.valueOf(20));
  }

  private void applyLanguageDefaults()
  {
    showProjectionsCheckBox.setSelected(true);
    leftProjectionCheckBox.setSelected(true);
    rightProjectionCheckBox.setSelected(false);
    topProjectionCheckBox.setSelected(true);
    bottomProjectionCheckBox.setSelected(false);

    structureLeftOffsetSpinner.setValue(Integer.valueOf(2));
    structureRightOffsetSpinner.setValue(Integer.valueOf(2));
    structureTopOffsetSpinner.setValue(Integer.valueOf(2));
    structureBottomOffsetSpinner.setValue(Integer.valueOf(2));

    leftProjectionPositionSpinner.setValue(Integer.valueOf(2));
    rightProjectionPositionSpinner.setValue(Integer.valueOf(30));
    topProjectionPositionSpinner.setValue(Integer.valueOf(0));
    bottomProjectionPositionSpinner.setValue(Integer.valueOf(30));

    gridColumnWidthSpinner.setValue(Integer.valueOf(20));
    gridRowHeightSpinner.setValue(Integer.valueOf(20));
  }

  private void applyAnaphorDefaults()
  {
    showProjectionsCheckBox.setSelected(true);
    leftProjectionCheckBox.setSelected(true);
    rightProjectionCheckBox.setSelected(true);
    topProjectionCheckBox.setSelected(true);
    bottomProjectionCheckBox.setSelected(false);

    structureLeftOffsetSpinner.setValue(Integer.valueOf(2));
    structureRightOffsetSpinner.setValue(Integer.valueOf(2));
    structureTopOffsetSpinner.setValue(Integer.valueOf(2));
    structureBottomOffsetSpinner.setValue(Integer.valueOf(2));

    leftProjectionPositionSpinner.setValue(Integer.valueOf(2));
    rightProjectionPositionSpinner.setValue(Integer.valueOf(32));
    topProjectionPositionSpinner.setValue(Integer.valueOf(0));
    bottomProjectionPositionSpinner.setValue(Integer.valueOf(34));

    gridColumnWidthSpinner.setValue(Integer.valueOf(20));
    gridRowHeightSpinner.setValue(Integer.valueOf(20));
  }

  private void applyOtherDefaults()
  {
    showProjectionsCheckBox.setSelected(false);
    leftProjectionCheckBox.setSelected(false);
    rightProjectionCheckBox.setSelected(false);
    topProjectionCheckBox.setSelected(false);
    bottomProjectionCheckBox.setSelected(false);

    structureLeftOffsetSpinner.setValue(Integer.valueOf(2));
    structureRightOffsetSpinner.setValue(Integer.valueOf(2));
    structureTopOffsetSpinner.setValue(Integer.valueOf(2));
    structureBottomOffsetSpinner.setValue(Integer.valueOf(2));

    leftProjectionPositionSpinner.setValue(Integer.valueOf(2));
    rightProjectionPositionSpinner.setValue(Integer.valueOf(30));
    topProjectionPositionSpinner.setValue(Integer.valueOf(0));
    bottomProjectionPositionSpinner.setValue(Integer.valueOf(30));

    gridColumnWidthSpinner.setValue(Integer.valueOf(20));
    gridRowHeightSpinner.setValue(Integer.valueOf(20));
  }

  private int parseInt(String value, int fallback)
  {
    if (value == null)
    {
      return fallback;
    }
    try
    {
      return Integer.parseInt(value.trim());
    }
    catch (NumberFormatException nfe)
    {
      return fallback;
    }
  }

  private boolean parseBoolean(String value, boolean fallback)
  {
    if (value == null)
    {
      return fallback;
    }
    return "true".equalsIgnoreCase(value.trim()) || "yes".equalsIgnoreCase(value.trim());
  }

  public void saveUserPreferences()
  {
    FileOutputStream out = null;
    try
    {
      File configDir = new File("config");
      if (!configDir.exists())
      {
        configDir.mkdirs();
      }

      Properties props = createPropertiesFromCurrentState();
      out = new FileOutputStream(USER_SETTINGS_PATH);
      props.store(out, "Kruin user settings");
      loadUserSettingsRequested = true;

      JOptionPane.showMessageDialog(this,
        "User settings saved.",
        "Kruin",
        JOptionPane.INFORMATION_MESSAGE);
    }
    catch (IOException ioe)
    {
      JOptionPane.showMessageDialog(this,
        "Unable to save user settings.",
        "Kruin",
        JOptionPane.ERROR_MESSAGE);
    }
    finally
    {
      if (out != null)
      {
        try { out.close(); } catch (IOException ioe) { }
      }
    }
  }

  private void saveUserPreferencesSilently()
  {
    FileOutputStream out = null;
    try
    {
      File configDir = new File("config");
      if (!configDir.exists())
      {
        configDir.mkdirs();
      }

      Properties props = createPropertiesFromCurrentState();
      out = new FileOutputStream(USER_SETTINGS_PATH);
      props.store(out, "Kruin user settings");
      loadUserSettingsRequested = true;
    }
    catch (IOException ioe)
    {
    }
    finally
    {
      if (out != null)
      {
        try { out.close(); } catch (IOException ioe) { }
      }
    }
  }

  private Properties createPropertiesFromCurrentState()
  {
    Properties props = new Properties();

    props.setProperty("structure.type", String.valueOf(getStructureType()));
    props.setProperty("projections.show", String.valueOf(getShowProjections()));
    props.setProperty("projection.left.enabled", String.valueOf(getLeftProjectionEnabled()));
    props.setProperty("projection.right.enabled", String.valueOf(getRightProjectionEnabled()));
    props.setProperty("projection.top.enabled", String.valueOf(getTopProjectionEnabled()));
    props.setProperty("projection.bottom.enabled", String.valueOf(getBottomProjectionEnabled()));

    props.setProperty("structure.offset.left", String.valueOf(getStructureOffsetFromLeft()));
    props.setProperty("structure.offset.right", String.valueOf(getStructureOffsetFromRight()));
    props.setProperty("structure.offset.top", String.valueOf(getStructureOffsetFromTop()));
    props.setProperty("structure.offset.bottom", String.valueOf(getStructureOffsetFromBottom()));

    props.setProperty("projection.left.position", String.valueOf(getLeftProjectionPosition()));
    props.setProperty("projection.right.position", String.valueOf(getRightProjectionPosition()));
    props.setProperty("projection.top.position", String.valueOf(getTopProjectionPosition()));
    props.setProperty("projection.bottom.position", String.valueOf(getBottomProjectionPosition()));

    props.setProperty("grid.column.width", String.valueOf(getGridColumnWidth()));
    props.setProperty("grid.row.height", String.valueOf(getGridRowHeight()));

    return props;
  }

  public int getSelectedMethodNumber()
  {
    return 1;
  }

  public boolean usePreviousOpenStructureDefaultsSelected()
  {
    return loadUserSettingsRequested;
  }

  public int getStructureType()
  {
    if (languageTreeButton.isSelected())
    {
      return STRUCTURE_LANGUAGE_TREE;
    }
    if (anaphorTreeButton.isSelected())
    {
      return STRUCTURE_ANAPHOR_TREE;
    }
    if (otherTreeButton.isSelected())
    {
      return STRUCTURE_OTHER;
    }
    return STRUCTURE_SIMPLE_TREE;
  }

  public boolean getShowProjections()
  {
    if (getStructureType() == STRUCTURE_SIMPLE_TREE)
    {
      return false;
    }
    return showProjectionsCheckBox.isSelected();
  }

  public boolean getLeftProjectionEnabled()
  {
    if (getStructureType() == STRUCTURE_SIMPLE_TREE)
    {
      return false;
    }
    return leftProjectionCheckBox.isSelected();
  }

  public boolean getRightProjectionEnabled()
  {
    if (getStructureType() == STRUCTURE_SIMPLE_TREE)
    {
      return false;
    }
    return rightProjectionCheckBox.isSelected();
  }

  public boolean getTopProjectionEnabled()
  {
    if (getStructureType() == STRUCTURE_SIMPLE_TREE)
    {
      return false;
    }
    return topProjectionCheckBox.isSelected();
  }

  public boolean getBottomProjectionEnabled()
  {
    if (getStructureType() == STRUCTURE_SIMPLE_TREE)
    {
      return false;
    }
    return bottomProjectionCheckBox.isSelected();
  }

  public int getStructureOffsetFromLeft() { return ((Integer)structureLeftOffsetSpinner.getValue()).intValue(); }
  public int getStructureOffsetFromRight() { return ((Integer)structureRightOffsetSpinner.getValue()).intValue(); }
  public int getStructureOffsetFromTop() { return ((Integer)structureTopOffsetSpinner.getValue()).intValue(); }
  public int getStructureOffsetFromBottom() { return ((Integer)structureBottomOffsetSpinner.getValue()).intValue(); }

  public int getLeftProjectionPosition() { return ((Integer)leftProjectionPositionSpinner.getValue()).intValue(); }
  public int getRightProjectionPosition() { return ((Integer)rightProjectionPositionSpinner.getValue()).intValue(); }
  public int getTopProjectionPosition() { return ((Integer)topProjectionPositionSpinner.getValue()).intValue(); }
  public int getBottomProjectionPosition() { return ((Integer)bottomProjectionPositionSpinner.getValue()).intValue(); }

  public int getGridColumnWidth() { return ((Integer)gridColumnWidthSpinner.getValue()).intValue(); }
  public int getGridRowHeight() { return ((Integer)gridRowHeightSpinner.getValue()).intValue(); }

  public void enableRunButton() { runButton.setEnabled(true); }
  public void disableRunButton() { runButton.setEnabled(false); }
  public GraphEditorWindow getOwner() { return owner; }
  public void setOwner(GraphEditorWindow o) { owner = o; }

  public void actionPerformed(ActionEvent e)
  {
    saveUserPreferencesSilently();
    // overridden by anonymous subclass in GraphController
  }
}
