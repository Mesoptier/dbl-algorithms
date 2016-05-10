import java.util.ArrayList;
import java.util.List;

public class Curve {

  protected List<Edge> edges;

  public Curve() {
    edges = new ArrayList<Edge>();
  }

  public List<Edge> getEdges() {
    return edges;
  }

  public void addEdge(Edge edge) {
    edges.add(edge);
  }

}
