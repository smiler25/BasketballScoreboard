package com.smiler.basketball_scoreboard.elements.lists;

public interface ExpandableListListener {
    void onExpListItemSelected();
    void onExpListItemDeleted(boolean empty);
    void onListEmpty();

}
