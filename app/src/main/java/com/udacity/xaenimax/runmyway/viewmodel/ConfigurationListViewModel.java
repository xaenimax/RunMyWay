package com.udacity.xaenimax.runmyway.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.udacity.xaenimax.runmyway.model.RunMyWayRepository;
import com.udacity.xaenimax.runmyway.model.dao.AppDatabase;
import com.udacity.xaenimax.runmyway.model.dao.AppExecutor;
import com.udacity.xaenimax.runmyway.model.entity.Configuration;

import java.util.List;

public class ConfigurationListViewModel extends AndroidViewModel {
    private static final String LOG_TAG = ConfigurationListViewModel.class.getSimpleName();

    public LiveData<List<Configuration>> getConfigurationList() {
        return mConfiguration;
    }

    private LiveData<List<Configuration>> mConfiguration;


    public ConfigurationListViewModel(@NonNull Application application){//, RunMyWayRepository repository) {
        super(application);
        AppDatabase appDatabase =  AppDatabase.getInstance(this.getApplication());
        RunMyWayRepository myWayRepository = RunMyWayRepository.getInstance(appDatabase.configurationDao(),
                appDatabase.runSessionDao(),
                AppExecutor.getInstance(),
                appDatabase.configurationStepDao());
        mConfiguration = myWayRepository.getConfigurations();
    }

}
