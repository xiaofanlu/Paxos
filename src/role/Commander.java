package role;

import exec.Server;
import msg.*;
import util.BallotNum;
import util.Constants;
import util.Pvalue;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by xiaofan on 3/26/15.
 */
public class Commander extends Role {
  int lambda;
  int[] acceptors;
  int[] replicas;
  Pvalue pv;
  BallotNum b;
  Set<Integer> waitfor = new HashSet<Integer>();
  p2aSender sender = new p2aSender();

  public Commander(int pid, Server ctrl, int lambda, int[] acceptors,
                   int[] replicas, Pvalue p) {
    super(pid, ctrl);
    this.lambda = lambda;
    this.acceptors = acceptors;
    this.replicas = replicas;
    this.pv = p;
    this.b = p.ballotNum;


    ctrl.roles.put(pid, this);
  }

  public void exec() {
    // performance trick, send to non-leader acceptor first
    for (int acpt : acceptors) {
      if (acpt/ Constants.BASE != ctrl.leaderID) {
        waitfor.add(acpt);
        send(acpt, new P2aMsg(pid, pv));
      }
    }
    for (int acpt : acceptors) {
      if (acpt/ Constants.BASE == ctrl.leaderID) {
        waitfor.add(acpt);
        send(acpt, new P2aMsg(pid, pv));
      }
    }
    sender.start();


    while (!ctrl.shutdown) {
      Message msg = receive();
      if (ctrl.shutdown) {
        sender.finished = true;
        return;
      }
      if (msg instanceof P2bMsg) {
        P2bMsg p2b = (P2bMsg) msg;
        if (b.compareTo(p2b.ballotNum) == 0) {
          if (waitfor.contains(p2b.src)) {
            waitfor.remove(p2b.src);
          }
          if (waitfor.size() < (acceptors.length + 1) / 2) {
            for (int p : replicas) {
              Message decision = new DecisionMsg(pid, pv.slotNum, pv.prop);
              send(p, decision);
            }
            sender.finished = true;
            return; // exit();
          }
        } else {
          send(lambda, new PreemptedMsg(pid, p2b.ballotNum));
          sender.finished = true;
          return; // exit();
        }
      }
    }
  }

  public String myName() {
    return "Commander";
  }


  public class p2aSender extends Thread {
    boolean finished = false;

    public void run () {
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      while (!finished) {
        HashSet<Integer> waitSet = new HashSet<Integer>(waitfor);
        for (int acpt : waitSet) {
          send(acpt, new P2aMsg(pid, pv));
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
