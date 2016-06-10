import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebugState {

  private List<Vertex> vertices = new ArrayList<>();
  private Map<Vertex, Color> vertexColorMap = new HashMap<>();
  private List<Edge> edges = new ArrayList<>();
  private Map<Edge, Color> edgeColorMap = new HashMap<>();
  private String message;
  private List<Circle> circles = new ArrayList<>();
  private List<Pacman> pacmen = new ArrayList<>();

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

  public Color getVertexColor(Vertex vertex) {
    return vertexColorMap.get(vertex);
  }

  public List<Vertex> getVertices() {
    return vertices;
  }

  public void addVertices(List<Vertex> vertices) {
    addVertices(vertices, Color.BLACK);
  }

  public void addVertices(List<Vertex> vertices, Color color) {
    for (Vertex vertex : vertices) {
      addVertex(vertex, color);
    }
  }

  public void addVertex(Vertex vertex) {
    addVertex(vertex, Color.BLACK);
  }

  public void addVertex(Vertex vertex, Color color) {
    this.vertices.add(new Vertex(vertex));
    this.vertexColorMap.put(vertex, color);
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void addCircles(List<Circle> balls){ this.circles = balls; }

  public List<Circle> getCircles(){ return circles; }

  public void addPacmen(List<Pacman> pacmen){ this.pacmen = pacmen; }


  public List<Pacman> getPacmen() {
    return pacmen;
  }

  public void addPacman(Pacman pacman) { this.pacmen.add(pacman); }

  public void addCircle(Circle circle) { this.circles.add(circle); }
}
