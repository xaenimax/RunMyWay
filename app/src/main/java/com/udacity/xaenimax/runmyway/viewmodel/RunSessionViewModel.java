package com.udacity.xaenimax.runmyway.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.udacity.xaenimax.runmyway.model.RunMyWayRepository;
import com.udacity.xaenimax.runmyway.model.dao.AppDatabase;
import com.udacity.xaenimax.runmyway.model.entity.ConfigurationStep;

import java.util.List;

class RunSessionViewModel extends ViewModel {

    private final LiveData<List<ConfigurationStep>> mConfigurationSteps;
    private final RunMyWayRepository mRunMyWayRepository;
    private final long mConfigurationId;

    public RunSessionViewModel(RunMyWayRepository myWayRepository, long configurationId) {
        mRunMyWayRepository = myWayRepository;
        mConfigurationId = configurationId;
        mConfigurationSteps = mRunMyWayRepository.getConfigurationStepsByConfigurationId(mConfigurationId);
    }

    public LiveData<List<ConfigurationStep>> getConfigurationSteps() {
        return mConfigurationSteps;
    }
}
