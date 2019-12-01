package com.game.ugh.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.game.ugh.R;
import com.game.ugh.levels.Level;
import com.game.ugh.levels.UIController;
import com.game.ugh.utility.GameUtility;
import com.game.ugh.views.DefeatDialog;
import com.game.ugh.views.GameView;
import com.game.ugh.drawables.Player;
import com.game.ugh.views.VictoryDialog;

import java.io.IOException;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

public class GameActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    GameView gameView;
    SensorManager sensManager;
    Sensor accelSensor;

    MediaPlayer mediaPlayer;
    public static VictoryDialog victoryDialog;
    public static DefeatDialog defeatDialog;
    public static int lastPlayedLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFullscreen();
        setContentView(R.layout.activity_game);

        UIController.getInstance().nextStation = findViewById(R.id.text_next_crate);
        UIController.getInstance().totalCrates = findViewById(R.id.text_crates_total);
        UIController.getInstance().timer = findViewById(R.id.text_duration);
        UIController.getInstance().context = getApplicationContext();

        gameView = findViewById(R.id.game_view);
        gameView.setOnTouchListener(screenTouchListener);
        lastPlayedLevel = getIntent().getExtras().getInt("levelIndex");
        gameView.init(getApplicationContext(), lastPlayedLevel);
        //gameView = new GameView(this);
        //setContentView(gameView);
        //this.gameView.setOnTouchListener(screenTouchListener);
        sensManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelSensor = sensManager.getDefaultSensor(TYPE_ACCELEROMETER);

        victoryDialog = findViewById(R.id.victory_dialog);
        defeatDialog = findViewById(R.id.defeat_dialog);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.arcade_music_loop);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setFullscreen();
        sensManager.registerListener(sensorEventListener, accelSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensManager.unregisterListener(sensorEventListener);
        gameView = null;
        mediaPlayer.stop();


        Intent intent = new Intent(this, LevelSelectActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPrepared(MediaPlayer player)
    {
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void setFullscreen()
    {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | FLAG_LAYOUT_NO_LIMITS;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private View.OnTouchListener screenTouchListener = new View.OnTouchListener()
    {

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    Player.touchDetected = true;
                    break;
                case MotionEvent.ACTION_UP:
                    Player.touchDetected = false;
                    break;
            }
            return true;
        }
    };

    private SensorEventListener sensorEventListener = new SensorEventListener()
    {
        //Rotation vector
        @Override
        public void onSensorChanged(SensorEvent event)
        {
            //Log.d("SENSORS", String.valueOf(event.values[0]) + " " + String.valueOf(event.values[1])
            //        + " " + String.valueOf(event.values[2]) + " " + event.accuracy);
            Player.accelValues.add(event.values[0]);
            //Log.d("SENSORS", event.toString() + " " + event.sensor.getStringType());
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {

        }
    };

}
