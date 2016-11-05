package com.smiler.basketball_scoreboard.adapters;

import android.content.Context;

import com.smiler.basketball_scoreboard.db.Results;

import io.realm.RealmResults;

public class RealmResultsAdapter extends RealmModelAdapter<Results> {

    public RealmResultsAdapter(Context context, RealmResults<Results> realmResults) {
        super(context, realmResults);
    }
}