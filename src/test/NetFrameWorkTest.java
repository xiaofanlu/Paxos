package test;

/**
 * Created by xiaofan on 4/7/15.
 */

import framework.Config;
import framework.NetController;

import java.util.List;

public class NetFrameWorkTest {
  public static void main(String[] args) {
    node n1 = new node(0, 2);
    node n2 = new node(1, 2);

    n1.start();
    n2.start();
    System.out.println("send1");
    for (int i = 0; i < 100; i ++) {
      n1.send(1, "from 0 to 1, how are u?");
      n1.send(1, "from 0 to 1, how are u?");
    }

    System.out.println("send2");
    for (int i = 0; i < 100; i ++) {
      n2.send(0, "from 1 to 0, how are u?");
      n2.send(0, "from 1 to 0, how are u?");
    }

    System.out.println("send3");

  }


  public static class node extends Thread {
    public int id;
    public int num;
    public Config config;
    public NetController nc;

    public node (int index, int total) {
      id = index;
      num = total;
      config = new Config(index, total);
      nc = new NetController(config);
    }

    public void send(int index, String msg) {
      nc.sendMsg(index, msg);
    }

    public void run() {
      while (true) {
        List<String> buffer = nc.getReceivedMsgs();
        for (String str : buffer) {
          System.out.println(str);
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
