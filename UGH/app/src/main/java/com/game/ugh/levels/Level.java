package com.game.ugh.levels;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.service.quicksettings.Tile;
import android.util.Log;

import com.game.ugh.R;
import com.game.ugh.views.GameView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Level
{
    private static Level instance;

    private String[][] level;
    public TileType[][] levelTileTypes;
    public ArrayList<Point> stations = new ArrayList<>();

    public int width;
    public int height;
    public float tileWidth;
    public float tileHeight;
    private int currLevelNumber;

    private HashMap<String, Bitmap> tileResourceIDDict;

    private AssetManager assetManager;
    private String LEVEL_ASSET_PREFIX = "ugh";
    private String CSV_DELIMITER = ",";


    public Level(Context context)
    {
        Level.instance = this;
        assetManager = context.getAssets();
        loadLevelMatrix(1);
        tileResourceIDDict = new HashMap<>();

        tileResourceIDDict.put("0", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.grass_center), (int)tileWidth, (int)tileHeight, false));
        tileResourceIDDict.put("1", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.grass_mid), (int)tileWidth, (int)tileHeight, false));
        tileResourceIDDict.put("2", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.stone_center), (int)tileWidth, (int)tileHeight, false));
        tileResourceIDDict.put("3", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.stone_mid), (int)tileWidth, (int)tileHeight, false));
        tileResourceIDDict.put("5", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.station1), (int)tileWidth, (int)tileHeight, false));
        tileResourceIDDict.put("6", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.station2), (int)tileWidth, (int)tileHeight, false));
        tileResourceIDDict.put("7", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.station3), (int)tileWidth, (int)tileHeight, false));
        tileResourceIDDict.put("8", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.station4), (int)tileWidth, (int)tileHeight, false));
        tileResourceIDDict.put("9", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.station5), (int)tileWidth, (int)tileHeight, false));
    }

    public static Level getInstance()
    {
        return Level.instance;
    }

    private void loadLevelMatrix(int levelNumber)
    {
        try
        {
            InputStream inStream = assetManager.open("levels/" + LEVEL_ASSET_PREFIX + levelNumber + ".csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            String line;
            ArrayList<String> lines = new ArrayList<>();
            while ((line = reader.readLine()) != null)
            {
                lines.add(line);
            }
            height = lines.size();
            for(int rowIndex = 0; rowIndex < lines.size(); rowIndex++)
            {
                String[] tileNumbers = lines.get(rowIndex).split(CSV_DELIMITER);
                if(rowIndex == 0)
                {
                    width = tileNumbers.length;
                    this.level = new String[width][height];
                    this.levelTileTypes = new TileType[width][height];
                }
                for(int columnIndex = 0; columnIndex < tileNumbers.length; columnIndex++)
                {
                    int currTileNum = Integer.valueOf(tileNumbers[columnIndex]);
                    this.level[columnIndex][rowIndex] = tileNumbers[columnIndex];
                    TileType currTileType;
                    if(currTileNum == -1)
                        currTileType = TileType.Empty;
                    else if(currTileNum >= 5 && currTileNum <= 9)
                    {
                        currTileType = TileType.Station;
                        stations.add(new Point(columnIndex, rowIndex));
                    }
                    else
                        currTileType = TileType.Collidable;
                    this.levelTileTypes[columnIndex][rowIndex] = currTileType;

                }
            }
            tileHeight = (float)GameView.windowDimensions.y / height;
            tileWidth = (float)GameView.windowDimensions.x / width;
            currLevelNumber = levelNumber;
            Log.wtf("tileW, tileH", tileWidth + " " + tileHeight);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void drawLevel(Canvas canvas)
    {
        for(int columnIdx = 0; columnIdx < width; columnIdx++)
        {
            for(int rowIdx = 0; rowIdx < height; rowIdx++)
            {
                if(!level[columnIdx][rowIdx].equals("-1"))
                    canvas.drawBitmap(tileResourceIDDict.get(level[columnIdx][rowIdx]), columnIdx * tileWidth, rowIdx * tileHeight, null);
            }
        }
    }
}
