package client;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output; //Out to server
    private ObjectInputStream input; //into client
    private String message;
    private String serverIP;
    private Socket connection;
    
    //constructor
    public Client(String host){
        super("Awsome client for instant messenger");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                sendMessage(event.getActionCommand()); //Send the message in the text area userText
                userText.setText("");
            }
        }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300, 150);
        setVisible(true);
    }
    
    //connect to server
    public void  startRunning(){
        try{
            connectToServer();
            setUpStreams();
            whileChatting();
        }catch(EOFException ex){
            showMessage("\n Client terminated the connection");
        }catch(IOException ex){
            ex.printStackTrace();
        }finally{
            closeCrap();
        }
    }
    
    //connect to the server
    private void connectToServer() throws IOException{
        showMessage("Hold on, attempting connection.");
        connection = new Socket(InetAddress.getByName(serverIP), 6789); //InetAddress.getByName(serverIP) returns a ip address.
        showMessage("Connected to: " + connection.getInetAddress().getHostName());
    }
    
    //set up streams to receive and send messages
    private void setUpStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream()); //to server
        output.flush(); //good practice
        input = new ObjectInputStream(connection.getInputStream()); //from server
        showMessage("\n Streams are connected. \n");
    }
}
