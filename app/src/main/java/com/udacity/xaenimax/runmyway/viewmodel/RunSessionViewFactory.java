package com.udacity.xaenimax.runmyway.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.udacity.xaenimax.runmyway.model.RunMyWayRepository;

public class RunSessionViewFactory extends ViewModelProvider.NewInstanceFactory {

    private final RunMyWayRepository mRepository;
    private final long mConfigurationId;

    public RunSessionViewFactory(RunMyWayRepository repository, long configurationStepId){
        mRepository = repository;
        mConfigurationId = configurationStepId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new RunSessionViewModel(mRepository, mConfigurationId);
    }

}
