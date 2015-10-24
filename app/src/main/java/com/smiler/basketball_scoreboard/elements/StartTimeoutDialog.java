package com.smiler.basketball_scoreboard.elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.smiler.basketball_scoreboard.R;


public class StartTimeoutDialog extends DialogFragment {

    public static StartTimeoutDialog newInstance(String type) {
        StartTimeoutDialog f = new StartTimeoutDialog();
        Bundle args = new Bundle();
        args.putString("type", type);
        f.setArguments(args);
        return f;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String type = args.getString("type");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (type.equals("timeout")) {
            builder.setItems(R.array.timeout_variants, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onTimeoutDialogItemClick(which);}
            });
        } else if (type.equals("new_period")) {
            builder.setItems(R.array.new_period_variants, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onNewPeriodDialogItemClick(which);}
            });
        }
        builder.setCancelable(true);
        return builder.create();
    }

    public interface NewTimeoutDialogListener {
        void onTimeoutDialogItemClick(int which);
        void onNewPeriodDialogItemClick(int which);
    }

    NewTimeoutDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NewTimeoutDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NewTimeoutDialogListener");
        }
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}