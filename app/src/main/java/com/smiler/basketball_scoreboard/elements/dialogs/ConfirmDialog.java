package com.smiler.basketball_scoreboard.elements.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.smiler.basketball_scoreboard.R;

public class ConfirmDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private static String argType = "type";
    private static String argTeamType = "teamType";
    private DialogTypes type;
    private ConfirmDialogListener listener;
    private int teamType;

    public static ConfirmDialog newInstance(DialogTypes type) {
        ConfirmDialog f = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putSerializable(argType, type);
        f.setArguments(args);
        return f;
    }

    public static ConfirmDialog newInstance(DialogTypes type, int team) {
        ConfirmDialog f = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putSerializable(argType, type);
        args.putInt(argTeamType, team);
        f.setArguments(args);
        return f;
    }

    public static ConfirmDialog newInstance(DialogTypes type, String winTeam, int winScore, int loseScore) {
        ConfirmDialog f = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putSerializable(argType, type);
        args.putString("winTeam", winTeam);
        args.putInt("winScore", winScore);
        args.putInt("loseScore", loseScore);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.yes, this)
                .setNegativeButton(R.string.no, this);

        Bundle args = getArguments();
        type = (DialogTypes) args.get(argType);

        if (type != null) {
            int titleId = R.string.action_confirm;
            String title = "";
            String msg = "";
            switch (type) {
                case GAME_END:
                    String team = args.getString("winTeam", "");
                    int winScore = args.getInt("winScore");
                    int loseScore = args.getInt("loseScore");
                    title = String.format(getResources().getString(R.string.game_end_message), team, winScore, loseScore);
                    msg = getResources().getString(R.string.action_confirm_new_game);
                    builder.setNeutralButton(R.string.game_end_dialog_neutral, this);
                    break;
                case NEW_GAME:
                    titleId = R.string.action_confirm_new_game;
                    msg = getResources().getString(R.string.new_game_settings_changed);
                    break;
                case RESULT_SAVE:
                    titleId = R.string.action_save_result;
                    break;
                case CAPTAIN_ALREADY_ASSIGNED:
                    titleId = R.string.edit_player_dialog_captain_confirm;
                    break;
                case TEAM_ALREADY_SELECTED:
                    titleId = R.string.team_already_selected;
                    msg = getResources().getString(R.string.team_already_selected_dialog_msg);
                    teamType = args.getInt(argTeamType);
                    break;
                case TEAM_PLAYERS_FEW:
                    titleId = R.string.team_few_players_dialog_title;
                    msg = getResources().getString(R.string.team_few_players_dialog_msg);
                    teamType = args.getInt(argTeamType);
                    break;
            }
            if (title != null && !title.equals("")) {
                builder.setTitle(title);
            } else {
                builder.setTitle(titleId);
            }
            if (!msg.equals("")) {
                builder.setMessage(msg);
            }
        }
        return builder.create();
    }

    public interface ConfirmDialogListener {
        void onConfirmDialogPositive(DialogTypes type);
        void onConfirmDialogPositive(DialogTypes type, int teamType);
        void onConfirmDialogNegative(DialogTypes type);
        void onConfirmDialogNegative(DialogTypes type, int teamType);
        void onConfirmDialogNeutral();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ConfirmDialogListener) activity;
        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString() + " must implement ConfirmDialogListener");
        }
    }

    public ConfirmDialog setListener(ConfirmDialogListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                switch (type) {
                    case TEAM_ALREADY_SELECTED:
                    case TEAM_PLAYERS_FEW:
                        listener.onConfirmDialogPositive(type, teamType);
                        break;
                    default:
                        listener.onConfirmDialogPositive(type);
                }
                break;
            case Dialog.BUTTON_NEUTRAL:
                listener.onConfirmDialogNeutral();
                break;
            case Dialog.BUTTON_NEGATIVE:
                switch (type) {
                    case TEAM_ALREADY_SELECTED:
                    case TEAM_PLAYERS_FEW:
                        listener.onConfirmDialogNegative(type, teamType);
                        break;
                    default:
                        listener.onConfirmDialogNegative(type);
                }
                break;
        }
    }
}