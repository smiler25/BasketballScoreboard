package com.smiler.basketball_scoreboard.elements;

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
    OnEditPlayerListener editPlayerListener;
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

    public interface OnEditPlayerListener {
        void onEditPlayer(boolean left, int number, String name, boolean captain);
        void onEditPlayer(boolean left, int id, int number, String name, boolean captain);
        void onEditPlayer(boolean left, int id);
        int onEditPlayerCheck(boolean left, int number, boolean captain);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            editPlayerListener = (OnEditPlayerListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must OnEditPlayerListener");
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
        if (args != null) {
            left = args.getBoolean("left", true);
            editNumber = args.getInt("number", -1);
            numberView.setText(Integer.toString(editNumber));
            numberView.selectAll();
            nameView.setText(args.getString("name", ""));
            nameView.selectAll();
            captainView.setChecked(args.getBoolean("captain", false));
            add_another_bu.setText("Delete");
            edit = true;
            id = args.getInt("id", -1);
            add_another_bu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editPlayerListener.onEditPlayer(left, id);
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
            editPlayerListener.onEditPlayer(left, id, Integer.parseInt(numberView.getText().toString()), nameView.getText().toString(), captainView.isChecked());
        } else {
            editPlayerListener.onEditPlayer(left, Integer.parseInt(numberView.getText().toString()), nameView.getText().toString(), captainView.isChecked());
        }
    }

    private boolean checkForm() {
        int status = editPlayerListener.onEditPlayerCheck(left, Integer.parseInt(numberView.getText().toString()), captainView.isChecked());
        if (status != 0 && edit && editNumber == Integer.parseInt(numberView.getText().toString())) {
            status--;
        }
        if (status == 1 || status == 3) {
            Toast.makeText(getActivity(), getResources().getString(R.string.edit_player_dialog_number_warning), Toast.LENGTH_LONG).show();
        } else if (status == 2) {
            ConfirmDialog.newInstance("edit_player_captain").show(getFragmentManager(), Constants.TAG_FRAGMENT_CONFIRM);
        }
        return status == 0;
    }

    public void changeCaptainConfirmed() {
        apply();

    }
}