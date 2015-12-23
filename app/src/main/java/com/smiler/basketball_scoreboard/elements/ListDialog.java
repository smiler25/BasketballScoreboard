package com.smiler.basketball_scoreboard.elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.smiler.basketball_scoreboard.R;

import java.util.ArrayList;


public class ListDialog extends DialogFragment {

    public static final String TAG = "ListDialog";
    private boolean left = false;

    public static ListDialog newInstance(String type) {
        ListDialog f = new ListDialog();
        Bundle args = new Bundle();
        args.putString("type", type);
        f.setArguments(args);
        return f;
    }

    public static ListDialog newInstance(String type, ArrayList<String> values, boolean left) {
        ListDialog f = new ListDialog();
        Bundle args = new Bundle();
        System.out.println("newInstance values = " + values);
        args.putString("type", type);
        args.putStringArrayList("values", values);
        args.putBoolean("left", left);
        f.setArguments(args);
        return f;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String type = args.getString("type", "");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch (type) {
            case "timeout":
                builder.setItems(R.array.timeout_variants, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onTimeoutDialogItemClick(which);
                    }
                });
                break;
            case "new_period":
                builder.setItems(R.array.new_period_variants, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onNewPeriodDialogItemClick(which);
                    }
                });
                break;
            case "substitute":
                left = args.getBoolean("left", true);
                ArrayList<String> values = args.getStringArrayList("values");
                System.out.println("values = " + values);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.substitute_dialog_list_item, values);
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, values);
                builder.setAdapter(adapter, myClickListener);
                builder.setTitle("Select substitute for home player №9");
//                ArrayAdapter titles = new ArrayAdapter();
//                builder.setItems(titles, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        mListener.onSubstituteListSelect(which, left);
//                    }
//                });
                break;
        }
        builder.setCancelable(true);
        return builder.create();
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            System.out.println("which = " + which);
        }
    };

    public interface NewTimeoutDialogListener {
        void onTimeoutDialogItemClick(int which);
        void onNewPeriodDialogItemClick(int which);
        void onSubstituteListSelect(int which, boolean left);
    }

    private NewTimeoutDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NewTimeoutDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NewTimeoutDialogListener");
        }
    }
}