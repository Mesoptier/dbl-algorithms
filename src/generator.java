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

  public static void main(String[] args) {
    new generator().generateRandom(2000);
  }
}
