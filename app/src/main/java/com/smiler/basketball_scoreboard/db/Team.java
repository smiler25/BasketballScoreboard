package com.smiler.basketball_scoreboard.db;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
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
    private float avgPointsOpp;
    private int wins;
    private int loses;

    @Ignore
    private ArrayList<Player> gamePlayers;

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
    public void incrementWins() {
        wins++;
    }

    public int getLoses() {
        return loses;
    }
    public void incrementLoses() {
        loses++;
    }

    public float getAvgPoints() {
        return avgPoints;
    }
    public void calcAvgPoints(int score, int scoreOpp) {
        if (games.size() == 0) {
            avgPoints = score;
            avgPointsOpp = scoreOpp;
            return;
        }
        int total = 0, totalOpp = 0;
        for (Results game : games) {
            if (this.equals(game.getFirstTeam())) {
                total += game.getFirstScore();
                totalOpp += game.getSecondScore();
            } else {
                total += game.getSecondScore();
                totalOpp += game.getFirstScore();
            }
        }
        avgPoints = total / games.size();
        avgPointsOpp = totalOpp / games.size();
    }

    public float getAvgPointsOpp() {
        return avgPointsOpp;
    }

    public void setGamePlayers(ArrayList<Player> players) {
        gamePlayers = players;
    }
    public ArrayList<Player> getGamePlayers() {
        return gamePlayers;
    }

    public Player getCaptain() {
        if (players.size() == 0) {
            return null;
        }
        for (Player player : players) {
            if (player.getCaptain()) {
                return player;
            }
        }
        return null;
    }

    public void cancelCaptain() {
        if (players.size() == 0) {
            return;
        }
        for (Player player : players) {
            if (player.getCaptain()) {
                player.setCaptain(false);
            }
        }
    }
}
