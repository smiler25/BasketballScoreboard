package com.smiler.basketball_scoreboard.preferences;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;

public class SeekBarPreference extends DialogPreference implements DialogInterface.OnClickListener,
SeekBar.OnSeekBarChangeListener{

    // TODO: логика для случаев, когда min==0 и min!=0,
    SeekBarDialogListener listener;
    private int max = 10;
    private int min = 0;
    private int current = 3;
    private TextView currentTextView;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.pref_seekbar);
        parseAttrs(context, attrs);
        attachListener(context);
    }

    private void currentFromPreferences(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        current = prefs.getInt(key, min);
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDialogLayoutResource(R.layout.pref_seekbar);
        parseAttrs(context, attrs);
        attachListener(context);
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        for (int i=0; i < attrs.getAttributeCount(); i++) {
            switch (attrs.getAttributeName(i).toLowerCase()) {
                case "max":
                    max = attrs.getAttributeIntValue(i, max);
                    break;
                case "min":
                    min = attrs.getAttributeIntValue(i, min);
                    break;
                case "key":
                    currentFromPreferences(context, attrs.getAttributeValue(i));
                    break;
            }
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        ((TextView) view.findViewById(R.id.seekbar_min)).setText(Integer.toString(min));
        ((TextView) view.findViewById(R.id.seekbar_max)).setText(Integer.toString(max));
        currentTextView = (TextView) view.findViewById(R.id.seekbar_current);
        currentTextView.setText(Integer.toString(current));
        seekBar.setMax(max - 1);
        seekBar.setProgress(current - 1);
        seekBar.setOnSeekBarChangeListener(this);
        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        setSummary(getSummary());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        current = progress + 1;
        currentTextView.setText(Integer.toString(current));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    public interface SeekBarDialogListener {
        void onAcceptSeekBarValue(int value);
    }

    public void attachListener(Context activity) {
        try {
            listener = (SeekBarDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SeekBarDialogListener");
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                listener.onAcceptSeekBarValue(current);
                break;
            case Dialog.BUTTON_NEGATIVE:
                break;
        }
    }

    @Override
    public CharSequence getSummary() {
        return String.format(getContext().getResources().getString(R.string.pref_horn_length_descr), current);

    }

}
