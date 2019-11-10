package com.game.ugh;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.game.ugh.drawables.Player;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

public class GameActivity extends AppCompatActivity {

    GameView gameView;
    SensorManager sensManager;
    Sensor accelSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFullscreen();
        gameView = new GameView(this);
        setContentView(gameView);
        this.gameView.setOnTouchListener(screenTouchListener);
        sensManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelSensor = sensManager.getDefaultSensor(TYPE_ACCELEROMETER);
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
