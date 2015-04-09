package exec;

import util.Constants;

import java.util.HashSet;

/**
 * Created by xiaofan on 3/30/15.
 * For test only
 */
public class NetSim {
  public boolean debug = false;

  public Server[] servers;
  public Client[] clients;

  public NetSim(int numServers, int numClients) {
    servers = new Server[numServers];
    for (int i = 0; i < numServers; i++) {
      servers[i] = new Server(i, numServers, numClients, false);
    }

    clients = new Client[numClients];
    for (int i = 0; i < numClients; i++) {
      clients[i] = new Client(i, numServers, numClients);
      clients[i].start();
    }

    for (int i = numServers - 1; i >= 0; i--) {
      servers[i].start();
    }
  }

  /*
   * // For test only, no longer used
  public void send(Message msg) {
    if (debug) {
      //System.out.println(msg.print());
      if (!(msg instanceof HeartBeatMsg)) {
        System.out.println(msg.print());
      }
    }
    if (msg instanceof ResponseMsg) {
      // only message to client
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
  */

  public void clientMsg(int cid, String msg) {
    assert cid >= 0 && cid < clients.length;
    clients[cid].broadcast(msg);
  }

  public String printCharLog(int cid) {
    assert cid >= 0 && cid < clients.length;
    return clients[cid].printChatLog();
  }

  public void crashServer(int sid) {
    servers[sid].cleanShutDown();
  }


  /**
   * Create a new thread to rebuild the leader as we need to receive message.
   */
  public void restartServer(int sid) {
    /* wait for socket to restart */
    takeSnap(100);
    servers[sid] = new Server(sid, servers.length, clients.length, true);
    servers[sid].recover();
    servers[sid].start();
  }


  public void allClear() {
    if (true) {

      // must be longer than timeout for heart beat
      takeSnap(Constants.TIMEOUT + 50);
      HashSet<Server> busyNode = new HashSet<Server>();
      for (Server s : servers) {
        if (s!= null && !s.shutdown) {
          busyNode.add(s);
          // wait for new leader election
          while (servers[s.leaderID].shutdown) {
            takeSnap(Constants.TIMEOUT);
          }
        }
      }
      while (true) {
       for (Server s : servers) {
          if (busyNode.contains(s) && !s.busy) {
            if (debug) {
              System.out.println(s.pid + " is not busy, remove");
            }
            busyNode.remove(s);
            if (busyNode.isEmpty()) {
              if (debug) {
                System.out.println("\n>>>>>>>>>>>>>>>>> all clear " +
                    "<<<<<<<<<<<<<<<<<<\n");
              }
              takeSnap(50);
              return;
            }
          }
        }
        for (Server s : busyNode) {
          if (debug) {
            System.out.println("Still waiting for node : " + s.pid);
          }
        }
        takeSnap(100);
      }

    } else {
        takeSnap(300);
    }
  }


  public void takeSnap (int time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


}
