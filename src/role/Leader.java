package role;

import exec.Server;
import msg.*;
import util.BallotNum;
import util.Command;
import util.Constants;
import util.Pvalue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by xiaofan on 3/26/15.
 */
public class Leader extends Role {
  public BallotNum ballotNum;
  public boolean active;
  public Map<Integer, Command> proposals;
  public int[] acceptors;
  public int[] replicas;

  public Leader(int pid, Server ctrl, int[] acceptors, int[] replicas) {
    super(pid, ctrl);
    this.acceptors = acceptors;
    this.replicas = replicas;
    ballotNum = new BallotNum(0, pid);
    active = false;
    proposals = new HashMap<Integer, Command>();
    ctrl.roles.put(pid, this);
  }

  public void exec () {
    new HeartBeater().start();
    new Scout(ctrl.nextId(), ctrl, pid, acceptors, ballotNum).start();
    while (!ctrl.shutdown) {
      Message msg = receive();
      if (ctrl.shutdown) {
        return;
      }
      if (msg instanceof ProposeMsg) {
        ProposeMsg propMsg = (ProposeMsg) msg;
        if (!proposals.containsKey(propMsg.slotNum)) {
          int s = propMsg.slotNum;
          Command p = propMsg.prop;
          proposals.put(s, p);
          if (active) {
            new Commander(ctrl.nextId(), ctrl, pid, acceptors, replicas, new
                Pvalue(ballotNum, s, p)).start();
          }
        }
      }
      if (msg instanceof AdoptedMsg) {
        AdoptedMsg adptMsg = (AdoptedMsg) msg;
        update(adptMsg.accepted);
        for (int s : proposals.keySet()) {
          new Commander(ctrl.nextId(), ctrl, pid, acceptors, replicas, new
              Pvalue(ballotNum, s, proposals.get(s))).start();
        }
        active = true;
      }
      if (msg instanceof PreemptedMsg) {
        PreemptedMsg pmptMsg = (PreemptedMsg) msg;
        if (pmptMsg.ballotNum.compareTo(ballotNum) > 0) {
          active = false;
          ballotNum = new BallotNum(pmptMsg.ballotNum.round, pid);
          new Scout(ctrl.nextId(), ctrl, pid, acceptors, ballotNum).start();
        }
      }
    }
  }

  /*
   * proposals := proposals + pmax(pvals);
   */
  public void update(Set<Pvalue> accepted) {
    Map<Integer, Pvalue> pmax = new HashMap<Integer, Pvalue>();
    for (Pvalue pv : accepted) {
      int s = pv.slotNum;
      if (!pmax.containsKey(s)) {
        pmax.put(s, pv);
      } else {
        if (pv.compareTo(pmax.get(s)) >= 0) {
          pmax.put(s, pv);
        }
      }
    }
    for (int s : pmax.keySet()) {
      proposals.put(s, pmax.get(s).prop);
    }
  }

  public String myName() {
    return "Leader";
  }


  class HeartBeater extends Thread {
    public void run() {
      while (!ctrl.shutdown) {
        broadcast(new HeartBeatMsg(pid));
        try {
          Thread.sleep(Constants.TIMEGAP);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
