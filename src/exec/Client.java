package exec;

/**
 * Created by xiaofan on 3/28/15.
 */

import msg.Message;
import msg.RequestMsg;
import msg.ResponseMsg;
import util.Command;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class Client extends NetNode {
  public boolean GUI_on = true;


  int sequenceNum;
  /* client id, negative num for pid */
  int cid;
  Map<Integer, ResponseMsg> log;


  // For GUI
  JFrame frame;
  JTextField textField;
  JTextArea messageArea;


  public Client(int cid, int numServers, int numClients) {
    super(numServers + cid, numServers, numClients);

    this.sequenceNum = 0;
    this.cid = cid;

    log = new HashMap<Integer, ResponseMsg>();
    // Layout GUI
    if (GUI_on) {
      guiSetup();
    }
  }

  public void run() {
    while (true){
      Message msg = receive();
      if (msg instanceof ResponseMsg) {
        ResponseMsg rspnMsg = (ResponseMsg) msg;
        if (!log.containsKey(rspnMsg.slotNum)) {
          log.put(rspnMsg.slotNum, rspnMsg);
          if (GUI_on) {
            messageArea.append(rspnMsg.format() + "\n");
          }
        }
      }
    }
  }

  class TextInput implements ActionListener {
    /**
     * Responds to pressing the enter key in the textfield by sending
     * the contents of the text field to the server.    Then clear
     * the text area in preparation for the next message.
     */
    public void actionPerformed(ActionEvent e) {
      String text = textField.getText();
      textField.setText("");
      broadcast(text);
    }
  }

  public void broadcast (String s) {
    Command prop = new Command(cid, sequenceNum, s);
    sequenceNum++;
    for (int id : replicas) {
      send(id, new RequestMsg(pid, prop));
    }
  }

  public void guiSetup() {
    int windowWidth = 10;
    int windowHeight = 50;


    textField = new JTextField(24);
    messageArea = new JTextArea(35, 24);  //(h, w)
    textField.setEditable(false);
    messageArea.setEditable(false);
    frame = new JFrame("Client " + cid);
    frame.getContentPane().add(textField, "South");
    frame.getContentPane().add(new JScrollPane(messageArea), "Center");
    frame.setSize(windowWidth, windowHeight);
    frame.setLocation(windowWidth * 30 * cid, 0);
    frame.pack();

    // Add Listeners
    textField.addActionListener(new TextInput());

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    textField.setEditable(true);
  }

  public String printChatLog () {
    StringBuilder sb = new StringBuilder();
    int slotNum = 0;
    while (log.containsKey(slotNum)) {
      sb.append(log.get(slotNum).format() + "\n");
      slotNum++;
    }
    return sb.toString();
  }


  public String myName() {
    return "Client";
  }
}



