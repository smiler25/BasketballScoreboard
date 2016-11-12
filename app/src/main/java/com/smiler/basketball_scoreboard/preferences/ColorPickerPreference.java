package com.smiler.basketball_scoreboard.preferences;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.elements.CustomFontTextView;

public class ColorPickerPreference extends DialogPreference implements
        DialogInterface.OnClickListener,
        SeekBar.OnSeekBarChangeListener {

    private SharedPreferences prefs;
    private ColorPickerListener listener;
    private CustomFontTextView colorPreview;
    private Spinner elementPicker;
    private SeekBar redSeekbar, greenSeekbar, blueSeekbar;
    private int defaultColor;
    private int red = 0;
    private int green = 0;
    private int blue = 0;

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setDialogLayoutResource(R.layout.pref_color_picker);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        defaultColor = context.getResources().getColor(R.color.orange);
        attachListener(context);
    }

    interface ColorPickerListener {
        void onAcceptColor();
    }

    @Override
    protected void onBindDialogView(View view) {
        redSeekbar = (SeekBar) view.findViewById(R.id.seekbar_red);
        greenSeekbar = (SeekBar) view.findViewById(R.id.seekbar_green);
        blueSeekbar = (SeekBar) view.findViewById(R.id.seekbar_blue);
        redSeekbar.setOnSeekBarChangeListener(this);
        greenSeekbar.setOnSeekBarChangeListener(this);
        blueSeekbar.setOnSeekBarChangeListener(this);
        colorPreview = (CustomFontTextView) view.findViewById(R.id.color_picker_preview);
        elementPicker = (Spinner) view.findViewById(R.id.color_picker_element);
        elementPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                setPreviewColor(selectedId);
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        view.findViewById(R.id.apply_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveColor();
            }
        });
        view.findViewById(R.id.reset_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetColor();
            }
        });
        setPreviewColor(0);
        int max = 255;
        redSeekbar.setMax(max);
        greenSeekbar.setMax(max);
        blueSeekbar.setMax(max);
        redSeekbar.setProgress(red);
        greenSeekbar.setProgress(green);
        blueSeekbar.setProgress(blue);

        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        setSummary(getSummary());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekbar_red:
                red = progress;
                break;
            case R.id.seekbar_green:
                green = progress;
                break;
            case R.id.seekbar_blue:
                blue = progress;
                break;
        }
        colorPreview.setColor(red, green, blue);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    private void attachListener(Context activity) {
        try {
            listener = (ColorPickerListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ColorPickerDialogListener");
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                saveColor();
                listener.onAcceptColor();
                break;
            case Dialog.BUTTON_NEGATIVE:
                break;
        }
    }

    private void saveColor() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(getKeyName(elementPicker.getSelectedItemId()), Color.rgb(red, green, blue));
        editor.apply();
    }

    private void resetColor() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(getKeyName(elementPicker.getSelectedItemId()));
        editor.apply();
        setPreviewColor(elementPicker.getSelectedItemId());
    }

    public static String getKeyName(long id) {
        return String.format("color_%d", id);
    }

//    public static int getStringArrayRes() {
//        return R.array.pref_custom_color_elements;
//    }

    private void setPreviewColor(long id) {
        int color = prefs.getInt(getKeyName(id), defaultColor);
        red = Color.red(color);
        green = Color.green(color);
        blue = Color.blue(color);
        redSeekbar.setProgress(red);
        greenSeekbar.setProgress(green);
        blueSeekbar.setProgress(blue);
        colorPreview.setColor(red, green, blue);
    }

}
