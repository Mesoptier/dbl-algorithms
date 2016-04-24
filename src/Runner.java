import java.io.InputStream;
import java.io.OutputStream;

public class Runner {

  private final ProblemInput problemInput;

  public Runner(ProblemInput problemInput) {
    this.problemInput = problemInput;
  }

  public ProblemOutput start() {
    Reconstruct reconstruct = Reconstruct.fromVariant(problemInput.getVariant());
    return reconstruct.start(problemInput.getPoints());
  }

  public static void main(String[] args) {
    ProblemInput input = ProblemInput.fromInputStream(System.in);
    Runner runner = new Runner(input);
    ProblemOutput output = runner.start();
    output.printToOutputStream(System.out, input);
  }

}
