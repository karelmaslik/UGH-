package com.game.ugh.levels;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.constraintlayout.solver.widgets.Rectangle;

import com.game.ugh.R;
import com.game.ugh.activities.GameActivity;
import com.game.ugh.activities.MainActivity;
import com.game.ugh.drawables.Crate;
import com.game.ugh.drawables.HotAirBalloon;
import com.game.ugh.drawables.IEnemy;
import com.game.ugh.drawables.Plane;
import com.game.ugh.drawables.Player;
import com.game.ugh.enums.CrateState;
import com.game.ugh.enums.Direction;
import com.game.ugh.enums.LossReason;
import com.game.ugh.utility.GameUtility;
import com.game.ugh.utility.PointD;
import com.game.ugh.views.GameView;
import com.game.ugh.views.VictoryDialog;

import java.util.ArrayList;
import java.util.Random;

import static android.view.View.VISIBLE;

public class LevelStateController
{
    private static LevelStateController instance;

    public Context context;
    public Canvas canvas;
    public Player player;

    Crate currCrate;
    public Rectangle movedPlayerHitbox;
    Integer lastSenderStation = null;
    Integer lastDeliverStation = null;
    ArrayList<Point> stations;

    int deliveredCrates = 0;
    int MAX_CRATES_PER_LEVEL = 5;

    private long lastEnemySpawnTime;
    private long spawnInterval = 15 * 1_000_000_000L;
    private IEnemy enemy;

    private boolean gameWon = false;
    private boolean gameLost = false;
    private LossReason lossReason;

    private LevelStateController()
    {
        stations = Level.getInstance().stations;
        lastEnemySpawnTime = GameUtility.getInstance().currTime + spawnInterval;
        currCrate = null;
        gameLost = false;
        gameWon = false;
        lossReason = null;
    }

    public static LevelStateController getInstance()
    {
        if(LevelStateController.instance == null)
        {
            LevelStateController.instance = new LevelStateController();
            return LevelStateController.instance;
        }
        else
            return LevelStateController.instance;
    }

    public void reset()
    {
        LevelStateController.instance = null;
    }

    public void handleLevelChanges()
    {
        handleCrateSpawn();
        handleCrateCollection();
        handleCrateDelivery();
        drawCrate();

        handleEnemyDespawn();
        handleEnemySpawn();
        handleObstacles();

        handleUICommunication();

        handleWinLossConditions();
    }

    private void handleUICommunication()
    {
        if(currCrate.state == CrateState.Waiting)
            UIController.getInstance().setCrateInfo(CrateState.Waiting, lastSenderStation);
        else
            UIController.getInstance().setCrateInfo(CrateState.PickedUp, lastDeliverStation);

        UIController.getInstance().setObjectiveInfo(deliveredCrates, MAX_CRATES_PER_LEVEL);
    }

    private void handleWinLossConditions()
    {
        if(deliveredCrates >= 1)
            gameWon = true;
        if(gameWon)
        {
            handleGameWin();
        }
        else if(gameLost)
        {
            handleGameLoss();
        }
    }

    private void handleGameLoss()
    {
        switch(this.lossReason)
        {
            case QuickFall:
                Toast.makeText(context, "The helicopter broke!", Toast.LENGTH_LONG);
                break;
            case EnemyCollision:
                Toast.makeText(context, "You collided with another air vehicle!", Toast.LENGTH_LONG);
        }
        if(this.lossReason != null)
            Log.d("WINSTATE", String.valueOf(this.lossReason));

        GameActivity.defeatDialog.layout.setVisibility(VISIBLE);
        Log.d("WINSTATE", String.valueOf(this.lossReason));
    }

    private void handleGameWin()
    {
        GameActivity.victoryDialog.layout.setVisibility(VISIBLE);
    }

    public void setGameLost(LossReason lossReason)
    {
        this.lossReason = lossReason;
        this.gameLost = true;
    }


    private void handleEnemySpawn()
    {
        if(enemy == null && GameUtility.getInstance().currTime - lastEnemySpawnTime > spawnInterval)
        {
            //Spawn random enemy, not using booleans for easier implementation of more enemies/obstacles in the future
            Random rand = new Random(System.nanoTime());
            int choiceEnemy = rand.nextInt();
            Log.d("ENEMY", "Spawning plane");

            if(choiceEnemy < 0)
            {
                int centerPlayerX = (int) Math.round(player.posX + player.width / 2);

                if(centerPlayerX > GameView.windowDimensions.x / 2)
                    spawnPlane(Direction.LEFT);
                else
                    spawnPlane(Direction.RIGHT);
            }
            else
            {
                int centerPlayerY = (int) Math.round(player.posY + player.height / 2);
                if(centerPlayerY > GameView.windowDimensions.y / 2)
                    spawnBalloon(Direction.UP);
                else
                    spawnBalloon(Direction.DOWN);
            }
        }
    }

    private void spawnPlane(Direction spawnDirection)
    {
        Random rand = new Random(System.nanoTime());
        //Starting y position difference from the players y position
        int yDiff = rand.nextInt() % (int) (3 * Level.getInstance().tileHeight);
        int spawnX;
        Plane plane = new Plane(context, 0, 0);
        if(spawnDirection == Direction.LEFT)
            spawnX = 0 - plane.width;
        else
            spawnX = GameView.windowDimensions.x + 1;
        plane.setStartPos(spawnX, (int)(player.posY + yDiff));

        float yDeviation = (rand.nextInt() % 100) / (float)1000;
        yDeviation = (yDeviation < 0) ? (float)(yDeviation - 0.03) : (float) (yDeviation + 0.03);
        double movX;
        if(spawnDirection == Direction.LEFT)
            movX = plane.MOVEMENT_VEL_X;
        else
            movX = -plane.MOVEMENT_VEL_X;
        double movY = plane.MOVEMENT_VEL_Y * yDeviation;
        plane.movementVector = new PointD(movX, movY);

        this.enemy = (IEnemy) plane;
        this.lastEnemySpawnTime = System.nanoTime();
    }

    private void spawnBalloon(Direction spawnDirection)
    {
        Random rand = new Random(System.nanoTime());
        //Starting y position difference from the players y position
        int xDiff = rand.nextInt() % (int) (3 * Level.getInstance().tileWidth);
        int spawnY;
        HotAirBalloon balloon = new HotAirBalloon(context, 0, 0);
        if(spawnDirection == Direction.UP)
            spawnY = 0 - balloon.height;
        else
            spawnY = GameView.windowDimensions.y + 1;
        balloon.setStartPos((int) player.posX + xDiff, spawnY);

        float xDeviation = (rand.nextInt() % 100) / (float)1000;
        xDeviation = (xDeviation < 0) ? (float)(xDeviation - 0.03) : (float) (xDeviation + 0.03);
        double movY;
        if (spawnDirection == Direction.UP)
        {
            movY = balloon.MOVEMENT_VEL_X;
        }
        else
        {
            movY = -balloon.MOVEMENT_VEL_X;
        }
        double movX = balloon.MOVEMENT_VEL_Y * xDeviation;
        balloon.movementVector = new PointD(movX, movY);

        this.enemy = (IEnemy) balloon;
        this.lastEnemySpawnTime = System.nanoTime();
    }

    private void handleObstacles()
    {
        if(enemy != null)
        {
            enemy.move();
            enemy.draw(canvas);
            if(checkCollision(player.getCurrentPlayerHitbox(), enemy.getHitbox(), 10))
                setGameLost(LossReason.EnemyCollision);
        }

    }

    private void handleEnemyDespawn()
    {
        if(enemy != null && enemy.isOutOfBounds())
        {
            enemy = null;
            Log.d("ENEMY", "Despaned");
        }
    }

    private void handleCrateCollection()
    {
        if(checkCollision(movedPlayerHitbox, getCrateHitbox(), 0) && Player.groundCollision == true)
        {
            currCrate.state = CrateState.PickedUp;
        }
    }

    private void handleCrateDelivery()
    {
        if(checkCollision(movedPlayerHitbox, getStationHitbox(lastDeliverStation), 0) && currCrate.state == CrateState.PickedUp && Player.groundCollision == true)
        {
            Log.wtf("DELIVERY", "Delivered");
            currCrate.state = CrateState.Delivered;
            deliveredCrates++;
        }
    }

    private void handleCrateSpawn()
    {
        if((currCrate == null || currCrate.state == CrateState.Delivered)&& deliveredCrates < MAX_CRATES_PER_LEVEL)
        {
            Integer sender = null;
            if(lastDeliverStation == null)
            {
                lastSenderStation = Math.abs(new Random(System.nanoTime()).nextInt()) % stations.size();
            }
            else
            {
                sender = Math.abs(new Random(System.nanoTime()).nextInt()) % stations.size();
                while(sender == lastDeliverStation)
                    sender = Math.abs(new Random(System.nanoTime()).nextInt()) % stations.size();
                lastSenderStation = sender;
            }

            Integer deliver = null;
            if(lastDeliverStation == null)
            {
                lastDeliverStation = Math.abs(new Random(System.nanoTime()).nextInt()) % stations.size();
                while(lastDeliverStation == lastSenderStation)
                    lastDeliverStation = Math.abs(new Random(System.nanoTime()).nextInt()) % stations.size();
            }
            else
            {
                deliver = Math.abs(new Random(System.nanoTime()).nextInt()) % stations.size();
                while(deliver == lastDeliverStation || deliver == sender)
                    deliver = Math.abs(new Random(System.nanoTime()).nextInt()) % stations.size();
                lastDeliverStation = deliver;
            }

            this.currCrate = new Crate(context, stations.get(lastSenderStation).x, stations.get(lastSenderStation).y);
            Log.wtf("DELIVER", String.valueOf("Spawned " + lastDeliverStation));
        }
    }

    private void drawCrate()
    {
        if(currCrate.state == CrateState.Waiting)
        {
            currCrate.draw(this.canvas);
        }
    }

    private Rectangle getCrateHitbox()
    {
        Rectangle crateHitbox = new Rectangle();
        crateHitbox.setBounds(currCrate.posX, currCrate.posY, currCrate.width, currCrate.height);
        return crateHitbox;
    }

    private Rectangle getStationHitbox(int stationIndex)
    {
        Point station = stations.get(stationIndex);
        Level level = Level.getInstance();
        Rectangle stationHitbox = new Rectangle();
        stationHitbox.setBounds((int)(station.x * level.tileWidth), (int)(station.y * level.tileHeight), (int)level.tileWidth, (int)level.tileHeight);

        return stationHitbox;
    }

    private boolean checkCollision(Rectangle hitbox1, Rectangle hitbox2, int safeguard)
    {
        return hitbox1.y + safeguard < hitbox2.y + hitbox2.height && hitbox1.y + hitbox1.height > hitbox2.y + safeguard
                && hitbox1.x + safeguard < hitbox2.x + hitbox2.width && hitbox1.x + hitbox1.width > hitbox2.x + safeguard;
    }


}
