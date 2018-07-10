package com.udacity.xaenimax.runmyway.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.udacity.xaenimax.runmyway.model.RunMyWayRepository;
import com.udacity.xaenimax.runmyway.model.dao.AppDatabase;

public class AddConfigurationStepViewFactory extends ViewModelProvider.NewInstanceFactory {
    /*
    private final AppDatabase mAppDatabase;
    private final int mConfigurationId;

    public AddConfigurationStepViewFactory(RunMyWayRepository repository, int configurationStepId){
        //mAppDatabase = database;
        mConfigurationId = configurationStepId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddConfigurationStepViewModel(mAppDatabase, mConfigurationId);
    }
    */
}
