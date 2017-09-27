package com.smiler.basketball_scoreboard.game;

import com.smiler.basketball_scoreboard.db.Player;
import com.smiler.basketball_scoreboard.db.PlayerEntry;

public class InGamePlayer implements PlayerEntry {

    private String name;
    private int points;
    private int number;
    private int fouls;
    private boolean captain;
    private Player dbRecord;

    public InGamePlayer() {
    }

    public InGamePlayer(Player dbRecord) {
        setDbRecord(dbRecord);
    }

    public InGamePlayer(int number, String name, int points, int fouls, boolean captain) {
        this.number = number;
        this.name = name;
        this.points = points;
        this.fouls = fouls;
        this.captain = captain;
    }

    public InGamePlayer setDbRecord(Player value) {
        dbRecord = value;
        name = value.getName();
        number = value.getNumber();
        return this;
    }

    public void setInfo(int number, String name, boolean captain) {
        this.number = number;
        this.name = name;
        this.captain = captain;
    }

    @Override
    public int getNumber() {
        return number;
    }

    public void setNumber(int value) {
        number = value;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public int getPoints() {
        return points;
    }

    public int changePoints(int value) {
        points += value;
        return points;
    }

    public int getFouls() {
        return fouls;
    }

    public int changeFouls(int value) {
        fouls += value;
        return fouls;
    }

    @Override
    public boolean isCaptain() {
        return captain;
    }

    public void setCaptain(boolean value) {
        captain = value;
    }

    public void clear() {
        points = 0;
        fouls = 0;
    }

    public boolean hasDbRecord() {
        return dbRecord != null;
    }
}