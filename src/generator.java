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
    for (int i=0; i <= n; i++) {
      Double l = 0.47 + 0.06 * r.nextDouble();
      Double k = r.nextDouble();
      Logger.log(i + " " + l + " " + k);
    }
  }

  public static void main(String[] args) {
    new generator().generateLine(2000);
  }
}
