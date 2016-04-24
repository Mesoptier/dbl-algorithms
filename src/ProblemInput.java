import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

public class ProblemInput {

  private final String variant;
  private final int numPoints;
  private final Point[] points;

  public ProblemInput(String variant, int numPoints, Point[] points) {
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

    Point[] points = new Point[numPoints];

    for (int i = 0; i < numPoints; i++) {
      int id = scanner.nextInt();
      float x = scanner.nextFloat();
      float y = scanner.nextFloat();
      points[i] = new Point(id, x, y);

      scanner.nextLine();
    }

    return new ProblemInput(variant, numPoints, points);
  }

  public String getVariant() {
    return variant;
  }

  public int getNumPoints() {
    return numPoints;
  }

  public Point[] getPoints() {
    return points;
  }

}
