package com.game.ugh.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.game.ugh.R;
import com.game.ugh.utility.LevelSelectAdapter;

import java.io.IOException;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

public class LevelSelectActivity extends AppCompatActivity
{
    private GridView levSelGridView;
    private LevelSelectAdapter adapter;

    private AssetManager assetManager;

    private int numOfLevels;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            if(getIntent().getExtras().get("startLevel") != null)
                startGame(GameActivity.lastPlayedLevel);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_level_select);
        setFullscreen();

        assetManager = getAssets();
        numOfLevels = getNumOfLevels();

        levSelGridView = findViewById(R.id.level_select_gridview);
        adapter = new LevelSelectAdapter(getApplicationContext(), numOfLevels);
        levSelGridView.setAdapter(adapter);
        levSelGridView.setOnItemClickListener(levelItemClickListener);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setFullscreen();
    }

    public void startGame(int levelIndex)
    {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("levelIndex", levelIndex);
        startActivity(intent);

    }

    public static void startGame(int levelIndex, Context context)
    {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra("levelIndex", levelIndex);
        context.startActivity(intent);
    }

    public int getNumOfLevels()
    {
        try
        {
            String[] files = assetManager.list("levels/");
            return files.length;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    AdapterView.OnItemClickListener levelItemClickListener = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        startGame(position);
    }};


    private void setFullscreen()
    {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | FLAG_LAYOUT_NO_LIMITS;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
