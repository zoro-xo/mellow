package com.looksmaxxing.mellow.Activities.Classroom;

public interface SetClickListner {
    void onSetClicked(String setId, String name, String moduleId, boolean watched);
    void onSetItemClicked(String setId, String itemId);

}
