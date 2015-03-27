package msg;

import util.BallotNum;

/**
 * Created by xiaofan on 3/26/15.
 * PreemptedMsg for Commander
 */

public class PreemptedMsg extends Message {
  BallotNum ballot_num;

  public PreemptedMsg(int pid, BallotNum b) {
    src = pid;
    ballot_num = b;
  }

  @Override
  public String toString() {
    return "PreemptedMsg: " + src + " " + ballot_num;
  }
}
