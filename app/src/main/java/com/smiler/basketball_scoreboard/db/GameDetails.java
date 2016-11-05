package com.smiler.basketball_scoreboard.db;

import io.realm.RealmObject;

public class GameDetails extends RealmObject {

    private String play_by_play;
    private int lead_changed;
    private int home_max_lead;
    private int guest_max_lead;
    private int tie;

//    public Results getGame() { return game; }
//    public GameDetails setGame(Results value) {
//        game = value;
//        return this;
//    }
    public String getPlayByPlay() { return play_by_play; }
    public GameDetails setPlayByPlay(String value) {
        play_by_play = value;
        return this;
    }
    public int getLeadChanged() { return lead_changed; }
    public GameDetails setLeadChanged(int value) {
        lead_changed = value;
        return this;
    }
    public int getHomeMaxLead() { return home_max_lead; }
    public GameDetails setHomeMaxLead(int value) {
        home_max_lead = value;
        return this;
    }
    public int getGuestMaxLead() { return guest_max_lead; }
    public GameDetails setGuestMaxLead(int value) {
        guest_max_lead = value;
        return this;
    }
    public int getTie() { return tie; }
    public GameDetails setTie(int value) {
        tie = value;
        return this;
    }

}
