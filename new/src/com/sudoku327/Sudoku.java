package com.sudoku327;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Sudoku extends Activity implements OnClickListener 
{
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // lock screen rotation
                setContentView(R.layout.activity_sudoku);

                View newButton = findViewById(R.id.new_button);
                newButton.setOnClickListener(this);

                View exitButton = findViewById(R.id.exit_button);
                exitButton.setOnClickListener(this);

                View aboutButton = findViewById(R.id.about_button);
                aboutButton.setOnClickListener(this); 
        }

        //function tells the program where to go once a button has been clicked
        public void onClick(View v) {
                switch (v.getId()) 
                {
                case R.id.new_button:
                        openNewGameDialog();
                        break;
                case R.id.about_button:
                		openRules(v);
                		break;
                case R.id.exit_button:
                        finish();
                        break;
                }
        }

    // starts a new game with difficulty [easy, medium, hard]
    private void startGame(int difficultyIndex) {
        Intent intent = new Intent(Sudoku.this, Game.class);
        intent.putExtra("difficulty", difficultyIndex);
        startActivity(intent);
    }

    private void openRules(View v)
    {
        Intent intent = new Intent(v.getContext(), About.class);
        startActivityForResult(intent, 0);
    }
  
        private void openNewGameDialog() {
        AlertDialog.Builder newGameDialog = new AlertDialog.Builder(this);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int difficultyIndex) {
                startGame(difficultyIndex);
            }
        };

        newGameDialog.setTitle("Difficulty");

        // add the difficulty levels to the dialog
        CharSequence[] difficulties = new CharSequence[] { "Easy", "Medium", "Hard", "Random"};
        newGameDialog.setItems(difficulties, listener);

        newGameDialog.show();
        }

}