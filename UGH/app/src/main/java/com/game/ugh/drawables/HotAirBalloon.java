package com.game.ugh.drawables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.game.ugh.R;
import com.game.ugh.levels.Level;
import com.game.ugh.utility.GameUtility;
import com.game.ugh.utility.PointD;

public class HotAirBalloon implements IEnemy
{
    public int posX;
    public int posY;
    public int width;
    public int height;

    public static double MOVEMENT_VEL_X = 1;
    public static double MOVEMENT_VEL_Y = 10;

    private Bitmap image;
    private PointD movementVector;

    public HotAirBalloon(Context context, int x, int y)
    {
        Bitmap source = BitmapFactory.decodeResource(context.getResources(), R.drawable.hot_air_balloon_full);
        double ratio = source.getWidth() / source.getHeight();

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
        posX = (int) (posX + deltaTime * movementVector.x);
        posY = (int) (posY + deltaTime * movementVector.y);
    }

    @Override
    public boolean isOutOfBounds()
    {
        return false;
    }
}
