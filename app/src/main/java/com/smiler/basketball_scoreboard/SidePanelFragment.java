package com.smiler.basketball_scoreboard;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.smiler.basketball_scoreboard.elements.EditPlayerDialog;
import com.smiler.basketball_scoreboard.elements.SidePanelRow;

import java.util.ArrayList;
import java.util.HashMap;

public class SidePanelFragment extends Fragment implements View.OnClickListener {

    LeftPanelListener listener;
    private TableLayout table;
    private HashMap<Integer, SidePanelRow> rows = new HashMap<>();
    private ArrayList<String> playersNumbers = new ArrayList<>();
    private ArrayList<SidePanelRow> activePlayers = new ArrayList<>();
    private SidePanelRow captainPlayer;
    private ToggleButton panelSelect;
    private boolean left = true;

    public static SidePanelFragment newInstance(boolean left) {
        SidePanelFragment f = new SidePanelFragment();
        Bundle args = new Bundle();
        args.putBoolean("left", left);
        f.setArguments(args);
        return f;
    }

    public SidePanelFragment() {}

    public interface LeftPanelListener {
        void onSidePanelClose(boolean left);
        void onLeftPanelActiveSelected(ArrayList<SidePanelRow> rows);
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
        Bundle args = getArguments();
        left = args.getBoolean("left", true);
        int layout_id, close_bu_id, table_layout_id, add_bu_id, add_auto_bu_id, toggle_bu_id;
        if (left) {
            layout_id = R.layout.side_panel_full_left;
            close_bu_id = R.id.left_panel_close;
            table_layout_id = R.id.left_panel_table;
            add_bu_id = R.id.left_panel_add;
            add_auto_bu_id = R.id.left_panel_add_auto;
            toggle_bu_id = R.id.left_panel_select_active;
        } else {
            layout_id = R.layout.side_panel_full_right;
            close_bu_id = R.id.right_panel_close;
            table_layout_id = R.id.right_panel_table;
            add_bu_id = R.id.right_panel_add;
            add_auto_bu_id = R.id.right_panel_add_auto;
            toggle_bu_id = R.id.right_panel_select_active;
        }

        View v = inflater.inflate(layout_id, container, false);
        table = (TableLayout) v.findViewById(table_layout_id);
        addHeader();
        v.findViewById(close_bu_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!panelSelect.isChecked()) {
                    listener.onSidePanelClose(left);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.side_panel_confirm), Toast.LENGTH_LONG).show();
                }
            }
        });
        v.findViewById(add_bu_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new EditPlayerDialog()).show(getFragmentManager(), EditPlayerDialog.TAG);

            }
        });
        v.findViewById(add_auto_bu_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRowsAuto();
            }
        });
        panelSelect = (ToggleButton) v.findViewById(toggle_bu_id);
        panelSelect.setOnClickListener(this);

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
        if (panelSelect.isChecked() || activePlayers.size() == 5) {
            View.OnClickListener l = panelSelect.isChecked() ? this : null;
            for (SidePanelRow row : rows.values()) { row.setOnClickListener(l); }
            if (!panelSelect.isChecked()) { listener.onLeftPanelActiveSelected(activePlayers);
            }
        } else {
            panelSelect.setChecked(true);
            Toast.makeText(getActivity(),
                    String.format((activePlayers.size() < 5)
                            ? getResources().getString(R.string.side_panel_few) : getResources().getString(R.string.side_panel_many),
                            activePlayers.size()),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void addHeader() {
        table.addView(new SidePanelRow(getActivity().getApplicationContext(), true, true));
    }

    public void editRow(int id, String number, String name, boolean captain) {
        SidePanelRow row = rows.get(id);
        String old_number = row.getNumber();
        row.edit(number, name, captain);
        if (!old_number.equals(number)) {
            playersNumbers.remove(old_number);
            playersNumbers.add(number);
        }
    }

    public void deleteRow(int id) {
        SidePanelRow row = rows.get(id);
        table.removeView(row);
        playersNumbers.remove(row.getNumber());
        activePlayers.remove(row);
    }

    public void addRow(String number, String name, boolean captain) {
        SidePanelRow row = new SidePanelRow(getActivity(), number, name, captain, true);
        if (captain) {
            if (captainPlayer != null) { captainPlayer.cancelCaptain(); }
            captainPlayer = row;
        }
        playersNumbers.add(number);
        table.addView(row);
        rows.put(row.getId(), row);
//        rows.add(row);
    }

    private void addRowsAuto() {
        for (int i=1; i <= 12; i++) {
            SidePanelRow row = new SidePanelRow(getActivity(), Integer.toString(i), "Player" + i, false, true);
            playersNumbers.add(Integer.toString(i));
            table.addView(row);
            rows.put(row.getId(), row);
//            rows.add(row);
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

    public boolean selectionConfirmed() {
        return !panelSelect.isChecked();
    }
}