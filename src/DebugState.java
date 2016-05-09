import java.util.ArrayList;
import java.util.List;

public class DebugState {

  private ArrayList<Edge> edges;

  public ArrayList<Edge> getEdges() {
    return edges;
  }

  public DebugState(List<Edge> edges){
    this.edges = new ArrayList(edges.size());
    for (Edge edge : edges) {
      this.edges.add(new Edge(edge));
    }
  }

  public void setEdges(List<Edge> edges) {
    this.edges = new ArrayList(edges.size());
    for (Edge edge : edges) {
      this.edges.add(new Edge(edge));
    }
  }

}
