package com.smiler.basketball_scoreboard;

import android.app.Application;

import io.realm.Realm;

public class ScoreboardApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}