package role;

import exec.Server;
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
  p1aSender sender = new p1aSender();

  public Scout(int pid, Server ctrl, int lambda, int[] acceptors,
               BallotNum b) {
    super(pid, ctrl);
    this.lambda = lambda;
    this.acceptors = acceptors;
    this.b = b;

    ctrl.roles.put(pid, this);
  }

  @Override
  public void exec () {
    for (int acpt : acceptors) {
      waitfor.add(acpt);
      send(acpt, new P1aMsg(pid, b));
    }
    sender.start();

    while (!ctrl.shutdown) {
      Message msg = receive();
      if (ctrl.shutdown) {
        sender.finished = true;
        return;
      }
      if (msg instanceof P1bMsg) {
        P1bMsg p1b = (P1bMsg) msg;
        if (b.compareTo(p1b.ballotNum) == 0) {
          if (waitfor.contains(p1b.src)) {
            pvalues.addAll(p1b.accepted);
            waitfor.remove(p1b.src);
          }
          if (waitfor.size() < (acceptors.length + 1) / 2) {
            Message adopted = new AdoptedMsg(pid, b, pvalues);
            send(lambda, adopted);
            sender.finished = true;
            return; // exit();
          }
        } else {
          send(lambda, new PreemptedMsg(pid, p1b.ballotNum));
          sender.finished = true;
          return; // exit();
        }
      }
    }
  }


  public String myName() {
    return "Scout";
  }

  public class p1aSender extends Thread {
    boolean finished = false;

    public void run () {
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      while (!finished) {
        for (int acpt : waitfor) {
          send(acpt, new P1aMsg(pid, b));
        }
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
