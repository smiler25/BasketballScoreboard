package com.smiler.basketball_scoreboard.models;

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
        return this.number;
    }

    public void setNumber(int value) {
        this.number = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public int getPoints() {
        return this.points;
    }

    public int changePoints(int value) {
        this.points += value;
        return this.points;
    }

    public int getFouls() {
        return this.fouls;
    }

    public int changeFouls(int value) {
        this.fouls += value;
        return this.fouls;
    }

    public boolean isCaptain() {
        return this.captain;
    }

    public void setCaptain(boolean value) {
        this.captain = value;
    }

    public void clear() {
        this.points = 0;
        this.fouls = 0;
    }

}