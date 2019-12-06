package com.game.ugh.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.game.ugh.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class LeaderboardAdapter extends BaseAdapter
{
    public ArrayList<LeaderboardItem> items;
    private LayoutInflater inflater;

    public LeaderboardAdapter(Context context)
    {
        this.items = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);

        LeaderboardItem headerItem = new LeaderboardItem();
        this.items.add(headerItem);
    }

    @Override
    public int getCount()
    {
        return items.size();
    }

    @Override
    public Object getItem(int position)
    {
        return items.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return items.get(position).position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.leaderboard_item, parent, false);

            TextView positionText = convertView.findViewById(R.id.position_text);
            TextView playerNameText = convertView.findViewById(R.id.player_name_text);
            TextView timeText = convertView.findViewById(R.id.time_text);

            if(position != 0)
            {
                LeaderboardItem currItem = items.get(position);

                // -1 is the value if there are not enough entries in the database to fill the entire leaderboard
                if(currItem.mapIndex == -1)
                {
                    positionText.setText(String.valueOf(currItem.position));
                    playerNameText.setText("");
                    timeText.setText("");
                }
                else
                {
                    positionText.setText(String.valueOf(currItem.position));
                    playerNameText.setText(currItem.playerName);
                    timeText.setText(String.valueOf(currItem.completionTime));
                }
            }
        }

        return convertView;
    }
}
