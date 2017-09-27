package com.smiler.basketball_scoreboard.db;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Results extends RealmObject {
    @PrimaryKey
    private int id;
    private Date date;
    private String firstTeamName;
    private String secondTeamName;
    private int firstScore;
    private int secondScore;
    private String firstPeriods;
    private String secondPeriods;
    private String shareString;
    private int regularPeriods;
    private boolean complete;
    private GameDetails details;
    private Team firstTeam;
    private Team secondTeam;

    public int getId() { return id; }
    public Results setId(int id) {
        this.id = id;
        return this;
    }
    public Date getDate() { return date; }
    public Results setDate(Date date) {
        this.date = date;
        return this;
    }
    public String getFirstTeamName() { return firstTeamName; }
    public Results setFirstTeamName(String value) {
        firstTeamName = value;
        return this;
    }
    public String getSecondTeamName() { return secondTeamName; }
    public Results setSecondTeamName(String value) {
        secondTeamName = value;
        return this;
    }
    public int getFirstScore() { return firstScore; }
    public Results setFirstScore(int value) {
        firstScore = value;
        return this;
    }
    public int getSecondScore() { return secondScore; }
    public Results setSecondScore(int value) {
        secondScore = value;
        return this;
    }
    public String getFirstPeriods() { return firstPeriods; }
    public Results setFirstPeriods(String value) {
        firstPeriods = value;
        return this;
    }
    public String getSecondPeriods() { return secondPeriods; }
    public Results setSecondPeriods(String value) {
        secondPeriods = value;
        return this;
    }
    public String getShareString() { return shareString; }
    public Results setShareString(String value) {
        shareString = value;
        return this;
    }
    public int getRegularPeriods() { return regularPeriods; }
    public Results setRegularPeriods(int value) {
        regularPeriods = value;
        return this;
    }
    public boolean getComplete() { return complete; }
    public Results setComplete(boolean value) {
        complete = value;
        return this;
    }
    public GameDetails getDetails() { return details; }
    public Results setDetails(GameDetails value) {
        details = value;
        return this;
    }

    public Results setFirstTeam(Team team) {
        firstTeam = team;
        return this;
    }

    public Team getFirstTeam() {
        return firstTeam;
    }

    public Results setSecondTeam(Team team) {
        secondTeam = team;
        return this;
    }

    public Team getSecondTeam() {
        return secondTeam;
    }
}
