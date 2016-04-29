import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
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
  public static final File DEFAULT_DIRECTORY = new File(new File("."), "test");

  public Gui() {
    initGui();
  }

  private void initGui() {
    GridBagConstraints c;

    // Window
    JFrame window = new JFrame("DBL Algorithms: Curve Deconstruction");
    window.setSize(640, 480);
    window.setResizable(false);
    window.setLayout(new GridBagLayout());
    window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    // - Problem panel
    JPanel problemPanelContainer = new JPanel();
    problemPanelContainer.setBorder(BorderFactory.createEtchedBorder());
    c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;
    window.add(problemPanelContainer, c);

    problemPanel = new GuiProblemPanel();
    problemPanelContainer.add(problemPanel);

    // - Info panel
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new GridBagLayout());
    c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 1;
    c.gridy = 0;
    window.add(infoPanel, c);

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
        "Test case files", "in");
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
        // do something
        return;
      }

      startRunner();

    } catch (FileNotFoundException e) {
      // do something
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
      // do something
      return;
    }
  }

  private void startRunner() {
    ProblemInput input = ProblemInput.fromString(inputText.getText());

    problemPanel.setProblemInput(input);

    Runner runner = new Runner(input);
    ProblemOutput output = runner.start();
    problemPanel.setProblemOutput(output);

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
      default:
    }
  }

  public static void main(String[] args) {
    new Gui();
  }

}
