package com.smiler.basketball_scoreboard.elements.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.elements.CustomNumberPicker;

import java.util.concurrent.TimeUnit;

import static com.smiler.basketball_scoreboard.Constants.SECONDS_60;


public class TimePickerDialog extends DialogFragment {

    private ChangeTimeListener listener;
    private CustomNumberPicker minutesPicker;
    private CustomNumberPicker secondsPicker;
    private CustomNumberPicker millisPicker;
    private boolean isMain = true;

    public static TimePickerDialog newInstance(long time, boolean main) {
        TimePickerDialog f = new TimePickerDialog();
        Bundle args = new Bundle();
        args.putLong("millis", time);
        args.putBoolean("isMain", main);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        isMain = args.getBoolean("isMain");
        int layoutId = isMain ? R.layout.time_picker_main : R.layout.time_picker_shot;

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(layoutId, null);
        builder.setView(v);

        long time = args.getLong("millis", 0);
        int seconds, millis;
        if (isMain) {
            int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)));
            seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
            millis = (int) (time % 1000) / 100;

            minutesPicker = (CustomNumberPicker) v.findViewById(R.id.pickerMinutes);
            minutesPicker.setValue(minutes);

        } else {
            seconds = (int) time / 1000;
            millis = (int) (time % 1000) / 100;

        }
        secondsPicker = (CustomNumberPicker) v.findViewById(R.id.pickerSeconds);
        millisPicker = (CustomNumberPicker) v.findViewById(R.id.pickerMillis);
        secondsPicker.setValue(seconds);
        millisPicker.setValue(millis);

        v.findViewById(R.id.dialog_apply).setOnClickListener(v1 -> {
            if (isMain) {
                listener.onTimeChanged(getMainTime(), isMain);
            } else {
                listener.onTimeChanged(getShotTime(), isMain);
            }
            dismiss();
        });
        return builder.create();
    }

    public interface ChangeTimeListener {
        void onTimeChanged(long time, boolean main);
    }

    private long getMainTime() {
        return minutesPicker.getValue() * SECONDS_60 +
                secondsPicker.getValue() * 1000 +
                millisPicker.getValue() * 100;
    }

    private long getShotTime() {
        return secondsPicker.getValue() * 1000 + millisPicker.getValue() * 100;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ChangeTimeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onChangeTimeListener");
        }
    }

}