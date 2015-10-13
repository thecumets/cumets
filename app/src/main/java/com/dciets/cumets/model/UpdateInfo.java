package com.dciets.cumets.model;

/**
 * Created by marc-antoinehinse on 15-10-13.
 */
public class UpdateInfo {
    private String facebookId;
    private double distance;

    public UpdateInfo(String facebookId, double distance) {
        this.facebookId = facebookId;
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public String getFacebookId() {
        return facebookId;
    }
}
