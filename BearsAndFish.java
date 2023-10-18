
/*******************************************************************************
 * Data Structures Post-AP
 *
 * Description:
 *    Simulation Mechanics
 *    - The river is 20 by 20 spaces (400 total spaces)
 *    - Spikes cannot move, and they kill both bears and fish.
 *    - Bears and Fish can either move left, right, up, or down, or stay.
 *    - When a bear moves to a bear, or fish moves to a fish, they will breed
 *      and generate new animal of same type in random null location.
 *
 *    The Game is finished when either:
 *    - There are 0 bears left
 *    - There are 0 fish left
 *    - There are both 0 bears and 0 fish left
 *
 *******************************************************************************/

import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;

public class BearsAndFish
{
   public static void main(String[] args)
   {
      Scanner scanner = new Scanner(System.in);
      
      while (true)
      {
         // Create and start a new Ecosystem simulation.
         Ecosystem e = new Ecosystem();
         e.run();
         
         System.out.println("Press any key to start a new simulation:");
         String input = scanner.nextLine();
      }
   }
}

class Ecosystem 
{
   private Thing[][] river;
   private int numBears, numFish, numSpikes; // Stores the number of bears, fish, and spikes in the river
   
   private boolean gameOver = false;
   private String endMsg;
   
   public Ecosystem()
   {
      river = new Thing[20][20]; // Generates a river with 400 total spaces
   }
   
   /* Main function for the ecosystem which prompts user input and updates the river.**/
   public void run()
   {
      initializeRiver();
      
      Scanner scanner = new Scanner(System.in);
      while (true)
      {
         printRiver();
         if (!gameOver)
         {
            System.out.println("Enter 'a' to continue 1 iteration, 'b' to skip 10 iterations, or 'c' to skip to end of simulation:");
            String input = scanner.nextLine();
            
            if (input.equals("a")) {
               // Update river once.
               updateRiver();
            }
            else if (input.equals("b")) {
               // Update river 10 times or until game is over.
               for (int i = 0; i < 10; i++) {
                  updateRiver();
                  if (gameOver)
                     break;
               }
            }
            else if (input.equals("c")) {
               // Update river until game is over.
               while (true) {
                  updateRiver();
                  if (gameOver)
                     break;
               }
            }
         }
         else {
            System.out.println("===========");
            System.out.println("GAME OVER.");
            System.out.println(endMsg);
            break;
         }
      }
   }
   
   /* Initializes the river. Generates the Bears, Fish, and Spikes
    * at the beginning of simulation **/
   public void initializeRiver()
   {
      // Initial values for amount of bears, fish, and spikes
      numBears = 5;
      numFish = 20;
      numSpikes = 10;
      
      generateItem("Bear", numBears, false);
      generateItem("Fish", numFish, false);
      generateItem("Spike", numSpikes, false);     
   }
   
   /* Updates the river -> every animal in the river will move or stay. **/
   public void updateRiver()
   {
      // Iterate through each spot in the river
      for (int y = 0; y < river.length; y++) {
         for (int x = 0; x < river[y].length; x++) {
            if (river[y][x] == null || river[y][x] instanceof Spike) // If it's an empty spot or spike, don't move it.
            {
            }
            else if ((river[y][x] instanceof Bear || river[y][x] instanceof Fish) && river[y][x].hasMoved == false) // If it's a Bear or Fish and hasn't moved yet, generate random action.
            {
               String action = generateRandomMove(x, y);
               int newX = x;
               int newY = y;
               if (action == "U")
                  newY--;
               else if (action == "D")
                  newY++;
               else if (action == "L")
                  newX--;
               else if (action == "R")
                  newX++;
               
               if (river[newY][newX] == null) // Bear or Fish to empty space
               {
                  river[newY][newX] = river[y][x];
                  river[y][x] = null;
                  river[newY][newX].hasMoved = true;
               }
               else if (river[newY][newX] instanceof Spike) // Bear or Fish on to spike
               {
                  if (river[y][x] instanceof Bear)
                     numBears--;
                  if (river[y][x] instanceof Fish)
                     numFish--;
                  river[y][x] = null;
               }
               else if (river[y][x] instanceof Bear && river[newY][newX] instanceof Fish) // Bear on to fish
               {
                  river[newY][newX] = river[y][x];
                  river[y][x] = null;
                  numFish--;
                  river[newY][newX].hasMoved = true;
               }
               else if (river[y][x] instanceof Fish && river[newY][newX] instanceof Bear) // Fish on to bear
               {
                  river[y][x] = null;
                  numFish--;
               }
               else if ((river[y][x] instanceof Fish && river[newY][newX] instanceof Fish) && (river[y][x].hasBred == false)) // Fish breeding (Fish to Fish)
               {
                  generateItem("Fish", 1, true);
                  numFish++;
                  river[y][x].hasMoved = true;
               }
               else if ((river[y][x] instanceof Bear && river[newY][newX] instanceof Bear) && (river[y][x].hasBred == false)) // Bear breeding (Bear to Bear)
               {
                  generateItem("Bear", 1, true);
                  numBears++;  
                  river[y][x].hasMoved = true;
               }
            }
         }
      }
      
      // Set all the hasMoved attributes back to false
      for (int y = 0; y < river.length; y++) {
         for (int x = 0; x < river[y].length; x++) {
            if (river[y][x] != null)
               river[y][x].hasMoved = false;
         }
      }
      
      // Detect if game is over or not.
      // This is when number of bears is 0 or number of fish is 0.
      if (numBears == 0 && numFish == 0)
      {
         gameOver = true;
         endMsg = "There are no fish or bears left! SPIKE WIN!";
      }
      else if (numBears == 0)
      {
         gameOver = true;
         endMsg = "There are no bears left! FISH WIN!";
      }
      else if (numFish == 0)
      {
         gameOver = true;
         endMsg = "There are no fish left! BEAR WIN!";
      }
   }
   
   /* Adds new animals to the ecosystem in random empty locations **/
   public void generateItem(String animal, int amount, boolean fromBreeding)
   {
      for (int i = 0; i < amount; i++)
      {
         while (true) {
            if (!isEcosystemFull()) // If ecosystem is completely full (no nulls), don't generate any more animals
               return;
            
            // Generate random X and Y coordinates from 0-19 for the matrix
            int x = (int)(Math.random() * 20);
            int y = (int)(Math.random() * 20);
            if (river[y][x] == null) // Make sure that the space is empty, otherwise generate a new spot.
            {
               if (animal.equals("Bear"))
                  river[y][x] = new Bear(fromBreeding, fromBreeding);
               else if (animal.equals("Fish"))
                  river[y][x] = new Fish(fromBreeding, fromBreeding);
               else if (animal.equals("Spike"))
                  river[y][x] = new Spike();
               break;
            }
         }
      }
   }
   
   /* Returns if ecosystem is full or not **/
   public boolean isEcosystemFull()
   {
      boolean foundEmpty = false;
      for (int y = 0; y < river.length; y++) {
         for (int x = 0; x < river[y].length; x++) {
            if (river[y][x] == null)
               foundEmpty = true;
         }
      }
      return foundEmpty;
   }
   
   /* Generates random possible moves for the animals in the ecosystem.
    * Handles cases where animals are on the edge/border **/
   public String generateRandomMove(int x, int y)
   {
      ArrayList<String> actions = new ArrayList<>();
      
      // 0,0 = top left, 9,9 = bottom right
      actions.add("S");
      if (y > 0)
         actions.add("U");
      if (y < 19)
         actions.add("D");
      if (x > 0)
         actions.add("L");
      if (x < 19)
         actions.add("R");
      
      int randomActionIndex = (int)(Math.random() * actions.size());
      return actions.get(randomActionIndex);
   }
   
   /* Prints the current River Ecosystem **/
   public void printRiver()
   {
      System.out.println("Ecosystem:");
      for (int y = 0; y < river.length; y++) {
         for (int x = 0; x < river[y].length; x++) {
            if (river[y][x] != null)
               System.out.print(river[y][x] + " ");
            else
               System.out.print("- ");
         }
         System.out.println();
      }
      
      // Print counts of bear, fish, and spike
      System.out.println("Bear Count: " + numBears);
      System.out.println("Fish Count: " + numFish);
      System.out.println("Spike Count: " + numSpikes + "\n");
   }
}

class Thing
{
   public boolean hasMoved = false;
   public boolean hasBred = false;
}

class Bear extends Thing
{
   public Bear()
   {
   }
   
   public Bear(boolean h, boolean h1)
   {
      this.hasBred = h;
      this.hasMoved = h1;
      
   }
   
   @Override
   public String toString() {
      return "B";
   }
}

class Fish extends Thing
{
   public Fish()
   {
   }
   
   public Fish(boolean h, boolean h1)
   {
      this.hasBred = h;
      this.hasMoved = h1;
      
   }
   
   public boolean isBred()
   {
      return this.hasBred;
   }
   
   @Override
   public String toString() {
      return "F";
   }
}

class Spike extends Thing
{
   @Override
   public String toString() {
      return "x";
   }
}