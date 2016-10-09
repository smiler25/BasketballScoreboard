package com.smiler.basketball_scoreboard.models;

import android.content.res.Resources;

import com.smiler.basketball_scoreboard.R;

import java.util.HashMap;

public class ResultGameDetails {
    public static String LEADER_CHANGED = "lead_changed";
    public static String TIE = "tie";
    public static String HOME_MAX_LEAD = "home_max_lead";
    public static String GUEST_MAX_LEAD = "guest_max_lead";

    public static HashMap<String, String> strings(final Resources resources) {
        return new HashMap<String, String>()
        {{
            put(LEADER_CHANGED, resources.getString(R.string.lead_changed));
            put(TIE, resources.getString(R.string.tie));
            put(HOME_MAX_LEAD, resources.getString(R.string.home_max_lead));
            put(GUEST_MAX_LEAD, resources.getString(R.string.guest_max_lead));
        }};
    }
}