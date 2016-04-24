import java.io.OutputStream;
import java.io.PrintStream;

public class ProblemOutput {

  private final Point[] points;
  private final Segment[] segments;

  public ProblemOutput(Point[] points, Segment[] segments) {
    this.points = points;
    this.segments = segments;
  }

  public Point[] getPoints() {
    return points;
  }

  public Segment[] getSegments() {
    return segments;
  }

  public void printToOutputStream(OutputStream outputStream, ProblemInput input) {
    PrintStream printStream = new PrintStream(outputStream);

    // Repeat input
    printStream.println("reconstruct " + input.getVariant());
    printStream.println(input.getNumPoints() + " number of sample points");
    for (Point point : input.getPoints()) {
      printStream.println(point.getId() + " " + point.getX() + " " + point.getY());
    }

    // Print extra points only for the network variant
    if (input.getVariant().equals("network")) {
      int extraPoints = points.length - input.getNumPoints();
      printStream.println(extraPoints + " number of extra points");

      // TODO: Print extra points
    }

    // Print segments
    printStream.println(segments.length + " number of segments");

    for (Segment segment : segments) {
      printStream.println(segment.getPoint1().getId() + " " + segment.getPoint2().getId());
    }
  }

}
