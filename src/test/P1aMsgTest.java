package test;

import msg.Message;
import msg.P1aMsg;
import org.junit.Test;
import util.BallotNum;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by xiaofan on 3/28/15.
 */

public class P1aMsgTest {
  final String logPath = "./msg.txt";

  @Test
  public void test1() {
    Message msg = new P1aMsg(10, new BallotNum(1, 10));
    flushToDisk(msg);
    Message msg2 = loadFromDisk();
    assertTrue(msg2 instanceof P1aMsg);
    P1aMsg p1a = (P1aMsg) msg2;
    assertEquals(p1a.ballotNum.round, 1);
    assertEquals(p1a.ballotNum.pid, 10);
    assertEquals(p1a.src, 10);
  }

  /**
   * Load log from persistent storage at logPath.
   */
  @SuppressWarnings("unchecked")
  public Message loadFromDisk() {
    ObjectInputStream inputStream = null;
    Message rst = null;

    try {
      inputStream = new ObjectInputStream(new FileInputStream(logPath));
      rst = (Message) inputStream.readObject();
    } catch (Exception e) {
      return null;
    } finally {
      // if log did not exist, creating empty entries list
      try {
        if (inputStream != null) {
          inputStream.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      return rst;
    }
  }

  /**
   * Writes the log to persistent storage at logPath.
   */
  public void flushToDisk(Message msg) {
    ObjectOutputStream outputStream = null;

    try {
      outputStream = new ObjectOutputStream(new FileOutputStream(logPath));
      outputStream.writeObject(msg);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (outputStream != null) {
          outputStream.flush();
          outputStream.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
