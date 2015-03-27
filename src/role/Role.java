package role;

import msg.Message;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xiaofan on 3/26/15.
 */
public class Role extends Thread {
  int pid;
  Controller ctr;
  Queue<Message> inbox;

  public Role (int id, Controller c) {
    pid = id;
    ctr = c;
    inbox = new LinkedBlockingQueue<Message>();
  }

  public void send (int dst, Message msg) {
    msg.dst = dst;
    ctr.send(msg);
  }

  public void deliver (Message msg) {
    inbox.offer(msg);
  }

  public Message receive () {
    return inbox.poll();
  }


}
