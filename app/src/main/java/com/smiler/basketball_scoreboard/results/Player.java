package com.smiler.basketball_scoreboard.results;

public class Player {

    private String name;
    private int points;
//    private int team;
    private int number;
    private int fouls;
    private boolean captain;

//    Player(int number, String name, int team, int points, int fouls, boolean captain) {
    Player(int number, String name, int points, int fouls, boolean captain) {
        this.number = number;
        this.name = name;
        this.points = points;
        this.fouls = fouls;
        this.captain = captain;
    }

    public int getNumber() {
        return this.number;
    }

//    public int getTeam() {
//        return this.team;
//    }

    public String getName() {
        return this.name;
    }

    public int getPoints() {
        return this.points;
    }

    public int getFouls() {
        return this.fouls;
    }

    public boolean isCaptain() {
        return this.captain;
    }
}