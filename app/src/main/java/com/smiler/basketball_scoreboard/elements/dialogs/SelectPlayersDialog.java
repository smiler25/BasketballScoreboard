package com.smiler.basketball_scoreboard.elements.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.ListView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.Player;
import com.smiler.basketball_scoreboard.db.Team;

import java.util.ArrayList;

import io.realm.RealmList;


public class SelectPlayersDialog extends DialogFragment {

    public static final String TAG = "BS-SelectPlayersDialog";
    private Team team;
    private int teamType;
    private SelectPlayersDialogListener listener;

    public interface SelectPlayersDialogListener {
        void onSelectPlayers(int teamType);
    }

    public static SelectPlayersDialog newInstance() {
        return new SelectPlayersDialog();
    }

    public SelectPlayersDialog setTeam(Team team, int teamType) {
        this.team = team;
        this.teamType = teamType;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (team == null) {
            dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);

        final RealmList<Player> players = team.getPlayers();
        CharSequence[] names = new CharSequence[players.size()];
        for (int i = 0; i < players.size(); i++) {
            names[i] = String.format("%d: %s", players.get(i).getNumber(), players.get(i).getName());
        }

        builder.setTitle(R.string.team_lot_players_dialog_title)
                .setMultiChoiceItems(names, null, null)
                .setPositiveButton(R.string.action_apply,
                        (dialog, id) -> {
                            ListView list = ((AlertDialog) dialog).getListView();
                            ArrayList<Player> selected = new ArrayList<>();
                            for (int i = 0; i < list.getCount(); i++) {
                                if (list.isItemChecked(i)) {
                                    selected.add(players.get(i));
                                }
                            }
                            team.setGamePlayers(selected);
                            listener.onSelectPlayers(teamType);
                        })
                .setNegativeButton(R.string.action_cancel,
                        (dialog, id) -> dialog.cancel());
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (SelectPlayersDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SelectPlayersDialogListener");
        }
    }
}