package com.game.ugh.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.game.ugh.R;

import java.util.ArrayList;



public class LevelSelectAdapter extends BaseAdapter {

    ArrayList<String> levels;

    LayoutInflater inflater;

    public LevelSelectAdapter(Context context, int numOfLevels)
    {
        super();
        this.inflater = LayoutInflater.from(context);
        levels = new ArrayList<>();
        for(int i = 0; i < numOfLevels; i++)
        {
            levels.add("Level " + (i + 1));
        }
    }

    @Override
    public int getCount()
    {
        return levels.size();
    }

    @Override
    public Object getItem(int position)
    {
        return levels.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.level_select_item, parent, false);

            TextView levelNameText = (TextView) convertView.findViewById(R.id.level_name);

            levelNameText.setText(levels.get(position));
        }

        return convertView;
    }
}
