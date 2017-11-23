package com.smiler.basketball_scoreboard.elements.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.Constants;
import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.Player;

public class PlayerEditDialog extends DialogFragment {

    public static String TAG = "BS-PlayerEditDialog";
    EditPlayerInGameListener listenerInGame;
    EditPlayerListener listener;
    private EditText numberView, nameView;
    private Switch captainView;
    private boolean edit, left;
    private int editNumber;
    private int id;
    private int teamId, playerId = -1;
    private static String leftArg = "left";
    private static String inGameArg = "inGame";
    private static String teamIdArg = "teamId";
    private static String playerIdArg = "playerId";
    private boolean inGame;

    public static PlayerEditDialog newInstance(boolean left, int id, int number, String name, boolean captain) {
        PlayerEditDialog f = new PlayerEditDialog();
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putInt("number", number);
        args.putString("name", name);
        args.putBoolean("captain", captain);
        args.putBoolean(leftArg, left);
        args.putBoolean(inGameArg, true);
        f.setArguments(args);
        return f;
    }

    public static PlayerEditDialog newInstance(boolean left) {
        PlayerEditDialog f = new PlayerEditDialog();
        Bundle args = new Bundle();
        args.putBoolean(leftArg, left);
        args.putBoolean(inGameArg, true);
        f.setArguments(args);
        return f;
    }

    public static PlayerEditDialog newInstance(int teamId) {
        PlayerEditDialog f = new PlayerEditDialog();
        Bundle args = new Bundle();
        args.putBoolean(inGameArg, false);
        args.putInt(teamIdArg, teamId);
        f.setArguments(args);
        return f;
    }

    public static PlayerEditDialog newInstance(int teamId, Player player) {
        PlayerEditDialog f = new PlayerEditDialog();
        Bundle args = new Bundle();
        args.putBoolean(inGameArg, false);
        args.putInt(teamIdArg, teamId);
        args.putInt(playerIdArg, player.getId());
        args.putInt("number", player.getNumber());
        args.putString("name", player.getName());
        args.putBoolean("captain", player.getCaptain());
        f.setArguments(args);
        return f;
    }

    public interface EditPlayerInGameListener {
        void onAddPlayerInGame(boolean left, int number, String name, boolean captain);
        void onEditPlayerInGame(boolean left, int id, int number, String name, boolean captain);
        void onDeletePlayerInGame(boolean left, int id);
        int onCheckPlayerInGame(boolean left, int number, boolean captain);
    }

    public interface EditPlayerListener {
        boolean onAddPlayer(int teamId, int number, String name, boolean captain);
        boolean onEditPlayer(int teamId, int playerId, int number, String name, boolean captain);
        boolean onDeletePlayer(int playerId);
    }

    public PlayerEditDialog setListener(EditPlayerListener listener) {
        this.listener = listener;
        return this;
    }

    public PlayerEditDialog setListenerInGame(EditPlayerInGameListener listener) {
        listenerInGame = listener;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_edit_player, null);
        builder.setView(v).setCancelable(true);

        nameView = (EditText) v.findViewById(R.id.edit_player_name);
        numberView = (EditText) v.findViewById(R.id.edit_player_number);
        captainView = (Switch) v.findViewById(R.id.edit_player_captain);

        v.findViewById(R.id.button_add).setOnClickListener(v14 -> {
            if (checkForm()) {
                apply();
                dismiss();
            }
        });
        v.findViewById(R.id.button_cancel).setOnClickListener(v13 -> dismiss());
        Bundle args = getArguments();
        Button actionButton = (Button)v.findViewById(R.id.button_custom_action);
        if (args == null) {
            return null;
        }
        left = args.getBoolean(leftArg, true);
        inGame = args.getBoolean(inGameArg, true);
        if (!inGame) {
            teamId = args.getInt(teamIdArg);
            playerId = args.getInt(playerIdArg, -1);
        }
        id = args.getInt("id", -1);
        if (id != -1 || playerId != -1) {
            editNumber = args.getInt("number", -1);
            numberView.setText(Integer.toString(editNumber));
            numberView.selectAll();
            nameView.setText(args.getString("name", ""));
            nameView.selectAll();
            captainView.setChecked(args.getBoolean("captain", false));
            actionButton.setText(getResources().getText(R.string.action_delete));
            edit = true;
            actionButton.setOnClickListener(v12 -> {
                if (inGame) {
                    listenerInGame.onDeletePlayerInGame(left, id);
                } else {
                    listener.onDeletePlayer(playerId);
                }
                dismiss();
            });
        } else {
            actionButton.setOnClickListener(v1 -> {
                if (checkForm()) {
                    apply();
                    clearForm();
                }
            });
        }
        return builder.create();
    }

    private boolean apply() {
        if (inGame) {
            if (listenerInGame == null) {
                Toast.makeText(getActivity(), R.string.edit_player_dialog_error, Toast.LENGTH_LONG).show();
                return true;
            }
            if (edit) {
                listenerInGame.onEditPlayerInGame(left, id, Integer.parseInt(numberView.getText().toString()), nameView.getText().toString(), captainView.isChecked());
            } else {
                listenerInGame.onAddPlayerInGame(left, Integer.parseInt(numberView.getText().toString()), nameView.getText().toString(), captainView.isChecked());
            }
            return true;
        } else {
            if (listener == null) {
                Toast.makeText(getActivity(), R.string.edit_player_dialog_error, Toast.LENGTH_LONG).show();
                return true;
            }
            if (edit) {
                return listener.onEditPlayer(teamId, playerId, Integer.parseInt(numberView.getText().toString()), nameView.getText().toString(), captainView.isChecked());
            } else {
                return listener.onAddPlayer(teamId, Integer.parseInt(numberView.getText().toString()), nameView.getText().toString(), captainView.isChecked());
            }
        }
    }

    private void clearForm() {
        nameView.getText().clear();
        numberView.getText().clear();
        captainView.setChecked(false);
    }

    private boolean checkForm() {
        String numberText = numberView.getText().toString().trim();
        if(numberText.equals("")){
            Toast.makeText(getActivity(), getResources().getString(R.string.edit_player_dialog_number_required), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!inGame) {
            return true;
        }
        int status = listenerInGame.onCheckPlayerInGame(left, Integer.parseInt(numberText), captainView.isChecked());
        if (status != 0 && edit && editNumber == Integer.parseInt(numberView.getText().toString())) {
            status--;
        }
        if (status == 1 || status == 3) {
            Toast.makeText(getActivity(), getResources().getString(R.string.edit_player_dialog_number_warning), Toast.LENGTH_LONG).show();
        } else if (status == 2) {
            ConfirmDialog.newInstance(DialogTypes.CAPTAIN_ALREADY_ASSIGNED).show(getFragmentManager(), Constants.TAG_FRAGMENT_CONFIRM);

        }
        return status == 0;
    }

    public void changeCaptainConfirmed() {
        apply();
    }
}