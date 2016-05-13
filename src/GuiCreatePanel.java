import java.awt.*;

import javax.swing.*;

public class GuiCreatePanel extends JPanel {

  private ProblemInput problemInput;

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
          g.fillOval((int) (vertex.getX() * size), (int) ((1-vertex.getY()) * size), 6, 6);
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
}
