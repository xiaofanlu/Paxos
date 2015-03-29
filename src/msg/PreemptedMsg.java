package msg;

import util.BallotNum;

/**
 * Created by xiaofan on 3/26/15.
 * PreemptedMsg for Commander
 */

public class PreemptedMsg extends Message {
  public BallotNum ballotNum;

  public PreemptedMsg(int pid, BallotNum b) {
    src = pid;
    ballotNum = b;
  }

  @Override
  public String toString() {
    return "PreemptedMsg: " + src + " " + ballotNum;
  }
}
