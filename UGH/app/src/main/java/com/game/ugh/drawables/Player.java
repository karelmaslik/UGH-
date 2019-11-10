package com.game.ugh.drawables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import com.game.ugh.GameView;
import com.game.ugh.R;
import com.game.ugh.enums.Direction;
import com.game.ugh.utility.GameUtility;
import com.game.ugh.utility.PointD;

import java.util.ArrayList;
import java.util.List;

public class Player
{
    private static Player instance;
    Context context;

    private Bitmap image;
    private Bitmap[] images;

    private int currAnimIndex = 0;
    private int currAnimTimer = 0;
    private int singleAnimFrame = 100;
    private int animLength = 8;
    private int fullAnim = singleAnimFrame * animLength;

    public double posX;
    public double posY;
    public int width;
    public int height;
    private boolean groundCollision;

    private Direction directionVert;
    private Direction directionHoriz;
    private double velocity = 0;
    private double GRAVITY = 40;
    private double LIFT_POWER = -0.3;
    private final double UPPER_VEL_LIMIT = -10;
    private final double TERMINAL_VELOCITY = 25;
    private final double VELOCITY_COEFFICIENT = 0.0008;
    private final double HORIZONTAL_COEFFICIENT = 0.12;
    private final int ACCEL_VALUES_SMOOTHING_VALUES = 5;
    private final int BOTTOM_EDGE_BORDER_INCREASE = (int)(GameView.windowDimensions.y * 0.04);

    public static boolean touchDetected = false;
    public static ArrayList<Float> accelValues = new ArrayList<>();

    public Player(Context context)
    {
        double scale = 1.2;
        int textureWidth = 78;
        int textureHeight = 32;
        int importWidth = (int)Math.floor(textureWidth * context.getResources().getDisplayMetrics().density);
        int importHeight = (int)Math.floor(textureHeight * context.getResources().getDisplayMetrics().density);
        double ratio = importWidth / importHeight;
        this.images = new Bitmap[animLength];
        Bitmap source = BitmapFactory.decodeResource(context.getResources(), R.drawable.heli_rmk);
        for(int i = 0; i < animLength; i++)
        {
            this.images[i] = Bitmap.createBitmap(source, i * importWidth, 0, importWidth, importHeight);
            this.images[i] = Bitmap.createScaledBitmap(this.images[i], (int)Math.round(scale * importWidth), (int)Math.round(scale * importWidth / ratio), false);
            this.width = (int)(scale * importWidth);
            this.height = (int)(scale * importHeight);
        }
        this.image = images[currAnimIndex];
        Player.instance = this;
    }

    public Bitmap getCurrImage()
    {
        currAnimTimer += GameUtility.getInstance().getDeltaTime();
        while(currAnimTimer >= fullAnim)
            currAnimTimer -= fullAnim;
        currAnimIndex = (int)Math.floor(currAnimTimer / singleAnimFrame);

        if(directionHoriz == Direction.LEFT)
            return GameUtility.getInstance().flipBitmapHorizontally(images[currAnimIndex]);
        else
            return images[currAnimIndex];
    }

    public void movePlayer()
    {
        double movementVectorY = calculateVerticalMovement();
        double movementVectorX = calculateHorizontalMovement();
        PointD movementVector = new PointD(movementVectorX, movementVectorY);
        boolean collisionDetected = handlePlayerBorderCollision(movementVector);

    }

    private double calculateHorizontalMovement()
    {
        int dt = GameUtility.getInstance().getDeltaTime();
        double dx = 0;
        double movementVectorX;

        while(accelValues.size() > ACCEL_VALUES_SMOOTHING_VALUES)
            accelValues.remove(0);
        float tilt = GameUtility.getInstance().getAverageFromList(accelValues);
        //If the phone is approximately in the middle (not askew), do not move player to the side
        float safeZone = 0.6f;
        float rightPoint = -5.0f;
        float leftPoint = 5.0f;

        if(tilt < 0)
            Log.d("SENSORS", String.valueOf(tilt));

        //RIGHT TILT
        if(tilt < -safeZone && tilt > rightPoint)
        {
            dx = -tilt;
        }
        //MAX RIGHT TILT
        else if(tilt <= rightPoint)
        {
            dx = -rightPoint;
        }
        //LEFT TILT
        else if(tilt > safeZone && tilt < leftPoint)
        {
            dx = -tilt;
        }
        //MAX LEFT TILT
        else if(tilt >= leftPoint)
        {
            dx = -leftPoint;
        }
        this.directionHoriz = dx < 0 ? Direction.LEFT : Direction.RIGHT;
        movementVectorX = dx * dt * HORIZONTAL_COEFFICIENT;

        return movementVectorX;
    }

    private double calculateVerticalMovement()
    {
        int dt = GameUtility.getInstance().getDeltaTime();
        double movementVectorY = 0;

        this.directionVert = Player.touchDetected == true ? Direction.UP : Direction.DOWN;
        switch (directionVert)
        {
            case UP:
            {
                double dy = this.LIFT_POWER * dt;
                if(velocity + dy > this.UPPER_VEL_LIMIT)
                    velocity += dy;
                else
                    velocity = this.UPPER_VEL_LIMIT;
                movementVectorY = velocity;
                break;
            }
            case DOWN:
            {
                double dy = this.GRAVITY * dt * this.VELOCITY_COEFFICIENT;
                if(velocity + dy < this.TERMINAL_VELOCITY)
                    velocity += dy;
                else
                    velocity = this.TERMINAL_VELOCITY;
                movementVectorY = velocity;
                break;
            }
        }

        Log.d("VELOCITY", String.valueOf(velocity));
        return movementVectorY;
    }

    public static Player getInstance()
    {
        return instance;
    }

    private boolean handlePlayerBorderCollision(PointD movementVector)
    {
        //TODO: end game state if velocity is too high and a collision happens
        boolean collisionDetected = false;
        groundCollision = false;
        if(posY + movementVector.y + height > GameView.windowDimensions.y - BOTTOM_EDGE_BORDER_INCREASE)
        {
            posY = GameView.windowDimensions.y - height - BOTTOM_EDGE_BORDER_INCREASE;
            groundCollision = true;
        }
        else if(posY + movementVector.y < 0)
        {
            posY = 0;
        }
        else
        {
            posY += movementVector.y;
        }
        if(!groundCollision)
        {
            if(posX + movementVector.x + width > GameView.windowDimensions.x)
            {
                posX = GameView.windowDimensions.x - width;
            }
            else if(posX + movementVector.x < 0)
            {
                posX = 0;
            }
            else
            {
                posX += movementVector.x;
            }
        }


        Log.d("POSITION", String.valueOf(posY));
        return collisionDetected;
    }

    public void drawPlayer(Canvas canvas)
    {
        canvas.drawBitmap(this.getCurrImage(), (int)this.posX, (int)this.posY, null);
    }

}
