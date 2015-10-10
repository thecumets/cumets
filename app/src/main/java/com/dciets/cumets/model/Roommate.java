package com.dciets.cumets.model;

/**
 * Created by marc-antoinehinse on 2015-10-10.
 */
public class Roommate {
    private String name;
    private String profileId;
    private String profilePictureUrl;

    public Roommate(String name, String profileId, String profilePictureUrl) {
        this.name = name;
        this.profileId = profileId;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getName() {
        return name;
    }

    public String getProfileId() {
        return profileId;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}
