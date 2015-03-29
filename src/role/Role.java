package role;

import msg.Message;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xiaofan on 3/26/15.
 */
public class Role extends Thread {
  int pid;
  Controller ctrl;
  Queue<Message> inbox;

  public Role (int id, Controller c) {
    pid = id;
    ctrl = c;
    inbox = new LinkedBlockingQueue<Message>();
    if (ctrl.debug) {
     System.out.println(myName() + " created with id: " + pid);
    }
  }

  public void send (int dst, Message msg) {
    msg.dst = dst;
    ctrl.send(msg);
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
