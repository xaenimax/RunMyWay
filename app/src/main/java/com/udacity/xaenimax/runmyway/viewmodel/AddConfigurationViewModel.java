package com.udacity.xaenimax.runmyway.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.udacity.xaenimax.runmyway.model.entity.Configuration;
import com.udacity.xaenimax.runmyway.model.dao.AppDatabase;

public class AddConfigurationViewModel extends AndroidViewModel{

    private Configuration mConfiguration;

    public AddConfigurationViewModel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getInstance(application);
    }

    public Configuration getConfigurationStep() {
        return mConfiguration;
    }

}
