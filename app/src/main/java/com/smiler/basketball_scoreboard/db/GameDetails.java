package com.smiler.basketball_scoreboard.db;

import io.realm.RealmObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class GameDetails extends RealmObject {
    private String playByPlay;
    private int leadChanged;
    private int homeMaxLead;
    private int guestMaxLead;
    private int tie;
}
