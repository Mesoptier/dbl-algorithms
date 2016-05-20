import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ProblemInput {

  private final String variant;
  private final int numVertices;
  private final List<Vertex> vertices;

  public ProblemInput(String variant, int numVertices, List<Vertex> vertices) {
    this.variant = variant;
    this.numVertices = numVertices;
    this.vertices = vertices;
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

    int numVertices = scanner.nextInt();
    scanner.nextLine(); // "number of sample points"

    List<Vertex> vertices = new ArrayList<>(numVertices);

    for (int i = 0; i < numVertices; i++) {
      int id = scanner.nextInt();
      double x = scanner.nextDouble();
      double y = scanner.nextDouble();
      vertices.add(new Vertex(id, x, y));

      if (scanner.hasNextLine()) {
        scanner.nextLine();
      } else {
        break;
      }
    }

    return new ProblemInput(variant, numVertices, vertices);
  }

  public String getVariant() {
    return variant;
  }

  public int getNumVertices() {
    return numVertices;
  }

  public List<Vertex> getVertices() {
    return vertices;
  }

}
