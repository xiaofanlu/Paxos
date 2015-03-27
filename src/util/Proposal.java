package util;

/**
 * Created by xiaofan on 3/26/15.
 */

public class Proposal {
  int clientId;
  int slotNum;
  String text;

  public Proposal (int c, int s, String t) {
    clientId = c;
    slotNum = s;
    text = t;
  }

  @Override
  public String toString () {
    return "Prop(" + clientId + ", " + slotNum + ", " + text + ")";
  }
}
