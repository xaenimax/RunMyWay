package com.udacity.xaenimax.runmyway.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.udacity.xaenimax.runmyway.model.RunSession;
import com.udacity.xaenimax.runmyway.model.dao.AppDatabase;

public class MainViewModel extends AndroidViewModel {

    public LiveData<RunSession> getRunSession() {
        return mRunSession;
    }

    private LiveData<RunSession> mRunSession;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRunSession = AppDatabase.getInstance(this.getApplication()).runSessionDao().getLastRunSession();
    }
}
