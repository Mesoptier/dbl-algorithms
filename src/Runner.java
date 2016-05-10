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
      state.setEdges(problemOutput.getEdges());
      state.setVertices(problemOutput.getVertices());
      debug.addState(state);
    }

    return problemOutput;
  }

  public ProblemOutput start() {
    return start(null);
  }

  public static void main(String[] args) {
    ProblemInput input = ProblemInput.fromInputStream(System.in);
    Runner runner = new Runner(input);
    ProblemOutput output = runner.start();
    output.printToOutputStream(System.out, input);
  }

}
