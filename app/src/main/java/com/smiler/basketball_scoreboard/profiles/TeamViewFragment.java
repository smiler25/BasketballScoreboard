package com.smiler.basketball_scoreboard.profiles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smiler.basketball_scoreboard.Constants;
import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.Player;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.elements.ReattachedFragment;
import com.smiler.basketball_scoreboard.elements.dialogs.ConfirmDialog;
import com.smiler.basketball_scoreboard.elements.dialogs.DialogTypes;
import com.smiler.basketball_scoreboard.elements.dialogs.PlayerEditDialog;
import com.smiler.basketball_scoreboard.elements.dialogs.TeamEditDialog;
import com.smiler.basketball_scoreboard.exceptions.CaptainAlreadyAssignedException;
import com.smiler.basketball_scoreboard.profiles.views.TeamView;

public class TeamViewFragment extends ReattachedFragment implements
        ConfirmDialog.ConfirmDialogListener,
        TeamViewCallback,
        PlayerEditDialog.EditPlayerListener
{
    public static String TAG = "BS-TeamViewFragment";
    private static final String teamIdArg = "teamId";
    private int teamId = -1;
    private int editedPlayerNumber;
    private String editedPlayerName;
    private boolean editedPlayerCaptain;

    public static TeamViewFragment newInstance(int teamId) {
        TeamViewFragment fragment = new TeamViewFragment();
        Bundle args = new Bundle();
        args.putInt(teamIdArg, teamId);
        fragment.setArguments(args);
        return fragment;
    }

    public static TeamViewFragment newInstance() {
        return new TeamViewFragment();
    }

    public TeamViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (teamId == -1) {
            if (getArguments() == null) {
                return initEmptyView(inflater, container);
            }
            teamId = getArguments().getInt(teamIdArg);
        }
        return new TeamView(getActivity(), teamId, this);
    }

    public void updateContent(int itemId) {
        teamId = itemId;
        ViewGroup view = (ViewGroup) getView();
        if (view != null) {
            view.removeAllViews();
            view.addView(new TeamView(getActivity(), itemId, this));
        }
    }

    private View initEmptyView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.team_view_empty, container, false);
        view.findViewById(R.id.text_view_empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateTeamDialog();
            }
        });
        return view;
    }

    private void showCreateTeamDialog() {
        TeamEditDialog.newInstance().show(getActivity().getFragmentManager(), TeamEditDialog.TAG);
    }

    @Override
    public void onTeamPlayerAdd() {
        PlayerEditDialog.newInstance(teamId)
                .setListener(this)
                .show(getActivity().getFragmentManager(), PlayerEditDialog.TAG);
    }

    @Override
    public void onTeamPlayerEdit(int playerId) {
        Player player = RealmController.with().getPlayer(playerId);
        PlayerEditDialog.newInstance(teamId, player)
                .setListener(this)
                .show(getActivity().getFragmentManager(), PlayerEditDialog.TAG);
    }

    @Override
    public boolean onAddPlayer(int teamId, int number, String name, boolean captain) {
        try {
            RealmController.with().createPlayer(teamId, number, name, captain, false);
            reAttach();
            return true;
        } catch (CaptainAlreadyAssignedException e) {
            editedPlayerNumber = number;
            editedPlayerName = name;
            editedPlayerCaptain = captain;
            ConfirmDialog.newInstance(DialogTypes.CAPTAIN_ALREADY_ASSIGNED)
                    .setListener(this)
                    .show(getActivity().getFragmentManager(), Constants.TAG_FRAGMENT_CONFIRM);
            return false;
        }
    }

    @Override
    public boolean onEditPlayer(int teamId, int playerId, int number, String name, boolean captain) {
        RealmController.with().editPlayer(playerId, number, name, captain);
        reAttach();
        return true;
    }

    @Override
    public boolean onDeletePlayer(int playerId) {
        RealmController.with().deletePlayer(playerId);
        reAttach();
        return true;
    }

    @Override
    public void onConfirmDialogPositive(DialogTypes type) {
        if (type == DialogTypes.CAPTAIN_ALREADY_ASSIGNED) {
            if (editedPlayerName != null) {
                try {
                    RealmController.with().createPlayer(teamId, editedPlayerNumber, editedPlayerName, editedPlayerCaptain, true);
                } catch (CaptainAlreadyAssignedException e) {
                    ConfirmDialog.newInstance(DialogTypes.CAPTAIN_ALREADY_ASSIGNED)
                            .setListener(this)
                            .show(getActivity().getFragmentManager(), Constants.TAG_FRAGMENT_CONFIRM);
                }
            }
            reAttach();
        }
    }

    @Override
    public void onConfirmDialogPositive(DialogTypes type, int teamType) {}

    @Override
    public void onConfirmDialogNegative(DialogTypes type) {}

    @Override
    public void onConfirmDialogNegative(DialogTypes type, int teamType) {}

    @Override
    public void onConfirmDialogNeutral() {}
}