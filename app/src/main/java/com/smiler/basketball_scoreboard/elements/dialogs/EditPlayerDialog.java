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
import android.widget.Switch;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.Constants;
import com.smiler.basketball_scoreboard.R;

public class EditPlayerDialog extends DialogFragment {

    public static String TAG = "EditPlayerDialog";
    OnPanelsListener editPlayerListener;
    private EditText numberView, nameView;
    private Switch captainView;
    private boolean edit, left;
    private int editNumber;
    private int id;

    public static EditPlayerDialog newInstance(boolean left, int id, int number, String name, boolean captain) {
        EditPlayerDialog f = new EditPlayerDialog();
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putInt("number", number);
        args.putString("name", name);
        args.putBoolean("captain", captain);
        args.putBoolean("left", left);
        f.setArguments(args);
        return f;
    }

    public static EditPlayerDialog newInstance(boolean left) {
        EditPlayerDialog f = new EditPlayerDialog();
        Bundle args = new Bundle();
        args.putBoolean("left", left);
        f.setArguments(args);
        return f;
    }

    public interface OnPanelsListener {
        void onPanelAddPlayer(boolean left, int number, String name, boolean captain);
        void onPanelEditPlayer(boolean left, int id, int number, String name, boolean captain);
        void onPanelDeletePlayer(boolean left, int id);
        int onPanelCheckPlayer(boolean left, int number, boolean captain);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            editPlayerListener = (OnPanelsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must OnPanelsListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.edit_player_dialog, null);
        builder.setView(v).setCancelable(true);

        nameView = (EditText) v.findViewById(R.id.edit_player_name);
        numberView = (EditText) v.findViewById(R.id.edit_player_number);
        captainView = (Switch) v.findViewById(R.id.edit_player_captain);

        v.findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForm()) {
                    apply();
                    dismiss();
                }
            }
        });
        v.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        Bundle args = getArguments();
        Button add_another_bu = (Button)v.findViewById(R.id.button_custom_action);
        if (args == null) {
            return null;
        }
        left = args.getBoolean("left", true);
        id = args.getInt("id", -1);
        if (id != -1) {
            editNumber = args.getInt("number", -1);
            numberView.setText(Integer.toString(editNumber));
            numberView.selectAll();
            nameView.setText(args.getString("name", ""));
            nameView.selectAll();
            captainView.setChecked(args.getBoolean("captain", false));
            add_another_bu.setText(getResources().getText(R.string.action_delete));
            edit = true;
            add_another_bu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editPlayerListener.onPanelDeletePlayer(left, id);
                    dismiss();
                }
            });
        } else {
            add_another_bu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkForm()) {
                        apply();
                        clearForm();
                    }
                }
            });
        }
        return builder.create();
    }

    private void clearForm() {
        nameView.getText().clear();
        numberView.getText().clear();
        captainView.setChecked(false);
    }

    private void apply() {
        if (edit) {
            editPlayerListener.onPanelEditPlayer(left, id, Integer.parseInt(numberView.getText().toString()), nameView.getText().toString(), captainView.isChecked());
        } else {
            editPlayerListener.onPanelAddPlayer(left, Integer.parseInt(numberView.getText().toString()), nameView.getText().toString(), captainView.isChecked());
        }
    }

    private boolean checkForm() {
        String numberText = numberView.getText().toString().trim();
        if(numberText.equals("")){
            Toast.makeText(getActivity(), getResources().getString(R.string.edit_player_dialog_number_required), Toast.LENGTH_SHORT).show();
            return false;
        }
        int status = editPlayerListener.onPanelCheckPlayer(left, Integer.parseInt(numberText), captainView.isChecked());
        if (status != 0 && edit && editNumber == Integer.parseInt(numberView.getText().toString())) {
            status--;
        }
        if (status == 1 || status == 3) {
            Toast.makeText(getActivity(), getResources().getString(R.string.edit_player_dialog_number_warning), Toast.LENGTH_LONG).show();
        } else if (status == 2) {
            ConfirmDialog.newInstance(DialogTypes.EDIT_CAPTAIN).show(getFragmentManager(), Constants.TAG_FRAGMENT_CONFIRM);

        }
        return status == 0;
    }

    public void changeCaptainConfirmed() {
        apply();

    }
}