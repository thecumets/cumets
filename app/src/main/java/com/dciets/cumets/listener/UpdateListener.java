package com.dciets.cumets.listener;

import com.dciets.cumets.model.UpdateInfo;

import java.util.ArrayList;

/**
 * Created by marc-antoinehinse on 15-10-13.
 */
public interface UpdateListener {
    void onNewUpdateInformation(final UpdateInfo update);
    void onError();
}
