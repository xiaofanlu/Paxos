package role;

import exec.Server;
import msg.Message;
import util.Constants;
import util.MsgQueue;

/**
 * Created by xiaofan on 3/26/15.
 */
public class Role extends Thread {
  int pid;
  Server ctrl;
  MsgQueue inbox;

  public Role (int id, Server s) {
    pid = id;
    ctrl = s;
    inbox = new MsgQueue();
    if (ctrl.debug) {
     System.out.println(myName() + " created with id: " + pid);
    }
  }

  public void send (int dst, Message msg) {
    msg.dst = dst;
    ctrl.send(msg);
  }

  public void broadcast (Message msg) {
    for (int i = 0; i < ctrl.numServers; i++) {
      send(i * Constants.BASE, msg);
    }
  }

  public void exec() {
  }

  public void run() {
    exec();
    ctrl.remove(pid);
  }

  public void deliver (Message msg) {
    inbox.offer(msg);
  }

  public Message receive () {
    return inbox.poll();
  }

  public String myName() {
    return "Role";
  }


}
