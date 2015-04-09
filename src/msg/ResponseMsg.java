package msg;

import util.Command;

/**
 * Created by xiaofan on 3/26/15.
 * Response Message to client
 */

public class ResponseMsg extends Message {
  public int slotNum;
  public Command prop;

  public ResponseMsg(int pid, int s, Command p) {
    src = pid;
    slotNum = s;
    prop = p;
  }

  @Override
  public String toString() {
    return "ResponseMsg: " + src + " " + slotNum + " " + prop;
  }

  public String format () {
    return slotNum + " " + prop.kappa + ": " + prop.text;
  }

}
