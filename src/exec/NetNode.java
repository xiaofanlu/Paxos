package exec;

import framework.Config;
import framework.NetController;
import msg.HeartBeatMsg;
import msg.Message;
import org.apache.commons.codec.binary.Base64;
import util.Constants;
import util.MsgQueue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Created by xiaofan on 3/30/15.
 */
public class NetNode extends Thread {
  public boolean debug = true;

  public int pid;
  public int numServers;
  MsgQueue inbox;

  int[] acceptors;
  int[] replicas;
  public int[] clients;

  Config config;
  NetController nc;

  public volatile boolean shutdown = false;
  public volatile boolean busy = false;
  public volatile int count = 0;


  public NetNode(int id, int numServers, int numClients) {
    pid = id;
    this.numServers = numServers;
    inbox = new MsgQueue();

    acceptors = new int[numServers];
    replicas = new int[numServers];
    for (int i = 0; i < numServers; i++) {
      acceptors[i] = combine(i, Constants.ACCEPTOR);
      replicas[i]  = combine(i, Constants.REPLICA);
    }
    clients = new int[numClients];
    for (int i = 0; i < numClients; i++) {
      clients[i]  = (i + numServers) * Constants.BASE;
    }

    int numNodes = numServers + numClients;
    config = new Config(id, numNodes);
    nc = new NetController(config);
    new Listener().start();
  }

  /**
   * Send message to Network simulator
   * @param msg
   */
  public void send(Message msg) {
    //ns.send(msg);
    int serverId = msg.dst / Constants.BASE;
    if (debug) {
      if (!(msg instanceof HeartBeatMsg)) {
        System.out.println("To: " + serverId + " "+  msg.print());
      }
    }
    nc.sendMsg(serverId, serialize(msg));
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


  /**
   * Translate the Message to a string to transmit through socket
   * Don't want to modify existing socket framework
   */
  public String serialize (Message msg) {
    String rst = "";
    try {
      ByteArrayOutputStream bo = new ByteArrayOutputStream();
      ObjectOutputStream so = new ObjectOutputStream(bo);
      so.writeObject(msg);
      so.flush();

      //rst = bo.toString();
      rst = new String(Base64.encodeBase64(bo.toByteArray()));
    } catch (Exception e) {
      System.out.println(e);
    }
    return rst;
  }

  /**
   * Translate String to a Message upon receiving from socket
   * Don't want to modify existing socket framework
   */
  public Message deserialize (String str) {
    Message msg = null;
    try {
      //byte b[] = str.getBytes();
      //byte b[] = str.getBytes("ISO-8859-1");
      byte b[] = Base64.decodeBase64(str.getBytes());
      ByteArrayInputStream bi = new ByteArrayInputStream(b);
      ObjectInputStream si = new ObjectInputStream(bi);
      msg = (Message) si.readObject();
    } catch (Exception e) {
      System.out.println(e);
    }
    return msg;
  }

  /**
   * Clean shutdown the server, including all the
   * Replica, Acceptor, Leader, etc. associated with this server
   */
  public void cleanShutDown () {
    shutdown = true;
    nc.shutdown();
    try {
      Thread.sleep(300);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Inner listener thread
   */
  class Listener extends Thread {
    public void run() {
      while (!shutdown) {
        List<String> buffer = nc.getReceivedMsgs();
        for (String str : buffer) {
          Message msg = deserialize(str);
          if (msg != null) {
            deliver(msg);
            //System.out.println(pid + ": " + msg.print());
            if (!(msg instanceof HeartBeatMsg)) {
              busy = true;
              count = 0;
            } else if (busy) {
              /*
               * set to non-busy after ten consecutive heartbeat
               */
              count++;
              if (count == 5) {
                busy = false;
              }
            }
          }
        }
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
