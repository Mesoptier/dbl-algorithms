import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebugState {

  private List<Vertex> vertices = new ArrayList<>();
  private List<Edge> edges = new ArrayList<>();
  private Map<Edge, Color> edgeColorMap = new HashMap<>();
  private String message;

  public Color getEdgeColor(Edge edge) {
    return edgeColorMap.get(edge);
  }

  public List<Edge> getEdges() {
    return edges;
  }

  public void addEdges(List<Edge> edges) {
    addEdges(edges, Color.BLACK);
  }

  public void addEdges(List<Edge> edges, Color color) {
    for (Edge edge : edges) {
      addEdge(edge, color);
    }
  }

  public void addEdge(Edge edge) {
    addEdge(edge, Color.BLACK);
  }

  public void addEdge(Edge edge, Color color) {
    this.edges.add(new Edge(edge));
    this.edgeColorMap.put(edge, color);
  }

  public List<Vertex> getVertices() {
    return vertices;
  }

  public void addVertices(List<Vertex> vertices) {
    for (Vertex vertex : vertices) {
      this.vertices.add(new Vertex(vertex));
    }
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}
