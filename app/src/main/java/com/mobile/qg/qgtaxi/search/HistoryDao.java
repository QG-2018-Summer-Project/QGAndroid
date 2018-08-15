package com.mobile.qg.qgtaxi.search;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.ArrayList;

import io.reactivex.Flowable;

/**
 * Created by 11234 on 2018/8/15.
 */
@Dao
public interface HistoryDao {

    @Query("SELECT * FROM history")
    ArrayList<History> getALl();

    @Query("SELECT * FROM history")
    Flowable<ArrayList<History>> getFlowable();

    @Insert
    void insert(History... tips);

    @Delete
    void delete(History tip);

}
