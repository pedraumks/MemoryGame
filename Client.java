/*
Program Name: Client
Author: Pedraum Safarzadeh
Date: March 31 2015
Course: CPSC 1181
Compiler: JDK 1.7
*/

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.Random;

/**
* The client that sends and receives commands based on the memory game from the server  
*/
public class Client extends JFrame implements Runnable, CommandConstants {
   
   private Socket socket;
   private DataOutputStream toServer;
   private DataInputStream fromServer;
   private PlayerCard[] cards;
   private PlayerCard[] backOfCard;
   private String matchingStr;
   private String playerNumStr;
   private String statusStr;
   private JLabel matchingPairsLabel;
   private JLabel playerNumberLabel;
   private JLabel statusLabel;
   private JButton quitButton;
   private ActionListener quitListener;
   private ActionListener cardListener;
   private int cardNum;
   private boolean isPlaying;
   private int playerNum;
   private int numOfMatchingPairs;
   private boolean[] isShown;
   private ImageIcon[] images;
   
   public static final int CONTAINS_ARGS = 1;
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
   public static final int ROWS = 3;
   public static final int COLS = 4;
   public static final int PLAYER0 = 0;
   public static final int PLAYER1 = 1;
   public static final int NUM_OF_IMAGES = 7;
   public static final int IMAGE_1 = 1;
   public static final int IMAGE_2 = 2;
   public static final int IMAGE_3 = 3;
   public static final int IMAGE_4 = 4;
   public static final int IMAGE_5 = 5;
   public static final int IMAGE_6 = 6;

   /**
      * The main method that creates the client
      * @param args - the line commands   
      */
   public static void main(String[] args) {
      String host = "localhost";
      boolean containsError = false;
      if (args.length == 0)
		   containsError = true;
	   int i = 0;
	   while(!containsError && i < args.length) {
		   if(args[i].equals("-help")) {
			   containsError = true;
			}
		   else if(args[i].equals("-server")) {
			   i++;
			   if(args.length == i)
				   break;
			   else {
				   host = args[i];
					i++;
			   }
		   }
      }
     
      if(containsError = true)
         System.out.println(usageString());
      
       Client client = new Client(host);
   }
   
   /**
      * Returns a string when program is executed
      * @return the usage string 
      */
   private static String usageString() {
      String str = "Usage: ";
      str += "Usage: \tjava Player [-help]";
      str += "\t-server hostAddr serverAddr";
      return str;
   }
   
   /**
      * The constuctor that builds the GUI for the client and connects to the server
      * @param host - the host name used to connect to the server  
      */
   public Client(String host) {
      //backOfCard = new PlayerCard[NUM_OF_CARDS];
      isShown = new boolean[NUM_OF_CARDS];
      images = new ImageIcon[NUM_OF_IMAGES];
      images[IMAGE_1] = new ImageIcon("1.jpg");
      images[IMAGE_2] = new ImageIcon("2.jpg");
      images[IMAGE_3] = new ImageIcon("3.jpg");
      images[IMAGE_4] = new ImageIcon("4.jpg");
      images[IMAGE_5] = new ImageIcon("5.jpg");
      images[IMAGE_6] = new ImageIcon("6.jpg");
      matchingStr = "Number of matching pairs: 0";
      playerNumStr = "Player number: ";
      statusStr = "Status: ";
      quitButton = new JButton("Quit Game");
      buildGUI();
      connectToServer(host);
      Thread t = new Thread(this);
      t.start();
   } 
   
   /**
      * Connects the client to a server using a socket
      * @param host - the host name used to connect to the server   
      */
   private void connectToServer(String host) {
      try {
         socket = new Socket(host, PORT);
         fromServer = new DataInputStream(socket.getInputStream()); //gets from server
         toServer = new DataOutputStream(socket.getOutputStream()); //sends to server
      }
      catch (IOException e) {
         e.printStackTrace(System.err);
      }
   }
   
   /**
      * Sends and receives messages from the server  
      */
   public void run() {
      boolean done = false;
      try {
         while (!done) {
            int msg = fromServer.readInt();
            if (msg == PLAYERNUMBER) {
               playerNum = fromServer.readInt();
               playerNumberLabel.setText("Player number: " + playerNum + "\n"); //gets a player number from the server
            }
            else if(msg == SHUFFLE) {
               cards[CARD_0].setImageNum(fromServer.readInt());
               cards[CARD_1].setImageNum(fromServer.readInt());
               cards[CARD_2].setImageNum(fromServer.readInt());
               cards[CARD_3].setImageNum(fromServer.readInt());
               cards[CARD_4].setImageNum(fromServer.readInt());
               cards[CARD_5].setImageNum(fromServer.readInt());
               cards[CARD_6].setImageNum(fromServer.readInt());
               cards[CARD_7].setImageNum(fromServer.readInt());
               cards[CARD_8].setImageNum(fromServer.readInt());
               cards[CARD_9].setImageNum(fromServer.readInt());
               cards[CARD_10].setImageNum(fromServer.readInt());
               cards[CARD_11].setImageNum(fromServer.readInt());
            }
            else if (msg == DISPLAY) {
               cardNum = fromServer.readInt();
               int num = cards[cardNum].getImageNum();
               cards[cardNum].setIcon(images[num]);
               isShown[cardNum] = true;
            }
            else if (msg == REVEAL) {
               cardNum = fromServer.readInt();
               int num = cards[cardNum].getImageNum();
               cards[cardNum].setIcon(images[num]);
               isShown[cardNum] = true;
            }
            else if (msg == MATCHING) {
               numOfMatchingPairs++;
               matchingPairsLabel.setText("Number of matching pairs: " + numOfMatchingPairs);
            }
            else if (msg == COVER) {
            	cardNum = fromServer.readInt();
            	cards[cardNum].setIcon(new ImageIcon("landscape.jpg"));
               isShown[cardNum] = false;
            }
            else if (msg == PLAYING) {
               int playerPlaying = fromServer.readInt();
               if (playerPlaying == playerNum) {
                  statusLabel.setText("Status: It's your turn");
                  isPlaying = true;
               }
               else {
                  statusLabel.setText("Status: Wait");
                  isPlaying = false;
               }
            }
            else if (msg == WIN) {
               int winner = fromServer.readInt();
               if (winner == playerNum) {
                  statusLabel.setText("Status: YOU WIN");
                  isPlaying = false;
                  done = true;
               }
               else {
                  statusLabel.setText("Status: YOU LOSE");
                  isPlaying = false;
                  done = true;
               }
            }
            else {
               done = true;
               statusLabel.setText("Status: Unknown message from server");
            }
         }
      } 
      catch (IOException e) {
         e.printStackTrace(System.err);
      }
      finally {
         cleanUp();
      } 
   } 
   
   /**
      * Closes the input and output data streams and the socket  
      */
   private void cleanUp() {
      try {
         fromServer.close(); 
         toServer.close(); 
         socket.close();     
      }
      catch (IOException e) {
         e.printStackTrace(System.err);
      }
   }
   
   /**
      * A quit listener that quits the game when the quit button is pressed  
      */
   class QuitListener implements ActionListener {
      /**
            * Quits the game when quit button is pressed  
            * @param event - an action event
            */
      public void actionPerformed(ActionEvent event) {
         quitGame();
      }
   }
   
   /**
      * A card listener that tries a card when a card is clicked   
      */
   class CardListener implements ActionListener {
      /**
            * Tries a card when a card is clicked 
            * @param event - an action event
            */
      public void actionPerformed(ActionEvent event) {
         if (isPlaying) {
            try {
               for (int i = 0; i < cards.length; i++) {
                  if (event.getSource() == cards[i]) {
                     if (isShown[i] == false) {
                        toServer.writeInt(TRY);
                        toServer.writeInt(i);
                        toServer.flush();
                     }
                  }
               }
            }
            catch(IOException e) {
            }
         }
      }
   }
      
   /**
      * quits the game by sending a quit command to the server  
      */
   public void quitGame() {
      if (isPlaying) {
         try {
            toServer.writeInt(QUIT); //sends a quit command to the server
            toServer.flush();
         }
         catch(IOException e) {
         }
      }
   }
   
   /**
      * builds the GUI for the player frame
      */
   public void buildGUI() {
      final int FRAME_WIDTH = 800;
      final int FRAME_HEIGHT = 800;
      final int LOC_X = 800;
      final int LOC_Y = 0;
      
      matchingPairsLabel = new JLabel(matchingStr); //displays number of matching card at all times for the player to see
      playerNumberLabel = new JLabel(playerNumStr); //displays the player number
      statusLabel = new JLabel(statusStr); //either wait or play

      //contains the labels for the player to see at all times when playing the game
      JPanel messagesPanel = new JPanel();
      messagesPanel.setLayout(new BorderLayout());
      messagesPanel.add(matchingPairsLabel, BorderLayout.WEST);
      messagesPanel.add(playerNumberLabel, BorderLayout.SOUTH);
      messagesPanel.add(statusLabel, BorderLayout.EAST);     
      
      cards = new PlayerCard[NUM_OF_CARDS]; //stores all the player cards

      cardListener = new CardListener();
      
      //contains the player cards in a grid
      JPanel cardsPanel = new JPanel();
      cardsPanel.setLayout(new GridLayout(ROWS, COLS));
      for (int i = 0; i < cards.length; i++) { 
         cards[i] = new PlayerCard();
         cardsPanel.add(cards[i]);
         cards[i].addActionListener(cardListener);
      }
      
      JPanel buttonPanel = new JPanel();
      buttonPanel.add(quitButton);
      
      //puts the labels above the player cards
      setLayout(new BorderLayout());
      add(messagesPanel, BorderLayout.NORTH);
      add(cardsPanel, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);
      
      quitListener = new QuitListener();
      quitButton.addActionListener(quitListener);
      
      setTitle("Client");
      setSize(FRAME_WIDTH, FRAME_HEIGHT);
      setLocation(LOC_X, LOC_Y);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setVisible(true);
   } 
} 