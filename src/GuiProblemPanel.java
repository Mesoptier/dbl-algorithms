import java.awt.*;

import javax.swing.*;

public class GuiProblemPanel extends JPanel {

  private ProblemInput problemInput;
  private ProblemOutput problemOutput;

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    int size = getWidth() - 5;

    g.setColor(Color.WHITE);
    g.fillRect(0, 0, size + 5, size + 5);

    if (problemInput != null) {
      g.setColor(Color.BLACK);

      for (Point point : problemInput.getPoints()) {
        g.fillOval((int)(point.getX() * size), size - (int)(point.getY() * size), 5, 5);
      }

      for (Segment segment : problemOutput.getSegments()) {
        Point p1 = segment.getPoint1();
        Point p2 = segment.getPoint2();
        g.drawLine((int) (p1.getX() * size), size - (int) (p1.getY() * size), (int) (p2.getX() * size), size - (int) (p2.getY() * size));
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
    this.repaint();
  }

  public void setProblemOutput(ProblemOutput problemOutput) {
    this.problemOutput = problemOutput;
    this.repaint();
  }

}
