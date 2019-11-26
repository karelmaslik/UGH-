package com.game.ugh.drawables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import com.game.ugh.R;
import com.game.ugh.levels.Level;
import com.game.ugh.utility.GameUtility;
import com.game.ugh.utility.PointD;
import com.game.ugh.views.GameView;

public class HotAirBalloon implements IEnemy
{
    public int posX;
    public int posY;
    public int width;
    public int height;

    public double MOVEMENT_VEL_X = 0.2;
    public double MOVEMENT_VEL_Y = 0.4;

    private Bitmap image;
    public PointD movementVector;

    public HotAirBalloon(Context context, int x, int y)
    {
        Bitmap source = BitmapFactory.decodeResource(context.getResources(), R.drawable.hot_air_balloon_small);
        double ratio = (float)source.getWidth() / source.getHeight();

        int width = (int) (3 * Level.getInstance().tileWidth);
        int height = (int) (width / ratio);

        long free = Runtime.getRuntime().freeMemory();
        this.image = Bitmap.createScaledBitmap((source), width, height, false);
        this.width = image.getWidth();
        this.height = image.getHeight();

        this.posX = x;
        this.posY = y;
    }


    @Override
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, posX, posY, null);
    }


    @Override
    public void move()
    {
        int deltaTime = GameUtility.getInstance().getDeltaTime();
        posX = (int) Math.round(posX + deltaTime * movementVector.x);
        posY = (int) Math.round(posY + deltaTime * movementVector.y);
    }

    @Override
    public boolean isOutOfBounds()
    {
        if(posY + height < 0 || posY > GameView.windowDimensions.y)
            return true;
        else
            return false;
    }

    public void setStartPos(int x, int y)
    {
        posX = x;
        posY = y;
    }
}
