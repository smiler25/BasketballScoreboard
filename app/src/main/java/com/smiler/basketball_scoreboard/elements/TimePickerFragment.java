package com.smiler.basketball_scoreboard.elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.smiler.basketball_scoreboard.R;


public class TimePickerFragment extends DialogFragment {

    OnChangeTimeListener changeTimeListener;
    CustomNumberPicker minutesPicker, secondsPicker, millisPicker;
    boolean isMain = true;

    public static TimePickerFragment newInstance(int minutes, int seconds, int millis) {
        TimePickerFragment f = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putInt("minutes", minutes);
        args.putInt("seconds", seconds);
        args.putInt("millis", millis);
        args.putBoolean("isMain", true);
        f.setArguments(args);
        return f;
    }

    public static TimePickerFragment newInstance(int seconds, int millis) {
        TimePickerFragment f = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putInt("seconds", seconds);
        args.putInt("millis", millis);
        args.putBoolean("isMain", false);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        isMain = args.getBoolean("isMain");
        int layoutId = isMain ? R.layout.main_time_picker : R.layout.shot_time_picker;

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(layoutId, null);
        builder.setView(v);
        if (isMain) {
            minutesPicker = (CustomNumberPicker) v.findViewById(R.id.pickerMinutes);
            minutesPicker.setValue(args.getInt("minutes", 0));
        }
        secondsPicker = (CustomNumberPicker) v.findViewById(R.id.pickerSeconds);
        millisPicker = (CustomNumberPicker) v.findViewById(R.id.pickerMillis);
        secondsPicker.setValue(args.getInt("seconds", 0));
        millisPicker.setValue(args.getInt("millis", 0));

        v.findViewById(R.id.buttonApply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMain) {
                    changeTimeListener.onTimeChanged(minutesPicker.getValue(), secondsPicker.getValue(), millisPicker.getValue());
                } else {
                    changeTimeListener.onTimeChanged(secondsPicker.getValue(), millisPicker.getValue());
                }
                dismiss();
            }
        });
        return builder.create();
    }

    public interface OnChangeTimeListener {
        void onTimeChanged(int minutes, int seconds, int millis);
        void onTimeChanged(int seconds, int millis);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            changeTimeListener = (OnChangeTimeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onChangeTimeListener");
        }
    }

}