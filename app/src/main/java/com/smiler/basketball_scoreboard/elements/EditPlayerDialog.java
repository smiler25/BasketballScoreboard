package com.smiler.basketball_scoreboard.elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.Constants;
import com.smiler.basketball_scoreboard.R;

public class EditPlayerDialog extends DialogFragment{

    public static String TAG = "EditPlayerDialog";
    OnEditPlayerListener editPlayerListener;
    private EditText numberView, nameView;
    private Switch captainView;

    public static EditPlayerDialog newInstance(String number, String name, boolean captain) {
        EditPlayerDialog f = new EditPlayerDialog();
        Bundle args = new Bundle();
        args.putString("number", number);
        args.putString("name", name);
        args.putBoolean("captain", captain);
        f.setArguments(args);
        return f;
    }

    public interface OnEditPlayerListener {
        void onEditPlayer(String number, String name, boolean captain);
        int onEditPlayerCheck(String number, boolean captain);
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

        Bundle args = getArguments();
        if (args != null) {
            numberView.setText(args.getString("number", ""));
            numberView.selectAll();
            nameView.setText(args.getString("name", ""));
            nameView.selectAll();
            captainView.setChecked(args.getBoolean("captain", false));
        }

        v.findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForm()) {
                    editPlayerListener.onEditPlayer(numberView.getText().toString(), nameView.getText().toString(), captainView.isChecked());
                    dismiss();
                }
            }
        });
        v.findViewById(R.id.button_add_another).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForm()) {
                    editPlayerListener.onEditPlayer(numberView.getText().toString(), nameView.getText().toString(), captainView.isChecked());
                    clearForm();
                }
            }
        });
        v.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return builder.create();
    }

    private void clearForm() {
        nameView.getText().clear();
        numberView.getText().clear();
        captainView.setChecked(false);
    }
    private boolean checkForm() {
        int status = editPlayerListener.onEditPlayerCheck(numberView.getText().toString(), captainView.isChecked());
        if (status == 1 || status == 3) {
            Toast.makeText(getActivity(), "number already entered", Toast.LENGTH_LONG).show();
        } else if (status == 2) {
            ConfirmDialog.newInstance("edit_player_captain").show(getFragmentManager(), Constants.TAG_FRAGMENT_CONFIRM);
        }
        return status == 0;
    }
    public void changeCaptainConfirmed() {
        editPlayerListener.onEditPlayer(numberView.getText().toString(), nameView.getText().toString(), captainView.isChecked());

    }
}