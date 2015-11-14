package com.smiler.basketball_scoreboard;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

public class PrefFragment extends PreferenceFragment {
    OnSelectTimePreference selectTimePreferenceListener;

    @Override
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_activity);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals("time_screen")) {
            selectTimePreferenceListener.onSelectTimePreference();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public interface OnSelectTimePreference {
        void onSelectTimePreference();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            selectTimePreferenceListener = (OnSelectTimePreference) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must selectNestedPreferenceListener");
        }
    }


}
