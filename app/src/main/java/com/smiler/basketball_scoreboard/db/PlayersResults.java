package com.smiler.basketball_scoreboard.db;

import io.realm.RealmObject;

public class PlayersResults extends RealmObject {
    private int id;
    private Results game;
    private String team;
    private int number;
    private String name;
    private int points;
    private int fouls;
    private boolean captain;
    private boolean active;
    private long timePlayed;

    public Results getGame() { return game; }
    public PlayersResults setGame(Results value) {
        game = value;
        return this;
    }
    public String getTeam() { return team; }
    public PlayersResults setTeam(String value) {
        team = value;
        return this;
    }
    public int getNumber() { return number; }
    public PlayersResults setNumber(int value) {
        number = value;
        return this;
    }
    public String getPlayerName() { return name; }
    public PlayersResults setName(String value) {
        name = value;
        return this;
    }
    public int getPoints() { return points; }
    public PlayersResults setPoints(int value) {
        points = value;
        return this;
    }
    public int getFouls() { return fouls; }
    public PlayersResults setFouls(int value) {
        fouls = value;
        return this;
    }
    public boolean getCaptain() { return captain; }
    public PlayersResults setCaptain(boolean value) {
        captain = value;
        return this;
    }
    public boolean getActive() { return active; }
    public PlayersResults setActive(boolean value) {
        active = value;
        return this;
    }

}
