package com.smiler.basketball_scoreboard.layout;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BoardFragment extends Fragment implements
        CameraLayout.ClickListener, CameraLayout.LongClickListener {

    public static String TAG = "BS-BoardFragment";
    public static final String FRAGMENT_TAG = "BoardFragment";
    private BaseLayout layout;

    public static BoardFragment newInstance() {
        return new BoardFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return layout;
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