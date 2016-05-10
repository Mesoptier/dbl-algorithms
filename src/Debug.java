import java.util.ArrayList;

public class Debug {

  private int position = 0;
  private ArrayList<DebugState> states = new ArrayList<>();

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public void addState(DebugState state) {
    states.add(state);
  }

  public int getStateCount() {
    return states.size();
  }

  public DebugState getCurrentState() {
    return states.get(position);
  }

  public boolean hasNextState() {
    return states.size() > position + 1;
  }

  public void nextState() {
    if (hasNextState()) {
      setState(position + 1);
    }
  }

  public boolean hasPreviousState() {
    return position > 0;
  }

  public void previousState() {
    if (hasPreviousState()) {
      setState(position - 1);
    }
  }

  public void setState(int position) {
    this.position = position;
  }

}
