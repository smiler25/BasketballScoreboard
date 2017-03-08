package com.smiler.basketball_scoreboard.layout;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smiler.basketball_scoreboard.preferences.Preferences;

public class StandardViewFragment extends Fragment implements
        CameraLayout.ClickListener, CameraLayout.LongClickListener {

    public static String TAG = "BS-StandardFragment";
    public static final String FRAGMENT_TAG = "StandardFragment";
    private Preferences preferences;
    private BaseLayout layout;

    public static StandardViewFragment newInstance(int layout) {
        StandardViewFragment f = new StandardViewFragment();
        Bundle args = new Bundle();
        args.putInt("layout", layout);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Bundle args = getArguments();
//        int layoutId = args != null ? args.getInt("layout", R.layout.help_main_fragment) : R.layout.help_main_fragment;
        return layout;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public void setLayout(BaseLayout layout) {
        this.layout = layout;
    }

    @Override
    public void onChangeScoreClick(int team) {

    }

    @Override
    public void onMainTimeClick() {

    }

    @Override
    public void onPeriodClick() {

    }

    @Override
    public void onShotTimeClick() {

    }

    @Override
    public void onTakePictureClick() {

    }

    @Override
    public boolean onScoreLongClick(int team) {
        return false;
    }

    @Override
    public boolean onMainTimeLongClick() {
        return false;
    }

    @Override
    public boolean onPeriodLongClick() {
        return false;
    }

    @Override
    public boolean onShotTimeLongClick() {
        return false;
    }
}