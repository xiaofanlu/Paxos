package msg;

import util.BallotNum;
import util.Pvalue;

import java.util.Set;

/**
 * Created by xiaofan on 3/26/15.
 * prepare message reply
 */

public class P1bMsg extends Message {
  public BallotNum ballotNum;
  public Set<Pvalue> accepted;

  public P1bMsg(int pid, BallotNum b, Set<Pvalue> a) {
    src = pid;
    ballotNum = b;
    accepted = a;
  }

  @Override
  public String toString() {
    return "P1bMsg: " + src + " " + ballotNum;
  }
}
