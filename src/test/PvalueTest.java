package test;

import org.junit.Test;
import util.BallotNum;
import util.Command;
import util.Pvalue;

public class PvalueTest {
  BallotNum b = new BallotNum(1, 1);
  Command p = new Command(1, 10, "Hello!");
  Pvalue pv = new Pvalue(b, 10, p);

  @Test
  public void testToString() throws Exception {
    pv.toString();
    assert(true);
  }
}