package com.mobile.qg.qgtaxi.history;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by 11234 on 2018/8/15.
 * Dao
 */
@Dao
public interface HistoryDao {

    @Query("SELECT * FROM history")
    Flowable<List<History>> getFlowable();

    @Insert
    void insert(History... histories);

    @Delete
    void delete(History history);

}
