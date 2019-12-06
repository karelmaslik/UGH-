package com.game.ugh.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.game.ugh.R;
import com.game.ugh.utility.LeaderboardAdapter;
import com.game.ugh.utility.LeaderboardItem;

import java.util.ArrayList;
import java.util.Set;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
import static com.game.ugh.activities.SettingsActivity.DEFAULT_USERNAME;
import static com.game.ugh.activities.SettingsActivity.PREFERENCES_NAME;

public class LeaderboardActivity extends Activity {

    SQLiteDatabase database;
    LeaderboardAdapter leaderboardAdapter;

    ListView leaderboardLV;

    public static String DATABASE_NAME = "leaderboardDatabase";

    public static String MAP_NUMBER_INTENT_KEY = "mapNumber";
    public static String COMPLETION_TIME_INTENT_KEY = "completionTime";

    public static Integer mapNumber;
    public static Double completionTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        setFullscreen();

        database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE,null);
        setupDatabase();
        Log.d("Leaderboard", "Here 1");
        leaderboardAdapter = new LeaderboardAdapter(getApplicationContext());
        leaderboardLV = findViewById(R.id.leaderboard_list_view);

        Bundle extras = getIntent().getExtras();
        //int mapNumber = extras.getInt(MAP_NUMBER_INTENT_KEY);
        //double completionTime = extras.getDouble(COMPLETION_TIME_INTENT_KEY);
        String playerName = getCurrentPlayerName();

        //Clear this
        //mapNumber = 2;
        //completionTime = 2.353;
        leaderboardAdapter.items.addAll(retrieveTop10(mapNumber));
        leaderboardAdapter.notifyDataSetChanged();
        leaderboardLV.setAdapter(leaderboardAdapter);

        TextView yourTime = findViewById(R.id.your_time_text);
        TextView yourPosition = findViewById(R.id.your_position_text);
        String yourTimeS = getText(R.string.your_time) + " " + String.valueOf(completionTime);
        String yourPositionS = getText(R.string.your_position) + " " + String.valueOf(retrievePlayerPosition(mapNumber, completionTime));
        yourTime.setText(yourTimeS);
        yourPosition.setText(yourPositionS);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setFullscreen();
    }

    private void setFullscreen()
    {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | FLAG_LAYOUT_NO_LIMITS;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void setupDatabase()
    {
        database.execSQL("CREATE TABLE IF NOT EXISTS Leaderboard(id INTEGER PRIMARY KEY AUTOINCREMENT, mapIndex Integer, playerName VARCHAR, completionTime real);");
    }

    public int retrievePlayerPosition(int mapNumber, double completionTime)
    {
        Cursor cursor = database.rawQuery(   "SELECT COUNT(*)\n" +
                                                "FROM Leaderboard\n" +
                                                "WHERE mapindex == " + mapNumber + " AND completionTime < " + completionTime + ";", null);
        cursor.moveToFirst();
        int position = cursor.getInt(0) + 1;
        cursor.close();

        return position;
    }

    public ArrayList<LeaderboardItem> retrieveTop10(int mapNumber)
    {
        Cursor cursor = database.rawQuery(  "SELECT mapIndex, playerName, completionTime \n" +
                                "FROM Leaderboard\n" +
                                "WHERE mapindex = " + mapNumber + "\n" +
                                "ORDER BY completiontime ASC\n" +
                                "LIMIT 10;", null);

        ArrayList<LeaderboardItem> leaderboardItems = new ArrayList<>();
        int pos = 1;
        if(cursor != null && cursor.moveToFirst())
        {
            do
            {
                LeaderboardItem item = new LeaderboardItem();
                item.position = pos++;
                item.mapIndex = cursor.getInt(0);
                item.playerName = cursor.getString(1);
                item.completionTime = cursor.getDouble(2);
                leaderboardItems.add(item);
            }
            while(cursor.moveToNext());
        }
        cursor.close();

        while(pos < 11)
        {
            LeaderboardItem item = new LeaderboardItem();
            item.position = pos++;
            item.mapIndex = -1;
            item.playerName = "";
            item.completionTime = 0.0;
            leaderboardItems.add(item);
        }

        return leaderboardItems;
    }

    private String getCurrentPlayerName()
    {
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFERENCES_NAME, MODE_PRIVATE);
        String playerName = prefs.getString(SettingsActivity.PLAYER_NAME_KEY, SettingsActivity.DEFAULT_USERNAME);

        return playerName;
    }

}
