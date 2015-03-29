package role;

import msg.*;
import util.Command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaofan on 3/26/15.
 */
public class Replica extends Role {
  public List<Command> state;
  public int slotNum;
  public Map<Integer, Command> proposals;
  public Map<Integer, Command> decisions;
  public int[] leaders;

  public Replica(int pid, Controller ctrl, int[] leaders, List<Command>
      initialState) {
    super(pid, ctrl);
    this.leaders = leaders;
    state = initialState;
    slotNum = 0;
    proposals = new HashMap<Integer, Command>();
    decisions = new HashMap<Integer, Command>();

    ctrl.roles.put(pid, this);
  }

  /**
   *  Helper method to get the max unused slot number
   */
  public int getMaxSlotNum() {
    int s = 0;
    while (proposals.containsKey(s) || decisions.containsKey(s)) {
      s++;
    }
    return s;
  }

  public void propose(Command p) {
    if (!decisions.containsValue(p)) {
      int s = getMaxSlotNum();
      proposals.put(s, p);
      for (int lambda : leaders) {
        send(lambda, new ProposeMsg(pid, s, p));
      }
    }
  }

  /*
   * determine the lowest unused slot number s'
   * and adds <s', p> to its set of proposals
   */

  public void perform(Command p) {
    // if it has already performed the command
    for (int s = 0; s < slotNum; s++) {
      if (decisions.get(s).equals(p)) {
         slotNum++;
        return;
      }
    }
    // execute the command
    state.add(p);
    //send(p.kappa, new ResponseMsg());
    for (int cid : ctrl.clients) {
      send(cid, new ResponseMsg(pid, slotNum, p));
    }
    slotNum++;
  }

  public void exec () {
    while (true) {
      Message msg = receive();
      if (msg instanceof RequestMsg) {
        RequestMsg rqstMsg = (RequestMsg) msg;
        propose(rqstMsg.prop);
      }
      if (msg instanceof DecisionMsg) {
        DecisionMsg dsnMsg = (DecisionMsg) msg;
        decisions.put(dsnMsg.slotNum, dsnMsg.prop);
        while (decisions.containsKey(slotNum)) {
          if (proposals.containsKey(slotNum) &&
              !decisions.get(slotNum).equals(proposals.get(slotNum))) {
            propose(proposals.get(slotNum));
          }
          perform(decisions.get(slotNum));
        }
      }
    }
  }

  public String myName() {
    return "Replica";
  }
}
