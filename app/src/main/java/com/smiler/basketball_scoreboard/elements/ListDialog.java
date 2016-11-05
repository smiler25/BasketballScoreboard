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
    private boolean left;

    public static ListDialog newInstance(String type) {
        ListDialog f = new ListDialog();
        Bundle args = new Bundle();
        args.putString("type", type);
        f.setArguments(args);
        return f;
    }

    public static ListDialog newInstance(String type, boolean left) {
        ListDialog f = new ListDialog();
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putBoolean("left", left);
        f.setArguments(args);
        return f;
    }

    public static ListDialog newInstance(String type, ArrayList<String> values, boolean left, int number) {
        ListDialog f = new ListDialog();
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putStringArrayList("values", values);
        args.putBoolean("left", left);
        args.putInt("number", number);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String type = args.getString("type", "");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch (type) {
            case "timeout":
                builder.setItems(R.array.timeout_variants, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onTimeoutDialogItemClick(which);
                    }
                });
                break;
            case "new_period":
                builder.setItems(R.array.new_period_variants, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onNewPeriodDialogItemClick(which);
                    }
                });
                break;
            case "clear_panel":
                left = args.getBoolean("left", true);
                builder.setItems(R.array.side_panels_clear_titles, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onClearPanelDialogItemClick(which, left);
                    }
                });
                break;
            case "substitute":
                left = args.getBoolean("left", true);
                ArrayList<String> values = args.getStringArrayList("values");
                if (values == null) { values = new ArrayList<>(); }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.substitute_dialog_list_item, values);
                builder.setAdapter(adapter, listClickListener);
                int number = args.getInt("number", -1);
                String title;
                if (left) {
                    title = number == -1
                            ? getResources().getString(R.string.substitute_dialog_title_home0)
                            : String.format(getResources().getString(R.string.substitute_dialog_title_home), number);
                } else {
                    title = number == -1
                            ? getResources().getString(R.string.substitute_dialog_title_guest0)
                            : String.format(getResources().getString(R.string.substitute_dialog_title_guest), number);
                }
                builder.setTitle(title);
                break;
        }
        builder.setCancelable(true);
        return builder.create();
    }

    DialogInterface.OnClickListener listClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            listener.onSubstituteListSelect(left,
                    Integer.parseInt(((AlertDialog) dialog).getListView().getAdapter().getItem(which).toString().split(":")[0]));
        }
    };

    public interface NewTimeoutDialogListener {
        void onTimeoutDialogItemClick(int which);
        void onNewPeriodDialogItemClick(int which);
        void onClearPanelDialogItemClick(int which, boolean left);
        void onSubstituteListSelect(boolean left, int newNumber);
    }

    private NewTimeoutDialogListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (NewTimeoutDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NewTimeoutDialogListener");
        }
    }
}