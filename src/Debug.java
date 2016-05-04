import java.util.ArrayList;

public class Debug {

  private int position;
  private ArrayList<DebugState> states = new ArrayList();

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
    return states.get(this.position);
  }

}
