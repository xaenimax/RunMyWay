package com.udacity.xaenimax.runmyway.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.udacity.xaenimax.runmyway.model.RunMyWayRepository;
import com.udacity.xaenimax.runmyway.model.dao.AppExecutor;
import com.udacity.xaenimax.runmyway.model.entity.RunSession;
import com.udacity.xaenimax.runmyway.model.dao.AppDatabase;

public class MainViewModel extends AndroidViewModel {
    private static final String LOG_TAG = MainViewModel.class.getSimpleName();

    public LiveData<RunSession> getRunSession() {
        return mRunSession;
    }

    private LiveData<RunSession> mRunSession;


    public MainViewModel(@NonNull Application application){//, RunMyWayRepository repository) {
        super(application);
        AppDatabase appDatabase =  AppDatabase.getInstance(this.getApplication());
        Log.d(LOG_TAG, "Retrieving last run session from Room DB");
        RunMyWayRepository myWayRepository = RunMyWayRepository.getInstance(appDatabase.configurationDao(),
                appDatabase.runSessionDao(),
                AppExecutor.getInstance(),
                appDatabase.configurationStepDao());
        mRunSession = myWayRepository.getLastRunSession();
    }
}
