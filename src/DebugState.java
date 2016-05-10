import java.util.ArrayList;
import java.util.List;

public class DebugState {

  private List<Vertex> vertices;
  private List<Edge> edges;

  public List<Edge> getEdges() {
    return edges;
  }

  public void setEdges(List<Edge> edges) {
    this.edges = new ArrayList<>(edges.size());
    for (Edge edge : edges) {
      this.edges.add(new Edge(edge));
    }
  }

  public List<Vertex> getVertices() {
    return vertices;
  }

  public void setVertices(List<Vertex> vertices) {
    this.vertices = new ArrayList<>(vertices.size());
    for (Vertex vertex : vertices) {
      this.vertices.add(new Vertex(vertex));
    }
  }

}
