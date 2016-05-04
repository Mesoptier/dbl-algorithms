import java.io.OutputStream;
import java.io.PrintStream;

public class ProblemOutput {

  private final Vertex[] vertices;
  private final Segment[] segments;

  public ProblemOutput(Vertex[] vertices, Segment[] segments) {
    this.vertices = vertices;
    this.segments = segments;
  }

  public Vertex[] getVertices() {
    return vertices;
  }

  public Segment[] getSegments() {
    return segments;
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
      int extraVertices = vertices.length - input.getNumVertices();
      printStream.println(extraVertices + " number of extra vertices");

      // TODO: Print extra vertices
    }

    // Print segments
    printStream.println(segments.length + " number of segments");

    for (Segment segment : segments) {
      printStream.println(segment.getVertex1().getId() + " " + segment.getVertex2().getId());
    }
  }

}
