package com.smiler.basketball_scoreboard.results;

public interface ResultsExpListListener {
    void onExpListItemSelected();
    void onExpListItemDeleted(boolean empty);
    void onListEmpty();

}
