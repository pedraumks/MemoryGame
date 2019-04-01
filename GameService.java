import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import java.util.Random;

/**
* The service that allows the server to communicate with the two clients  
*/
public class GameService implements Runnable, CommandConstants {

   private Socket socket0;
   private Socket socket1;   
   private DataInputStream fromClient0;
   private DataOutputStream toClient0;
   private DataInputStream fromClient1;
   private DataOutputStream toClient1;
   private JTextArea msgsArea;
   private String[] backOfCard;
   private Random random;
   private int client0NumOfMatchingPairs;
   private int client1NumOfMatchingPairs;
   
   public static final int PLAYER0 = 0;
   public static final int PLAYER1 = 1;
   public static final int NUM_OF_CARDS = 12;
   public static final int CARD_0 = 0;
   public static final int CARD_1 = 1;
   public static final int CARD_2 = 2;
   public static final int CARD_3 = 3;
   public static final int CARD_4 = 4;
   public static final int CARD_5 = 5;
   public static final int CARD_6 = 6;
   public static final int CARD_7 = 7;
   public static final int CARD_8 = 8;
   public static final int CARD_9 = 9;
   public static final int CARD_10 = 10;
   public static final int CARD_11 = 11; 
   public static final int MAX_NUM_OF_PAIRS = 6;

   /**
      * The constuctor that creates the two sockets and the messages area for the server
      * @param s0 - socket for player 0
      * @param s1 - socket for player 1
      * @param mA - messages area for the server   
      */
   public GameService(Socket s0, Socket s1, JTextArea mA) {
      socket0 = s0;
      socket1 = s1;
      msgsArea = mA;
      random = new Random();
      backOfCard = new String[NUM_OF_CARDS];
      backOfCard[CARD_0] = "1";
      backOfCard[CARD_1] = "1";
      backOfCard[CARD_2] = "2";
      backOfCard[CARD_3] = "2";
      backOfCard[CARD_4] = "3";
      backOfCard[CARD_5] = "3";
      backOfCard[CARD_6] = "4";
      backOfCard[CARD_7] = "4";
      backOfCard[CARD_8] = "5";
      backOfCard[CARD_9] = "5";
      backOfCard[CARD_10] = "6";
      backOfCard[CARD_11] = "6";
   }

   /**
      * Writes messages in the message area 
      * @param msg - the message thats written   
      */
   private void report(String msg) {
      msgsArea.append(msg + '\n');
   }
   
   /**
      * Sends and receives messages from the two clients  
      */
   public void run() {
      try {
         try {
            fromClient0 = new DataInputStream(socket0.getInputStream());
            toClient0 = new DataOutputStream(socket0.getOutputStream());
            fromClient1 = new DataInputStream(socket1.getInputStream());
            toClient1 = new DataOutputStream(socket1.getOutputStream());
            executeCmds();
         }
         finally {
            fromClient0.close();
            toClient0.close();
            socket0.close();
            fromClient1.close();
            toClient1.close();
            socket1.close();
         }
      }
      catch (Exception e) {  
         report(e.toString() + "\n");
         e.printStackTrace(System.err);
      }
   }
   
   /**
      * Does the command received from the client and sends commands to the client  
      */
   public void executeCmds() throws IOException {
      shuffle();
      toClient0.writeInt(SHUFFLE);
      toClient0.flush();
      toClient1.writeInt(SHUFFLE);
      toClient1.flush();      
      for (int i = 0; i < backOfCard.length; i++) {
         toClient0.writeInt(Integer.parseInt(backOfCard[i]));
         toClient0.flush();
         toClient1.writeInt(Integer.parseInt(backOfCard[i]));
         toClient1.flush();
      }
      
      while (true) {
         toClient0.writeInt(PLAYERNUMBER);
         toClient0.writeInt(PLAYER0); //gives client 0 a player number of 0
         toClient0.flush();
         toClient1.writeInt(PLAYERNUMBER);
         toClient1.writeInt(PLAYER1); //gives client 1 a player number of 1
         toClient1.flush();
         
         if (client0NumOfMatchingPairs + client1NumOfMatchingPairs == MAX_NUM_OF_PAIRS) 
            break;
         
         doPlayer0Commands();
         doPlayer1Commands();
      }
      
      if (client0NumOfMatchingPairs >= client1NumOfMatchingPairs) {
         toClient0.writeInt(WIN);
         toClient0.writeInt(PLAYER0);
         toClient0.flush();
         
         toClient1.writeInt(WIN);
         toClient1.writeInt(PLAYER0);
         toClient1.flush();
      }
      else {
         toClient0.writeInt(WIN);
         toClient0.writeInt(PLAYER1);
         toClient0.flush();
         
         toClient1.writeInt(WIN);
         toClient1.writeInt(PLAYER1);
         toClient1.flush();
      }
   }

   /**
      * Does the command for client 0  
      */
   public void doPlayer0Commands() throws IOException {
      toClient0.writeInt(PLAYING);
      toClient0.writeInt(PLAYER0);
      toClient0.flush();
      
      toClient1.writeInt(PLAYING);
      toClient1.writeInt(PLAYER0);
      toClient1.flush();
      
      int cmd0 = fromClient0.readInt();
      if (cmd0 == QUIT) {
         report("QUIT command received from client 0. Client 1 wins.");
        
         toClient0.writeInt(WIN);
         toClient0.writeInt(PLAYER1);
         toClient0.flush();
         
         toClient1.writeInt(WIN);
         toClient1.writeInt(PLAYER1);
         toClient1.flush();
         return;
      }
      else if (cmd0 == TRY) {
         int cardNum = fromClient0.readInt();
         report("TRY card " + cardNum + " received from client 0");
         toClient0.writeInt(DISPLAY);
         toClient0.writeInt(cardNum);
         toClient0.flush();
         
         toClient1.writeInt(DISPLAY);
         toClient1.writeInt(cardNum);
         toClient1.flush();
         
         int nextCommand = fromClient0.readInt();
         if(nextCommand == TRY) {
            int cardNum2 = fromClient0.readInt();
            report("TRY card " + cardNum2 + " received from client 0");
            
            toClient0.writeInt(DISPLAY);
            toClient0.writeInt(cardNum2);
            toClient0.flush();
            
            toClient1.writeInt(DISPLAY);
            toClient1.writeInt(cardNum2);
            toClient1.flush();
            
            if (backOfCard[cardNum].equals(backOfCard[cardNum2])) {
               report("There is a match for Client 0");
               report("Reveal " + cardNum + " and " + cardNum2);
               
               client0NumOfMatchingPairs++;
               
               toClient0.writeInt(MATCHING);
               toClient0.flush();
               
               toClient0.writeInt(REVEAL);
               toClient0.writeInt(cardNum);
               toClient0.flush();
               
               toClient0.writeInt(REVEAL);
               toClient0.writeInt(cardNum2);
               toClient0.flush();
               
               toClient1.writeInt(REVEAL);
               toClient1.writeInt(cardNum);
               toClient1.flush();
               
               toClient1.writeInt(REVEAL);
               toClient1.writeInt(cardNum2);
               toClient1.flush();
            }
            
         	else {
               try {
                  Thread.sleep(1000);
               }
               catch(InterruptedException e) {
               }
               toClient1.writeInt(COVER);
               toClient1.writeInt(cardNum);
               toClient1.flush();
               
               toClient1.writeInt(COVER);
               toClient1.writeInt(cardNum2);
               toClient1.flush();
               
               toClient0.writeInt(COVER);
               toClient0.writeInt(cardNum);
               toClient0.flush();
               
               toClient0.writeInt(COVER);
               toClient0.writeInt(cardNum2);
               toClient0.flush();
            }
         }
         return;
      }
      else {
         report("Unknown command received by client.");
      }
   }

   /**
      * Does the command for client 1 
      */
   public void doPlayer1Commands() throws IOException {
      toClient0.writeInt(PLAYING);
      toClient0.writeInt(PLAYER1);
      toClient0.flush();
      
      toClient1.writeInt(PLAYING);
      toClient1.writeInt(PLAYER1);
      toClient1.flush();
      
      int cmd1 = fromClient1.readInt();
      if (cmd1 == QUIT) {
         report("QUIT command received from client 1. Cleint 0 wins.");
         
         toClient0.writeInt(WIN);
         toClient0.writeInt(PLAYER0);
         toClient0.flush();
         
         toClient1.writeInt(WIN);
         toClient1.writeInt(PLAYER0);
         toClient1.flush();
         return;
      }
      else if (cmd1 == TRY) {
         int cardNum = fromClient1.readInt();
         report("TRY card " + cardNum + " received from client 1");
         
         toClient0.writeInt(DISPLAY);
         toClient0.writeInt(cardNum);
         toClient0.flush();
         
         toClient1.writeInt(DISPLAY);
         toClient1.writeInt(cardNum);
         toClient1.flush();
         
         int nextCommand = fromClient1.readInt();
         if(nextCommand == TRY) {
            int cardNum2 = fromClient1.readInt();
            report("TRY card " + cardNum2 + " received from client 1");
            
            toClient0.writeInt(DISPLAY);
            toClient0.writeInt(cardNum2);
            toClient0.flush();
            
            toClient1.writeInt(DISPLAY);
            toClient1.writeInt(cardNum2);
            toClient1.flush();
            
            if (backOfCard[cardNum].equals(backOfCard[cardNum2])) {
               report("There is a match for Client 1");
               report("Reveal " + cardNum + " and " + cardNum2);
               
               client1NumOfMatchingPairs++;
               
               toClient0.writeInt(REVEAL);
               toClient0.writeInt(cardNum);
               toClient0.flush();
               
               toClient0.writeInt(REVEAL);
               toClient0.writeInt(cardNum2);
               toClient0.flush();
               
               toClient1.writeInt(MATCHING);
               toClient1.flush();
               
               toClient1.writeInt(REVEAL);
               toClient1.writeInt(cardNum);
               toClient1.flush();
               
               toClient1.writeInt(REVEAL);
               toClient1.writeInt(cardNum2);
               toClient1.flush();
            }
            
         	else {
               try {
                  Thread.sleep(1000);
               }
               catch(InterruptedException e) {
               }
               toClient1.writeInt(COVER);
               toClient1.writeInt(cardNum);
               toClient1.flush();
               
               toClient1.writeInt(COVER);
               toClient1.writeInt(cardNum2);
               toClient1.flush();
               
               toClient0.writeInt(COVER);
               toClient0.writeInt(cardNum);
               toClient0.flush();
               
               toClient0.writeInt(COVER);
               toClient0.writeInt(cardNum2);
               toClient0.flush();
            }
         }
         return;
      }
      else {
         report("Unknown command received by client.");
      }
   }

   /**
      * Shuffles the elements of an array  
      */
   public void shuffle() {
      int index = 0;
      for (int i = 0; i < backOfCard.length; i++) {
         index = random.nextInt(i + 1);
         String temp = backOfCard[index];
         backOfCard[index] = backOfCard[i];
         backOfCard[i] = temp;
      }
   }   
} 
