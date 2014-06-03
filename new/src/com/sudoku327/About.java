package com.sudoku327;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class About extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // lock screen rotation
        setContentView(R.layout.about);
        return;
    }
}