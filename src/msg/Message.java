package msg;

import java.io.Serializable;

/**
 * Created by xiaofan on 3/26/15.
 */

public abstract class Message implements Serializable {
  public static final long serialVersionUID = 6473128480951955693L;

  public int src;
  public int dst;

  public String print () {
    String rst = "\n" + src + " -> " + dst + "\t";
    rst += this.toString() + "\n";
    return rst;
  }
}

