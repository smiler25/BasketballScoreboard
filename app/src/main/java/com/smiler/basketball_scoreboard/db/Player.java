package com.smiler.basketball_scoreboard.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Player extends RealmObject {
    @PrimaryKey
    private int id;
    private Team team;
    @Required
    private String name;
    private int number;
    private boolean captain;
}
