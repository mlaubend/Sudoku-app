package com.sudoku327;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.sudokuRandom.Board;
import com.sudokuSolver.SudokuSolver;

public class Game extends Activity
{
        public static final int EASY = 0;
        public static final int MEDIUM = 1;
        public static final int HARD = 2;
        public static final int RANDOM = 3;
        private int sudoku[] = new int[81];
        private int solution[] = new int[81];
        private boolean startLocations[] = new boolean[81];
        private GameView gameView;
        public Board board;
        public boolean done = false;
        private final SudokuSolver solver = new SudokuSolver();
        int index;


        // assigned[x][y] maps (x, y) to an int[9] of values assigned to other house members
    private final int assigned[][][] = new int[9][9][9];


        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
                super.onCreate(savedInstanceState);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // lock screen rotation
                int difficulty = getIntent().getIntExtra("difficulty", EASY);
                getNewSudoku(difficulty);

                populateAssignedArray();
        for(int i = 0; i < 81; i++)
        {
        	if(sudoku[i] != 0)
        		startLocations[i] = true;
                        
            solution[i] = sudoku[i];
        }
        		solver.Solve(solution);
                gameView = new GameView(this);

                setContentView(gameView);
        }

    protected boolean assignValueIfValid(int x, int y, int value) {
        int tiles[] = getHouseValues(x, y);

        if (value != 0)
            for (int tile: tiles)
                if (tile == value) // value is already assigned
                {
                    Toast toast3 = Toast.makeText(this, "Invalid number choice", Toast.LENGTH_LONG);
                    toast3.setGravity(Gravity.CENTER, 0, 0);
                    toast3.show();
                    return false;  // to another house member
                }

        setValue(x, y, value);
        populateAssignedArray();
        congratulate();

        return true;
    }

    protected void showKeypad(int x, int y)
        {
                int houseValues[] = getHouseValues(x, y);
                for (int value : houseValues)
                	if (value == 0) {                                       // there's a legal assignment
                	if(done == false && !startLocations[index])
                	{
                		Dialog v = new KeypadDialog(this, houseValues, gameView); // so show the keypad
                		v.show();
                	}
                return;
            }

        // if we're here, there are no legal assignments
        Toast toast = Toast.makeText(this, "No moves", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        }

    // populates the entire assigned array
        private void populateAssignedArray() {
                for (int x = 0; x < 9; ++x)
                        for (int y = 0; y < 9; ++y)
                                assigned[x][y] = populateAssignedArray(x, y);
        }

    // populates only assigned[x][y]
        private int[] populateAssignedArray(int x, int y) {
                int values[] = new int[9];
                
                // iterate down the column
                for (int i = 0; i < 9; i++) {
                        if (i == y)   // skip the tile
                                continue; // in question
                        int value = getValue(x, i);
                        if (value != 0)
                                values[value - 1] = value;
                }
                
                // iterate along the row
                for (int i = 0; i < 9; i++) {
                        if (i == x)   // skip the tile
                                continue; // in question
                        int value = getValue(i, y);
                        if (value != 0)
                                values[value - 1] = value;
                }
                
                // iterate over the box
                int startX = (x / 3) * 3;
                int startY = (y / 3) * 3;
                for (int i = startX; i < startX + 3; ++i) {
                        for (int j = startY; j < startY + 3; ++j) {
                                if (i == x && j == y) // skip the tile
                                        continue;         // in question
                                int value = getValue(i, j);
                                if (value != 0)
                                        values[value - 1] = value;
                        }
                }

        return values;
        }

    // returns the list of all values assigned to (x, y)'s house
    private int[] getHouseValues(int x, int y) {
        return assigned[x][y];
    }

        // generates a new sudoku puzzle using the Board object in com.sudokuRandom
        private void getNewSudoku(int difficulty) {
                switch (difficulty) {
        case RANDOM:
            board = new Board(4);  //makes a random-difficulty board (initial difficulty is inconsequential)
            board.getBoard(sudoku);
            break;
                case HARD:
            board = new Board(3);
            board.getBoard(sudoku);
            break;
                case MEDIUM:
            board = new Board(2);
            board.getBoard(sudoku);
            break;
                case EASY:
        default:
            board = new Board(1);
            board.getBoard(sudoku);
                        break;
                }
        }

    private void setValue(int x, int y, int value) {
        sudoku[y * 9 + x] = value;
    }

        protected int getValue(int x, int y) {
                return sudoku[y * 9 + x];
        }

    public int[] retGame()
    {
        return sudoku;
    }

    public int[] retSolution()
    {
        return solution;
    }

    public void congratulate() //prints "Puzzle Finished!" if all the tiles are filled accurately
    {
        for(int i = 0; i < sudoku.length; i++)
        {
            if(sudoku[i] == 0)
            {
                break;
            }
            else if(i == 80 && sudoku[i] != 0)
            {
                Toast toast2 = Toast.makeText(this, "Puzzle finished!", Toast.LENGTH_LONG);
                toast2.setGravity(Gravity.CENTER, 0, 0);
                toast2.show();
                done = true; //will not bring up keypad anymore.
            }
        }
    }
}