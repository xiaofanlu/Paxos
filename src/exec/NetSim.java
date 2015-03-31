package exec;

import msg.HeartBeatMsg;
import msg.Message;
import msg.ResponseMsg;
import util.Constants;

/**
 * Created by xiaofan on 3/30/15.
 */
public class NetSim {
  public static final boolean debug = false;

  public Server[] servers;
  public Client[] clients;

  public NetSim(int numServers, int numClients) {
    servers = new Server[numServers];
    for (int i = 1; i <= numServers; i++) {
      servers[i - 1] = new Server(i, numServers, numClients, this, false);
    }

    clients = new Client[numClients];
    for (int i = 0; i < numClients; i++) {
      clients[i] = new Client(i, numServers, numClients, this);
      clients[i].start();
    }

    for (int i = numServers - 1; i >= 0; i--) {
      servers[i].start();
    }
  }

  public void send(Message msg) {
    if (debug) {
      //System.out.println(msg.print());
      if (!(msg instanceof HeartBeatMsg)) {
        System.out.println(msg.print());
      }
    }
    if (msg instanceof ResponseMsg) {
      /* only message to client */
      int clientId = Math.abs(msg.dst);
      assert clientId < clients.length;
      clients[clientId].deliver(msg);
     // System.out.println("Delivered to client: " + clientId);
    } else {
      int serverId = msg.dst / Constants.BASE;
     // System.out.println("*********>>>>" + serverId + "\t" + msg.dst);
      assert serverId <= servers.length;
      servers[serverId - 1].deliver(msg);
    }
  }

  public void clientMsg(int cid, String msg) {
    assert cid >= 0 && cid < clients.length;
    clients[cid].broadcast(msg);
  }

  public String printCharLog(int cid) {
    assert cid >= 0 && cid < clients.length;
    return clients[cid].printChatLog();
  }

  public void crashServer (int sid) {
    servers[sid].cleanShutDown();
  }


  /**
   *  Create a new thread to rebuild the leader as we need to receive message.
   */
  public void restartServer (int sid) {
    servers[sid] = new Server(sid + 1, servers.length, clients.length, this,
        true);
    servers[sid].recover();
    servers[sid].start();
  }
}
