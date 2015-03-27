package role;

import msg.*;
import util.BallotNum;
import util.Pvalue;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by xiaofan on 3/26/15.
 */
public class Scout extends Role {
  int lambda;
  int[] acceptors;
  BallotNum b;
  Set<Integer> waitfor = new HashSet<Integer>();
  Set<Pvalue> pvalues = new HashSet<Pvalue>();

  public Scout(int pid, Controller ctl, int lambda, int[] acceptors,
               BallotNum b) {
    super(pid, ctl);
    this.lambda = lambda;
    this.acceptors = acceptors;
    this.b = b;
  }

  public void start () {
    for (int acpt : acceptors) {
      waitfor.add(acpt);
      send(acpt, new P1aMsg(pid, b));
    }

    while (true) {
      Message msg = receive();
      if (msg instanceof P1bMsg) {
        P1bMsg p1b = (P1bMsg) msg;
        if (b.compareTo(p1b.ballotNum) == 0) {
          if (waitfor.contains(p1b.src)) {
            pvalues.addAll(p1b.accepted);
            waitfor.remove(p1b.src);
          }
          if (waitfor.size() < acceptors.length / 2) {
            Message adopted = new AdoptedMsg(pid, b, pvalues);
            send(lambda, adopted);
            return; // exit();
          }
        } else {
          send(lambda, new PreemptedMsg(pid, p1b.ballotNum));
          return; // exit();
        }
      }
    }
  }
}
