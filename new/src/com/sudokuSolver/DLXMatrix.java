package com.sudokuSolver;

import java.util.ArrayList;

// Owns the quadruply-linked circular lists DLX requires.
public class DLXMatrix {
    private DLXNode _root = new DLXNode();
    private ArrayList<DLXNode> _columns;
    private ArrayList<DLXNode> _rows = new ArrayList<DLXNode>();

    public DLXMatrix(int columns){
        _root.right = _root.left = _root;
        _columns = new ArrayList<DLXNode>(columns);

        CreateColumns(columns);
    }

    // append DLXNode at the end of columnHeader's list
    public void AppendToColumn(DLXNode node, int columnIndex) {
        // note the column is circular: header->up points to
        // the end of the list and end->down points to header

        DLXNode columnHeader = _columns.get(columnIndex);
        node.column = node.down = columnHeader;
        node.up = columnHeader.up;
        columnHeader.up.down = node;
        columnHeader.up = node;

        columnHeader.data++;
    }

    public int AddRow(DLXNode header) {
        _rows.add(header);
        return _rows.size() - 1;
    }

    public DLXNode Root() {
        return _root;
    }

    public DLXNode RowHeader(int rowIndex) {
        return _rows.get(rowIndex);
    }

    // creates a column list with numColumns headers
    private void CreateColumns(int numColumns) {
        for (int i = 0; i < numColumns; ++i) {
            DLXNode node = new DLXNode();
            node.index = i;
            node.left = _root.left;
            node.right = _root;
            node.up = node.down = node.column = node;

            _root.left.right = node;
            _root.left = node;

            _columns.add(i, node);
        }
    }
}