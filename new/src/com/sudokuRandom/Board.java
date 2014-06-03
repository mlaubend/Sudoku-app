/*
 * This object generates a sudoku game using a pre-solved game as a seed that is then shuffled.
 * It can create easy, medium, and hard games, or can generate a game of random difficulty.
 * This method of generation does not need to make use of zones, only rows and columns. 
 * 
 * The following public functions are available:
 *              Board(int difficulty)
 *              void getBoard(int [] nums)
 * The following functions are private:
 *              void shuffle()
 *              void randomizeIndexes(int givens, int [] randIndexes)
 *              void pickHoles(int givens, int [] randIndexes)
 *              void checkBound(int givens, int bound)
 *              void addValue(House house)
 *              void removeValue(House house, int bound)
 */
package com.sudokuRandom;
import java.util.Random; // necessary to generate pseudo-random numbers

public class Board
{
        // Member Variables
        Tile [] boxes = new Tile[81]; // Contains the boxes which make up the game
        House [] rows = new House[9]; // Contains the rows in which the tiles lives
        House [] cols = new House[9]; // Contains the columns in which the tiles lives
        int [] seed = {9, 3, 2, 5, 7, 8, 6, 4, 1,
                                   4, 8, 5, 1, 2, 6, 7, 9, 3,
                                   1, 6, 7, 9, 4, 3, 8, 2, 5,
                                   7, 9, 1, 3, 6, 2, 4, 5, 8,
                                   8, 5, 6, 4, 1, 9, 3, 7, 2,
                                   2, 4, 3, 7, 8, 5, 9, 1, 6,
                                   3, 7, 8, 2, 9, 1, 5, 6, 4,
                                   6, 1, 4, 8, 5, 7, 2, 3, 9,
                                   5, 2, 9, 6, 3, 4, 1, 8, 7}; // This array is used to seed the pseudo-random game generation.
        int [] solution = new int[81]; // This holds the resultant game solution grid after shuffling the seed array.
        Random rand = new Random(); // Random number generator taken from java.util.Random
        int randNum; // Holds a randomly generated number
        
        
        /*
         * Public functions.
         */
         
         
        /*
         * This constructor accepts a difficulty level and generates a random sudoku board of according difficulty.
         */
        public Board(int difficulty)
        {
                int rowNum; // Holds the row number of the current box
                int colNum; // Holds the column number of the current box
                int givens = 30; // This value is just to make sure the program runs, but 30 will never be used
                int bound = 0; // Defaults to 0, which will only be used in the completely random case
                
                // Instantiate each member of the House Arrays (27 Houses in total)
                for (int i = 0; i < 9; i++)
                {
                        rows[i] = new House('r'); // 9 rows
                        cols[i] = new House('c'); // 9 columns
                }
                
                // Create all 81 boxes and place them in their Houses
                for (int i = 0; i < 81; i++)
                {
                        boxes[i] = new Tile(i, seed[i]);
                        rowNum = (i/9); // Get the row number it belongs in.
                        colNum = (i%9); // Get the column number it belongs in.
                        
                        // Fill rows
                        rows[rowNum].setMember(colNum, boxes[i]); // Put the box into the correct column within its row.
                        boxes[i].setRow(rowNum); // Give the box object its row number.
                        
                        // Fill columns
                        cols[colNum].setMember(rowNum, boxes[i]); // Put the box into the correct row within its column.
                        boxes[i].setCol(colNum); // Give the box object its column number.
                }
                
                /*
                 * The difficulty level is determined by the number of givens and the bound.
                 * givens = the number of starting positions given to the user
                 * bound = the minimum number of givens that can be in a row or column (not a zone, though)
                 *
                 * Case 1: Create a random EASY game. 40 - 49 givens, lower bound of 4 per row/col
                 * Case 2: Create a random MEDIUM game. 32 - 39 givens, lower bound of 3 per row/col
                 * Case 3: Create a random HARD game. 27 - 31 givens, lower bound of 2 per row/col
                 * Case 4: Randomly generate a new game
                 * Default: Should never be reached
                 */
                switch(difficulty)
                {
                        case 1:
                                givens = Math.abs(rand.nextInt()) % 10 + 40;
                                bound = 4;
                                break;
                        case 2:
                                givens = Math.abs(rand.nextInt()) % 8 + 32;
                                bound = 3;
                                break;
                        case 3:
                                givens = Math.abs(rand.nextInt()) % 5 + 27;
                                bound = 2;
                                break;
                        case 4:
                                givens = Math.abs(rand.nextInt()) % 23 + 27;
                                break;
                        default:
                                break;
                }
                int [] randIndexes = new int[givens]; // Contains which of the 81 boxes will be seeded with a number
                randomizeIndexes(givens, randIndexes); // Fill with random indexes between 0 and 80
                
                shuffle(); // Shuffle the board to get a new game.
                getBoard(solution); // Record the solution to the new game.
                pickHoles(givens, randIndexes); // Pick holes in the board to get a starting game.
                checkBound(givens, bound); // Even out board to help control the difficulty level.
        }
        
        /*
         * This function will fill an array of integers with the current values of each tile.
         * It must be public as this is how the rest of the app will interact with this object.
         * Note: It will fill an array in place when it is called, thus it MUST be called with an 81 member array.
         * If the function is called with a shorter array, it will return an IndexOutOfBounds exception.
         */
        public void getBoard(int[] nums)
        {
                for (int i = 0; i < 81; i++)
                {
                        nums[i] = boxes[i].getValue();
                }
        }
        
        /*
         * Private functions.
         */
        
        /*
         * This function will shuffle the seed board a random number of times between 0 and 1000.
         * It uses 3 propagation techniques in order to do this:
         * 1) Mutual exchange everywhere in the puzzle of 2 digits
         * 2) Mutual exchange of two columns/rows in the same column/row of blocks
         * 3) Mutual exchange of two columns/rows of blocks
         */
        private void shuffle()
        {
                int repetitions = Math.abs(rand.nextInt()%1000);
                int propagation, spot1, spot2, temp;
                for(int i = 0; i < repetitions; i++)
                {
                        propagation = Math.abs(rand.nextInt()%5);
                        switch(propagation)
                        {
                        
                                case 0:
                                        // mutual exchange of two digits
                                        spot1 = Math.abs(rand.nextInt()%9)+1;
                                        do
                                        {
                                                spot2 = Math.abs(rand.nextInt()%9)+1;
                                        }while(spot1 == spot2);
                                        for(int j = 0; j < 81; j++)
                                        {
                                                if(boxes[j].getValue() == spot1)
                                                        boxes[j].setValue(spot2);
                                                else if(boxes[j].getValue() == spot2)
                                                        boxes[j].setValue(spot1);
                                        }
                                        break;
                                        
                                case 1:
                                        // mutual exchange of two columns in same column of blocks
                                        spot1 = Math.abs(rand.nextInt()%9);
                                        if((spot1%3)==0)
                                        {
                                                spot2 = spot1 + Math.abs(rand.nextInt()%2)+1;
                                        }
                                        else if((spot1%3) == 1)
                                        {
                                                temp = Math.abs(rand.nextInt()%2);
                                                if(temp == 0)
                                                        spot2 = spot1 - 1;
                                                else
                                                        spot2 = spot1 + 1;
                                        }
                                        else
                                        {
                                                spot2 = spot1 - (Math.abs(rand.nextInt()%2)+1);
                                        }
                                        for(int j = 0; j < 9; j++)
                                        {
                                                temp = cols[spot1].getMemberValue(j);
                                                cols[spot1].setMemberValue(j, cols[spot2].getMemberValue(j));
                                                cols[spot2].setMemberValue(j, temp);
                                        }
                                        break;
                                        
                                case 2:
                                        // mutual exchange of two rows in same row of blocks
                                        spot1 = Math.abs(rand.nextInt()%9);
                                        if((spot1%3)==0)
                                        {
                                                spot2 = spot1 + Math.abs(rand.nextInt()%2)+1;
                                        }
                                        else if((spot1%3) == 1)
                                        {
                                                temp = Math.abs(rand.nextInt()%2);
                                                if(temp == 0)
                                                        spot2 = spot1 - 1;
                                                else
                                                        spot2 = spot1 + 1;
                                        }
                                        else
                                        {
                                                spot2 = spot1 - (Math.abs(rand.nextInt()%2)+1);
                                        }
                                        for(int j = 0; j < 9; j++)
                                        {
                                                temp = rows[spot1].getMemberValue(j);
                                                rows[spot1].setMemberValue(j, rows[spot2].getMemberValue(j));
                                                rows[spot2].setMemberValue(j, temp);
                                        }
                                        break;
                                
                                case 3:
                                        // mutual exchange of two columns of blocks
                                        spot1 = 3*Math.abs(rand.nextInt()%3);
                                        do
                                        {
                                                spot2 = 3*Math.abs(rand.nextInt()%3);
                                        }while(spot1 == spot2);
                                        for(int j = 0; j < 3; j++)
                                        {
                                                for(int k = 0; k < 9; k++)
                                                {
                                                        temp = cols[spot1+j].getMemberValue(k);
                                                        cols[spot1+j].setMemberValue(k, cols[spot2+j].getMemberValue(k));
                                                        cols[spot2+j].setMemberValue(k, temp);
                                                }
                                        }
                                        break;
                                
                                case 4:
                                        // mutual exchange of two rows of blocks
                                        spot1 = 3*Math.abs(rand.nextInt()%3);
                                        do
                                        {
                                                spot2 = 3*Math.abs(rand.nextInt()%3);
                                        }while(spot1 == spot2);
                                        for(int j = 0; j < 3; j++)
                                        {
                                                for(int k = 0; k < 9; k++)
                                                {
                                                        temp = rows[spot1+j].getMemberValue(k);
                                                        rows[spot1+j].setMemberValue(k, rows[spot2+j].getMemberValue(k));
                                                        rows[spot2+j].setMemberValue(k, temp);
                                                }
                                        }
                                        break;
                                default:
                                        break;
                        }
                }
        }
        
        /* 
         * This function will pick a number of random boxes based on int givens.
         * randNum will be used to hold an index value.
         */
        private void randomizeIndexes(int givens, int [] randIndexes)
        {
                int counter = 0;
                boolean advance; // Defaults to false
                // Using a do-while loop for more precise control of when to increment counter
                do
                {
                        randNum = Math.abs(rand.nextInt() % 81); // Get a value between 0 and 80
                        advance = true; // Reset advance to true and only make it false if the index already has been picked
                        for (int i = counter-1; i >= 0; i--) // Check all the indexes to see if this one has already been picked
                        {
                                if(randIndexes[i] == randNum) // If it has been picked, then stop checking and make advance false
                                {
                                        advance = false;
                                        break;
                                }
                        }
                        // Whether or not the index has already been picked, set it as a value in the array.
                        // If the index has already been picked, the next time through the loop this index will be replaced with a new one.
                        randIndexes[counter] = randNum;
                        if(advance)
                                counter++;
                } while(counter < givens);
        }
        
        /*
         * This function removes a large number of the answers in order to seed a game.
         * If a box's index is not included in the array randIndexes, then remove that box's value.
         * Otherwise, the box gets to keep its value.
         */
        private void pickHoles(int givens, int [] randIndexes)
        {
                boolean reserved = false;
                for(int i = 0; i < 81; i++)
                {
                        reserved = false;
                        for (int j = 0; j < randIndexes.length; j++)
                        {
                                if (i == randIndexes[j])
                                        reserved = true;
                        }
                        if(!reserved)
                                boxes[i].removeValue();
                }
        }
        /*
         * This function checks if any rows or columns are too small, then fixes their size accordingly.
         * While doing this, it finds the longest row or column and removes a value from it - this makes sure that
         * there is always the same number of givens.
         * It will stop checking when there are no rows or columns which are too small.
         */
        private void checkBound(int givens, int bound)
        {
                int tooSmallCount = 0; // This will keep track of how many rows and columns are too small.
                do
                {
                        // Every round of checking, reset to 0 the counters for number of rows and columns that are too small.
                        tooSmallCount = 0;
                        // Every row and column is indexed between 0 and 8, so cycle from 0 to 8 and check each row & column.
                        for (int i = 0; i < 9; i++)
                        {
                                // If a row is too small, add a value to it & remove a value from the longest row.
                                // Repeat this until the row is no longer too small.
                                while (rows[i].length() < bound)
                                {
                                        tooSmallCount++;
                                        addValue(rows[i]);
                                        // Next find the row with most values and remove 1 value from that row.
                                        int largestLength = 0; // Keeps track of the length of the largest row
                                        int largestIndex = 0; // Keeps track of the index of the largest row
                                        for (int j = 0; j < 9; j++)
                                        {
                                                // Compares the length of each row to find the largest
                                                if (rows[j].length() > largestLength)
                                                {
                                                        largestLength = rows[j].length();
                                                        largestIndex = j;
                                                }
                                        }
                                        removeValue(rows[largestIndex], bound); // Remove 1 value from the largest row
                                }
                                // This while loop does exactly the same thing for columns as was done for rows.
                                while(cols[i].length() < bound)
                                {
                                        tooSmallCount++;
                                        addValue(cols[i]);
                                        int largestLength = 0;
                                        int largestIndex = 0;
                                        for (int j = 0; j < 9; j++)
                                        {
                                                if (cols[j].length() > largestLength)
                                                {
                                                        largestLength = cols[j].length();
                                                        largestIndex = j;
                                                }
                                        }
                                        removeValue(cols[largestIndex], bound);
                                }
                        }
                } while(tooSmallCount != 0);
        }
        
        /* 
         * This function will add another value to a house.
         * This function will only be called to make sure each house has at least the minimum number of values (bound).
         */
        private void addValue(House house)
        {
                Tile tile; // This will be used to point to the tile that will have a value added to it.
                boolean success = false; // This keeps track of whether or not a new value was successfully added to the house.
                int tileID;
                // Keep trying to add a new value until the function succeeds.
                // It will not succeed if the position in which it is trying to add a new value is already occupied.
                do
                {
                        randNum = Math.abs(rand.nextInt()%9); // Get a random position in the House.
                        if(!(house.hasMember(randNum))) // Check if that position already has a value.
                        {
                                // Will only get here if the position does not already have a value.
                                tileID = house.getMember(randNum).getId();
                                boxes[tileID].setValue(solution[tileID]);
                                success = true; // Indicate that the function succeeded.
                        }
                } while (!success);
        }
        
        /*
         * This function will remove a value from a house.
         * This function will only be called to make sure the number of givens doesn't change.
         */
        private void removeValue(House house, int bound)
        {
                Tile tile; // This will be used to point to the tile that will have a value removed from it.
                for (int i = 0; i < 9; i++) // Cycle through all the members of the house until find one that can be removed.
                {
                        tile = house.getMember(i); // Points to the current member of the house.
                        if(tile.hasValue())
                        {
                                // Only get here if the tile has a value.
                                if(house.getType() == 'r' && cols[tile.getCol()].length() > bound)
                                {
                                        // Only get here if the house is a row and the tile is in a large enough column.
                                        tile.removeValue(tile.getValue()); // Remove the tile.
                                        break; // break the loop, exit the function
                                }
                                else if(house.getType() == 'c' && cols[tile.getRow()].length() > bound)
                                {
                                        // Only get here if the house is a column and the tile is in a large enough row.
                                        tile.removeValue(tile.getValue()); // Remove the tile.
                                        break; // break the loop, exit the function
                                }
                        }
                }
        }
}