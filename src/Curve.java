import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Curve {

  private List<Edge> edges;

  public Curve() {
    edges = new ArrayList<Edge>();
  }

  public List<Edge> getEdges() {
    return edges;
  }

  public void connect(Vertex head, Vertex tail) {
    edges.add(new Edge(head, tail));
  }

}
