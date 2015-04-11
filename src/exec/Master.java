package exec;

import util.Constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by xiaofan on 3/30/15.
 */
public class Master {
  public static boolean debug = false;
  public static Server[] servers;
  public static Client[] clients;

  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);
    while (scan.hasNextLine()) {
      String[] inputLine = scan.nextLine().split(" ");
      int clientIndex, nodeIndex;
      if (debug) {
        System.out.println("Command: " + Arrays.toString(inputLine));
      }

      if (inputLine[0].equals("start")) {
        /*
         * start up the right number of nodes and clients, and store the
         *  connections to them for sending further commands
         */
        int numNodes = Integer.parseInt(inputLine[1]);
        int numClients = Integer.parseInt(inputLine[2]);
        startExec(numNodes, numClients);
      } else if (inputLine[0].equals("sendMessage")) {
        /*
         * Instruct the client specified by clientIndex to send the message
         * to the proper paxos node
         */
        clientIndex = Integer.parseInt(inputLine[1]);
        String message = "";
        for (int i = 2; i < inputLine.length; i++) {
          message += inputLine[i];
          if (i != inputLine.length - 1) {
            message += " ";
          }
        }
        clientMsg(clientIndex, message);


      } else if (inputLine[0].equals("printChatLog")) {
        /*
         * Print out the client specified by clientIndex's chat history
         * in the format described on the handout.
         */
        clientIndex = Integer.parseInt(inputLine[1]);
        System.out.print(printCharLog(clientIndex));

      } else if (inputLine[0].equals("allClear")) {
       /*
        * Ensure that this blocks until all messages that are going to
        * come to consensus in PAXOS do, and that all clients have heard
        * of them
        */
        allClear();
      } else if (inputLine[0].equals("crashServer")) {
       /*
        * Immediately crash the server specified by nodeIndex
        */
        nodeIndex = Integer.parseInt(inputLine[1]);
        crashServer(nodeIndex);
      } else if (inputLine[0].equals("restartServer")) {
       /*
        * Restart the server specified by nodeIndex
        * introduce some delay for leader election.
        */
        nodeIndex = Integer.parseInt(inputLine[1]);
        restartServer(nodeIndex);
      } else if (inputLine[0].equals("timeBombLeader")) {
        int numMessages = Integer.parseInt(inputLine[1]);
       /*
        * Instruct the leader to crash after sending the number of paxos
        * related messages specified by numMessages
        */
        for (Server s : servers) {
          s.timeBombLeader(numMessages);
        }
      }
    }
    System.exit(1);
  }


  public static void startExec (int numServers, int numClients) {
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

  public static void clientMsg(int cid, String msg) {
    assert cid >= 0 && cid < clients.length;
    clients[cid].broadcast(msg);
  }

  public static String printCharLog(int cid) {
    assert cid >= 0 && cid < clients.length;
    return clients[cid].printChatLog();
  }

  public static void crashServer(int sid) {
    servers[sid].cleanShutDown();
  }


  /**
   * Create a new thread to rebuild the leader as we need to receive message.
   */
  public static void restartServer(int sid) {
    /* wait for socket to restart */
    takeSnap(100);
    servers[sid] = new Server(sid, servers.length, clients.length, true);
    servers[sid].recover();
    servers[sid].start();
  }


  public static void allClear() {
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
              takeSnap(200);
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


  public static void takeSnap (int time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}