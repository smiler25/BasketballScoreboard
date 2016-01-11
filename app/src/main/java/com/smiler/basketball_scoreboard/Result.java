package com.smiler.basketball_scoreboard;

import android.text.TextUtils;

import java.util.ArrayList;

public class Result {

    private int hScore, gScore;
    private String hName, gName;
    private ArrayList<Integer> hScorePeriods = new ArrayList<>();
    private ArrayList<Integer> gScorePeriods = new ArrayList<>();
    private boolean complete;
    private long date;
    private int numRegular;

    public Result(String hName, String gName) {
        this.hName = hName;
        this.gName = gName;
    }

    public Result(String hName, String gName, int hScore, int gScore) {
        this.hName = hName;
        this.gName = gName;
        this.hScore = hScore;
        this.gScore = gScore;
    }

    public Result(String hName, String gName, int hScore, int gScore,
                  ArrayList<Integer> hScorePeriods, ArrayList<Integer> gScorePeriods,
                  boolean complete, long date, int numRegular) {
        this.hName = hName;
        this.gName = gName;
        this.hScore = hScore;
        this.gScore = gScore;
        this.hScorePeriods = hScorePeriods;
        this.gScorePeriods = gScorePeriods;
        this.complete = complete;
        this.date = date;
        this.numRegular = numRegular;
    }

    public int getHomeScore() {
        return this.hScore;
    }

    public int getGuestScore() {
        return this.gScore;
    }

    public String getHomeName() {
        return this.hName;
    }

    public String getGuestName() {
        return this.gName;
    }

    public ArrayList<Integer> getHomeScoreByPeriod() {
        return this.hScorePeriods;
    }

    public ArrayList<Integer> getGuestScoreByPeriod() {
        return this.gScorePeriods;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void setHomeName(String name) {
        this.hName = name;
    }

    public void setGuestName(String name) {
        this.gName = name;
    }

    public void setScores(int hScore, int gScore) {
        this.hScore = hScore;
        this.gScore = gScore;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void setPeriodScores(ArrayList<Integer>hScorePeriods, ArrayList<Integer>gScorePeriods) {
        this.hScorePeriods = hScorePeriods;
        this.gScorePeriods = gScorePeriods;
    }

    public void addPeriodScores(int home, int guest) {
        if (hScorePeriods.isEmpty()) {
            hScorePeriods.add(home);
            gScorePeriods.add(guest);
        } else {
            hScorePeriods.add(home - hScore );
            gScorePeriods.add(guest - gScore);
        }
        hScore = home;
        gScore = guest;
    }

    public void replacePeriodScores(int period, int home, int guest) {
        if (--period < hScorePeriods.size()) {
            hScorePeriods.remove(period);
            gScorePeriods.remove(period);
            hScorePeriods.add(period, home - hScore);
            gScorePeriods.add(period, guest - gScore);
        }
        hScore = home;
        gScore = guest;

    }

    public String getHomeScoreByPeriodString() {
        return TextUtils.join("-", getHomeScoreByPeriod());
    }

    public String getGuestScoreByPeriodString() {
        return TextUtils.join("-", getGuestScoreByPeriod());
    }

    public String getResultString(boolean ot) {
        String result =  String.format("%s - %s: %d - %d", hName, gName, hScore, gScore);
        if (hScorePeriods.isEmpty()) { return result; }
        String fStr = "%d-%d";
        ArrayList<String> periodResult = new ArrayList<>();
        for (int i = 0; i < hScorePeriods.size(); i++) {
            periodResult.add(String.format(fStr, hScorePeriods.get(i), gScorePeriods.get(i)));
        }
        if (ot) { result += " (OT)"; }
        return String.format("%s (%s)", result, TextUtils.join(", ", periodResult));
    }

    public long getDate() {
        return date;
    }

    public int getNumRegular() {
        return numRegular;
    }
}