package msg;

/**
 * Created by xiaofan on 3/31/15.
 * State Query Message for rebuild the server
 *
 */

public class StateQueryMsg extends Message {

  public StateQueryMsg(int pid) {
    src = pid;
  }

  @Override
  public String toString() {
    return "State Query : " + src;
  }
}
