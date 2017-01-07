package com.smiler.basketball_scoreboard;

import android.app.Application;

import com.smiler.basketball_scoreboard.preferences.Preferences;

public class App extends Application {

    Preferences prefs;

    Preferences getPrefs() {
        return prefs;
    }

    void setPrefs(Preferences value) {
        this.prefs = value;
    }
}