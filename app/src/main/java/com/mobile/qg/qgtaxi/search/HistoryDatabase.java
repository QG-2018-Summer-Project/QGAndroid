package com.mobile.qg.qgtaxi.search;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by 11234 on 2018/8/15.
 */
@Database(entities = {History.class}, version = 1)
public abstract class HistoryDatabase extends RoomDatabase {

    private static HistoryDatabase sInstance;
    private static final Object LOCK = new Object();

    public abstract HistoryDao historyDao();

    public static HistoryDatabase getInstance(Context context) {

        synchronized (LOCK) {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(), HistoryDatabase.class, "history.db").build();
            }
            return sInstance;
        }

    }

}
