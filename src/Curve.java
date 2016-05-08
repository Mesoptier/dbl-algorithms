import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Curve {

  protected List<Edge> edges;

  public Curve() {
    edges = new ArrayList<Edge>();
  }

  public List<Edge> getEdges() {
    return edges;
  }

  public abstract void connect(Vertex head, Vertex tail);

}
