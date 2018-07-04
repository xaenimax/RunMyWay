package com.udacity.xaenimax.runmyway.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.udacity.xaenimax.runmyway.model.RunSession;

import java.util.List;

@Dao
public interface RunSessionDao {
    @Query("SELECT * FROM run_session ORDER BY session_date DESC")
    LiveData<List<RunSession>> listAllRunSessions();

    @Query("SELECT * FROM run_session ORDER BY session_date DESC LIMIT 1")
    LiveData<RunSession> getLastRunSession();

    @Insert
    void insertNewRunSession(RunSession runSession);

    @Update
    void updateRunSession(RunSession runSession);

    @Delete
    void deleteRunSession(RunSession runSession);
}
