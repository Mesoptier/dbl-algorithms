import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Gui implements ActionListener {

  private static final Font FONT_MONOSPACED = new Font("monospaced", Font.PLAIN, 12);

  private JTextArea inputText;
  private JTextArea outputText;
  private GuiProblemPanel problemPanel;

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

    // Window
    JFrame window = new JFrame("DBL Algorithms: Curve Deconstruction");
    window.setSize(730, 480);
    window.setResizable(false);
    window.setLayout(new BorderLayout());
    window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    // - Problem panel
    JPanel problemPanelContainer = new JPanel();
    problemPanelContainer.setLayout(new BorderLayout());
    problemPanelContainer.setPreferredSize(new Dimension(480, 440));
    window.add(problemPanelContainer, BorderLayout.WEST);

    problemPanel = new GuiProblemPanel();
    problemPanel.setBorder(BorderFactory.createEtchedBorder());
    problemPanelContainer.add(problemPanel, BorderLayout.CENTER);

    // - Debug panel
    JPanel debugPanel = new JPanel();
    debugPanel.setPreferredSize(new Dimension(480, 40));
    debugPanel.setLayout(new GridLayout(1, 2));
    problemPanelContainer.add(debugPanel, BorderLayout.SOUTH);

    // -- Next step button
    JButton nextStepButton =  new JButton();
    nextStepButton.setText("Next");
    nextStepButton.setActionCommand("nextStep");
    nextStepButton.addActionListener(this);
    debugPanel.add(nextStepButton);

    // -- Previous step button
    JButton prevStepButton =  new JButton();
    prevStepButton.setText("Previous");
    prevStepButton.setActionCommand("prevStep");
    prevStepButton.addActionListener(this);
    debugPanel.add(prevStepButton);

    // - Info panel
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new GridBagLayout());
    infoPanel.setPreferredSize(new Dimension(240, 480));
    window.add(infoPanel, BorderLayout.EAST);

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

    Runner runner = new Runner(input);
    ProblemOutput output = runner.start(debug);

    problemPanel.setState(debug.getCurrentState());

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
      case "nextStep":
        debug.nextState();
        problemPanel.setState(debug.getCurrentState());
        break;
      case "prevStep":
        debug.previousState();
        problemPanel.setState(debug.getCurrentState());
        break;
      default:
    }
  }

  public static void main(String[] args) {
    new Gui();
  }

}
