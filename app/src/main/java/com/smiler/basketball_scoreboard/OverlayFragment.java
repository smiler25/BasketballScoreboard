package com.smiler.basketball_scoreboard;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.smiler.basketball_scoreboard.Constants.OVERLAY_PANELS;
import static com.smiler.basketball_scoreboard.Constants.OVERLAY_SWITCH;

public class OverlayFragment extends Fragment{

    OverlayFragmentListener listener;
    public static String TAG = "OverlayFragment";
    public static String TAG_PANELS = "OverlayPANELS";
    public static String TAG_SWITCH = "OverlaySWITCH";

    public static OverlayFragment newInstance() {
        return new OverlayFragment();
    }

    public static OverlayFragment newInstance(int type) {
        OverlayFragment f = new OverlayFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        f.setArguments(args);
        return f;
    }

    public OverlayFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        int type = args == null ? OVERLAY_PANELS : args.getInt("type", OVERLAY_PANELS);
        View v = null;

        switch (type) {
            case OVERLAY_PANELS:
                v = inflater.inflate(R.layout.overlay_panels, container, false);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onOverlayClick();
                    }
                });
                v.findViewById(R.id.left_panel_overlay_open).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onOverlayOpenPanel(Constants.SIDE_PANELS_LEFT);
                    }
                });

                v.findViewById(R.id.right_panel_overlay_open).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onOverlayOpenPanel(Constants.SIDE_PANELS_RIGHT);
                    }
                });
                break;
            case OVERLAY_SWITCH:
                v = inflater.inflate(R.layout.overlay_switch, container, false);
                break;
        }
        return v;
    }

    interface OverlayFragmentListener {
        void onOverlayClick();
        void onOverlayOpenPanel(int type);
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