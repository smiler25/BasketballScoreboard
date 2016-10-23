package com.smiler.basketball_scoreboard.results;

public interface ResultsListListener {
    void onListItemSelected(int itemId);
    void onListItemDeleted(boolean empty);
    void onListEmpty();
}
