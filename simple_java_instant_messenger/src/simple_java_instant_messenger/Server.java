package simple_java_instant_messenger;

import java.io.*; //For streams
import java.net.*; //For sockets and networking
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
This program will sit on a public server that anyone can access.
 */
public class Server extends JFrame{
    private JTextField userText; //For writing messages
    private JTextArea chatWindow; //Displays message area
    private ObjectOutputStream output; //Output stream to connect to something(file or socket)
    private ObjectInputStream input; //inout stream to recieve data
    private ServerSocket server; //Initalize the socket
    private Socket connection; //This will be the socket that java connects the user to so they can chat
    
    //constructor that creates a GUI for the user to use
    public Server(){
        super("Awesome instant messenger!");
        userText = new JTextField();
        userText.setEditable(false); //We disable this so that we can't type in anything by default. When we connect a chat to another user we will change this.
        userText.addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Send the messaage
                sendMessage(e.getActionCommand()); //e.getActionCommand() is the message
                userText.setText(""); //Change to nothing after enter
            }
        }
        );
        
        add(userText, BorderLayout.NORTH);
        
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(300, 150);
        setVisible(true);
    }
    
    //Set up and run the server
    public void startRunning(){
        try{
            server = new ServerSocket(6789, 100); //port number and how many people can wait to access this ServerSocket. If there is too many people then the server will crash so we limit it to 100. This is technically called the q length or backlog.
            
            while(true){ //runs forever
                try{
                    //connect and have conversation
                    waitForConnection(); //Wait for someone to connect to our server
                    setupStreams(); //set up input and output
                    whileChatting(); //Allows us to send messages back and forth while we chat
                }catch(EOFException eOFException){ //EOFException means end of stream
                    showMessage("\n Server ended the connection! ");
                    eOFException.printStackTrace();
                }finally{
                    closeCrap(); //Close all sockets etc
                }
            }
            
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
    //wait for connection, then display connection information
    private void waitForConnection() throws IOException{
        showMessage("Waiting for someone to connect.. \n");
        connection = server.accept(); //Once someone asks to connect with us this will accept it and put that connection on a socket.
        showMessage("Now connectioed to "+ connection.getInetAddress().getHostName()); //getInetAddress() returns ip that the socket is connected to or null if not connected. getHostName() gets host name of ip address
    }
    
    //set up the streams to send and recieve data
    private void setUpStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream()); //connection.getOutputStream() gets the output stream that is going from the server to the user.
        output.flush(); //We don't want the buffer to try keep data but because we want the whole message sent together so we need to flush it. GOOD PRACTICE to do this.
        input = new ObjectInputStream(connection.getInputStream()); //connection.getInputStream() gets the input stream going from the computer to the server.
        //Can't use flush on input because only the stream to the user can flush. We can only flush on the server output and not on the users input. Basically we can make sure we push all the data to them but we can't go in and grab it from their computer.
        showMessage("\n Streams are now set up! \n");
    }
    
    //During the chat conversation
    private void whileChatting() throws IOException{
        String message = "You are now connected!";
        sendMessage(message); //Send message to screen
        ableToType(true); //Change userText setEditable to true
        do{
            //Have convo while clients are active
            try{
                message = (String) input.readObject(); //Read the object coming from the input stream coming from client. Cast it to a string.
                showMessage("\n" + message);
            }catch(ClassNotFoundException ex){
                //This exception will usually only be caught if the user is trying send other objects. Usually they will be sending a string.
                //If they are not they might be trying to hack us.
                showMessage("\n can't figure out what user sent. Wierd object sent.");
                ex.printStackTrace();
            }
        }while(!message.equals("CLIENT - END"));
    }
}
