import java.util.Random;

public class generator {

  private void generateRandom(int n) {
    Logger.log("reconstruct network");
    Logger.log(n + " number of sample points");
    for (int i = 1; i <= n; i++) {
      Double l = Math.random();
      Double k = Math.random();
      Logger.log(i + " " + l + " " + k);
    }
  }

  private void generateLine(int n) {
    Logger.log("reconstruct multiple");
    Logger.log(n+" number of sample points");
    Random r = new Random();
    for (int i=1; i <= n; i++) {
      Double x = i*(1/((double)n));
      Double y;
      if (i < n/2) {
        y = i*(1/(double)n);
      } else {
        y = 1 - i*(1/(double)n);
      }
      Logger.log(i + " " + x + " " + y);
    }
  }

  public static void main(String[] args) {
    new generator().generateLine(100);
  }
}
