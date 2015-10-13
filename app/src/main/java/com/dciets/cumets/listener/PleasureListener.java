package com.dciets.cumets.listener;

/**
 * Created by marc-antoinehinse on 15-10-12.
 */
public interface PleasureListener {
    void onPleasureStartedSuccessful();
    void onPleasureStartedError();
    void onPleasureStoppedSuccessful();
    void onPleasureStoppedError();
}
