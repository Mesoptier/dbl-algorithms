import java.awt.*;
import javax.swing.*;

public class DebugPanel extends JPanel {

  private DebugState state;

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    int size = getWidth() - 6;

    //TODO
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, size + 6, size + 6);

    if (state != null) {
      g.setColor(Color.BLACK);

      for (Edge edge : state.getEdges()) {
        if (edge != null) {
          Vertex v1 = edge.getHead();
          Vertex v2 = edge.getTail();
          g.fillOval((int) (v1.getX() * size), size - (int) (v1.getY() * size), 6, 6);
          g.fillOval((int) (v2.getX() * size), size - (int) (v2.getY() * size), 6, 6);
          g.drawLine((int) (v1.getX() * size) + 3, size - (int) (v1.getY() * size) + 3, (int) (v2.getX() * size) + 3, size - (int) (v2.getY() * size) + 3);
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

  public void setState(DebugState state) {
    this.state = state;
    this.repaint();
  }

}
