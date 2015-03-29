package role;

import msg.Message;
import util.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaofan on 3/26/15.
 */
public class Controller {
  public static int id = 0;
  public static final int numOfLeaders = 1;
  public static boolean debug = true;

  int[] replicas;
  int[] clients;
  int[] leaders;
  int[] acceptors;
  Map<Integer, Role> roles;

  public Controller(int numOfServers, int numOfClients) {
    roles = new HashMap<Integer, Role>();

    replicas = new int[numOfServers];
    clients = new int[numOfClients];
    leaders = new int[numOfLeaders];
    int numOfAcceptors = numOfServers * 2 - 1;
    acceptors = new int[numOfAcceptors];

    // start all the servers/replicas
    for (int i = 0; i < numOfServers; i++) {
      int id = nextId();
      replicas[i] = id;
      List<Command> initialState = new ArrayList<Command>();
      new Replica(id, this, leaders, initialState).start();
    }

    /**
     * start all the acceptors
     * Acceptors needs to be started before leader for scout?
     */
    for (int i = 0; i < numOfAcceptors; i++) {
      int id = nextId();
      acceptors[i] = id;
      new Acceptor(id, this).start();
    }


    // start all the leaders
    for (int i = 0; i < numOfLeaders; i++) {
      int id = nextId();
      leaders[i] = id;
      new Leader(id, this, acceptors, replicas).start();
    }

 // start all the clients
    for (int i = 0; i < numOfClients; i++) {
      int id = nextId();
      clients[i] = id;
      new Client(id, this, i).start();
    }

  }


  /**
   * tells a client to send a message to the chat room
   */
  public void sendMessage(int idx, String msg) {
    Client c = (Client) roles.get(clients[idx]);
    c.broadcast(msg);
  }

  public synchronized int nextId() {
    return id++;
  }

  public void send(Message msg) {
    if (roles.containsKey(msg.dst)) {
      roles.get(msg.dst).deliver(msg);
      System.out.println(msg.print());
    } else {
      System.out.println("Dst not found");
    }
  }

  public void remove(int pid) {
    roles.remove(pid);
  }
}
