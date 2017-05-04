package com.smiler.basketball_scoreboard.db;

import io.realm.RealmObject;

public class Player extends RealmObject {
    private int id;
    private Team team;
    private String name;
    private int number;
    private boolean captain;

    public String getName() { return name; }
    public Player setName(String value) {
        name = value;
        return this;
    }
    public int getNumber() { return number; }
    public Player setNumber(int value) {
        number = value;
        return this;
    }
    public boolean getCaptain() { return captain; }
    public Player setCaptain(boolean value) {
        captain = value;
        return this;
    }
    public Team getTeam() { return team; }
    public Player setTeam(Team value) {
        team = value;
        return this;
    }

//    public RealmList<Results> getGames() { return games; }
//    public Player setGames(RealmList<Results> value) {
//        games = value;
//        return this;
//    }
//
//    public Player fromGamePlayer(GamePlayer player) {
//        number = player.getNumber();
//        name = player.getName();
//        return this;
//    }

}
