/*
Program Name: CommandConstants
Author: Pedraum Safarzadeh
Date: March 31 2015
Course: CPSC 1181
Compiler: JDK 1.7
*/

/**
* Constants that are commands for the memory game 
*/
public interface CommandConstants {
   
   int PORT = 1181;
   
   int QUIT = 1; //QUIT sent by client who wants to quit to server
   int DISPLAY = 2; //DISPLAY sent by server to client who tried a card
   int COVER = 3; //COVER sent by server to client when the cards dont match
   int MATCHING = 4; //MATCHING sent by server to client who gets a matching pair
   int PLAYERNUMBER = 5; //PLAYERNUMBER sent by server to client 
   int PLAYING = 6; //PLAYING sent by server to client who is playing
   int WIN = 7; //WIN is sent by server to client when all cards are revealed or when a player quits
   int TRY = 8; //TRY sent by client who wants to display a card to server 
   int SHUFFLE = 9; //SHUFFLE sent by server to client in the beginning of the game to shuffle the cards every game 
   int REVEAL = 10; //REVEAL sent by server to client when a client gets a matching pair
   
   //this array contains the commands used by the server and client to communicate
   String[] CMD = {"QUIT", "DISPLAY", "COVER", "MATCHING", "PLAYERNUMBER", "PLAYING", "WIN", "TRY", "SHUFFLE", "REVEAL"};
}