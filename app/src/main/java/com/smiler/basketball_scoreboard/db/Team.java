package com.smiler.basketball_scoreboard.db;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Team extends RealmObject {
    @PrimaryKey
    private int id;
    @Required
    private String name;
    private boolean active;
    private RealmList<Player> players;
    private RealmList<Results> games;
    private float avgPoints;
    private int wins;

    public int getId() { return id; }
    public Team setId(int id) {
        this.id = id;
        return this;
    }
    public String getName() { return name; }
    public Team setName(String name) {
        this.name = name;
        return this;
    }
    public boolean getActive() { return active; }
    public Team setActive(boolean active) {
        this.active = active;
        return this;
    }
    public RealmList<Player> getPlayers() { return players; }
    public Team setPlayers(RealmList<Player> players) {
        this.players = players;
        return this;
    }
    public Team addPlayer(Player player) {
        players.add(player);
        return this;
    }

    public RealmList<Results> getGames() { return games; }
    public Team addGame(Results game) {
        games.add(game);
        return this;
    }
    public int getWins() {
        return wins;
    }
    public void setWins(int wins) {
        this.wins = wins;
    }
    public float getAvgPoints() {
        return avgPoints;
    }
    public void setAvgPoints(float avgPoints) {
        this.avgPoints = avgPoints;
    }
}
