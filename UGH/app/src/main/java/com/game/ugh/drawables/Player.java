package com.game.ugh.drawables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import androidx.constraintlayout.solver.widgets.Rectangle;

import com.game.ugh.enums.LossReason;
import com.game.ugh.levels.Level;
import com.game.ugh.levels.LevelStateController;
import com.game.ugh.levels.TileType;
import com.game.ugh.views.GameView;
import com.game.ugh.R;
import com.game.ugh.enums.Direction;
import com.game.ugh.utility.GameUtility;
import com.game.ugh.utility.PointD;

import java.util.ArrayList;

public class Player implements IDrawable
{
    private static Player instance;
    Context context;

    private Bitmap image;
    private Bitmap[] images;
    private Bitmap[] flippedImages;

    private int currAnimIndex = 0;
    private int currAnimTimer = 0;
    private int singleAnimFrame = 100;
    private int animLength = 8;
    private int fullAnim = singleAnimFrame * animLength;

    public double posX;
    public double posY;
    public int width;
    public int height;

    public static boolean groundCollision;

    private Direction directionVert;
    private Direction directionHoriz;

    private double velocity = 0;
    private double GRAVITY = 40;
    private double LIFT_POWER = -0.15;
    private final double UPPER_VEL_LIMIT = -10;
    private final double TERMINAL_VELOCITY = 25;
    private final double LOSS_VELOCITY = 20;
    private final double VELOCITY_COEFFICIENT = 0.0008;
    private final double HORIZONTAL_COEFFICIENT = 0.12;
    private final int ACCEL_VALUES_SMOOTHING_VALUES = 5;
    private final int BOTTOM_EDGE_BORDER_INCREASE = (int)(GameView.windowDimensions.y * 0.04);

    public static boolean touchDetected = false;
    public static ArrayList<Float> accelValues = new ArrayList<>();

    //TODO: split up player animation PNG to avoid glitching by a pixel at the end of the animation
    public Player(Context context)
    {
        double scale = 1.2;
        int textureWidth = 78;
        int textureHeight = 32;
        int importWidth = (int)Math.floor(textureWidth * context.getResources().getDisplayMetrics().density);
        int importHeight = (int)Math.floor(textureHeight * context.getResources().getDisplayMetrics().density);
        double ratio = importWidth / importHeight;
        this.images = new Bitmap[animLength];
        this.flippedImages = new Bitmap[animLength];
        Bitmap source = BitmapFactory.decodeResource(context.getResources(), R.drawable.heli_rmk);
        for(int i = 0; i < animLength; i++)
        {
            this.images[i] = Bitmap.createBitmap(source, i * importWidth, 0, importWidth, importHeight);
            this.images[i] = Bitmap.createScaledBitmap(this.images[i], (int)Math.round(scale * importWidth), (int)Math.round(scale * importWidth / ratio), false);
            this.flippedImages[i] = GameUtility.getInstance().flipBitmapHorizontally(images[i]);
        }
        this.width = (int)this.images[0].getWidth();
        this.height = this.images[0].getHeight();
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
            return flippedImages[currAnimIndex];
        else
            return images[currAnimIndex];
    }

    public void movePlayer()
    {
        double movementVectorY = calculateVerticalMovement();
        double movementVectorX = calculateHorizontalMovement();
        PointD movementVector = new PointD(movementVectorX, movementVectorY);
        boolean collisionDetectedAtBorder = handlePlayerBorderCollision(movementVector);
        boolean collisionDetectedTiles = handleLevelCollision(movementVector);

        if(groundCollision)
        {
            velocity = 0;
            movementVector.y = 0;
        }

        if(Math.abs(movementVector.x) < Level.getInstance().tileWidth / 2)
        {
            if(!groundCollision)
                posX = posX + movementVector.x;
        }
        if(Math.abs(movementVector.y) < Level.getInstance().tileHeight / 2)
            posY = posY + movementVector.y;

        Rectangle movedHitbox = this.getPlayerHitbox(movementVector);
        LevelStateController.getInstance().movedPlayerHitbox = movedHitbox;


        Log.d("MOVEMENT", movementVector.x + " " + movementVector.y);
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

        return movementVectorY;
    }

    public static Player getInstance()
    {
        return instance;
    }

    private boolean handlePlayerBorderCollision(PointD movementVector)
    {
        //TODO: end game state if velocity is too high and a collision happens
        boolean borderCollisionDetected = false;
        groundCollision = false;
        if(posY + movementVector.y + height > GameView.windowDimensions.y - BOTTOM_EDGE_BORDER_INCREASE)
        {
            posY = GameView.windowDimensions.y - height - BOTTOM_EDGE_BORDER_INCREASE;
            borderCollisionDetected = true;
            groundCollision = true;
            if(movementVector.y > LOSS_VELOCITY)
                LevelStateController.getInstance().setGameLost(LossReason.QuickFall);
            movementVector.y = 0;
        }
        else if(posY + movementVector.y < 0)
        {
            posY = 0;
            borderCollisionDetected = true;
            movementVector.y = 0;
        }
        if(!groundCollision)
        {
            if(posX + movementVector.x + width > GameView.windowDimensions.x)
            {
                posX = GameView.windowDimensions.x - width;
                borderCollisionDetected = true;
                movementVector.x = 0;
            }
            else if(posX + movementVector.x < 0)
            {
                posX = 0;
                borderCollisionDetected = true;
                movementVector.x = 0;
            }
        }

        return borderCollisionDetected;
    }

    private boolean handleLevelCollision(PointD movementVector)
    {
        boolean playerCollision = false;
        Rectangle tilesToCheckForCollisions = getCollisionTilesToCheck(movementVector);
        Rectangle playerHitbox = getPlayerHitbox(movementVector);
        Level level = Level.getInstance();
        extendCheckedBorders(tilesToCheckForCollisions);
        PointD workMovVec = new PointD(movementVector.x, movementVector.y);
        Double movVecXAdjustment = null;
        Double movVecYAdjustment = null;

        /*
        for(int col = tilesToCheckForCollisions.x; col < tilesToCheckForCollisions.x + tilesToCheckForCollisions.width; col++)
        {
            for(int row = tilesToCheckForCollisions.y; row < tilesToCheckForCollisions.y + tilesToCheckForCollisions.height; row++)
            {*/
        for(int col = 0; col < level.width; col++)
        {
            for(int row = 0; row < level.height; row++)
            {
                if(level.levelTileTypes[col][row] == TileType.Collidable)
                {
                    Rectangle tileHitbox = new Rectangle();
                    tileHitbox.setBounds(Math.round(col * level.tileWidth), Math.round(row * level.tileHeight), Math.round(level.tileWidth), Math.round(level.tileHeight));

                    //This if checks for collision with a specific tile
                    int safeguard = 3;
                    if (playerHitbox.y + safeguard < tileHitbox.y + tileHitbox.height && playerHitbox.y + playerHitbox.height > tileHitbox.y + safeguard
                                && playerHitbox.x + safeguard < tileHitbox.x + tileHitbox.width && playerHitbox.x + playerHitbox.width > tileHitbox.x + safeguard)
                    {
                        if(workMovVec.x > 0)
                        {
                            if(movVecXAdjustment == null || movVecXAdjustment > Math.abs(tileHitbox.x - (playerHitbox.x + playerHitbox.width)))
                            {
                                movVecXAdjustment = new Double(movementVector.x + (tileHitbox.x - (playerHitbox.x + playerHitbox.width)));
                            }
                        }
                        else if(workMovVec.x < 0)
                        {
                            if(movVecXAdjustment == null || movVecXAdjustment > Math.abs((tileHitbox.x + tileHitbox.width) - playerHitbox.x))
                            {
                                movVecXAdjustment = new Double(movementVector.x + ((tileHitbox.x + tileHitbox.width) - playerHitbox.x));
                            }
                        }

                        if(workMovVec.y > 0)
                        {
                            if(movVecYAdjustment == null || movVecYAdjustment > Math.abs(tileHitbox.y - (playerHitbox.y + playerHitbox.height)))
                            {
                                movVecYAdjustment = new Double(movementVector.y + (tileHitbox.y - (playerHitbox.y + playerHitbox.height)));
                                //Log.d("WINSTATE", String.valueOf(workMovVec.y));
                                if(workMovVec.y > LOSS_VELOCITY)
                                {
                                    LevelStateController.getInstance().setGameLost(LossReason.QuickFall);
                                }
                                groundCollision = true;
                            }
                        }
                        else if(workMovVec.y < 0)
                        {
                            if(movVecYAdjustment == null || movVecYAdjustment > Math.abs(tileHitbox.y - (playerHitbox.y + playerHitbox.height)))
                            {
                                movVecYAdjustment = new Double(movementVector.y + ((tileHitbox.y + tileHitbox.height) - playerHitbox.y));
                            }
                        }
                    }
                }
                else
                    continue;
            }
        }

        if(movVecXAdjustment != null && movVecXAdjustment < level.tileWidth / 2)
            movementVector.x = movVecXAdjustment;
        //else if(movVecXAdjustment != null)
        //    movementVector.x = movVecXAdjustment / 20;
        if(movVecYAdjustment != null && movVecYAdjustment < level.tileHeight / 2)
            movementVector.y = movVecYAdjustment;
        //else if(movVecYAdjustment != null)
        //    movementVector.y = movVecXAdjustment / 20;

        return playerCollision;
    }

    private Rectangle getCollisionTilesToCheck(PointD movementVector)
    {
        Rectangle collisionTilesRect = new Rectangle();
        Level level = Level.getInstance();
        int firstHorizontalTile = (int) Math.floor(posX / level.tileWidth);
        int firstVerticalTile = (int) Math.floor(posY / level.tileHeight);
        int width = (int) Math.ceil(this.width / level.tileWidth);
        int height = (int) Math.ceil(this.height / level.tileHeight);

        collisionTilesRect.setBounds(firstHorizontalTile, firstVerticalTile, width, height);
        return collisionTilesRect;
    }

    private Rectangle getPlayerHitbox(PointD movementVector)
    {
        Rectangle playerHitbox = new Rectangle();
        playerHitbox.setBounds((int)(posX + movementVector.x), (int)(posY + movementVector.y), width, height);

        return playerHitbox;
    }

    public Rectangle getCurrentPlayerHitbox()
    {
        return getPlayerHitbox(new PointD(0, 0));
    }

    public void extendCheckedBorders(Rectangle tilesToCheckForCollisions)
    {
        //Extending borders of needed collision checks by 1 if possible
        if(tilesToCheckForCollisions.x > 0)
            tilesToCheckForCollisions.x--;
        if(tilesToCheckForCollisions.x + tilesToCheckForCollisions.width - 1 < Level.getInstance().width - 1)
            tilesToCheckForCollisions.width++;
        if(tilesToCheckForCollisions.y > 0)
            tilesToCheckForCollisions.y--;
        if(tilesToCheckForCollisions.y + tilesToCheckForCollisions.height - 1 < Level.getInstance().height - 1)
            tilesToCheckForCollisions.height++;
    }



    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(this.getCurrImage(), (int)this.posX, (int)this.posY, null);
    }



}
