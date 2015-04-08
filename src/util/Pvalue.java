package util;


import java.io.Serializable;

/**
 * Created by xiaofan on 3/26/15.
 */
public class Pvalue implements Comparable<Pvalue>, Serializable {
  public static final long serialVersionUID = 6473128480951855693L;
  public BallotNum ballotNum;
  public int slotNum;
  public Command prop;

  public Pvalue (BallotNum b, int s, Command p) {
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
    //System.out.println(rst);
    return rst;
  }

  @Override
  public int compareTo(Pvalue o) {
    return ballotNum.compareTo(o.ballotNum);
  }
}
