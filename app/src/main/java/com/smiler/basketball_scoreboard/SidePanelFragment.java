package com.smiler.basketball_scoreboard;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.elements.SidePanelRow;

import java.util.ArrayList;

public class SidePanelFragment extends Fragment {

    private TableLayout table;
    private ArrayList<SidePanelRow> rows = new ArrayList<>();

    public static SidePanelFragment newInstance() {
        return new SidePanelFragment();
    }

    public SidePanelFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.left_panel_full, container, false);
        table = (TableLayout) v.findViewById(R.id.left_panel_table);
        addHeader();
        TextView leftPanelClose = (TextView) v.findViewById(R.id.left_panel_close);
        leftPanelClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLeftPanelClose();
            }
        });
        TextView leftPanelAdd = (TextView) v.findViewById(R.id.left_panel_add);
        leftPanelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRow();
            }
        });
        TextView leftPanelAddAuto = (TextView) v.findViewById(R.id.left_panel_add_auto);
        leftPanelAddAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRowsAuto();
            }
        });

        return v;
    }

    private void addHeader() {
        table.addView(new SidePanelRow(getActivity().getApplicationContext(), true));
    }
    private void addRow() {
        SidePanelRow row = new SidePanelRow(getActivity().getApplicationContext());
        table.addView(row);
        rows.add(row);
    }

    private void addRowsAuto() {
        for (int i=1; i < 10; i++) {
            SidePanelRow row = new SidePanelRow(getActivity().getApplicationContext(), i, "Player" + i);
            table.addView(row);
            rows.add(row);
        }
    }

    private void editRow(int index, int number, String name) {
        SidePanelRow row = rows.get(index);
        row.setName(name);
    }

    public interface LeftPanelListener {
        void onLeftPanelClose();
    }

    LeftPanelListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (LeftPanelListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement LeftPanelListener");
        }
    }
}