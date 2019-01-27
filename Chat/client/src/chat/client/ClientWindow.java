package chat.client;

import chat.network.TCPConnection;
import chat.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {
    private static  final String IP ="127.0.0.1";
  //  private static  final String IP ="192.168.1.6";
    private static  final int PORT =8081;

    public static void main(String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new ClientWindow();
                }
            });
    }
    private final JTextArea log = new JTextArea();
    private final JTextField nickname = new JTextField("Alex");
    private final JTextField input = new JTextField();

    private TCPConnection connection;

    private ClientWindow(){
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(1000,1000);
            setLocationRelativeTo(null);

            log.setEditable(false);
            log.setLineWrap(true);
            add(log, BorderLayout.CENTER);
            input.addActionListener(this);
            add(input, BorderLayout.SOUTH);

            add(nickname, BorderLayout.NORTH);

        setVisible(true);
        try {
            connection = new TCPConnection(this,IP,PORT);
        } catch (IOException e) {
           printMessage("Exception"+e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = input.getText();
        if(msg.equals(""))return;
        input.setText(null);
        connection.sendString(nickname.getText()+": "+ msg);

    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready");
    }

    @Override
    public synchronized void onReciveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connecton closed");
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
      printMessage("Exception"+e);
    }

    private synchronized void printMessage(String msg){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
              //  log.setCaretPosition(log.getDocument().getLength()); //автоскролл
            }
        });
    }
}
