package com.smiler.basketball_scoreboard.elements.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import static com.smiler.basketball_scoreboard.Constants.HOME;


public class NameEditDialog extends DialogFragment {

    OnChangeNameListener changeTimeListener;
    private EditText editView;
    private int team;

    public static NameEditDialog newInstance(int team, String name) {
        NameEditDialog f = new NameEditDialog();
        Bundle args = new Bundle();
        args.putInt("team", team);
        args.putString("name", name);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        team = args.getInt("team", HOME);
        int strId;
        if (team == HOME){
            strId = R.string.edit_home_name;
        } else {
            strId = R.string.edit_guest_name;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.team_name_edit, null);

        builder.setView(v).setCancelable(true);

        TextView title = (TextView) v.findViewById(R.id.dialog_title);
        title.setText(String.format(getResources().getString(strId), team));
        editView = (EditText) v.findViewById(R.id.edit_text);

        String name = args.getString("name");
        if (name != null) {
            editView.setText(name);
            editView.selectAll();
        }

        v.findViewById(R.id.buttonApply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTimeListener.onNameChanged(editView.getText().toString(), team);
                dismiss();
            }
        });
        return builder.create();
    }


    public interface OnChangeNameListener {
        void onNameChanged(String value, int team);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            changeTimeListener = (OnChangeNameListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must onChangeNameListener");
        }
    }

}