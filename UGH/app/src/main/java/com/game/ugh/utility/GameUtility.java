package com.game.ugh.utility;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.time.Duration;
import java.util.ArrayList;



public class GameUtility
{
    private static GameUtility instance;
    public long lastTimeStamp;
    public long currTime;
    private int deltaTime;
    public long levelDuration;

    private GameUtility()
    {
        lastTimeStamp = System.nanoTime() - 1;
        currTime = System.nanoTime();
        levelDuration = 0;
    }

    public static GameUtility getInstance()
    {
        if(GameUtility.instance == null)
        {
            GameUtility.instance = new GameUtility();
            return GameUtility.instance;
        }
        else
            return GameUtility.instance;
    }

    public void reset()
    {
        GameUtility.instance = new GameUtility();
    }


    public void updateTimers()
    {
        this.lastTimeStamp = this.currTime;
        this.currTime = System.nanoTime();
        this.levelDuration += (this.currTime - this.lastTimeStamp);
        this.deltaTime = (int)((this.currTime - this.lastTimeStamp) / 1000000);
    }

    public Float getAverageFromList(ArrayList<Float> list)
    {
        Float result = 0.0f;
        if(!list.isEmpty())
        {
            for (Float num : list)
            {
                result += num;
            }
            return result / list.size();
        }
        return result;
    }

    public int getDeltaTime()
    {
        return this.deltaTime;
    }

    public Bitmap flipBitmapHorizontally(Bitmap bitmap)
    {
        Bitmap flippedBitmap;
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        flippedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return flippedBitmap;
    }
}
