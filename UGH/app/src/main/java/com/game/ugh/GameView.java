package com.game.ugh;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;

import com.game.ugh.drawables.Player;
import com.game.ugh.utility.GameUtility;

public class GameView extends View
{
    Bitmap background;
    Display display;

    public static Point windowDimensions = new Point();

    Player player;


    public GameView(Context context)
    {
        super(context);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background1);
        display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();

        display.getRealSize(windowDimensions);
        //display.getSize(windowDimensions);

        this.player = new Player(context);
        player.posX = windowDimensions.x / 2 - player.width / 2;
        player.posY = windowDimensions.y / 2 - player.height / 2;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        GameUtility.getInstance().updateTimers();
        drawBackground(canvas);
        drawPlayer(canvas);

        invalidate();
    }

    private void drawBackground(Canvas canvas)
    {
        canvas.drawBitmap(background, null, new Rect(0, 0, windowDimensions.x, windowDimensions.y), null);
        //* getContext().getResources().getDisplayMetrics().density)
    }

    private void drawPlayer(Canvas canvas)
    {
        Player.getInstance().movePlayer();
        Player.getInstance().drawPlayer(canvas);

    }


}
