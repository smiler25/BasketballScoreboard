package com.smiler.basketball_scoreboard.db;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
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

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void addGame(Results game) {
        games.add(game);
    }

    public void incrementWins() {
        wins++;
    }

    public void incrementLoses() {
        loses++;
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

    public Player getCaptain() {
        if (players.size() == 0) {
            return null;
        }
        for (Player player : players) {
            if (player.isCaptain()) {
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
            if (player.isCaptain()) {
                player.setCaptain(false);
            }
        }
    }
}
