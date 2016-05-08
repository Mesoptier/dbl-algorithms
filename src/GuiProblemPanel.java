import java.awt.*;

import javax.swing.*;

public class GuiProblemPanel extends JPanel {

  private ProblemInput problemInput;
  private ProblemOutput problemOutput;

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    int size = getWidth() - 6;

    g.setColor(Color.WHITE);
    g.fillRect(0, 0, size + 6, size + 6);

    if (problemInput != null) {
      g.setColor(Color.BLACK);

      for (Vertex vertex : problemInput.getVertices()) {
        if (vertex != null) {
          g.fillOval((int) (vertex.getX() * size), size - (int) (vertex.getY() * size), 6, 6);
        }
      }

      for (Edge edge : problemOutput.getEdges()) {
        if (edge != null) {
          Vertex head = edge.getHead();
          Vertex tail = edge.getTail();
          g.drawLine((int)(head.getX() * size) + 3, size - (int)(head.getY() * size) + 3,
              (int)(tail.getX() * size) + 3, size - (int)(tail.getY() * size) + 3);
        }
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
