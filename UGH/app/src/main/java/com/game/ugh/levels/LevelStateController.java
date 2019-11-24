package com.game.ugh.levels;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.widget.Toast;

import androidx.constraintlayout.solver.widgets.Rectangle;

import com.game.ugh.drawables.Crate;
import com.game.ugh.enums.CrateState;

import java.util.ArrayList;
import java.util.Random;

public class LevelStateController
{
    private static LevelStateController instance;

    public Context context;
    public Canvas canvas;

    Crate currCrate;
    public Rectangle movedPlayerHitbox;
    Integer lastSenderStation = null;
    Integer lastDeliverStation = null;
    ArrayList<Point> stations;


    int deliveredCrates = 0;
    int MAX_CRATES_PER_LEVEL = 5;

    private LevelStateController()
    {
        stations = Level.getInstance().stations;
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

    public void handleLevelChanges()
    {
        handleCrateSpawn();
        handleCrateCollection();
        handleCrateDelivery();
        drawCrate();




    }

    private void handleCrateCollection()
    {
        if(checkCollision(movedPlayerHitbox, getCrateHitbox()))
        {
            currCrate.state = CrateState.PickedUp;
        }
    }

    private void handleCrateDelivery()
    {
        if(checkCollision(movedPlayerHitbox, getStationHitbox(lastDeliverStation)) && currCrate.state == CrateState.PickedUp)
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

    private boolean checkCollision(Rectangle hitbox1, Rectangle hitbox2)
    {
        int safeguard = 0;
        return hitbox1.y + safeguard < hitbox2.y + hitbox2.height && hitbox1.y + hitbox1.height > hitbox2.y + safeguard
                && hitbox1.x + safeguard < hitbox2.x + hitbox2.width && hitbox1.x + hitbox1.width > hitbox2.x + safeguard;
    }

}
