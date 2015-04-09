package exec;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by xiaofan on 3/30/15.
 */
public class Master {
  public static boolean debug = false;

  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);
    int numNodes = 0, numClients = 0;
    NetSim ns = null;

    while (scan.hasNextLine()) {
      String[] inputLine = scan.nextLine().split(" ");
      int clientIndex, nodeIndex;
      //System.out.println(inputLine[0]);
      if (debug) {
        System.out.println("Command: " + Arrays.toString(inputLine));
      }
        if (inputLine[0].equals("start")) {
        numNodes = Integer.parseInt(inputLine[1]);
        numClients = Integer.parseInt(inputLine[2]);
              /*
               * start up the right number of nodes and clients, and store the
               *  connections to them for sending further commands
               */
        ns = new NetSim(numNodes, numClients);
        // startExec(numNodes, numClients);

      } else if (inputLine[0].equals("sendMessage")) {
        clientIndex = Integer.parseInt(inputLine[1]);
        String message = "";
        for (int i = 2; i < inputLine.length; i++) {
          message += inputLine[i];
          if (i != inputLine.length - 1) {
            message += " ";
          }
        }
        assert ns != null;
        ns.clientMsg(clientIndex, message);

              /*
               * Instruct the client specified by clientIndex to send the message
               * to the proper paxos node
               */

      } else if (inputLine[0].equals("printChatLog")) {
        clientIndex = Integer.parseInt(inputLine[1]);
              /*
               * Print out the client specified by clientIndex's chat history
               * in the format described on the handout.
               */
        System.out.print(ns.printCharLog(clientIndex));

      } else if (inputLine[0].equals("allClear")) {/*
               * Ensure that this blocks until all messages that are going to
               * come to consensus in PAXOS do, and that all clients have heard
               * of them
               */
        ns.allClear();
      } else if (inputLine[0].equals("crashServer")) {
        nodeIndex = Integer.parseInt(inputLine[1]);
              /*
               * Immediately crash the server specified by nodeIndex
               */

        ns.crashServer(nodeIndex);
      } else if (inputLine[0].equals("restartServer")) {
        nodeIndex = Integer.parseInt(inputLine[1]);
              /*
               * Restart the server specified by nodeIndex
               * introduce some delay for leader election.
               */
        ns.restartServer(nodeIndex);

      } else if (inputLine[0].equals("timeBombLeader")) {
        int numMessages = Integer.parseInt(inputLine[1]);
              /*
               * Instruct the leader to crash after sending the number of paxos
               * related messages specified by numMessages
               */
        for (int i = 0; i < numNodes; i++) {
          ns.servers[i].timeBombLeader(numMessages);
        }
      }
    }
    System.exit(1);
  }

}