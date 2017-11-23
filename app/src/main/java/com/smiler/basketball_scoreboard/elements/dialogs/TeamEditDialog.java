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

import com.smiler.basketball_scoreboard.R;

public class TeamEditDialog extends DialogFragment {

    public static String TAG = "BS-TeamEditDialog";
    EditTeamCallback listener;
    private EditText nameView;
    private Switch activeView;
    private boolean edit;
    private int id;

    public interface EditTeamCallback {
        void onCreateTeam(String name, boolean active);
        void onEditTeam(int id, String name, boolean active);
    }

    public static TeamEditDialog newInstance(int id, String name, boolean active) {
        TeamEditDialog f = new TeamEditDialog();
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putString("name", name);
        args.putBoolean("active", active);
        f.setArguments(args);
        return f;
    }

    public static TeamEditDialog newInstance() {
        return new TeamEditDialog();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (EditTeamCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must EditTeamCallback");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        id = args != null ? args.getInt("id", -1) : -1;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.edit_team_dialog, null);
        builder.setView(v).setCancelable(true);

        nameView = v.findViewById(R.id.edit_team_name);
        activeView = v.findViewById(R.id.edit_team_active);
        activeView.setChecked(true);

        v.findViewById(R.id.button_add).setOnClickListener(v14 -> {
            if (checkForm()) {
                apply();
                dismiss();
            }
        });
        v.findViewById(R.id.button_cancel).setOnClickListener(v13 -> dismiss());
        Button add_another_bu = v.findViewById(R.id.button_custom_action);
        if (args != null) {
            nameView.setText(args.getString("name", ""));
            nameView.selectAll();
            activeView.setChecked(args.getBoolean("active", true));
            add_another_bu.setText(getResources().getText(R.string.action_delete));
            edit = true;
            add_another_bu.setOnClickListener(v12 -> {
                if (checkForm()) {
                    apply();
                    dismiss();
                }
            });
        } else {
            add_another_bu.setOnClickListener(v1 -> {
                if (checkForm()) {
                    apply();
                    clearForm();
                }
            });
        }
        return builder.create();
    }

    private void clearForm() {
        nameView.getText().clear();
        activeView.setChecked(true);
    }

    private void apply() {
        if (edit) {
            listener.onEditTeam(id, nameView.getText().toString(), activeView.isChecked());
        } else {
            listener.onCreateTeam(nameView.getText().toString(), activeView.isChecked());
        }
    }

    private boolean checkForm() {
        String name = nameView.getText().toString().trim();
        if(name.equals("")){
            Toast.makeText(getActivity(), getResources().getString(R.string.edit_team_dialog_name_required), Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (status == 1 || status == 3) {
//            Toast.makeText(getActivity(), getResources().getString(R.string.edit_player_dialog_number_warning), Toast.LENGTH_LONG).show();
//        } else if (status == 2) {
//            ConfirmDialog.newInstance(DialogTypes.CAPTAIN_ALREADY_ASSIGNED).show(getFragmentManager(), Constants.TAG_FRAGMENT_CONFIRM);
//
//        }
//        return status == 0;
        return true;
    }
}