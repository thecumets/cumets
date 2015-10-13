package com.dciets.cumets.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesController {
    private static final String PREF_NAME = "cumets";

    public static final String TOKEN = "USER_PREF_TOKEN";
    public static final String ALERT_DISTANCE = "USER_PREF_ALERT_DISTANCE";

    public static void setPreference(final Context context, final String preference, final String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(preference, value);
        editor.commit();
    }

    public static String getPreference(final Context context, final String preference) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String restoredText = prefs.getString(preference, null);
        return restoredText;
    }

    public static int getIntPreference(final Context context, final String preference) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int restoredText = prefs.getInt(preference, -1);
        return restoredText;
    }
}