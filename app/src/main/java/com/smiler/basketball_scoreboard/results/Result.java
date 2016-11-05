package com.smiler.basketball_scoreboard.results;

import android.text.TextUtils;

import com.smiler.basketball_scoreboard.models.ActionRecord;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Stack;

public class Result {

    private int hScore, gScore;
    private String hName, gName;
    private ArrayList<Integer> hScorePeriods = new ArrayList<>();
    private ArrayList<Integer> gScorePeriods = new ArrayList<>();
    private Stack<ActionRecord> play_by_play = new Stack<>();
    private JSONArray play_by_play_by_period= new JSONArray();

    private boolean complete;
    private long date;
    private int numRegular;

    public Result(String hName, String gName) {
        this.hName = hName;
        this.gName = gName;
    }

    Result(String hName, String gName, int hScore, int gScore,
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

    int getHomeScore() {
        return hScore;
    }

    int getGuestScore() {
        return gScore;
    }

    String getHomeName() {
        return hName;
    }

    String getGuestName() {
        return gName;
    }

    public ArrayList<Integer> getHomeScoreByPeriod() {
        return hScorePeriods;
    }

    ArrayList<Integer> getGuestScoreByPeriod() {
        return gScorePeriods;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setHomeName(String name) {
        hName = name;
    }

    public void setGuestName(String name) {
        gName = name;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
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
        completePlayByPlayPeriod();
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
        completePlayByPlayPeriod();
    }

    public String getHomeScoreByPeriodString() {
        return TextUtils.join("-", hScorePeriods);
    }

    public String getGuestScoreByPeriodString() {
        return TextUtils.join("-", gScorePeriods);
    }

    public String getPlayByPlayString() {
        return TextUtils.join("-", play_by_play);
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

    long getDate() {
        return date;
    }

    int getNumRegular() {
        return numRegular;
    }

    public ActionRecord addAction(long time, int type, int team, int number, int value) {
        ActionRecord record = new ActionRecord(time, type, team, number, value);
        play_by_play.push(record);
        return record;
    }

    public ActionRecord addAction(long time, int type, int team, int value) {
        ActionRecord record = new ActionRecord(time, type, team, value);
        play_by_play.push(record);
        return record;
    }

    public ActionRecord getLastAction() {
        return !play_by_play.isEmpty() ? play_by_play.pop() : null;
    }

    @Override
    public String toString() {
        JSONArray data = getJson();
        return data != null ? data.toString() : "";
    }

    private JSONArray getJson() {
        return play_by_play_by_period;
    }

    private void completePlayByPlayPeriod() {
        if (play_by_play.isEmpty()) {
            return;
        }
        JSONArray array = new JSONArray();
        for (ActionRecord record : play_by_play) {
            array.put(record.getJson());
        }
        play_by_play_by_period.put(array);
        play_by_play.clear();
    }
}