package com.dciets.cumets.listener;

/**
 * Created by marc-antoinehinse on 15-10-12.
 */
public interface MonitorListener {
    void onMonitorSuccessful(final String facebookId);
    void onMonitorError();
    void onUnmonitorSuccessful(final String facebookId);
    void onUnmonitorError();
}
