import java.io.OutputStream;
import java.io.PrintStream;

public class ReconstructSingle extends Reconstruct {

  @Override
  public void start(Point[] points, OutputStream outputStream) {
    PrintStream stream = new PrintStream(outputStream);

    stream.print("single");
  }

}
