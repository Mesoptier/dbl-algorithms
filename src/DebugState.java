import java.util.ArrayList;

public class DebugState {

  private ArrayList<Edge> edges;

  public ArrayList<Edge> getEdges() {
    return edges;
  }

  public void setEdges(ArrayList<Edge> edges) {
    this.edges = new ArrayList(edges.size());
    for (Edge edge : edges) {
      this.edges.add(new Edge(edge));
    }
  }

}
