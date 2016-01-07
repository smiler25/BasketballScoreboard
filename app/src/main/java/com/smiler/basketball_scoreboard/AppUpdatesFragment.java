package com.smiler.basketball_scoreboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class AppUpdatesFragment extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.app_updates_fragment, null);
        v.findViewById(R.id.buttonOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        String text = getResources().getString(R.string.appupdate_info);
        TextView textView = (TextView) v.findViewById(R.id.dialog_text);
        SpannableString spannable = new SpannableString(text);
        ClickableSpan openHelpSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                dismiss();
                startActivity(new Intent(getActivity(), HelpActivity.class));
            }
        };
        int i1 = text.indexOf("(") + 1;
        int i2 = text.indexOf(")") - 1;
        spannable.setSpan(openHelpSpan, i1, i2 + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setView(v).setCancelable(true);
        return builder.create();
    }
}