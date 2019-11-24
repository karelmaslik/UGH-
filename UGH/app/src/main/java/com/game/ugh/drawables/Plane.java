package com.game.ugh.drawables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.game.ugh.R;
import com.game.ugh.enums.CrateState;
import com.game.ugh.levels.Level;
import com.game.ugh.utility.GameUtility;
import com.game.ugh.utility.PointD;

public class Plane implements IEnemy
{
    public int posX;
    public int posY;
    public int width;
    public int height;

    private Bitmap image;
    private PointD movementVector;

    public static double MOVEMENT_VEL_X = 10;
    public static double MOVEMENT_VEL_Y = 1;

    public Plane(Context context, int x, int y)
    {
        Bitmap source = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane);
        double ratio = source.getWidth() / source.getHeight();

        int height = (int) (3 * Level.getInstance().tileHeight);
        int width = (int) (ratio * height);

        this.image = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.plane), width, height, false);
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
    public boolean isOutOfBounds() {
        return false;
    }
}
