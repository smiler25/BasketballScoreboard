package com.smiler.basketball_scoreboard.elements.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.Team;

import static com.smiler.basketball_scoreboard.Constants.HOME;


public class TeamEditInGameDialog extends DialogFragment implements TeamSelector {
    public static String TAG = "BS-TeamEditInGameDialog";

    private ChangeTeamListener listener;
    private EditText editView;
    private int teamType;
    private Team team;
    private Button teamSelector;

    public static TeamEditInGameDialog newInstance(int team, String name) {
        TeamEditInGameDialog f = new TeamEditInGameDialog();
        Bundle args = new Bundle();
        args.putInt("teamType", team);
        args.putString("name", name);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        teamType = args.getInt("teamType", HOME);
        int titleResId = teamType == HOME ? R.string.team_home : R.string.team_guest;

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.team_edit_in_game, null);

        builder.setView(v).setCancelable(true);

        TextView title = v.findViewById(R.id.dialog_title);
        title.setText(String.format(getResources().getString(titleResId), teamType));
        editView = v.findViewById(R.id.edit_text);

        String name = args.getString("name");
        if (name != null) {
            editView.setText(name);
            editView.selectAll();
        }

        teamSelector = v.findViewById(R.id.new_game_select_first_team);
        teamSelector.setOnClickListener(v12 -> ListDialog.newInstance(DialogTypes.SELECT_TEAM, teamType).show(getFragmentManager(), ListDialog.TAG));

        v.findViewById(R.id.dialog_apply).setOnClickListener(v1 -> {
            if (team != null) {
                listener.onTeamChanged(team, teamType);
            } else {
                listener.onNameChanged(editView.getText().toString(), teamType);
            }
            dismiss();
        });
        return builder.create();
    }

    @Override
    public void handleTeamSelect(int type, Team team) {
        this.team = team;
        teamSelector.setText(String.format(getResources().getString(R.string.select_team_selected), team.getName()));
    }


    public interface ChangeTeamListener {
        void onNameChanged(String value, int team);
        void onTeamChanged(Team value, int team);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ChangeTeamListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement TeamEditInGameDialog");
        }
    }

}