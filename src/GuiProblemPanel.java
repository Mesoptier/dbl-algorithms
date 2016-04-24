import java.awt.*;

import javax.swing.*;

public class GuiProblemPanel extends JPanel {

  private ProblemInput problemInput;
  private ProblemOutput problemOutput;

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    int size = getWidth();

    g.setColor(Color.WHITE);
    g.fillRect(0, 0, size, size);

    if (problemInput != null) {
      g.setColor(Color.BLACK);

      for (Point point : problemInput.getPoints()) {
        g.fillOval((int)(point.getX() * size), (int)(point.getY() * size), 5, 5);
      }
    }
  }

  @Override
  public Dimension getPreferredSize() {
    Dimension parentSize = getParent().getSize();
    int width = parentSize.width - 10;
    int height = parentSize.height - 10;

    if (width > height) {
      return new Dimension(height, height);
    } else {
      return new Dimension(width, width);
    }
  }

  public void setProblemInput(ProblemInput problemInput) {
    this.problemInput = problemInput;
    this.revalidate();
  }

  public void setProblemOutput(ProblemOutput problemOutput) {
    this.problemOutput = problemOutput;
    this.revalidate();
  }

}
