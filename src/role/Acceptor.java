package role;

import msg.*;
import util.BallotNum;
import util.Pvalue;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by xiaofan on 3/26/15.
 */
public class Acceptor extends Role {
  BallotNum ballotNum = new BallotNum(-1, 0);
  Set<Pvalue> accepted = new HashSet<Pvalue>();

  public Acceptor(int id, Controller ctr) {
    super(id, ctr);
  }

  public void start () {
    while (true) {
      Message msg = receive();
      if (msg instanceof P1aMsg) {
        P1aMsg p1a = (P1aMsg) msg;
        BallotNum b = p1a.ballotNum;
        if (b.compareTo(ballotNum) > 0) {
          ballotNum.set(b);
        }
        send(p1a.src, getMsg(1));
      } else if (msg instanceof P2aMsg) {
        P2aMsg p2a = (P2aMsg) msg;
        BallotNum b = p2a.pv.ballotNum;
        if (b.compareTo(ballotNum) >= 0) {
          ballotNum.set(b);
          accepted.add(new Pvalue(p2a.pv));
        }
        send(p2a.src, getMsg(2));
      }
    }
  }

  public Message getMsg (int id) {
    switch (id) {
      case 1:
        return new P1bMsg(pid, ballotNum, new HashSet<Pvalue> (accepted));
      case 2:
        return new P2bMsg(pid, ballotNum);
      default:
        return null;
    }
  }

}
