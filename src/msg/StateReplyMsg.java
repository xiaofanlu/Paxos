package msg;

import util.Command;

import java.util.Map;

/**
 * Created by xiaofan on 3/31/15.
 * State Reply message for rebuild server
 *
 */

public class StateReplyMsg extends Message {
  public int slotNum;
  public int leaderID;
  public Map<Integer, Command> decisions;

  public StateReplyMsg(int pid, int s, Map<Integer, Command> d, int l) {
    src = pid;
    slotNum = s;
    decisions = d;
    leaderID = l;

  }

  @Override
  public String toString() {
    return "StateReply: " + src + ", " + slotNum + ", " + leaderID;
  }
}
