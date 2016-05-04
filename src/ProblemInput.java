import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

public class ProblemInput {

  private final String variant;
  private final int numPoints;
  private final Vertex[] points;

  public ProblemInput(String variant, int numPoints, Vertex[] points) {
    this.variant = variant;
    this.numPoints = numPoints;
    this.points = points;
  }

  public static ProblemInput fromString(String string) {
    InputStream inputStream = new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
    return fromInputStream(inputStream);
  }

  public static ProblemInput fromInputStream(InputStream inputStream) {
    Scanner scanner = new Scanner(inputStream);
    scanner.useLocale(Locale.US);

    scanner.next(); // "reconstruct"
    String variant = scanner.next();

    int numPoints = scanner.nextInt();
    scanner.nextLine(); // "number of sample points"

    Vertex[] points = new Vertex[numPoints];

    for (int i = 0; i < numPoints; i++) {
      int id = scanner.nextInt();
      float x = scanner.nextFloat();
      float y = scanner.nextFloat();
      points[i] = new Vertex(id, x, y);

      if (scanner.hasNextLine()) {
        scanner.nextLine();
      } else {
        break;
      }
    }

    return new ProblemInput(variant, numPoints, points);
  }

  public String getVariant() {
    return variant;
  }

  public int getNumPoints() {
    return numPoints;
  }

  public Vertex[] getPoints() {
    return points;
  }

}
