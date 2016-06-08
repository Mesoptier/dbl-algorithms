import javafx.scene.shape.Circle;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

import javax.swing.*;

public class GuiProblemPanel extends JPanel {

  private DebugState state;
  private boolean drawVertices = true;

  private static final int PADDING = 20;
  private static final float LINE_WIDTH = 2;
  private static final float POINT_SIZE = 4;

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Enable anti-aliasing
    Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int size = Math.min(getWidth(), getHeight()) - PADDING;
    int offsetX = (getWidth() - size) / 2;
    int offsetY = (getHeight() - size) / 2;

    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());

    if (state != null) {
      if (state.getMessage() != null) {
        g.setColor(Color.BLACK);

        g.drawString(state.getMessage(), 5, 15);
      }

      for (Edge edge : state.getEdges()) {
        if (edge != null) {
          g.setColor(state.getEdgeColor(edge));

          Vertex head = edge.getHead();
          Vertex tail = edge.getTail();

          g2.setStroke(new BasicStroke(LINE_WIDTH));
          g2.draw(new Line2D.Float(
              offsetX + (int)(head.getX() * size),
              offsetY + size - (int)(head.getY() * size),
              offsetX + (int)(tail.getX() * size),
              offsetY + size - (int)(tail.getY() * size)
          ));
        }
      }

      if (drawVertices) {
        for (Vertex vertex : state.getVertices()) {
          if (vertex != null) {
            g2.setColor(state.getVertexColor(vertex));
            g2.fill(new Ellipse2D.Float(
                offsetX + (int) (vertex.getX() * size) - POINT_SIZE / 2 - LINE_WIDTH,
                offsetY + size - (int) (vertex.getY() * size) - POINT_SIZE / 2 - LINE_WIDTH,
                POINT_SIZE + LINE_WIDTH * 2,
                POINT_SIZE + LINE_WIDTH * 2
            ));

            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Float(
                offsetX + (int) (vertex.getX() * size) - POINT_SIZE / 2,
                offsetY + size - (int) (vertex.getY() * size) - POINT_SIZE / 2,
                POINT_SIZE,
                POINT_SIZE
            ));
          }
        }
      }
      if (state.getCircles() != null){
        List<Circle> balls = state.getCircles();
        for (Circle ball : balls) {
          g2.setColor(Color.BLACK);
          g2.draw(new Ellipse2D.Double(offsetX + ((ball.getCenterX() - (ball.getRadius() / 2)) * size), offsetY + size - ((ball.getCenterY() + (ball.getRadius() / 2)) * size), ball.getRadius() * size, ball.getRadius() * size));
        }
      }
      if (state.getPacmen() != null) {
        List<Pacman> pacmen = state.getPacmen();
        for (Pacman pacman : pacmen) {
          g2.setColor(Color.RED);
          g2.draw(new Arc2D.Double(
              offsetX + (pacman.getCenterX() - pacman.getRadius()) * size,
              offsetY + size - (pacman.getCenterY() + pacman.getRadius()) * size,
              pacman.getRadius() * 2 * size,
              pacman.getRadius() * 2 * size,
              pacman.getRotation() + 45,
              270,
              Arc2D.PIE // Arc2D.PACMAN
          ));
        }
      }
    }
  }

  public void setState(DebugState state) {
    this.state = state;
    this.repaint();
  }

  public void setDrawVertices(boolean b) {
    drawVertices = b;
    repaint();
  }

}
