import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

public class ProblemOutput {

  private final List<Vertex> vertices;
  private final List<Edge> edges;

  public ProblemOutput(List<Vertex> vertices, List<Edge> edges) {
    this.vertices = vertices;
    this.edges = edges;
  }

  public List<Vertex> getVertices() {
    return vertices;
  }

  public List<Edge> getEdges() {
    return edges;
  }

  public void printToOutputStream(OutputStream outputStream, ProblemInput input) {
    PrintStream printStream = new PrintStream(outputStream);

    // Repeat input
    printStream.println("reconstruct " + input.getVariant());
    printStream.println(input.getNumVertices() + " number of sample vertices");
    for (Vertex vertex : input.getVertices()) {
      printStream.println(vertex.getId() + " " + vertex.getX() + " " + vertex.getY());
    }

    // Print extra vertices only for the network variant
    if (input.getVariant().equals("network")) {
      int extraVertices = vertices.size() - input.getNumVertices();
      printStream.println(extraVertices + " number of extra vertices");

      // TODO: Print extra vertices
    }

    // Print edges
    printStream.println(edges.size() + " number of segments");

    for (Edge edge : edges) {
      printStream.println(edge.getHead().getId() + " " + edge.getTail().getId());
    }
  }

}
