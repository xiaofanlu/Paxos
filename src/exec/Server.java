package exec;

import msg.*;
import role.Acceptor;
import role.Leader;
import role.Replica;
import role.Role;
import util.Command;
import util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xiaofan on 3/30/15.
 */



public class Server extends NetNode {
  public static boolean debug = false;

  /**
   * Server identifier
   */
  public int index;

  public int leaderID;

  public boolean shutdown = false;

  /**
   * For time bomb leader
   */
  public int timer = -1;
  ReentrantLock timerLock = new ReentrantLock();

  /**
   * Local server process sequence number
   * Reserved ID :
   * Replica : 0
   * Acceptor : 1
   * Leader : 2
   */
  public int id = 3;


  /**
   * Map from process sequence number to Role
   */
  public Map<Integer, Role> roles;

  Replica replica;
  Acceptor acceptor;
  Leader leader;

  HeartBeatTimer hbt;

  public Server(int idx, int numSevers, int numClients, NetSim net,
                boolean restart) {
    super(net, idx, numSevers, numClients);
    index = idx;
    roles = new HashMap<Integer, Role>();

    if (!restart) {
      leaderID = 1;
      /* initiates replica */
      Map<Integer, Command> committed = new HashMap<Integer, Command>();
      int rpid = combine(index, Constants.REPLICA);
      replica = new Replica(rpid, this, committed, 0);

      initialization();
    }
  }

  public void recover () {
    int rpid = combine(index, Constants.REPLICA);
    StateReplyMsg srm = getReply(rpid);
    if (srm != null) {
      if (debug) {
        System.out.println("State Reply Received!!");
        System.out.println(srm);
      }
      leaderID = srm.leaderID;
      replica = new Replica(rpid, this, srm.decisions, srm.slotNum);
      initialization();
    }
  }

  public void initialization () {
      /* initiates acceptor */
    acceptor = new Acceptor(combine(index, Constants.ACCEPTOR), this);

    if (isLeader()) {
      leader = new Leader(combine(index, Constants.LEADER), this,
          acceptors, replicas);
    } else {
      leader = null;
    }

    hbt = new HeartBeatTimer();
  }



  public StateReplyMsg getReply (int src) {
    for (int pid : replicas) {
      send(pid, new StateQueryMsg(src));
    }

    while (!shutdown) {
      Message msg = receive();
      if (shutdown) {
        return null;
      }
      if (msg instanceof StateReplyMsg) {
        return (StateReplyMsg) msg;
      }
    }
    return null;
  }


  public void run () {
    acceptor.start();
    replica.start();
    if (isLeader()) {
      leader.start();
    }
    hbt.start();


    while (!shutdown) {
      Message msg = receive();
      if (shutdown) {
        return;
      }
      if (msg instanceof HeartBeatMsg) {
        hbt.reset();
        if (debug) {
          //System.out.println (index + ": Timer refreshed");
        }
        int viewNum = msg.src / Constants.BASE;
        if (viewNum != leaderID) {
          leaderID = viewNum;
          if (debug) {
            System.out.println ("New leader detected: " + leaderID);
          }
        }
      } else  if (msg != null) {
        relay(msg);
      }
    }
  }

  public boolean isLeader() {
    return leaderID == index;
  }

  public int getLeader () {
    return combine(leaderID, Constants.LEADER);
  }

  public synchronized int nextId() {
    assert (id < Constants.BASE);
    return combine(index, id++);
  }

  /**
   *  Relay message to local process
   */
  public void relay(Message msg) {
    if (roles.containsKey(msg.dst)) {
      roles.get(msg.dst).deliver(msg);
      //System.out.println(msg.print());
    } else {
      if (debug) {
        System.out.print("\nServer: " + index + ": Dst not found: " + msg.print());
      }
    }
  }


  public void remove(int pid) {
    roles.remove(pid);
  }


  /**
   *
   * Send message to Network simulator
   * to support timebomb leader
   * @param msg
   */
  @Override
  public void send(Message msg) {
    if (msg instanceof P1aMsg || msg instanceof P2aMsg) {
      timerLock.lock();
      if (timer == 0) {
        System.out.println("Shutdown Now!");
        cleanShutDown();
        return;
      }
      if (timer > 0) {
        timer--;
        System.out.println("Shutdown timer: " + timer);
      }
      timerLock.unlock();
    }
    ns.send(msg);
  }

  public void timeBombLeader (int count) {
    timerLock.lock();
    if (isLeader()) {
      if (count == 0) {
        cleanShutDown();
      } else {
        timer = count;
      }
    }
    timerLock.unlock();
  }

  /**
   * Leader election with basic round robin fashion
   */
  public void leaderElection () {
    leaderID++;
    if (leaderID > numServers) {
      leaderID = 1;
    }
    if (isLeader()) {
      leader = new Leader(combine(index, Constants.LEADER), this,
          acceptors, replicas);
      leader.start();
    } else {
      leader = null;
    }
  }


  /**
   * Clean shutdown the server, including all the
   * Replica, Acceptor, Leader, etc. associated with this server
   */
  public void cleanShutDown () {
    shutdown = true;
  }


  public class HeartBeatTimer extends Thread{
    public TimeoutTask task;
    public Timer timer;


    public HeartBeatTimer(){
      timer = new Timer();
      task = new TimeoutTask();
    }

    public void cancel () {
      task.cancel();
    }

    public void reset() {
      setTimeout(Constants.TIMEOUT);
    }

    @Override
    public void run(){
      setTimeout(Constants.TIMEOUT * 2);
    }

    public void setTimeout(long delay){
      try {
        task.cancel();
        task = new TimeoutTask();
        timer.schedule(task, delay);
      } catch (IllegalStateException e) {
        //e.printStackTrace();
      }
    }
  }

  public class TimeoutTask extends TimerTask{
    /*
     * (non-Javadoc)
     * @see java.util.TimerTask#run()
     * Failed to receive heart-beat within some time.
     * Election and termination.
     */
    @Override
    public void run(){
      if (debug) {
        System.out.println(" >>> time out!!! Leader must be down...");
      }
      leaderElection();
    }
  }
}
