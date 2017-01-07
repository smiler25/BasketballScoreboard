package com.smiler.basketball_scoreboard.layout;

import android.content.Context;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.models.Game;
import com.smiler.basketball_scoreboard.preferences.Preferences;

abstract class BaseLayout extends LinearLayout {

    public static final String TAG = "BS-BaseLayout";
    private final Preferences preferences;
    private final Game.GAME_TYPE game_type;
    private Game.GAME_TYPE layoutType;

    public BaseLayout(Context context, Preferences preferences) {
        this(context, Game.GAME_TYPE.COMMON, preferences);
    }

    public BaseLayout(Context context, Game.GAME_TYPE type, Preferences preferences) {
        super(context);
        this.preferences = preferences;
        game_type = type;
    }

    abstract BaseLayout initLayout(ViewStub stub, Game.GAME_TYPE layoutType, int timeoutRules,
                                  OnClickListener clickListener, OnLongClickListener longClickListener);

    abstract void initLayout();

    abstract void setSavedState();

    abstract void zeroState();

    private void setColor(TextView v, int color) {
        v.setTextColor(color);
    }

    private void setColorRed(TextView v) {
        setColor(v, getResources().getColor(R.color.red));
    }

    private void setColorGreen(TextView v) {
        setColor(v, getResources().getColor(R.color.green));
    }
}
