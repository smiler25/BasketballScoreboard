package com.smiler.basketball_scoreboard.panels;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.smiler.basketball_scoreboard.Constants;
import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.PlayersResults;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.db.Results;
import com.smiler.basketball_scoreboard.elements.EditPlayerDialog;
import com.smiler.basketball_scoreboard.elements.ListDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import io.realm.Realm;
import io.realm.RealmResults;

public class SidePanelFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    public static final String TAG_LEFT_PANEL = "LeftSidePanel";
    public static final String TAG_RIGHT_PANEL = "RightSidePanel";

    private SidePanelListener listener;
    private TableLayout table;
    private TreeMap<Integer, SidePanelRow> rows = new TreeMap<>();
    private List<Integer> playersNumbers = new ArrayList<>();
    private TreeSet<SidePanelRow> activePlayers = new TreeSet<>();
    private SidePanelRow captainPlayer;
    private ToggleButton panelSelect;
    private boolean left = true;

    public static SidePanelFragment newInstance(boolean left) {
        Bundle args = new Bundle();
        args.putBoolean("left", left);
        SidePanelFragment f = new SidePanelFragment();
        f.setArguments(args);
        return f;
    }

    public SidePanelFragment() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (rows.size() > 0 && playersNumbers.size() > 0) {
            updateView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public interface SidePanelListener {
        void onSidePanelClose(boolean left);
        void onSidePanelActiveSelected(TreeSet<SidePanelRow> rows, boolean left);
        void onSidePanelNoActive(boolean left);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (SidePanelListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SidePanelListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        left = args.getBoolean("left", true);
        return initView(inflater, container);
    }

    @NonNull
    private View initView(LayoutInflater inflater, ViewGroup container) {
        int layout_id, table_layout_id, close_bu_id, clear_bu_id, add_bu_id, add_auto_bu_id, toggle_bu_id;
        if (left) {
            layout_id = R.layout.side_panel_full_left;
            close_bu_id = R.id.left_panel_close;
            table_layout_id = R.id.left_panel_table;
            add_bu_id = R.id.left_panel_add;
            add_auto_bu_id = R.id.left_panel_add_auto;
            toggle_bu_id = R.id.left_panel_select_active;
            clear_bu_id = R.id.left_panel_clear;
        } else {
            layout_id = R.layout.side_panel_full_right;
            close_bu_id = R.id.right_panel_close;
            table_layout_id = R.id.right_panel_table;
            add_bu_id = R.id.right_panel_add;
            add_auto_bu_id = R.id.right_panel_add_auto;
            toggle_bu_id = R.id.right_panel_select_active;
            clear_bu_id = R.id.right_panel_clear;
        }

        View v = inflater.inflate(layout_id, container, false);
        table = (TableLayout) v.findViewById(table_layout_id);
        addHeader();
        v.findViewById(close_bu_id).setOnClickListener(this);
        v.findViewById(add_bu_id).setOnClickListener(this);
        v.findViewById(clear_bu_id).setOnClickListener(this);
        View addAutoView = v.findViewById(add_auto_bu_id);
        addAutoView.setOnClickListener(this);
        addAutoView.setOnLongClickListener(this);
        panelSelect = (ToggleButton) v.findViewById(toggle_bu_id);
        panelSelect.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof SidePanelRow) {
            if (((SidePanelRow) v).toggleSelected()) {
                activePlayers.add((SidePanelRow) v);
            } else {
                activePlayers.remove(v);
            }
        } else {
            switch (v.getId()) {
                case R.id.left_panel_select_active:
                case R.id.right_panel_select_active:
                    handleSelection();
                    break;
                case R.id.left_panel_close:
                case R.id.right_panel_close:
                    if (!panelSelect.isChecked()) {
                        listener.onSidePanelClose(left);
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.side_panel_confirm), Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.left_panel_add:
                case R.id.right_panel_add:
                    if (!checkAddAvailable()) { return; }
                    EditPlayerDialog.newInstance(left).show(getFragmentManager(), EditPlayerDialog.TAG);
                    break;
                case R.id.left_panel_clear:
                case R.id.right_panel_clear:
                    clear();
                    break;
                case R.id.left_panel_add_auto:
                case R.id.right_panel_add_auto:
                    addRowsAuto();
                    break;
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.left_panel_add_auto:
            case R.id.right_panel_add_auto:
                restoreCurrentData();
                return true;
        }
        return false;
    }

    private void handleSelection() {
        if (panelSelect.isChecked() || activePlayers.size() <= 5) {
            View.OnClickListener l = panelSelect.isChecked() ? this : null;
            for (SidePanelRow row : rows.values()) { row.setOnClickListener(l); }
            if (!panelSelect.isChecked()) {
                listener.onSidePanelActiveSelected(activePlayers, left);
            }
        } else {
            panelSelect.setChecked(true);
            Toast.makeText(getActivity(),
                    String.format(activePlayers.size() < 5
                            ? getResources().getString(R.string.side_panel_few) : getResources().getString(R.string.side_panel_many),
                            activePlayers.size()),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void addHeader() {
        table.addView(new SidePanelRow(getActivity().getApplicationContext(), left));
    }

    public boolean editRow(int id, int number, String name, boolean captain) {
        SidePanelRow row = rows.get(id);
        int old_number = row.getNumber();
        row.edit(number, name, captain);
        if (old_number != number) {
            playersNumbers.remove(Integer.valueOf(old_number));
            playersNumbers.add(number);
        }
        return true;
    }

    public boolean deleteRow(int id) {
        SidePanelRow row = rows.get(id);
        table.removeView(row);
        playersNumbers.remove(Integer.valueOf(row.getNumber()));
        activePlayers.remove(row);
        rows.remove(row.getId());
        return true;
    }

    public SidePanelRow addRow(int number, String name, boolean captain) {
        if (!checkAddAvailable()) {return null;}
        SidePanelRow row = new SidePanelRow(getActivity(), number, name, captain, left);
        if (captain) {
            if (captainPlayer != null) { captainPlayer.cancelCaptain(); }
            captainPlayer = row;
        }
        playersNumbers.add(number);
        table.addView(row);
        rows.put(row.getId(), row);
        return row;
    }

    private void addRowsAuto() {
        if (!checkAddAvailable()) {return;}
        int count = playersNumbers.size();
        int number = 1;
        String name = getResources().getString(R.string.side_panel_player_name);
        while (count < Constants.MAX_PLAYERS) {
            while (playersNumbers.contains(number)) { number++; }
            SidePanelRow row = new SidePanelRow(getActivity(), number, String.format(name, number), false, left);
            playersNumbers.add(number);
            table.addView(row);
            rows.put(row.getId(), row);
            count++;
        }
    }

    public int checkNewPlayer(int number, boolean captain) {
        int status = 0;
        if (!numberAvailable(number)) { status = 1; }
        if (!(!captain || captainNotAssigned())) { status |= 2; }
        return status;
    }

//    public int count() {
//        return rows.size();
//    }

    private boolean captainNotAssigned() {
        return captainPlayer == null;
    }

    private boolean numberAvailable(int number) {
        return !playersNumbers.contains(number);
    }

    public TreeMap<Integer, SidePanelRow> getInactivePlayers() {
        TreeMap<Integer, SidePanelRow> res = new TreeMap<>();
        for (TreeMap.Entry<Integer, SidePanelRow> entry : rows.entrySet()) {
            if (!activePlayers.contains(entry.getValue())) {
                res.put(entry.getValue().getNumber(), entry.getValue());
            }
        }
        return res;
    }

    public TreeSet<SidePanelRow> getActivePlayers() {
        return activePlayers;
    }

    public TreeMap<Integer, SidePanelRow> getAllPlayers() {
        return rows;
    }

    public SidePanelRow getCaptainPlayer() {
        return captainPlayer;
    }

    public SidePanelRow getPlayer(int number) {
        return rows.get(number);
    }

    public boolean selectionConfirmed() {
        return !panelSelect.isChecked();
    }

    private boolean checkAddAvailable() {
        if (playersNumbers.size() < Constants.MAX_PLAYERS) {
            return true;
        }
        Toast.makeText(getActivity(), getResources().getString(R.string.side_panel_players_limit), Toast.LENGTH_LONG).show();
        return false;
    }

    public void substitute(SidePanelRow in, SidePanelRow out){
        if (out != null) {
            out.toggleSelected();
            activePlayers.remove(out);
        }
        in.toggleSelected();
        activePlayers.add(in);
    }

    public void clear() {
        if (rows.size() == 0) {
            return;
        }
        Fragment frag = getFragmentManager().findFragmentByTag(ListDialog.TAG);
        if (frag != null && frag.isAdded()) {
            return;
        }
        ListDialog.newInstance("clear_panel", left).show(getFragmentManager(), ListDialog.TAG);
    }

    public void clear(boolean delete) {
        if (delete) {
            rows.clear();
            playersNumbers.clear();
            activePlayers.clear();
            if (table != null) {
                clearTable();
                listener.onSidePanelNoActive(left);
            }
        } else {
            for (Map.Entry<Integer, SidePanelRow> entry : rows.entrySet()) {
                entry.getValue().clear();
            }
        }
    }

    public boolean saveCurrentData() {
        if (rows.size() == 0) {
            return true;
        }

        final String team = Boolean.toString(left);
        RealmController.with().deleteTmpPlayers(team);
        final Results tmpResult = RealmController.with().getTmpResult();
        Realm realm = RealmController.with().getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Map.Entry<Integer, SidePanelRow> entry : rows.entrySet()) {
                    SidePanelRow row = entry.getValue();
                    PlayersResults playersResults = realm.createObject(PlayersResults.class);
                    playersResults.setGame(tmpResult)
                            .setTeam(team)
                            .setNumber(row.getNumber())
                            .setName(row.getName())
                            .setPoints(row.getPoints())
                            .setFouls(row.getFouls())
                            .setCaptain(row.getCaptain())
                            .setActive(row.getSelected());
                }
            }
        });
        return true;
    }

    public static void clearCurrentData() {
        RealmController.with().deletePlayerResults(-1);
    }

    private void restoreCurrentData() {
        activePlayers = new TreeSet<>();
        RealmResults<PlayersResults> players = RealmController.with().getPlayers(-1, Boolean.toString(left));
        if (players.size() > 0) {
            for (PlayersResults player : players) {
                SidePanelRow row = addRow(player.getNumber(), player.getPlayerName(), player.getCaptain());
                row.changePoints(player.getPoints());
                row.changeFouls(player.getFouls());
                if (player.getActive()) {
                    activePlayers.add(row);
                    row.toggleSelected();
                }
            }
            listener.onSidePanelActiveSelected(activePlayers, left);
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.side_panel_no_save_data), Toast.LENGTH_LONG).show();
        }
    }

    public void changeRowsSide() {
        for (SidePanelRow row : rows.values()) {
            row.changeSide();
        }
    }

    public void replaceRows(TreeMap<Integer, SidePanelRow> rows, TreeSet<SidePanelRow> activePlayers, SidePanelRow captainPlayer) {
        this.rows = rows;
        this.activePlayers = activePlayers;
        this.captainPlayer = captainPlayer;
        updateView();
    }

    private void updateView() {
        if (table != null) {
            playersNumbers.clear();
            clearTable();
            ViewParent parent;
            for (SidePanelRow row : rows.values()) {
                parent = row.getParent();
                if (parent != null) {
                    ((TableLayout) parent).removeView(row);
                }
                table.addView(row);
                playersNumbers.add(row.getNumber());
            }
        }

        if (activePlayers.isEmpty()) {
            listener.onSidePanelNoActive(left);
        } else {
            listener.onSidePanelActiveSelected(activePlayers, left);
        }
    }

    public void clearTable() {
        if (table != null) {
            table.removeAllViews();
            addHeader();
        }
    }
}