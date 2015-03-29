package util;

import java.util.Objects;

/**
 * Created by xiaofan on 3/26/15.
 */

public class Command {
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
    return "Cmd(" + cid + ", " + cid + ", " + text + ")";
  }
}
