package msg;

import util.BallotNum;

/**
 * Created by xiaofan on 3/26/15.
 * prepare message
 */

public class P1aMsg extends Message {
  public BallotNum ballotNum;

  public P1aMsg(int pid, BallotNum b) {
    src = pid;
    ballotNum = b;
  }

  @Override
  public String toString() {
    return "P1aMsg : " + src + " " + ballotNum;
  }
}
