package com.smiler.basketball_scoreboard.elements.lists;

public interface ListListener {
    void onListElementClick(int value);
    void onListElementLongClick(int value);
    void onListEmpty();
}
