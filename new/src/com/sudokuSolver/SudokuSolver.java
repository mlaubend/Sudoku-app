package com.sudokuSolver;

import java.util.ArrayList;
import java.util.Stack;

// Encapsulates the reduction of a sudoku to an EXACT COVER instance and
// its solution via DLX.  Users need only call Solve.
public class SudokuSolver {
    private int _boxDimension;
    private int _cells;
    private ArrayList<DLXNode> _columns;
    private int _dimension;
    private DLX _dlx;
    private ArrayList<Integer> _givenRows;
    private DLXMatrix _matrix;
    private Stack<DLXNode> _solution;
    private int[] _sudoku;


    public SudokuSolver() {
        _dimension = 9;
        _boxDimension = (int)Math.sqrt(_dimension);
        _cells = _dimension * _dimension;
    }

    /**
     * @param dimension cardinality of a house; standard sudoku is dimension 9
     */
    public SudokuSolver(int dimension) {
        _dimension = dimension;
        _boxDimension = (int)Math.sqrt(dimension);
        _cells = dimension * dimension;
    }

    /**
     * This is the only function you ever need call.  If sudoku parameter is
     * solvable, this function will solve it in place and return true.  The
     * array parameter will be mutated; make a copy before the call if you
     * want to retain the unsolved puzzle.
     *
     * @param sudoku an array of 81 integers representing a sudoku using 0 to mark blank cells
     * @return true if puzzle is solved, false otherwise
    */
    public boolean Solve(int[] sudoku) {
        Reset();

        _sudoku = sudoku;
        BuildMatrix();
        _dlx.Solve(_matrix, _givenRows);
        _solution = _dlx.Solution();
        if (_solution.size() != _cells)
            return false; // puzzle is unsolvable

        OutputSolution();
        return true;
    }

    private void Reset() {
        _givenRows = new ArrayList<Integer>();
        _matrix = new DLXMatrix(_cells * 4);
        _dlx = new DLX();
        _solution = new Stack<DLXNode>();
        _columns = new ArrayList<DLXNode>();
    }

    // computes the index of the column encoding the constraint on a and b
    private int ConstraintColumnIndex(int a, int b, int offset) {
        return (a * _dimension) + b + (offset * _cells);
    }

    // computes the grid (row, column) coordinate of a constraint column
    private int[] ConstraintRowColumn(int columnIndex) {
        return new int[] { columnIndex / _dimension, columnIndex % _dimension };
    }

    /*
        returns the index of the box containing (row, column)
        in a 9x9 sudoku, boxes are indexed
                                0 1 2
                                3 4 5
                                6 7 8
        and each contains 3 * 3 = 9 cells
        */
    private int BoxIndex(int row, int column) {
        return (row - (row % _boxDimension)) + (column / _boxDimension);
    }

    // converts a (row, column) coordinate to an index into the one-dimensional sudoku array
    private int FlatArrayIndex(int row, int column) {
        return row * _dimension + column;
    }

    private boolean OutputSolution() {
        // the solution stack has one node from each
        // of the rows whose set union constitutes the
        // sudoku's solution; we need to reverse the
        // encoding to build the solved puzzle
        DLXNode node;
        int row, column = 0, value, columnIndex;
        while (_solution.size() > 0) {
            value = row = -1;
            node = _solution.pop();
            while (value < 0 || row < 0) {
                columnIndex = node.column.index;
                if (columnIndex < _cells) { // this column encodes the row-column constraint
                    int[] rowColumn = ConstraintRowColumn(columnIndex);
                    row = rowColumn[0];
                    column = rowColumn[1];
                }
                else if (columnIndex >= _cells * 3) // this one the box-value constraint
                    value = ConstraintRowColumn(columnIndex)[1];
                node = node.right;
            }
            int cell = FlatArrayIndex(row, column);
            _sudoku[cell] = value + 1;
        }

        return true;
    }

    // builds the row representing placement of value at (row, column)
    // and links it into the matrix; returns the row's index
    private int CreateRow(int row, int column, int value){
        // indices of the columns representing the operative constraints
        int[] columnIndices = { ConstraintColumnIndex(row, column, 0),
                ConstraintColumnIndex(row, value, 1),
                ConstraintColumnIndex(column, value, 2),
                ConstraintColumnIndex(BoxIndex(row, column), value, 3) };

        // each row has exactly four entries--one for each constraint
        DLXNode[] nodes = { new DLXNode(), new DLXNode(), new DLXNode(), new DLXNode() };
        for (int i = 0; i < 4; ++i) {
            _matrix.AppendToColumn(nodes[i], columnIndices[i]);
            if (i < 3)
                nodes[i].right = nodes[i + 1];
            if (i > 0)
                nodes[i].left = nodes[i - 1];
        }
        nodes[0].left = nodes[3];
        nodes[3].right = nodes[0];

        return _matrix.AddRow(nodes[0]);
    }

    /* Reducing standard Sudoku to EXACT COVER:
     *
     * Each column of the DLX matrix represents one of the four
     * constraints on digit placement:
     * (1) Each cell holds exactly one digit. (row-column constraint)
     * (2) Each digit appears exactly once in each row. (row-value constraint)
     * (3) Each digit appears exactly once in each column. (column-value constraint)
     * (4) Each digit appears exactly once in each 3x3 block. (box-value constraint)
     *
     * There are 81 cells in standard sudoku and each assignment of a digit to one
     * satisfies the constraints in a unique way.  Thus 81 columns are required to
     * encode each constraint over the entire puzzle. Hence the DLX matrix has
     * 81 * 4 = 324 columns.
     *
     * Each row of the matrix represents the assignment of one digit to a cell.
     * There are 9 possible assignments.  Hence there are 9 * 81 = 729 rows.
     * Because each assignment satisfies all four constraints, each row  has exactly
     * four entries. The overall structure has 236,196 cells 98.8% of which contain zero.
    */
    private void BuildMatrix() {
        _matrix = new DLXMatrix(_cells * 4);
        int row, column, rowIndex;

        for (int i = 0; i < _cells; ++i) {
            row = i / _dimension;
            column = i % _dimension;
            if (_sudoku[i] > 0 && _sudoku[i] <= _dimension) {  // cell contains a given; need only one row
                rowIndex = CreateRow(row, column, _sudoku[i] - 1);
                _givenRows.add(rowIndex);
            }
            else
                for (int j = 0; j < _dimension; ++j)
                    CreateRow(row, column, j);
        }
    }
}