package com.game.ugh.levels;

import android.content.Context;
import android.widget.TextView;

import com.game.ugh.R;
import com.game.ugh.enums.CrateState;
import com.game.ugh.utility.GameUtility;

public class UIController
{
    public TextView totalCrates;
    public TextView nextStation;
    public TextView timer;

    public String cratesText;
    public String nextStationText;
    private String timerText;

    private CrateState crateState;
    private int stationIndex;
    private int deliveredCrates;
    private int maxCrates;

    public Context context;

    private static UIController instance;

    private UIController()
    {

    }

    public static UIController getInstance()
    {
        if(UIController.instance == null)
            UIController.instance = new UIController();
        return UIController.instance;
    }

    public void update()
    {
        long levelDuration = GameUtility.getInstance().levelDuration;
        int miliSeconds = (int)(levelDuration / 1_000_000L);
        String secondsS = String.valueOf((int)Math.ceil(miliSeconds / 1000));
        String miliS = String.valueOf(miliSeconds % 1000);
        timerText = context.getString(R.string.level_time_label) + " " + secondsS + "." + miliS;
        if(crateState == CrateState.Waiting)
            nextStationText = context.getString(R.string.next_pickup_station_label) + " " + (stationIndex + 1);
        else
            nextStationText = context.getString(R.string.next_deliver_station_label) + " " + (stationIndex + 1);
        cratesText = deliveredCrates + "/" + maxCrates;

        timer.setText(timerText);
        nextStation.setText(nextStationText);
        totalCrates.setText(cratesText);
    }

    public void setCrateInfo(CrateState state, int stationIndex)
    {
        this.crateState = state;
        this.stationIndex = stationIndex;
    }

    public void setObjectiveInfo(int delivered, int max)
    {
        this.deliveredCrates = delivered;
        this.maxCrates = max;
    }
}
