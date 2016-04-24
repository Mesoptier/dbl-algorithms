import java.io.OutputStream;
import java.io.PrintStream;

public class ReconstructMultiple extends Reconstruct {

  @Override
  public void start(Point[] points, OutputStream outputStream) {
    PrintStream stream = new PrintStream(outputStream);

    stream.print("multiple");
  }

}
