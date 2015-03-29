package role;

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


public class Client extends Role {
  int sequenceNum = 0;
  int cid;

  // For GUI
  JFrame frame;
  JTextField textField;
  JTextArea messageArea;
  Map<Integer, Command> log;


  public Client(int pid, Controller ctrl, int cid) {

    super(pid, ctrl);
    this.cid = cid;
    log = new HashMap<Integer, Command>();
    // Layout GUI
    guiSetup();

    ctrl.roles.put(pid, this);
  }



  public void exec() {

    while (true){
      Message msg = receive();
      if (msg instanceof ResponseMsg) {
        ResponseMsg rspnMsg = (ResponseMsg) msg;
        if (!log.containsKey(rspnMsg.slotNum)) {
          log.put(rspnMsg.slotNum, rspnMsg.prop);
          messageArea.append(rspnMsg.format() + "\n");
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
    for (int id : ctrl.replicas) {
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

  public String myName() {
    return "Client";
  }
}



