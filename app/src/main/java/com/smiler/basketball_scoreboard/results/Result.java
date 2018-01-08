package com.smiler.basketball_scoreboard.results;

import android.text.TextUtils;

import com.smiler.basketball_scoreboard.game.ActionRecord;
import com.smiler.basketball_scoreboard.game.Actions;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Stack;

import lombok.Data;

@Data
public class Result {
    private int homeScore, guestScore;
    private String homeName, guestName;
    private ArrayList<Integer> homeScorePeriods = new ArrayList<>();
    private ArrayList<Integer> guestScorePeriods = new ArrayList<>();
    private Stack<ActionRecord> playByPlay = new Stack<>();
    private JSONArray playByPlayByPeriod = new JSONArray();

    private boolean complete;
    private long date;
    private int numRegular;

    public Result(String homeName, String guestName) {
        this.homeName = homeName;
        this.guestName = guestName;
    }

    public Result(String homeName, String guestName, int homeScore, int guestScore,
                  ArrayList<Integer> homeScorePeriods, ArrayList<Integer> guestScorePeriods,
                  boolean complete, long date, int numRegular) {
        this.homeName = homeName;
        this.guestName = guestName;
        this.homeScore = homeScore;
        this.guestScore = guestScore;
        this.homeScorePeriods = homeScorePeriods;
        this.guestScorePeriods = guestScorePeriods;
        this.complete = complete;
        this.date = date;
        this.numRegular = numRegular;
    }

    public boolean isComplete() {
        return complete;
    }

    public void addPeriodScores(int home, int guest) {
        if (homeScorePeriods.isEmpty()) {
            homeScorePeriods.add(home);
            guestScorePeriods.add(guest);
        } else {
            homeScorePeriods.add(home - homeScore );
            guestScorePeriods.add(guest - guestScore);
        }
        homeScore = home;
        guestScore = guest;
        completePlayByPlayPeriod();
    }

    public void replacePeriodScores(int period, int home, int guest) {
        if (--period < homeScorePeriods.size()) {
            homeScorePeriods.remove(period);
            guestScorePeriods.remove(period);
            homeScorePeriods.add(period, home - homeScore);
            guestScorePeriods.add(period, guest - guestScore);
        }
        homeScore = home;
        guestScore = guest;
        completePlayByPlayPeriod();
    }

    public String getHomeScoreByPeriodString() {
        return TextUtils.join("-", homeScorePeriods);
    }

    public String getGuestScoreByPeriodString() {
        return TextUtils.join("-", guestScorePeriods);
    }

    public String getResultString(boolean ot) {
        String result =  String.format("%s - %s: %d - %d", homeName, guestName, homeScore, guestScore);
        if (homeScorePeriods.isEmpty()) { return result; }
        String fStr = "%d-%d";
        ArrayList<String> periodResult = new ArrayList<>();
        for (int i = 0; i < homeScorePeriods.size(); i++) {
            periodResult.add(String.format(fStr, homeScorePeriods.get(i), guestScorePeriods.get(i)));
        }
        if (ot) { result += " (OT)"; }
        return String.format("%s (%s)", result, TextUtils.join(", ", periodResult));
    }

    public ActionRecord addAction(long time, Actions action, int team, int value) {
        ActionRecord record = new ActionRecord(time, action, team, value);
        playByPlay.push(record);
        return record;
    }

    public ActionRecord getLastAction() {
        return !playByPlay.isEmpty() ? playByPlay.pop() : null;
    }

    public String getString() {
        JSONArray data = getJson();
        return data != null ? data.toString() : "";
    }

    private JSONArray getJson() {
        return playByPlayByPeriod;
    }

    private void completePlayByPlayPeriod() {
        if (playByPlay.isEmpty()) {
            return;
        }
        JSONArray array = new JSONArray();
        for (ActionRecord record : playByPlay) {
            array.put(record.getJson());
        }
        playByPlayByPeriod.put(array);
        playByPlay.clear();
    }
}