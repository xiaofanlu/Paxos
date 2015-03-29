package msg;

import util.Command;

/**
 * Created by xiaofan on 3/26/15.
 * Decision Message for Commander
 */

public class RequestMsg extends Message {
  public Command prop;

  public RequestMsg(int pid, Command p) {
    src = pid;
    prop = p;
  }

  @Override
  public String toString() {
    return "RqstMsg: " + src + " " + prop;
  }
}
