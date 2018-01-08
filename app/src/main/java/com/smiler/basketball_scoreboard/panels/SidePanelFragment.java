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

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.Player;
import com.smiler.basketball_scoreboard.db.PlayersResults;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.db.Results;
import com.smiler.basketball_scoreboard.db.Team;
import com.smiler.basketball_scoreboard.elements.dialogs.DialogTypes;
import com.smiler.basketball_scoreboard.elements.dialogs.ListDialog;
import com.smiler.basketball_scoreboard.elements.dialogs.PlayerEditDialog;

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
    private static int maxPlayers, minPlayers;

    private SidePanelListener listener;
    private TableLayout table;
    private TreeMap<Integer, SidePanelRow> rows = new TreeMap<>();
    private List<Integer> playersNumbers = new ArrayList<>();
    private TreeSet<SidePanelRow> activePlayers = new TreeSet<>();
    private SidePanelRow captainPlayer;
    private ToggleButton panelSelect;
    private boolean left = true;
    private Team team;
    private boolean viewCreated = false;
    private boolean shouldRestore = false;

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

    public interface SidePanelListener {
        void onSidePanelClose(boolean left);
        void onSidePanelActiveSelected(TreeSet<SidePanelRow> rows, boolean left);
        void onSidePanelNoActive(boolean left);
        void onSidePanelShowConfirmDialog(DialogTypes type, boolean left);
        void onSidePanelShowSelectDialog(Team team, boolean left);
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
        View v = initView(inflater, container);
        if (!viewCreated) {
            viewCreated = afterViewCreate();
        }
        return v;
    }

    @NonNull
    private View initView(LayoutInflater inflater, ViewGroup container) {
        int layout_id, table_layout_id, close_bu_id, clear_bu_id, add_bu_id, add_auto_bu_id, toggle_bu_id;
        if (left) {
            layout_id = R.layout.sp_full_left;
            close_bu_id = R.id.left_panel_close;
            table_layout_id = R.id.left_panel_table;
            add_bu_id = R.id.left_panel_add;
            add_auto_bu_id = R.id.left_panel_add_auto;
            toggle_bu_id = R.id.left_panel_select_active;
            clear_bu_id = R.id.left_panel_clear;
        } else {
            layout_id = R.layout.sp_full_right;
            close_bu_id = R.id.right_panel_close;
            table_layout_id = R.id.right_panel_table;
            add_bu_id = R.id.right_panel_add;
            add_auto_bu_id = R.id.right_panel_add_auto;
            toggle_bu_id = R.id.right_panel_select_active;
            clear_bu_id = R.id.right_panel_clear;
        }

        View v = inflater.inflate(layout_id, container, false);
        table = v.findViewById(table_layout_id);
        addHeader();
        v.findViewById(close_bu_id).setOnClickListener(this);
        v.findViewById(add_bu_id).setOnClickListener(this);
        v.findViewById(clear_bu_id).setOnClickListener(this);
        View addAutoView = v.findViewById(add_auto_bu_id);
        addAutoView.setOnClickListener(this);
        addAutoView.setOnLongClickListener(this);
        panelSelect = v.findViewById(toggle_bu_id);
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
                        Toast.makeText(getActivity(), getResources().getString(R.string.sp_confirm), Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.left_panel_add:
                case R.id.right_panel_add:
                    if (!checkAddAvailable()) { return; }
                    PlayerEditDialog.newInstance(left)
                            .setListenerInGame((PlayerEditDialog.EditPlayerInGameListener) getActivity())
                            .show(getFragmentManager(), PlayerEditDialog.TAG);
                    break;
                case R.id.left_panel_clear:
                case R.id.right_panel_clear:
                    clear();
                    break;
                case R.id.left_panel_add_auto:
                case R.id.right_panel_add_auto:
                    askAddRows();
                    break;
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.left_panel_add_auto:
            case R.id.right_panel_add_auto:
                restoreCurrentData(false);
                return true;
        }
        return false;
    }

    public static void setMaxPlayers(int number) {
        maxPlayers = number;
    }

    public static void setMinPlayers(int number) {
        minPlayers = number;
    }

    public void setShouldRestore(boolean value) {
        shouldRestore = value;
    }

    private boolean afterViewCreate() {
        if (getActivity() == null) {
            return false;
        }
        if (team != null) {
            addPlayersRows(getActivity());
        } else if (shouldRestore) {
            restoreCurrentData(true);
        }
        return true;
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
                            ? getResources().getString(R.string.sp_few) : getResources().getString(R.string.sp_many),
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
        row.setRow(number, name, captain);
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

    public void askAddRows() {
        if (listener != null) {
            ListDialog.newInstance(DialogTypes.SELECT_ADD_PLAYERS, left)
                    .show(getFragmentManager(), ListDialog.TAG);
        }
    }

    public void addRowsAuto() {
        if (!checkAddAvailable()) {return;}
        int count = playersNumbers.size();
        int number = 1;
        String name = getResources().getString(R.string.sp_player_name);
        Activity activity = getActivity();
        while (count < maxPlayers) {
            while (playersNumbers.contains(number)) { number++; }
            SidePanelRow row = new SidePanelRow(activity, number, String.format(name, number), false, left);
            playersNumbers.add(number);
            table.addView(row);
            rows.put(row.getId(), row);
            count++;
        }
    }

    private boolean addRowsTeam() {
        Activity activity = getActivity();
        if (activity == null) {
            viewCreated = false;
            return true;
        }
        addPlayersRows(activity);
        return true;
    }

    private void addPlayersRows(Activity activity) {
        List<Player> players = team.getGamePlayers() != null && team.getGamePlayers().size() > 0
                ? team.getGamePlayers()
                : team.getPlayers();

        for (Player player : players) {
            SidePanelRow row = new SidePanelRow(activity, player, left);
            playersNumbers.add(player.getNumber());
            table.addView(row);
            rows.put(row.getId(), row);
        }
    }

    public int checkNewPlayer(int number, boolean captain) {
        int status = 0;
        if (!numberAvailable(number)) { status = 1; }
        if (!(!captain || captainNotAssigned())) { status |= 2; }
        return status;
    }

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
        if (playersNumbers.size() < maxPlayers) {
            return true;
        }
        Toast.makeText(getActivity(), String.format(getResources().getString(R.string.sp_players_limit), maxPlayers), Toast.LENGTH_LONG).show();
        return false;
    }

    private boolean checkTeamPlayers(Team team) {
        int size = team.getGamePlayers() != null && team.getGamePlayers().size() > 0
                ? team.getGamePlayers().size()
                : team.getPlayers().size();
        if (size > maxPlayers) {
            if (listener != null) {
                Toast.makeText(getActivity(), String.format(getResources().getString(R.string.sp_players_limit), maxPlayers), Toast.LENGTH_SHORT).show();
                listener.onSidePanelShowSelectDialog(team, left);
            }
            return false;
        }
        if (size < minPlayers) {
            if (listener != null) {
                listener.onSidePanelShowConfirmDialog(DialogTypes.TEAM_PLAYERS_FEW, left);
            }
            return false;
        }
        return true;
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
        ListDialog.newInstance(DialogTypes.PANEL_CLEAR, left).show(getFragmentManager(), ListDialog.TAG);
    }

    public void clear(boolean delete) {
        if (delete) {
            rows.clear();
            playersNumbers.clear();
            activePlayers.clear();
            team = null;
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
        realm.executeTransaction(realm1 -> {
            for (Map.Entry<Integer, SidePanelRow> entry : rows.entrySet()) {
                SidePanelRow row = entry.getValue();
                PlayersResults playersResults = realm1.createObject(PlayersResults.class);
                playersResults.setGame(tmpResult)
                        .setTeam(team)
                        .setNumber(row.getNumber())
                        .setName(row.getName())
                        .setPoints(row.getPoints())
                        .setFouls(row.getFouls())
                        .setCaptain(row.isCaptain())
                        .setActive(row.getSelected());
            }
        });
        return true;
    }

    public boolean savePlayers(Team team) {
        this.team = team;
        if (rows.size() == 0) {
            return true;
        }

        RealmController.with().createPlayers(team, rows.values());
        return true;
    }

    public static void clearCurrentData() {
        RealmController.with().deletePlayerResults(-1);
    }

    private void restoreCurrentData(boolean silent) {
        clear(true);
        RealmResults<PlayersResults> players = RealmController.with().getPlayers(-1, Boolean.toString(left));
        if (players.size() > 0) {
            for (PlayersResults player : players) {
                SidePanelRow row = addRow(player.getNumber(), player.getName(), player.isCaptain());
                row.changePoints(player.getPoints());
                row.changeFouls(player.getFouls());
                if (player.isActive()) {
                    activePlayers.add(row);
                    row.toggleSelected();
                }
                rows.put(row.getId(), row);
            }
            listener.onSidePanelActiveSelected(activePlayers, left);
        } else if (!silent){
            Toast.makeText(getActivity(), getResources().getString(R.string.sp_no_saved_data), Toast.LENGTH_LONG).show();
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

    public boolean setTeam(Team team) {
        if (team == null) {
            clear(true);
            return true;
        }
        if (this.team != null) {
            listener.onSidePanelShowConfirmDialog(DialogTypes.TEAM_ALREADY_SELECTED, left);
            return false;
        }
        this.team = team;
        return checkTeamPlayers(team) && addRowsTeam();
    }

    public boolean changeTeam(Team team) {
        if (!checkTeamPlayers(team)) {
            return false;
        }
        clear(true);
        this.team = team;
        return addRowsTeam();
    }

    public void confirmTeamPlayers(Team team) {
        clear(true);
        this.team = team;
        addRowsTeam();
    }
}