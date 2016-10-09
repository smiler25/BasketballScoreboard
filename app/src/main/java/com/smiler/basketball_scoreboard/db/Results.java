package com.smiler.basketball_scoreboard.db;

import java.util.Date;

import io.realm.RealmObject;

public class Results extends RealmObject {
    private int id;
    private Date date;
    private String home_team;
    private String guest_team;
    private int home_score;
    private int guest_score;
    private String home_periods;
    private String guest_periods;
    private String share_string;
    private int regular_periods;
    private boolean complete;
    private GameDetails details;

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
    public String getHomeTeam() { return home_team; }
    public Results setHomeTeam(String home_team) {
        this.home_team = home_team;
        return this;
    }
    public String getGuestTeam() { return guest_team; }
    public Results setGuestTeam(String guest_team) {
        this.guest_team = guest_team;
        return this;
    }
    public int getHomeScore() { return home_score; }
    public Results setHomeScore(int home_score) {
        this.home_score = home_score;
        return this;
    }
    public int getGuestScore() { return guest_score; }
    public Results setGuestScore(int guest_score) {
        this.guest_score = guest_score;
        return this;
    }
    public String getHomePeriods() { return home_periods; }
    public Results setHomePeriods(String home_periods) {
        this.home_periods = home_periods;
        return this;
    }
    public String getGuestPeriods() { return guest_periods; }
    public Results setGuestPeriods(String guest_periods) {
        this.guest_periods = guest_periods;
        return this;
    }
    public String getShareString() { return share_string; }
    public Results setShareString(String share_string) {
        this.share_string = share_string;
        return this;
    }
    public int getRegularPeriods() { return regular_periods; }
    public Results setRegularPeriods(int regular_periods) {
        this.regular_periods = regular_periods;
        return this;
    }
    public boolean getComplete() { return complete; }
    public Results setComplete(boolean complete) {
        this.complete = complete;
        return this;
    }
    public GameDetails getDetails() { return details; }
    public Results setDetails(GameDetails details) {
        this.details = details;
        return this;
    }
}
