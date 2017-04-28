package com.smiler.basketball_scoreboard.layout;

import android.content.Context;

import com.smiler.basketball_scoreboard.preferences.Preferences;

public class LayoutFactory {
    public static BaseLayout getLayout(Context context, Preferences preferences,
                                ClickListener clickListener, LongClickListener longClickListener) {
        switch (preferences.layoutType) {
            case COMMON:
            case SIMPLE:
                return new StandardLayout(context, preferences, clickListener, longClickListener);
            case STREETBALL:
                return new StreetballLayout(context, preferences, clickListener, longClickListener);
        }

        return null;
    }
}
