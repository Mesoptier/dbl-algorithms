import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Scanner;

public class Runner {

  private final InputStream in;
  private final OutputStream out;

  public Runner(InputStream in, OutputStream out) {
    this.in = in;
    this.out = out;
  }

  public void start() {
    Scanner scanner = new Scanner(this.in);
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
    reconstruct.setPoints(points);
  }

  public static void main(String[] args) {
    Runner runner = new Runner(System.in, System.out);
    runner.start();
  }

}
