package com.game.ugh.views;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.game.ugh.R;
import com.game.ugh.activities.LevelSelectActivity;

public class VictoryDialog extends ConstraintLayout
{

    public ConstraintLayout layout;
    private Button levels, leaderboard;

    public VictoryDialog(Context context)
    {
        super(context);
        init(context);
    }

    public VictoryDialog(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public VictoryDialog(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(final Context context)
    {
        inflate(context, R.layout.victory_dialog, this);
        layout = findViewById(R.id.victory_dialog_layout);
        levels = findViewById(R.id.levels_button);
        leaderboard = findViewById(R.id.leaderboard_button);

        levels.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LevelSelectActivity.class);
                context.startActivity(intent);

            }
        });
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        if(layout != null)
            layout.setVisibility(View.GONE);
    }
}
