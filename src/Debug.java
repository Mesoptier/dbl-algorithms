import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

public class Debug implements ActionListener {

  private int position = 0;
  private ArrayList<DebugState> states = new ArrayList<>();

  private DebugPanel debugPanel;

  public Debug(){
    initGui();
  }

  private void initGui() {
    GridBagConstraints c;

    // Window
    JFrame window = new JFrame("States");
    window.setSize(695, 480);
    window.setResizable(false);
    window.setLayout(new GridBagLayout());
    window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    // - Problem panel
    JPanel debugPanelContainer = new JPanel();
    debugPanelContainer.setBorder(BorderFactory.createEtchedBorder());
    c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 4;
    c.weighty = 4;
    c.gridx = 0;
    c.gridy = 0;
    window.add(debugPanelContainer, c);

    debugPanel = new DebugPanel();
    debugPanelContainer.add(debugPanel);

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

    // -- Next step button
    JButton nextStepButton =  new JButton();
    nextStepButton.setText("Next step");
    nextStepButton.setActionCommand("nextStep");
    nextStepButton.addActionListener(this);
    c = new GridBagConstraints();
    c.weightx = 0.5;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 1;
    c.insets = new Insets(5, 0, 5, 0);
    infoPanel.add(nextStepButton, c);

    // -- previous step button
    JButton prevStepButton =  new JButton();
    prevStepButton.setText("Previous step");
    prevStepButton.setActionCommand("prevStep");
    prevStepButton.addActionListener(this);
    c = new GridBagConstraints();
    c.weightx = 0.5;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 2;
    c.insets = new Insets(5, 0, 5, 0);
    infoPanel.add(prevStepButton, c);
    window.setVisible(true);
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public void addState(DebugState state) {
    states.add(state);
  }

  public int getStateCount() {
    return states.size();
  }

  public DebugState getCurrentState() {
    return states.get(this.position);
  }

  private void nextState() {
    position++;
    setState();
  }

  private void prevState() {
    if (position > 0) {
      position--;
    }
    setState();
  }

  private void setState(){
    debugPanel.setState(states.get(position));
  }

  public void draw(int position) {
    this.position = position;
    setState();
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    String command = event.getActionCommand();
    switch (command) {
      case "nextStep":
        nextState();
        break;
      case "prevStep":
        prevState();
        break;
      default:
    }
  }
}
