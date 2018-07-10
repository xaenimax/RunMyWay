package com.udacity.xaenimax.runmyway.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.udacity.xaenimax.runmyway.model.RunMyWayRepository;
import com.udacity.xaenimax.runmyway.model.entity.Configuration;
import com.udacity.xaenimax.runmyway.model.dao.AppDatabase;
import com.udacity.xaenimax.runmyway.model.entity.ConfigurationStep;
import com.udacity.xaenimax.runmyway.utils.InjectorUtils;

import java.util.List;

public class AddConfigurationViewModel extends AndroidViewModel {

    private Configuration mConfiguration;

    public AddConfigurationViewModel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getInstance(application);
    }

    public Configuration getConfiguration() {
        return mConfiguration;
    }

    public LiveData<List<ConfigurationStep>>getConfugurationSteps(){

    }

}
