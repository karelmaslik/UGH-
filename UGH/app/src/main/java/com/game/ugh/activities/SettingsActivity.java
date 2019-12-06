package com.game.ugh.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.game.ugh.R;

import java.util.prefs.Preferences;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

public class SettingsActivity extends Activity {

    TextView playerNameText;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public static String PREFERENCES_NAME = "settingsPreferences";
    public static String PLAYER_NAME_KEY = "playerName";
    public static String DEFAULT_USERNAME = "Username";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setFullscreen();

        playerNameText = findViewById(R.id.player_name_text);

        prefs = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        editor = prefs.edit();
        showPlayerName();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setFullscreen();
    }

    private void showPlayerName()
    {
        if(!prefs.contains(PLAYER_NAME_KEY))
        {
            editor.putString(PLAYER_NAME_KEY, DEFAULT_USERNAME);
            editor.commit();
        }

        playerNameText.setText(prefs.getString(PLAYER_NAME_KEY, DEFAULT_USERNAME));
    }

    private void setFullscreen()
    {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | FLAG_LAYOUT_NO_LIMITS;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void saveSettings(View view)
    {
        if(!playerNameText.getText().equals(""))
        {
            editor.putString(PLAYER_NAME_KEY, playerNameText.getText().toString());
            editor.commit();
        }
    }
}
