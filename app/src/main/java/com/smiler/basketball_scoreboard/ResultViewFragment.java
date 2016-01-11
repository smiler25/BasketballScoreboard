package com.smiler.basketball_scoreboard;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smiler.basketball_scoreboard.elements.ResultView;

public class ResultViewFragment extends Fragment {
    private static final String SQL_ARG = "sqlId";

    private ViewGroup view;

    public static ResultViewFragment newInstance(int sqlId) {
        ResultViewFragment fragment = new ResultViewFragment();
        Bundle args = new Bundle();
        args.putInt(SQL_ARG, sqlId);
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
        return new ResultView(getActivity(), getArguments().getInt(SQL_ARG));
    }

    public void updateContent(int sqlId) {
        if (view != null) {
            view.removeAllViews();
            view.addView(new ResultView(getActivity(), sqlId));
        }
    }

    public void clear() {
        view.removeAllViews();
        view.addView(getActivity().getLayoutInflater().inflate(R.layout.result_view_empty, view, false));
    }
}