package com.smiler.basketball_scoreboard.db;

import io.realm.RealmObject;

public class PlayersResults extends RealmObject {
    private Results game;
    private String player_team;
    private int player_number;
    private String player_name;
    private int player_points;
    private int player_fouls;
    private boolean captain;

    public Results getGame() { return game; }
    public PlayersResults setGame(Results value) {
        game = value;
        return this;
    }
    public String getPlayerTeam() { return player_team; }
    public PlayersResults setPlayerTeam(String value) {
        player_team = value;
        return this;
    }
    public int getPlayerNumber() { return player_number; }
    public PlayersResults setPlayerNumber(int value) {
        player_number = value;
        return this;
    }
    public String getPlayerName() { return player_name; }
    public PlayersResults setPlayerName(String value) {
        player_name = value;
        return this;
    }
    public int getPlayerPoints() { return player_points; }
    public PlayersResults setPlayerPoints(int value) {
        player_points = value;
        return this;
    }
    public int getPlayerFouls() { return player_fouls; }
    public PlayersResults setPlayerFouls(int value) {
        player_fouls = value;
        return this;
    }
    public boolean getCaptain() { return captain; }
    public PlayersResults setCaptain(boolean value) {
        captain = value;
        return this;
    }

}
