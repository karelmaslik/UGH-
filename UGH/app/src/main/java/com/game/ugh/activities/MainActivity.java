package com.game.ugh.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.game.ugh.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startGame();
    }

    private void startGame()
    {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);

    }
}
