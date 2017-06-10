package com.smiler.basketball_scoreboard.elements.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.smiler.basketball_scoreboard.R;

public class ConfirmDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private DialogTypes type;
    private CheckBox checkbox;

    public static ConfirmDialog newInstance(DialogTypes type) {
        ConfirmDialog f = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putSerializable("type", type);
        f.setArguments(args);
        return f;
    }

    public static ConfirmDialog newInstance(DialogTypes type, String win_team, int win_score, int score2) {
        ConfirmDialog f = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putSerializable("type", type);
        args.putString("team", win_team);
        args.putInt("win_score", win_score);
        args.putInt("score2", score2);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.yes, this)
                .setNegativeButton(R.string.no, this);

        Bundle args = getArguments();
        type = (DialogTypes) args.get("type");

        int titleId;
        if (type != null) {
            switch (type) {
                case NEW_GAME:
                    String msg;
                    titleId = R.string.action_confirm_new_game;
                    String team = args.getString("team", null);
                    if (team != null) {
                        int winScore = args.getInt("win_score");
                        int score2 = args.getInt("score2");
                        msg = String.format(getResources().getString(R.string.confirm_new_game_message_win),
                                team, winScore, score2);

                        View checkboxView = getActivity().getLayoutInflater().inflate(R.layout.confirm_dialog_checkbox, null);
                        checkbox = (CheckBox) checkboxView.findViewById(R.id.confirm_checkbox);
                        builder.setNeutralButton(R.string.confirm_new_neutral_option, this)
                                .setView(checkboxView);
                    } else {
                        msg = getResources().getString(R.string.confirm_new_game_message_settings);
                    }
                    builder.setMessage(msg);
                    break;
                case RESULT_SAVE:
                    titleId = R.string.action_save_result;
                    break;
                case EDIT_CAPTAIN:
                    titleId = R.string.edit_player_dialog_captain_confirm;
                    break;
                default:
                    titleId = R.string.action_confirm;
                    break;
            }
            builder.setTitle(titleId);
        }
        return builder.create();
    }

    public interface ConfirmDialogListener {
        void onConfirmDialogPositive(DialogTypes type);
        void onConfirmDialogPositive(DialogTypes type, boolean dontShow);
        void onConfirmDialogNegative(DialogTypes type, boolean dontShow);
        void onConfirmDialogNegative(DialogTypes type);
        void onConfirmDialogNeutral(boolean dontShow);
    }
    private ConfirmDialogListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ConfirmDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ConfirmDialogListener");
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                if (checkbox != null) {
                    listener.onConfirmDialogPositive(type, checkbox.isChecked());
                } else {
                    listener.onConfirmDialogPositive(type);
                }
                break;
            case Dialog.BUTTON_NEUTRAL:
                listener.onConfirmDialogNeutral(checkbox.isChecked());
                break;
            case Dialog.BUTTON_NEGATIVE:
                if (checkbox != null) {
                    listener.onConfirmDialogNegative(type, checkbox.isChecked());
                } else {
                    listener.onConfirmDialogNegative(type);
                }
                break;
        }
    }
}