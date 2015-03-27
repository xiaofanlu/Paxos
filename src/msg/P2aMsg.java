package msg;

import util.Pvalue;

/**
 * Created by xiaofan on 3/26/15.
 *
 * Accecpt request
 *
 */
public class P2aMsg extends Message {
  public Pvalue pv;

  public P2aMsg (int pid, Pvalue p) {
    src = pid;
    pv = p;
  }

  @Override
  public String toString () {
    return "P2aMsg: " + src + " " + pv;
  }

}
