import java.util.Locale;
import java.util.Scanner;

public class Runner {

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    scanner.useLocale(Locale.US);

    scanner.next(); // "reconstruct"
    String variant = scanner.next();
    Logger.log(variant);

    int n = scanner.nextInt();
    scanner.nextLine(); // "number of sample points"
    Logger.log(Integer.toString(n));

    Point[] points = new Point[n];

    for (int i = 0; i < n; i++) {
      int id = scanner.nextInt();
      float x = scanner.nextFloat();
      float y = scanner.nextFloat();
      points[i] = new Point(id, x, y);

      scanner.nextLine();

      Logger.log(points[i].toString());
    }
  }

}
