package com.mobile.qg.qgtaxi.history;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by 11234 on 2018/8/15.
 * 数据库
 */
@Database(entities = History.class, version = 1, exportSchema = false)
public abstract class HistoryDatabase extends RoomDatabase {

    public abstract HistoryDao historyDao();

}
