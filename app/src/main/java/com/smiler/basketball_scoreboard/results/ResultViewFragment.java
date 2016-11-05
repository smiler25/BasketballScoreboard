package com.smiler.basketball_scoreboard.results;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smiler.basketball_scoreboard.R;

public class ResultViewFragment extends Fragment {
    public static String TAG = "BS-ResultViewFragment";
    private static final String gameId = "gameId";
    private ViewGroup view;

    public static ResultViewFragment newInstance(int sqlId) {
        ResultViewFragment fragment = new ResultViewFragment();
        Bundle args = new Bundle();
        args.putInt(gameId, sqlId);
        fragment.setArguments(args);
        return fragment;
    }

    public static ResultViewFragment newInstance() {
        return new ResultViewFragment();
    }

    public ResultViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() == null) {
            view = (ViewGroup) inflater.inflate(R.layout.result_view_empty, container, false);
            return view;
        }
        return new ResultView(getActivity(), getArguments().getInt(gameId));
    }

    public void updateContent(int itemId) {
        if (view != null) {
            view.removeAllViews();
            view.addView(new ResultView(getActivity(), itemId));
        }
    }

    public void clear() {
        view.removeAllViews();
        view.addView(getActivity().getLayoutInflater().inflate(R.layout.result_view_empty, view, false));
    }
}