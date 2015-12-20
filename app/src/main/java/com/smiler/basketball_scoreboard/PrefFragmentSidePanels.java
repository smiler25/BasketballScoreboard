package com.smiler.basketball_scoreboard;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PrefFragmentSidePanels extends PreferenceFragment {
    @Override
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_activity_side_panels);
    }
}
