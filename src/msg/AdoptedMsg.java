package msg;

import util.BallotNum;
import util.Pvalue;

import java.util.Set;

/**
 * Created by xiaofan on 3/26/15.
 * prepare message reply
 */

public class AdoptedMsg extends Message {
  public BallotNum ballotNum;
  public Set<Pvalue> accepted;

  public AdoptedMsg(int pid, BallotNum b, Set<Pvalue> a) {
    src = pid;
    ballotNum = b;
    accepted = a;
  }

  @Override
  public String toString() {
    String rst = "AdoptedMsg: " + src + " " + ballotNum + "\n";
    for (Pvalue pv : accepted) {
      rst += pv.toString() + "\n";
    }
    rst += "+++++++++++++++++End Adopted Msg ++++++++++++++";
    return rst;
  }
}
