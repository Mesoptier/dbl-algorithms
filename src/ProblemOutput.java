import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ProblemOutput {

  private final List<Vertex> vertices;
  private final List<Edge> edges;
  private final List<? extends Curve> curves;

  public ProblemOutput(List<Vertex> vertices, List<? extends Curve> curves) {
    this.vertices = vertices;
    this.edges = new ArrayList<>();
    this.curves = curves;

    for (Curve curve : curves) {
      edges.addAll(curve.getEdges());
    }
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
    printStream.println(input.getNumVertices() + " number of sample points");
    for (int i = 0; i < input.getNumVertices(); i++) {
      Vertex vertex = vertices.get(i);
      printStream.println(vertex.getId() + " " + vertex.getX() + " " + vertex.getY());
    }

    // Print extra vertices only for the network variant
    if (input.getVariant().equals("network")) {
      int extraVertices = vertices.size() - input.getNumVertices();
      printStream.println(extraVertices + " number of extra points");

      for (int i = input.getNumVertices(); i < vertices.size(); i++) {
        Vertex vertex = vertices.get(i);
        printStream.println(vertex.getId() + " " + vertex.getX() + " " + vertex.getY());
      }
    }

    // Print edges
    printStream.println(edges.size() + " number of segments");

    for (Edge edge : edges) {
      printStream.println(edge.getHead().getId() + " " + edge.getTail().getId());
    }
  }

}
