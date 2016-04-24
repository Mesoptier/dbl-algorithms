import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Scanner;

public class Runner {

  private final InputStream inputStream;
  private final OutputStream outputStream;

  public Runner(InputStream inputStream, OutputStream outputStream) {
    this.inputStream = inputStream;
    this.outputStream = outputStream;
  }

  public void start() {
    Scanner scanner = new Scanner(inputStream);
    scanner.useLocale(Locale.US);

    scanner.next(); // "reconstruct"
    String variant = scanner.next();

    int n = scanner.nextInt();
    scanner.nextLine(); // "number of sample points"

    Point[] points = new Point[n];

    for (int i = 0; i < n; i++) {
      int id = scanner.nextInt();
      float x = scanner.nextFloat();
      float y = scanner.nextFloat();
      points[i] = new Point(id, x, y);

      scanner.nextLine();
    }

    Reconstruct reconstruct = Reconstruct.fromVariant(variant);
    reconstruct.start(points, outputStream);
  }

  public static void main(String[] args) {
    Runner runner = new Runner(System.in, System.out);
    runner.start();
  }

}
