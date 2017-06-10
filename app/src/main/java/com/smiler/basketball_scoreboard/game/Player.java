package com.smiler.basketball_scoreboard.game;

@SuppressWarnings("UnnecessaryThis")
public class Player {

    private String name;
    private int points;
    private int number;
    private int fouls;
    private boolean captain;

    public Player() {
    }

    public Player(int number, String name, int points, int fouls, boolean captain) {
        this.number = number;
        this.name = name;
        this.points = points;
        this.fouls = fouls;
        this.captain = captain;
    }

    public void setInfo(int number, String name, boolean captain) {
        this.number = number;
        this.name = name;
        this.captain = captain;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int value) {
        number = value;
    }

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

}