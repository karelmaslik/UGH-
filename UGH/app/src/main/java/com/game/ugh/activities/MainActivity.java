package com.game.ugh.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.game.ugh.R;
import com.game.ugh.views.VictoryDialog;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFullscreen();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setFullscreen();
    }

    public void launchLevelSelect(View view)
    {
        Intent intent = new Intent(this, LevelSelectActivity.class);
        startActivity(intent);
    }

    private void setFullscreen()
    {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | FLAG_LAYOUT_NO_LIMITS;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
