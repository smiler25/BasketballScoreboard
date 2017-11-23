package com.smiler.basketball_scoreboard.elements.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.help.HelpActivity;
import com.smiler.basketball_scoreboard.preferences.PrefActivity;

public class AppUpdatesDialog extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.app_updates_fragment, null);
        v.findViewById(R.id.buttonOk).setOnClickListener(v1 -> dismiss());

        String text = getResources().getString(R.string.appupdate_info);
        String help_text = getResources().getString(R.string.appupdate_info_link_help).toLowerCase();
        String settings_text = getResources().getString(R.string.action_settings).toLowerCase();
        TextView textView = (TextView) v.findViewById(R.id.dialog_text);
        SpannableString spannable = new SpannableString(text);
        ClickableSpan openHelpSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                dismiss();
                startActivity(new Intent(getActivity(), HelpActivity.class));
            }
        };

        ClickableSpan openSettingsSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                dismiss();
                startActivity(new Intent(getActivity(), PrefActivity.class));
            }
        };
        int help_i1 = text.indexOf(help_text + ")");
        int help_i2 = help_i1 + help_text.length();
        int settings_i1 = text.indexOf(settings_text);
        int settings_i2 = settings_i1 + settings_text.length();

        spannable.setSpan(openHelpSpan, help_i1, help_i2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(openSettingsSpan, settings_i1, settings_i2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setView(v).setCancelable(true);
        return builder.create();
    }
}