package com.smiler.basketball_scoreboard.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Player extends RealmObject {
    @PrimaryKey
    private int id;
    private Team team;
    @Required
    private String name;
    private int number;
    private boolean captain;

    public int getId() { return id; }
    public Player setId(int value) {
        id = value;
        return this;
    }

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

//    public RealmListFragment<Results> getGames() { return games; }
//    public Player setGames(RealmListFragment<Results> value) {
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
