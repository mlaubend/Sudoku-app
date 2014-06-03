package com.sudoku327;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

//colors set in keypad.xml

public class KeypadDialog extends Dialog// opens a dialog box instead of a static keypad
{
    private final int useds[];
        private final GameView gameView;

        public KeypadDialog(Context context, int useds[], GameView gameView)
        {
                super(context);
        this.gameView = gameView;
                this.useds = useds;
        setContentView(R.layout.keypad);

        View keypad = findViewById(R.id.keypad);

        View[] keyViews = new View[]
        {
            findViewById(R.id.keypad_1), // each keypad button has
            findViewById(R.id.keypad_2), // an associated view; we
            findViewById(R.id.keypad_3), // want to link these views
            findViewById(R.id.keypad_4), // to OnClickListeners so
            findViewById(R.id.keypad_5), // when a button is pressed
            findViewById(R.id.keypad_6), // a function is called to
            findViewById(R.id.keypad_7), // change the target tile's
            findViewById(R.id.keypad_8), // value to the value
            findViewById(R.id.keypad_9),  // represented by the button
            findViewById(R.id.hint)//find solve button
        };

        // TODO decide whether we're going to track illegal values for some purpose
        /*
                for (int element: useds)
                {
                        //sets invalid numbers invisible so they cannot be used
                        //I like this idea better than hints but we can take it out if we want to
                        if (element != 0)
                                keyViews[element - 1].setVisibility(View.INVISIBLE);
                }*/


        // now we have the views we can bind them to the listener function
        for (int i = 0; i < keyViews.length-1; i++)
        {
            final int j = i + 1;
            View.OnClickListener listener = new View.OnClickListener()
            {
                public void onClick(View view) {
                    keyListener(j);
                }
            };
            keyViews[i].setOnClickListener(listener);
        }

        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View view) {
                keyListener(0);
            }
        };
        keypad.setOnClickListener(listener);

        View.OnClickListener listen = new View.OnClickListener() {
            public void onClick(View view)
            {
                solver();
            }
        };
        keyViews[9].setOnClickListener(listen);
    }



    private void solver() {
        gameView.hint();
        dismiss();
    }

    // changes a tile's number
        private void keyListener(int newNumber)
    {
        gameView.changeSelectedTileNumber(newNumber);
                dismiss();
        }
}