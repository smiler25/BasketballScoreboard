package com.smiler.basketball_scoreboard.elements;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class SetDefaultPreference extends DialogPreference implements DialogInterface.OnClickListener{

    public SetDefaultPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        attachListener(context);
    }

    public SetDefaultPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        attachListener(context);
    }

    public interface SetDefaultDialogListener {
        void onSetPositive();
        void onSetNegative();
    }
    SetDefaultDialogListener mListener;

    public void attachListener(Context activity) {
        try {
            mListener = (SetDefaultDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SetDefaultDialogListener");
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                mListener.onSetPositive();
                break;
            case Dialog.BUTTON_NEGATIVE:
                mListener.onSetNegative();
                break;
        }
    }
}
