package com.smiler.basketball_scoreboard;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.smiler.basketball_scoreboard.elements.EditPlayerDialog;
import com.smiler.basketball_scoreboard.elements.SidePanelRow;

import java.util.ArrayList;

public class SidePanelFragment extends Fragment implements View.OnClickListener {

    LeftPanelListener listener;
    private TableLayout table;
    private ArrayList<SidePanelRow> rows = new ArrayList<>();
    private ArrayList<String> playersNumbers = new ArrayList<>();
    private ArrayList<SidePanelRow> activePlayers = new ArrayList<>();
    private SidePanelRow captainPlayer;
    private ToggleButton leftPanelSelect;

    public static SidePanelFragment newInstance() {
        return new SidePanelFragment();
    }

    public SidePanelFragment() {}

    public interface LeftPanelListener {
        void onLeftPanelClose();
        void onLeftPanelActiveSelected(ArrayList<String> numbers);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (LeftPanelListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement LeftPanelListener");
        }
    }

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
                (new EditPlayerDialog()).show(getFragmentManager(), EditPlayerDialog.TAG);

            }
        });
        TextView leftPanelAddAuto = (TextView) v.findViewById(R.id.left_panel_add_auto);
        leftPanelAddAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRowsAuto();
            }
        });
        leftPanelSelect = (ToggleButton) v.findViewById(R.id.left_panel_select_active);
        leftPanelSelect.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof SidePanelRow) {
            if (((SidePanelRow) v).select()) {
                activePlayers.add((SidePanelRow) v);
            } else {
                activePlayers.remove(v);
            }
        } else {
            switch (v.getId()) {
                case R.id.left_panel_select_active:
                    handleSelection();
                    break;
            }
        }
    }

    private void handleSelection() {
        if (leftPanelSelect.isChecked() || activePlayers.size() == 5) {
            View.OnClickListener l = leftPanelSelect.isChecked() ? this : null;
            for (SidePanelRow row : rows) { row.setOnClickListener(l); }
            if (!leftPanelSelect.isChecked()) { listener.onLeftPanelActiveSelected(getActivePlayersNumbers()); }
        } else {
            leftPanelSelect.setChecked(true);
            Toast.makeText(getActivity(),
                    (activePlayers.size() < 5) ? "Too few" : "Too many",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void addHeader() {
        table.addView(new SidePanelRow(getActivity().getApplicationContext(), true));
    }
    public void addRow() {
        SidePanelRow row = new SidePanelRow(getActivity().getApplicationContext());
        table.addView(row);
        rows.add(row);
    }
    public void addRow(String number, String name, boolean captain) {
        SidePanelRow row = new SidePanelRow(getActivity(), number, name, captain);
        if (captain) {
            if (captainPlayer != null) { captainPlayer.cancelCaptain(); }
            captainPlayer = row;
        }
        playersNumbers.add(number);
        table.addView(row);
        rows.add(row);
    }

    private void addRowsAuto() {
        for (int i=1; i <= 12; i++) {
            SidePanelRow row = new SidePanelRow(getActivity().getApplicationContext(), Integer.toString(i), "Player" + i, false);
            table.addView(row);
            rows.add(row);
        }
    }

    public int checkNewPlayer(String number, boolean captain) {
        int status = 0;
        if (!numberAvailable(number)) { status = 1; }
        if (!(!captain || captainNotAssigned())) { status |= 2; }
        return status;
    }
    public boolean captainNotAssigned() {
        return captainPlayer == null;
    }
    public boolean numberAvailable(String number) {
        return !(playersNumbers.contains(number));
    }
    public ArrayList<String> getActivePlayersNumbers() {
        ArrayList<String> res = new ArrayList<>();
        for (SidePanelRow row : activePlayers) {
            res.add(row.getNumber());
        }
        return res;
    }
}