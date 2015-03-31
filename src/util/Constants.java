package util;

/**
 * Created by xiaofan on 3/30/15.
 */
public class Constants {
  public static final int BASE = 10000;

  /**
   * reserved process id for replica, acceptor and leader
   */
  public static final int REPLICA = 0;
  public static final int ACCEPTOR = 1;
  public static final int LEADER = 2;


  /**
   * For heart beat message
   */
  public static final int TIMEOUT = 500;
  public static final int TIMEGAP = 100;
}
