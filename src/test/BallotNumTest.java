package test;

import util.BallotNum;

import static org.junit.Assert.assertEquals;

public class BallotNumTest {

  BallotNum b1 = new BallotNum(1, 1);
  BallotNum b2 = new BallotNum(2, 1);
  @org.junit.Test
  public void testCompareTo() throws Exception {
    assert(b1.compareTo(b2) < 0);
  }

  @org.junit.Test
  public void testToString() throws Exception {
    BallotNum b1 = new BallotNum(1, 1);
    String expected = "util.BallotNum => round: 1, pid: 1";
    assertEquals(b1.toString(), expected);
  }
}