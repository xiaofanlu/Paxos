package msg;

import util.Proposal;

/**
 * Created by xiaofan on 3/26/15.
 * Decision Message for Commander
 */

public class DecisionMsg extends Message {
  int slotNum;
  Proposal prop;

  public DecisionMsg(int pid, int s, Proposal p) {
    src = pid;
    slotNum = s;
    prop = p;
  }

  @Override
  public String toString() {
    return "DecisionMsg: " + src + " " + slotNum + " " + prop;
  }
}
