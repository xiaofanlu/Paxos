package util; /**
 * Created by xiaofan on 3/26/1
 */

public class BallotNum implements Comparable<BallotNum> {
  int round;
  int pid;

  public BallotNum (int r, int p) {
    round = r;
    pid = p;
  }


  public void set (BallotNum other) {
    round = other.round;
    pid = other.pid;
  }

  public int compareTo (BallotNum other) {
    if (this.round != other.round) {
      return this.round - other.round;
    } else {
      return this.pid - other.pid;
    }
  }

  @Override
  public String toString() {
    return "b(" + round + ", " + pid + ") ";
  }
}
