package com.sudokuSolver;

// Node structure for use in the matrix manipulated by DLX.
// All fields are public because the algorithm will make
// thousands of modifications to them--the overhead of calling
// getter/setter functions every time is substantial.
public class DLXNode {
    public DLXNode column = null;
    public DLXNode left = null;
    public DLXNode right = null;
    public DLXNode up = null;
    public DLXNode down = null;
    public int data = 0; // holds the length of a column for column headers
    public int index = 0; // row and column headers hold their own indices

    public DLXNode(){ }

}