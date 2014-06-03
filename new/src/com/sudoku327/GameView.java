package com.sudoku327;

import com.sudokuSolver.SudokuSolver;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class GameView extends View
{
    private final Game _game;
    private int _X;
    private int _Y;
    private float _width;
    private float _height;
    private final Rect _selectionRectangle = new Rect();
    private final SudokuSolver solver = new SudokuSolver();//initiates solver

    // these Paints are used to color UI elements
    private final Paint _background = new Paint();
    private final Paint _text = new Paint();
    private final Paint _foreground = new Paint();
    private final Paint _minorLine = new Paint();
    private final Paint _majorLine = new Paint();
    private final Paint _selected = new Paint();
    private final Paint _button = new Paint();
    private final Paint _buttonText = new Paint();
    
    
        public GameView(Context context) {
                super(context);
                _game =(Game) context;
                setFocusable(true);
                setFocusableInTouchMode(true);

        // set colors for Paint objects
        _foreground.setColor(getResources().getColor(R.color.foreground));
        _background.setColor(getResources().getColor(R.color.background));
        _minorLine.setColor(getResources().getColor(R.color.minorLine));
        _majorLine.setColor(getResources().getColor(R.color.majorLine));
        _selected.setColor(getResources().getColor(R.color.selected));
        _text.setColor(getResources().getColor(R.color.decimals));
        _button.setColor(getResources().getColor(R.color.button));
        _buttonText.setColor(getResources().getColor(R.color.text));
        //_text.setTypeface(Typeface.create()); // this Paint needs
        //_text.setStyle(Style.FILL);                                             // more info because
        _text.setTextAlign(Paint.Align.CENTER);                                 // it draws numbers
        _buttonText.setTextAlign(Paint.Align.CENTER);
    }

        @Override
        // calculates the size of each tile
        protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
                _width = w / 9f;
                _height = w / 9f;

        _text.setTextSize(_height * 0.75f);    // text size needs to adjust
        _text.setTextScaleX(_width / _width); // for new dimensions

                // creates the selection rectangle
                getRect(_X, _Y, _selectionRectangle);
                super.onSizeChanged(w, h, oldWidth, oldHeight);
        }
        
        private void getRect(int x, int y, Rect rect) {
                rect.set((int) (x * _width), (int) (y * _width), (int) (x * _width + _width), (int) (y * _width + _width));
        }
        
        // draws the board
        @Override
        protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getWidth(), _background); // large board rectangle
        canvas.drawRect(0, (_height * 9), getWidth(), getHeight(), _button);//bottom button
        canvas.drawLine(0, (_height * 9), getWidth(), getWidth(), _majorLine);
        _buttonText.setTextSize(64);
        canvas.drawText("Solve", getWidth()/2, _height * 11.5f, _buttonText);

        // draw the grid
        for (int i = 0; i < 9; ++i) {
            if (i % 3 == 0) continue;
            	canvas.drawLine(0, i * _width, getWidth(), i * _width, _minorLine);
            	canvas.drawLine(i * _width, 0, i * _width, getWidth(), _minorLine);
        }
        for (int i = 3; i < 9; i += 3) {
            canvas.drawLine(0, i * _width, getWidth(), i * _width, _majorLine);
            canvas.drawLine(i * _width, 0, i * _width, getWidth(), _majorLine);
        }

                // draw numbers
                FontMetrics metrics = _text.getFontMetrics();
                float centerX = _width / 2;
                float centerY = (_width / 2) - ( (metrics.ascent + metrics.descent) / 2);

                for (int i = 0; i < 9; ++i)
                        for (int j = 0; j < 9; ++j) {
                        	int value = _game.getValue(i, j);
                        	if (value != 0)
                        		canvas.drawText(Integer.toString(value),  i * _width + centerX, j * _width + centerY, _text);
            }

                canvas.drawRect(_selectionRectangle, _selected); // draw the selection rectangle
        }

    private void select(int x, int y) {
        invalidate(_selectionRectangle); // redraw previously selected rectangle
        _X = Math.min(Math.max(x, 0), 8);
        _Y = Math.min(Math.max(y, 0), 8);
        _game.index = ((9*_Y)+_X);
        getRect(_X, _Y, _selectionRectangle); // find newly selected rectangle
        invalidate(_selectionRectangle);      // and redraw it
    }

    // shows keypad after a tile is touched
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() !=MotionEvent.ACTION_DOWN)
            return super.onTouchEvent(event);

        if(event.getY() <= _height * 9)
        {
            select((int) (event.getX() / _width), (int) (event.getY() / _height));
            _game.showKeypad(_X, _Y);
            return true;
        }
        else
        {
            solveSudoku();
            return false;
        }
    }

    //changes the number on a tile
    public void changeSelectedTileNumber(int newNumber) {
        //may not need the if statement if we are not implementing hints
        //would only need the call to assignValueIfValid
        if (_game.assignValueIfValid(_X, _Y, newNumber)) {
            invalidate();
        }
    }

    public void solveSudoku()
    {
        //calls solver
        solver.Solve(_game.retGame());
        if(!solver.Solve(_game.retGame()))
        {
            Toast toast = Toast.makeText(_game, "Puzzle is unsolvable in current form. You've made a mistake!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        invalidate();
        _game.congratulate();
    }

    public void hint()
    {
        changeSelectedTileNumber(_game.retSolution()[_game.index]);
    }
}