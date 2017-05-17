/*
Program Name: GameServer
Author: Pedraum Safarzadeh
Date: March 31 2015
Course: CPSC 1181
Compiler: JDK 1.7
*/

import java.io.*;
import java.net.*;
import java.util.Date;
import java.awt.*;
import javax.swing.*;

/**
* The server that sends and receives commands based on the memory game from two clients who are playing the game 
*/
public class GameServer extends JFrame implements CommandConstants {

   private JTextArea msgsArea;

   /**
      * The main method that creates the server
      * @param args - the line commands   
      */
   public static void main(String[] args) {
      new GameServer();
   }

   /**
      * Writes messages in the message area 
      * @param msg - the message thats written   
      */
   private void report(String msg) {
      msgsArea.append(msg + '\n');
   }

   /**
      * builds the messages area for the server that will display messages from the clients 
      */
   private void buildMsgsArea() {
      final int FRAME_WIDTH = 700;
      final int FRAME_HEIGHT = 600;
      msgsArea = new JTextArea(); //messages get displayed here
      setLayout(new BorderLayout());
      add(new JScrollPane(msgsArea), BorderLayout.CENTER);
      setTitle("GameServer");
      setSize(FRAME_WIDTH, FRAME_HEIGHT);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setVisible(true); 
   } 
   
   /**
      * The constuctor that builds the messages area for the server and creates two sockets in order to communicate with the two clients 
      */
   public GameServer() {
      buildMsgsArea();
      ServerSocket serverSocket = null;
      try {
         serverSocket = new ServerSocket(PORT);  
         report("GameServer started at " + new Date());
        
         while (true) {
            Socket socket0 = serverSocket.accept(); //player 0's socket
            Socket socket1 = serverSocket.accept(); //player 1's socket
            GameService game = new GameService(socket0, socket1, msgsArea);
            Thread t = new Thread(game);
            t.start();
         }
      } 
      catch(IOException e) {
         e.printStackTrace(System.err);
      }
      finally {
         try {
            serverSocket.close();
         }
         catch (IOException e) {
            e.printStackTrace(System.err);
         }
      }
   }
}
