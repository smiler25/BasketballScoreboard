package com.smiler.basketball_scoreboard.db;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Results extends RealmObject {
    @PrimaryKey
    private int id;
    private Date date;
    private String firstTeamName;
    private String secondTeamName;
    private int firstScore;
    private int secondScore;
    private String firstPeriods;
    private String secondPeriods;
    private String shareString;
    private int regularPeriods;
    private boolean complete;
    private GameDetails details;
    private Team firstTeam;
    private Team secondTeam;
}
