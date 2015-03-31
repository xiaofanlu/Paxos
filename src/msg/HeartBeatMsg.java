package msg;

/**
 * Created by xiaofan on 3/31/15.
 * HeartBeat message for Leader
 */

public class HeartBeatMsg extends Message {

  public HeartBeatMsg(int pid) {
    src = pid;
  }

  @Override
  public String toString() {
    return "HeartBeat : " + src;
  }
}
