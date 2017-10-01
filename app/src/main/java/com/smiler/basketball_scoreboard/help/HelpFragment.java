package com.smiler.basketball_scoreboard.help;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.preferences.PrefActivity;

public class HelpFragment extends Fragment {

    private Switch panelsOn;

    public static HelpFragment newInstance(int layout) {
        HelpFragment f = new HelpFragment();
        Bundle args = new Bundle();
        args.putInt("layout", layout);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        int layoutId = args != null ? args.getInt("layout", R.layout.scroll_text_view) : R.layout.scroll_text_view;
        View v = inflater.inflate(layoutId, container, false);
        if (args != null && layoutId == R.layout.help_panels_fragment) {
            panelsOn = (Switch) v.findViewById(R.id.help_panels_switch);
            panelsOn.setChecked(getPanelsState());
            panelsOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    changePanelsState();
                }
            });
        }
        return v;
    }

    private boolean getPanelsState() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPref.getBoolean(PrefActivity.PREF_ENABLE_SIDE_PANELS, false);
    }

    private void changePanelsState() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(PrefActivity.PREF_ENABLE_SIDE_PANELS, panelsOn.isChecked());
        editor.apply();
        PrefActivity.prefChangedNoRestart = true;
    }

}