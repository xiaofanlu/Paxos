package msg;

import util.Command;

/**
 * Created by xiaofan on 3/26/15.
 * Decision Message for Commander
 */

public class ProposeMsg extends Message {
  public int slotNum;
  public Command prop;

  public ProposeMsg(int pid, int s, Command p) {
    src = pid;
    slotNum = s;
    prop = p;
  }

  @Override
  public String toString() {
    return "ProposeMsg: " + src + " " + slotNum + " " + prop;
  }
}
