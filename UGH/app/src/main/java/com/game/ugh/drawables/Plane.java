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
import com.game.ugh.views.GameView;

public class Plane implements IEnemy
{
    public int posX;
    public int posY;
    public int width;
    public int height;

    private Context context;
    private Bitmap image;
    private Bitmap imageFlippedRight;
    public PointD movementVector;

    public static double MOVEMENT_VEL_X = 0.3;
    public static double MOVEMENT_VEL_Y = 0.5;

    public Plane(Context context, int x, int y)
    {
        this.context = context;
        Bitmap source = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane);
        double ratio = (float)source.getWidth() / source.getHeight();

        int height = (int) (2.5 * Level.getInstance().tileHeight);
        int width = (int) (ratio * height);

        this.image = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.plane), width, height, false);
        this.imageFlippedRight = GameUtility.getInstance().flipBitmapHorizontally(image);
        this.width = image.getWidth();
        this.height = image.getHeight();

        this.posX = x;
        this.posY = y;
    }


    @Override
    public void draw(Canvas canvas)
    {
        if(movementVector.x > 0)
            canvas.drawBitmap(imageFlippedRight, posX, posY, null);
        else
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
        if(posX + width < 0 || posX > GameView.windowDimensions.x)
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
