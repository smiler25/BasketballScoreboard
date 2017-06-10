package com.smiler.basketball_scoreboard.profiles;

import com.smiler.basketball_scoreboard.db.Results;
import com.smiler.basketball_scoreboard.db.Team;

import io.realm.RealmList;

public class TeamStats {

    private int totalGames, wins;
    private float avgPoints, avgPointsOpponent;
    private RealmList<Results> games;

    public TeamStats(Team team) {
        games = team.getGames();
        calcStats();
    }

    private void calcStats() {
        totalGames = games.size();
        wins = totalGames;
        avgPoints = 0;
        avgPointsOpponent = 0;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public int getWins() {
        return wins;
    }
    public float getAvgPoints() {
        return avgPoints;
    }
    public float getAvgPointsOpponent() {
        return avgPointsOpponent;
    }
}