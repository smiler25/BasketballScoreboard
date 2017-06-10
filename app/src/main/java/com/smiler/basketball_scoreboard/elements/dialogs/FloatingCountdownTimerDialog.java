package com.smiler.basketball_scoreboard.elements.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.CountDownTimer;
import com.smiler.basketball_scoreboard.R;

import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT_SHORT;


public class FloatingCountdownTimerDialog extends DialogFragment {

    public String title = "Timeout";
    public long duration = 60000;
    private long timeLeft;
    private TextView clockView, titleView;
    private CountDownTimer timer;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            duration = savedInstanceState.getLong("timeLeft", duration);
            title = savedInstanceState.getString("title", title);
            titleView.setText(title);
            startCountDownTimer();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("timeLeft", timeLeft);
        outState.putCharSequence("title", titleView.getText());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.timeout_dialog, null);
        builder.setView(v);
        clockView = (TextView) v.findViewById(R.id.timeoutClock);
        titleView = (TextView) v.findViewById(R.id.dialog_title);
        titleView.setText(title);
        v.findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel();
                }
                dismiss();
            }
        });
        return builder.create();
    }

    public void startCountDownTimer() {
        long shotTickInterval = 100;
        timer = new CountDownTimer(duration, shotTickInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                setTimeText(timeLeft);
            }
            @Override
            public void onFinish() {
                 setTimeText(0);
            }
        }.start();
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    private void setTimeText(long millis) {
        if (millis >= 60000) {
            clockView.setText(TIME_FORMAT.format(millis));
        } else {
            clockView.setText(String.format(TIME_FORMAT_SHORT, millis / 1000, millis % 1000 / 100));
        }
    }
}