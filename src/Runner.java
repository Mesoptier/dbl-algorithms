import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Runner {

  private final ProblemInput problemInput;

  public Runner(ProblemInput problemInput) {
    this.problemInput = problemInput;
  }

  public ProblemOutput start(Debug debug) {
    Reconstruct reconstruct = Reconstruct.fromVariant(problemInput.getVariant());
    reconstruct.setVertices(problemInput.getVertices());
    reconstruct.setDebug(debug);
    ProblemOutput problemOutput = reconstruct.start();

    if (debug != null) {
      DebugState state = new DebugState();
      state.addEdges(problemOutput.getEdges());
      state.addVertices(problemOutput.getVertices());
      debug.addState(state);
    }

    return problemOutput;
  }

  public ProblemOutput start() {
    return start(null);
  }

  public static void main(String[] args) throws FileNotFoundException {
    ProblemInput input;

    if (args.length == 2 && args[0].equals("--file")) {
      Scanner scanner = new Scanner(System.in);
      scanner.next();

      long start = System.currentTimeMillis();
      input = ProblemInput.fromInputStream(new FileInputStream(args[1]));
      Runner runner = new Runner(input);
      ProblemOutput output = runner.start();
      long finish = System.currentTimeMillis();

      output.printToOutputStream(System.out, input);

      Logger.log("Total time: " + Long.toString(finish - start));

    } else {
      input = ProblemInput.fromInputStream(System.in);

      Runner runner = new Runner(input);
      ProblemOutput output = runner.start();
      output.printToOutputStream(System.out, input);
    }
  }

}
