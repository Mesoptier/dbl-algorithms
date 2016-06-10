import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Gui implements ActionListener {

  private static final Font FONT_MONOSPACED = new Font("monospaced", Font.PLAIN, 12);

  private JTextArea inputText;
  private JTextArea outputText;
  private GuiProblemPanel problemPanel;
  private GuiCreatePanel createPanel;
  private JCheckBoxMenuItem drawVerticesCheckBox;
  private int pointID = 0;

  /** The file chooser for open and save dialogs. */
  private final JFileChooser caseChooser = new JFileChooser();

  /** Default directory for loading and saving of test cases. */
  private static final File DEFAULT_DIRECTORY = new File(new File("."), "test");

  /** Logger. */
  private Logger logger = new Logger();
  private Debug debug;

  public Gui() {
    initGui();
  }

  private void initGui() {
    GridBagConstraints c;

//    try {
//      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//    } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException
//        | IllegalAccessException e) {
//      e.printStackTrace();
//    }

    // Window
    JFrame window = new JFrame("DBL Algorithms: Curve Deconstruction");
    window.setSize(1280, 560);
    window.setResizable(false);
    window.setLayout(new BorderLayout());
    window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    // - Problem panel
    JPanel problemPanelContainer = new JPanel();
    problemPanelContainer.setLayout(new BorderLayout(5, 5));
    problemPanelContainer.setPreferredSize(new Dimension(480, 520));
    problemPanelContainer.setBorder(new EmptyBorder(5, 5, 5, 5));
    window.add(problemPanelContainer, BorderLayout.WEST);

    problemPanel = new GuiProblemPanel();
    problemPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    problemPanelContainer.add(problemPanel, BorderLayout.CENTER);

    // - Debug panel
    JPanel debugPanel = new JPanel();
    debugPanel.setPreferredSize(new Dimension(480, 40));
    debugPanel.setLayout(new BorderLayout(5, 5));
    problemPanelContainer.add(debugPanel, BorderLayout.SOUTH);

    // -- Previous step button
    JButton prevStepButton =  new JButton();
    prevStepButton.setText("Previous");
    prevStepButton.setActionCommand("prevStep");
    prevStepButton.addActionListener(this);
    debugPanel.add(prevStepButton, BorderLayout.WEST);

    JButton runStepButton = new JButton();
    runStepButton.setText("Run");
    runStepButton.setActionCommand("runStep");
    runStepButton.addActionListener(this);
    debugPanel.add(runStepButton);

    // -- Next step button
    JButton nextStepButton =  new JButton();
    nextStepButton.setText("Next");
    nextStepButton.setActionCommand("nextStep");
    nextStepButton.addActionListener(this);
    debugPanel.add(nextStepButton, BorderLayout.EAST);

    // - Info panel
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new GridBagLayout());
    infoPanel.setPreferredSize(new Dimension(240, 480));
    window.add(infoPanel, BorderLayout.CENTER);

    // -- Input panel
    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new BorderLayout());
    TitledBorder inputPanelBorder = BorderFactory.createTitledBorder("Input");
    inputPanel.setBorder(inputPanelBorder);
    c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 0.5;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;
    c.insets = new Insets(5, 5, 5, 5);
    infoPanel.add(inputPanel, c);

    // -- Input text
    inputText = new JTextArea();
    inputText.setFont(FONT_MONOSPACED);
    inputPanel.add(new JScrollPane(inputText), BorderLayout.CENTER);

    // -- Start button
    JButton startButton =  new JButton();
    startButton.setText("Reconstruct");
    startButton.setActionCommand("startRunner");
    startButton.addActionListener(this);
    c = new GridBagConstraints();
    c.weightx = 0.5;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 1;
    c.insets = new Insets(5, 0, 5, 0);
    infoPanel.add(startButton, c);

    // -- Menu
    JMenuBar JMenuBar1 = new JMenuBar();
    JMenu JMenuFile = new JMenu();
    JMenuItem JMenuItemLoad = new JMenuItem();
    JMenuItem JMenuItemSave = new JMenuItem();

    JMenuFile.setText("File");

    JMenuItemLoad.setText("Load");
    JMenuItemLoad.setActionCommand("load");
    JMenuItemLoad.addActionListener(this);
    JMenuFile.add(JMenuItemLoad);

    JMenuItemSave.setText("Save");
    JMenuItemSave.setActionCommand("save");
    JMenuItemSave.addActionListener(this);
    JMenuFile.add(JMenuItemSave);

    JMenuBar1.add(JMenuFile);

    window.setJMenuBar(JMenuBar1);

    // -- File chooser
    caseChooser.setCurrentDirectory(DEFAULT_DIRECTORY);
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "Test case files (*.in)", "in");
    caseChooser.setFileFilter(filter);

    // -- Output panel
    JPanel outputPanel = new JPanel();
    outputPanel.setLayout(new BorderLayout());
    TitledBorder outputPanelBorder = BorderFactory.createTitledBorder("Output");
    outputPanel.setBorder(outputPanelBorder);
    c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 0.5;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 2;
    infoPanel.add(outputPanel, c);

    // --- Output text
    outputText = new JTextArea();
    outputText.setEditable(false);
    outputText.setFont(FONT_MONOSPACED);
    outputPanel.add(new JScrollPane(outputText), BorderLayout.CENTER);

    // - Problem panel
    JPanel createPanelContainer = new JPanel();
    createPanelContainer.setLayout(new BorderLayout(5, 5));
    createPanelContainer.setPreferredSize(new Dimension(480, 520));
    createPanelContainer.setBorder(new EmptyBorder(5, 5, 5, 5));
    window.add(createPanelContainer, BorderLayout.EAST);

    createPanel = new GuiCreatePanel();
    createPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    createPanelContainer.add(createPanel, BorderLayout.CENTER);

    JMenu JMenuCreate = new JMenu();
    JMenuItem JMenuItemSingle = new JMenuItem();
    JMenuItem JMenuItemMultiple = new JMenuItem();
    JMenuItem JMenuItemNetwork = new JMenuItem();

    JMenuCreate.setText("Create");

    JMenuItemSingle.setText("Single");
    JMenuItemSingle.setActionCommand("createSingle");
    JMenuItemSingle.addActionListener(this);
    JMenuCreate.add(JMenuItemSingle);

    JMenuItemMultiple.setText("Multiple");
    JMenuItemMultiple.setActionCommand("createMultiple");
    JMenuItemMultiple.addActionListener(this);
    JMenuCreate.add(JMenuItemMultiple);

    JMenuItemNetwork.setText("Network");
    JMenuItemNetwork.setActionCommand("createNetwork");
    JMenuItemNetwork.addActionListener(this);
    JMenuCreate.add(JMenuItemNetwork);

    JMenuBar1.add(JMenuCreate);

    JMenu JMenuView = new JMenu();
    JMenuView.setText("View");
    drawVerticesCheckBox = new JCheckBoxMenuItem();
    drawVerticesCheckBox.setText("Draw vertices");
    drawVerticesCheckBox.setState(true);
    drawVerticesCheckBox.setActionCommand("drawVertices");
    drawVerticesCheckBox.addActionListener(this);

    JMenuView.add(drawVerticesCheckBox);
    JMenuBar1.add(JMenuView);

    window.setJMenuBar(JMenuBar1);

    window.setVisible(true);
  }


  private void loadCase() {
    int result = caseChooser.showOpenDialog(null);

    if (result != JFileChooser.APPROVE_OPTION) {
      return; // canceled or error
    }

    File caseFile = caseChooser.getSelectedFile();
    try {
      InputStream inputStream = new FileInputStream(caseFile);

      try {
        inputText.setText("");
        BufferedReader fromFile = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = fromFile.readLine()) != null) {
          inputText.append(line + "\n");
        }
      } catch (IOException e) {
        logger.log("loadCase().IOException");
        e.printStackTrace();
        return;
      }

      startRunner();

    } catch (FileNotFoundException e) {
      logger.log("loadCase().FileNotFoundException");
      e.printStackTrace();
      return;
    }
  }

  private void saveCase() {
    int result = caseChooser.showSaveDialog(null);

    if (result != JFileChooser.APPROVE_OPTION) {
      return; // canceled or error
    }

    File caseFile = caseChooser.getSelectedFile();
    if (caseFile.exists ()) {
      int response = JOptionPane.showConfirmDialog(null,
          "Overwrite existing file?",
          "Confirm Overwrite",
          JOptionPane.OK_CANCEL_OPTION,
          JOptionPane.QUESTION_MESSAGE);
      if (response == JOptionPane.CANCEL_OPTION) {
        return;
      }
    }
    // can write file
    PrintWriter out;
    try {
      out = new PrintWriter(caseFile);
      out.print(inputText.getText());
      out.close();
    } catch (FileNotFoundException e) {
      logger.log("saveCase().FileNotFoundException");
      e.printStackTrace();
      return;
    }
  }

  private void startRunner() {
    ProblemInput input = ProblemInput.fromString(inputText.getText());
    debug = new Debug();
    long cTime = System.currentTimeMillis();
    Runner runner = new Runner(input);
    ProblemOutput output = runner.start(debug);
    System.out.println("Running time: " + (System.currentTimeMillis() - cTime) + "Ms");
    if (debug != null) {
      problemPanel.setState(debug.getCurrentState());
    } else {
      DebugState finalState = new DebugState();
      finalState.addEdges(output.getEdges());
      finalState.addVertices(output.getVertices());
      problemPanel.setState(finalState);
    }

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    output.printToOutputStream(outputStream, input);
    outputText.setText(outputStream.toString());
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    String command = event.getActionCommand();
    switch (command) {
      case "startRunner":
        startRunner();
        break;
      case "load":
        loadCase();
        break;
      case "save":
        saveCase();
        break;
      case "drawVertices":
        problemPanel.setDrawVertices(drawVerticesCheckBox.getState());
      case "nextStep":
        if (debug != null) {
          debug.nextState();
          problemPanel.setState(debug.getCurrentState());
        }
        break;
      case "prevStep":
        if (debug != null) {
          debug.previousState();
          problemPanel.setState(debug.getCurrentState());
        }
        break;
      case "runStep":
        /*
        while (debug.getPosition() < debug.getStateCount()-1){
          debug.nextState();
          problemPanel.setState(debug.getCurrentState());
        }
        */
        debug.setState(debug.getStateCount()-1);
        problemPanel.setState(debug.getCurrentState());
        break;
      case "createSingle":
        inputText.setText("");
        createProblem("single");
        //createPanel.removeAll();
        break;
      case "createMultiple":
        inputText.setText("");
        createProblem("multiple");
        break;
      case "createNetwork":
        inputText.setText("");
        createProblem("network");
        break;
      default:
    }
  }

  private void createProblem(String type){
    pointID = 0;
    ProblemInput input = null;
    createPanel.setProblemInput(input);
    inputText.append("reconstruct " + type + "\n");
    inputText.append("0 number of sample points \n");
    if (createPanel.getMouseListeners().length ==0) {
      createPanel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          Double x = 1.0 * e.getX();
          Double y = 1.0 * e.getY();
          int size = createPanel.getWidth() - 6;
          float xFloat = (float) (x / size);
          //xFloat = 1-xFloat;
          float yFloat = (float) (y / size);
          pointID++;

          inputText.append(pointID + " " + xFloat + " " + (1 - yFloat) + "\n");
          inputText.setText(inputText.getText().replaceAll(pointID - 1 + " number of sample points", pointID + " number of sample points"));
          updatePanels();
        }
      });
    }
  }

  private void updatePanels() {
    ProblemInput input = ProblemInput.fromString(inputText.getText());

    createPanel.setProblemInput(input);
  }

  public static void main(String[] args) {
    new Gui();
  }

}
