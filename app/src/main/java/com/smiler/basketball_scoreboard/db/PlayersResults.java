package com.smiler.basketball_scoreboard.db;

import io.realm.RealmObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class PlayersResults extends RealmObject {
    private int id;
    private Results game;
    private String team;
    private int number;
    private String name;
    private int points;
    private int fouls;
    private boolean captain;
    private boolean active;
    private long timePlayed;
}
