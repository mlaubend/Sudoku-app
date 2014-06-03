/* 
 * This object represents a particular tile in on the sudoku board.
 * It holds a value, and belongs to a row, a column and a zone, and an id number from 0 to 80.
 * The following public functions are available:
 *              Tile(int i)
 *              Tile(int i, int val)
 *              int getId()
 *              void setValue(int)
 *              void removeValue(int)
 *              boolean hasValue()
 *              int getValue()
 *              void setRow(int)
 *              void setCol(int)
 *              void setZone(int)
 *              int getRow()
 *              int getCol()
 *              int getZone()
 */
package com.sudokuRandom;
public class Tile
{
        // Member Variables
        public int value; // automatically initializes to 0
        private int id;
        private int row;
        private int col;
        private int zone;
        
        // Member functions.
        /*
         * This constructor sets this Tile's id, but not the value of this Tile.
         */
        public Tile(int i)
        {
                id = i;
        }
        
        /*
         * This constructor sets this Tile's id and the value of this Tile.
         */
        public Tile(int i, int val)
        {
                id = i;
                setValue(val);
        }
        
        /*
         * This function gets this Tile's id.
         */
        public int getId()
        {
                return id;
        }
        
        /*
         * This function sets the value of this Tile.
         */
        public void setValue (int val)
        {
                value = val;
        }
        
        /*
         * This function removes the value of this Tile (sets it to 0).
         * This function should be used when the value of the Tile is known.
         */
        public void removeValue (int val)
        {
                value = 0;
        }
        
        /*
         * This function also removes the value of this Tile (sets it to 0).
         * This function should be used when the value of the Tile is unknown.
         */
        public void removeValue()
        {
                value = 0;
        }
        
        /*
         * This function discovers if this Tile has a non-zero value assigned to it.
         * It returns true if there is a non-zero value, false if there is a zero value.
         */
        public boolean hasValue()
        {
                boolean result = false;
                if(value != 0)
                        result = true;
                return result;
        }
        
        /*
         * This function returns the current value of this Tile.
         */
        public int getValue()
        {
                return value;
        }
        
        /*
         * This function tells this Tile what row it belongs to, so long as it is valid.
         */
        public void setRow(int r)
        {
                if(!(r < 0 || r > 8))
                        row = r;
        }
        
        /*
         * This function tells this Tile what column it belongs to, so long as it is valid.
         */
        public void setCol(int c)
        {
                if(!(c < 0 || c > 8))
                        col = c;
        }
        
        /*
         * This function tells this Tile what zone it belongs to, so long as it is valid.
         */
        public void setZone(int z)
        {
                if(!(z < 0 || z > 8))
                        zone = z;
        }
        
        /*
         * This function returns the row to which this Tile belongs.
         */
        public int getRow()
        {
                return row;
        }
        
        /*
         * This function returns the column to which this Tile belongs.
         */
        public int getCol()
        {
                return col;
        }
        
        /*
         * This function returns the zone to which this Tile belongs.
         */
        public int getZone()
        {
                return zone;
        }
}