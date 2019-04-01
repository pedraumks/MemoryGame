import javax.swing.*;

/**
* The player card contained in the players GUI
*/
public class PlayerCard extends JButton {
   
   private int imageNum;
   
   public static final int IMAGE_1 = 1;
   public static final int IMAGE_2 = 2;
   public static final int IMAGE_3 = 3;
   public static final int IMAGE_4 = 4;
   public static final int IMAGE_5 = 5;
   public static final int IMAGE_6 = 6;
   
   /**
      * constructs the player card with a default text
      */
   public PlayerCard() {
      super(new ImageIcon("landscape.jpg"));
   }
   
   /**
      * sets the image number
      * @param i - image number
      */
   public void setImageNum(int i) {
      imageNum = i;
   }
   
   /**
      * gets the image number
      * @return image number
      */
   public int getImageNum() {
      return imageNum;
   }
}
