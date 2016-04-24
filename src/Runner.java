import java.io.InputStream;
import java.io.OutputStream;

public class Runner {

  private final InputStream inputStream;
  private final OutputStream outputStream;

  public Runner(InputStream inputStream, OutputStream outputStream) {
    this.inputStream = inputStream;
    this.outputStream = outputStream;
  }

  public ProblemOutput start() {
    ProblemInput input = ProblemInput.fromInputStream(inputStream);

    Reconstruct reconstruct = Reconstruct.fromVariant(input.getVariant());
    ProblemOutput output = reconstruct.start(input.getPoints());

    output.printToOutputStream(outputStream, input);
    return output;
  }

  public static void main(String[] args) {
    Runner runner = new Runner(System.in, System.out);
    runner.start();
  }

}
