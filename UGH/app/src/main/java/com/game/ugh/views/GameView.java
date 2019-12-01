package com.game.ugh.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.widget.Button;

import com.game.ugh.R;
import com.game.ugh.drawables.Player;
import com.game.ugh.levels.Level;
import com.game.ugh.levels.LevelStateController;
import com.game.ugh.levels.UIController;
import com.game.ugh.utility.GameUtility;

public class GameView extends View
{
    Bitmap background;
    Paint bgFilter;

    Display display;
    Level level;
    Player player;

    public static Point windowDimensions = new Point();

    public GameView(Context context)
    {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void init(Context context, int levelIndex)
    {
        GameUtility.getInstance().reset();


        background = BitmapFactory.decodeResource(getResources(), R.drawable.background1);
        GameUtility.getInstance().levelDuration = 0;
        display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        //display.getSize(windowDimensions);
        display.getRealSize(windowDimensions);

        level = new Level(context, levelIndex);
        LevelStateController.getInstance().reset();
        //Level.getInstance().reset();

        player = new Player(context);
        player.posX = windowDimensions.x / 2 - player.width / 2;
        player.posY = windowDimensions.y - player.BOTTOM_EDGE_BORDER_INCREASE - player.height;

        bgFilter = new Paint();
        bgFilter.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        LevelStateController.getInstance().context = context;
    }




    @Override
    protected void onDraw(Canvas canvas)
    {
        try
        {
            super.onDraw(canvas);
            LevelStateController.getInstance().canvas = canvas;
            GameUtility.getInstance().updateTimers();
            drawBackground(canvas);
            drawLevel(canvas);
            drawPlayer(canvas);
            LevelStateController.getInstance().player = player;
            LevelStateController.getInstance().handleLevelChanges();
            UIController.getInstance().update();


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        //new Crate(getContext(), 6, 16).draw(canvas);
        //new Plane(getContext(), 300, 300).draw(canvas);
        //new HotAirBalloon(getContext(), 800, 1000).draw(canvas);

        //TODO: reduce quality of balloon, reduce transparent borders


        invalidate();
    }

    private void drawBackground(Canvas canvas)
    {
        canvas.drawBitmap(background, null, new Rect(0, 0, windowDimensions.x, windowDimensions.y), bgFilter);
        //* getContext().getResources().getDisplayMetrics().density)
    }

    private void drawPlayer(Canvas canvas)
    {
        Player.getInstance().movePlayer();
        Player.getInstance().draw(canvas);

    }

    public void drawLevel(Canvas canvas)
    {
        level.drawLevel(canvas);
    }

}
