package com.smiler.basketball_scoreboard.elements;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;

public class ScrolledTextFragment extends Fragment {
    public static ScrolledTextFragment newInstance(int textId) {
        ScrolledTextFragment f = new ScrolledTextFragment();
        Bundle args = new Bundle();
        args.putInt("textId", textId);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.scroll_text_view, container, false);
        Bundle args = getArguments();
        if (args == null) {
            return v;
        }
        ((TextView) v.findViewById(R.id.scrolled_text)).setText(args.getInt("textId"));
        return v;
    }
}