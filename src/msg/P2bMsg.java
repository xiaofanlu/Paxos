package msg;

import util.BallotNum;

/**
 * Created by xiaofan on 3/26/15.
 */

public class P2bMsg extends Message {
  public BallotNum ballotNum;

  public P2bMsg (int pid, BallotNum b) {
    src = pid;
    ballotNum = b;
  }

  @Override
  public String toString () {
    return "P2bMsg: " + src + " " + ballotNum;
  }

}
