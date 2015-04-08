package util;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by xiaofan on 3/26/15.
 */

public class Command implements Serializable {
  public static final long serialVersionUID = 6473128480951955693L;

  /** Identifier of the client */
  public int kappa;

  /** Local sequence number */
  public int cid;

  /** Actual Message */
  public String text;

  public Command(int k, int c, String t) {
    kappa = k;
    cid = c;
    text = t;
  }

  @Override
  public int hashCode() {
    return Objects.hash(kappa, cid, text);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Command))
      return false;
    if (obj == this)
      return true;

    Command rhs = (Command) obj;
    return rhs.kappa == kappa  && rhs.cid == cid &&
           rhs.text .equals(text);
  }

  @Override
  public String toString () {
    return "Cmd(" + kappa + ", " + cid + ", " + text + ")";
  }
}
