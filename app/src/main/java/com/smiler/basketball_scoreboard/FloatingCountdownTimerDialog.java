package com.smiler.basketball_scoreboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class FloatingCountdownTimerDialog extends DialogFragment {

    public String title = "Timeout";
    public long duration = 60000;
    private long timeLeft;
    private TextView clockView, titleView;
    private CountDownTimer timer;


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
                timer.cancel();
                dismiss();
            }
        });
        return builder.create();
    }

    public void startCountDownTimer() {
        long shotTickInterval = 100;
        timer = new CountDownTimer(duration, shotTickInterval) {
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                setTimeText(timeLeft);
            }
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
            clockView.setText(Constants.timeFormat.format(millis));
        } else {
            clockView.setText(Constants.timeFormatMillis.format(millis));
        }
    }
}