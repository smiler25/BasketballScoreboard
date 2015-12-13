package com.smiler.basketball_scoreboard;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OverlayFragment extends Fragment{

    OverlayFragmentListener listener;
    public static String TAG = "OverlayFragment";

    public static OverlayFragment newInstance() {
        return new OverlayFragment();
    }

    public OverlayFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.overlay, container, false);
//        TextView leftPanelClose = (TextView) v.findViewById(R.id.left_panel_close);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onOverlayClick();
            }
        });
        return v;
    }

    public interface OverlayFragmentListener {
        void onOverlayClick();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OverlayFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OverlayFragmentListener");
        }
    }
}