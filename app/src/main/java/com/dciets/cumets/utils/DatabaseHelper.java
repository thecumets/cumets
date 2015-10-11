package com.dciets.cumets.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Message;
import android.util.Log;

import com.dciets.cumets.model.Roommate;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static DatabaseHelper instance;

    private DatabaseHelper(Context context) {
        super(context, "db", null, 1);
    }

    public static void init(final Context context) {
        if (instance != null) {
            return;
        }
        instance = new DatabaseHelper(context);
    }

    public static DatabaseHelper getInstance() {
        return instance;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE roommates (id INTEGER PRIMARY KEY AUTOINCREMENT, facebook_id TEXT, monitor INTEGER DEFAULT 0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE if exists roommates");
        onCreate(db);
    }

    public void addRoommate(String facebookId) {
        if(isUserAlreadyInDB(facebookId)) {
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv = new ContentValues();
        cv.put("facebook_id", facebookId);

        db.insert("roommates", null, cv);
    }

    public void updateMonitor(String facebookId, boolean isMonitor) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("monitor", isMonitor ? 1 : 0);
        db.update("roommates", cv, "facebook_id = ?", new String[]{facebookId});
    }

    public boolean isUserAlreadyInDB(final String facebookId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT COUNT(*) FROM roommates WHERE facebook_id = ?;", new String[]{facebookId});

        while (c.moveToNext()) {
            return c.getInt(0) > 0;
        }

        c.close();

        return false;
    }

    public boolean isMonitor(String facebookId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT monitor FROM roommates WHERE facebook_id = ?;", new String[]{facebookId});

        while (c.moveToNext()) {
            return c.getInt(0) == 1;
        }

        c.close();

        return false;
    }
}