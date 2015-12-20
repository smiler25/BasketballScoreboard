package com.smiler.basketball_scoreboard;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

public class PrefFragment extends PreferenceFragment {
    OnSelectNestedScreenPreference listener;

    @Override
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_activity);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals("time_screen")) {
            listener.onSelectTimePreference();
        } else if (preference.getKey().equals("side_panels_screen")) {
            listener.onSelectSidePanelsPreference();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public interface OnSelectNestedScreenPreference {
        void onSelectTimePreference();
        void onSelectSidePanelsPreference();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnSelectNestedScreenPreference) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must selectNestedPreferenceListener");
        }
    }


}
