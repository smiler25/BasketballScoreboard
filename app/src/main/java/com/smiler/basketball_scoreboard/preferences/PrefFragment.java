package com.smiler.basketball_scoreboard.preferences;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.smiler.basketball_scoreboard.R;

public class PrefFragment extends PreferenceFragment {
    OnSelectNestedScreenPreference listener;

    @Override
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_activity);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getKey()) {
            case "time_screen":
                listener.onSelectTimePreference();
                break;
            case "side_panels_screen":
                listener.onSelectSidePanelsPreference();
                break;
            case "sounds_screen":
                listener.onSelectSoundsPreference();
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public interface OnSelectNestedScreenPreference {
        void onSelectTimePreference();
        void onSelectSidePanelsPreference();
        void onSelectSoundsPreference();
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
