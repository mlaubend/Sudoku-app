package com.sudokuSolver;

import java.util.ArrayList;
import java.util.Stack;

// DLX solves an instance of EXACT COVER via a recursive, depth-first brute-force search.
// This search is surprisingly fast (though still not polynomial) because of the unique
// matrix structure it uses to encode the EXACT COVER matrix.
public class DLX {
    private boolean _done = false;
    private DLXMatrix _matrix;
    private Stack<DLXNode> _solution = new Stack<DLXNode>();

    public DLX() { }

    // used to mark matrix rows as given before solving begins
    public void AddRowToSolution(int rowIndex) {
        DLXNode node = _matrix.RowHeader(rowIndex);
        do
        {
            Cover(node.column);
            node = node.right;
        } while (node != _matrix.RowHeader(rowIndex));

        _solution.push(node);
    }

    public Stack<DLXNode> Solution() {
        return _solution;
    }

    // adds the given rows to the solution then begins the recursive search
    public void Solve(DLXMatrix matrix, ArrayList<Integer> givenRows) {
        _matrix = matrix;

        if (givenRows.size() > 0)
            for (Integer row : givenRows)
                AddRowToSolution(row);

        Recurse();
    }

    // Cover marks a column (i.e. a constraint) satisfied by removing it
    // from the list of columns.  Its links, however, are preserved,
    // making it easy (and fast) to unmark (Uncover) the column when a
    // search branch results in failure and backtracking is required.
    private void Cover(DLXNode columnHead){
        columnHead.right.left = columnHead.left;  // unlink the
        columnHead.left.right = columnHead.right; // column header

        for (DLXNode row = columnHead.down; row != columnHead; row = row.down)
            for (DLXNode col = row.right; col != row; col = col.right) {
                col.up.down = col.down; // for each row in the column
                col.down.up = col.up;   // unlink every node and
                col.column.data--;      // decrement the column length
            }
    }

    // the recursive search function; covers one column per call
    // in English:
    // (1) chooses an unsatisfied constraint (a column of the matrix)
    // (2) chooses a row that satisfies this constraint (a node in the selected column's up-down list)
    // (3) adds that row to the solution (push a node from the row onto the solution stack)
    // (4) covers the column and removes from consideration all rows linked to those columns
    //     (because the constraints those columns represent are now satisfied)
    // .. and eventually all columns are covered and the cardinality of the solution stack
    // indicates whether an exact cover has been found.
    // In the case of standard sudoku, if the solution stack's cardinality is 81, it encodes
    // a consistent assignment of values to each of the puzzle's cells; if not, no such
    // assignment exists.
    private void Recurse() {
        if (_matrix.Root().right == _matrix.Root()) {
            _done = true; // all columns covered,
            return;      // nothing left to do
        }

        DLXNode column = ShortestColumn();
        Cover(column);

        DLXNode row = column.down;
        while (row != column) {
            _solution.push(row);
            for (DLXNode right = row.right; right != row; right = right.right)
                Cover(right.column);

            if (!_done)
                Recurse();
            if (_done)
                return;

            // if we're here, we need to backtrack
            _solution.pop();
            column = row.column;
            for (DLXNode left = row.left; left != row; left = left.left)
                Uncover(left.column);

            row = row.down;
        }

        Uncover(column);
    }

    // covering the shortest column is a fail-early strategy that
    // significantly speeds up the search
    private DLXNode ShortestColumn() {
        DLXNode cursor = _matrix.Root().right;
        DLXNode shortestColumn = cursor;
        int minLength = cursor.data;

        while (cursor != _matrix.Root()) {
            if (cursor.data < minLength) {
                shortestColumn = cursor;
                minLength = cursor.data;
            }
            cursor = cursor.right;
        }

        return shortestColumn;
    }

    // the inverse operation of Cover, Uncover relinks an unlinked column header
    private void Uncover(DLXNode columnHead) {
        // this is the inverse operation of Cover
        // note it's possible because the pointers of
        // covered (i.e. unlinked) nodes weren't modified
        for (DLXNode row = columnHead.up; row != columnHead; row = row.up)
            for (DLXNode col = row.left; col != row; col = col.left)
            {
                col.up.down = col;
                col.down.up = col;
                col.column.data++;
            }

        columnHead.right.left = columnHead; // relink the
        columnHead.left.right = columnHead; // header
    }
}