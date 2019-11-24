package com.game.ugh.drawables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.game.ugh.R;
import com.game.ugh.enums.CrateState;
import com.game.ugh.levels.Level;

public class Crate implements IDrawable
{
    public int posX;
    public int posY;
    public int width;
    public int height;

    private Bitmap image;
    public CrateState state;


    public Crate(Context context, int col, int row)
    {
        int width = (int)Level.getInstance().tileWidth;
        int height = (int)Level.getInstance().tileHeight;
        this.posX = (int)(col * Level.getInstance().tileWidth);
        this.posY = (int)(row * Level.getInstance().tileHeight);

        this.image = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.box_double), width, height, false);
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.state = CrateState.Waiting;
    }

    @Override
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(this.image, this.posX, this.posY, null);
    }
}
