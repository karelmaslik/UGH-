package com.game.ugh.views;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.game.ugh.R;
import com.game.ugh.activities.GameActivity;
import com.game.ugh.activities.LevelSelectActivity;

public class DefeatDialog extends ConstraintLayout
{
    public ConstraintLayout layout;
    private Button levels, restart;

    public static boolean restartNeeded = false;

    public DefeatDialog(Context context)
    {
        super(context);
        init(context);
    }

    public DefeatDialog(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public DefeatDialog(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(final Context context)
    {
        inflate(context, R.layout.defeat_dialog, this);
        layout = findViewById(R.id.defeat_dialog_layout);
        levels = findViewById(R.id.levels_button);
        restart = findViewById(R.id.restart_button);

        levels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LevelSelectActivity.class);
                context.startActivity(intent);

            }
        });

        restart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LevelSelectActivity.class);
                intent.putExtra("startLevel", true);
                intent.putExtra("levelIndex", GameActivity.lastPlayedLevel);
                restartNeeded = true;
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
