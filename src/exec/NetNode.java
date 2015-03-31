package exec;

import msg.Message;
import util.Constants;
import util.MsgQueue;

/**
 * Created by xiaofan on 3/30/15.
 */
public class NetNode extends Thread {
  public int pid;
  public int numServers;
  MsgQueue inbox;
  NetSim ns;

  int[] acceptors;
  int[] replicas;
  public int[] clients;

  public NetNode(NetSim net, int id, int numServers, int numClients) {
    ns = net;
    pid = id;
    this.numServers = numServers;
    inbox = new MsgQueue();

    acceptors = new int[numServers];
    replicas = new int[numServers];
    for (int i = 1; i <= numServers; i++) {
      acceptors[i - 1] = combine(i, Constants.ACCEPTOR);
      replicas[i - 1]  = combine(i, Constants.REPLICA);
    }
    clients = new int[numClients];
    for (int i = 0; i < numClients; i++) {
      clients[i]  = i;
    }

  }

  /**
   * Send message to Network simulator
   * @param msg
   */
  public void send(Message msg) {
    ns.send(msg);
  }

  public void send(int dst, Message msg) {
    msg.dst = dst;
    send(msg);
  }

  public void deliver (Message msg) {
    inbox.offer(msg);
  }

  public Message receive () {
    return inbox.poll();
  }

  public int combine(int sid, int pid) {
    return sid * Constants.BASE + pid;
  }
}
