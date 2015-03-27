package util;


/**
 * Created by xiaofan on 3/26/15.
 */
public class Pvalue {
  public BallotNum ballotNum;
  public int slotNum;
  public Proposal prop;

  public Pvalue (BallotNum b, int s, Proposal p) {
    ballotNum = b;
    slotNum = s;
    prop = p;
  }

  public Pvalue (Pvalue pv) {
    ballotNum = pv.ballotNum;
    slotNum = pv.slotNum;
    prop = pv.prop;
  }

  @Override
  public String toString() {
    String rst = "pvalue => " + ballotNum + "slot(" + slotNum + ") " +
        prop;
    System.out.println(rst);
    return rst;
  }
}
