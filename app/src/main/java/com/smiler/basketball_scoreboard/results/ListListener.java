package com.smiler.basketball_scoreboard.results;

public interface ListListener {
    void onListElementClick(int value);
    void onListElementLongClick(int value);
    void onListEmpty();
}
