package com.udacity.xaenimax.runmyway.model;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.udacity.xaenimax.runmyway.model.dao.AppExecutor;
import com.udacity.xaenimax.runmyway.model.dao.ConfigurationDao;
import com.udacity.xaenimax.runmyway.model.dao.ConfigurationStepDao;
import com.udacity.xaenimax.runmyway.model.dao.RunSessionDao;
import com.udacity.xaenimax.runmyway.model.entity.Configuration;
import com.udacity.xaenimax.runmyway.model.entity.ConfigurationStep;
import com.udacity.xaenimax.runmyway.model.entity.RunSession;

import java.util.List;

public class RunMyWayRepository {

    private static final String LOG_TAG = RunMyWayRepository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static RunMyWayRepository sInstance;
    private final ConfigurationDao mConfigurationDao;
    private final RunSessionDao mRunSessionDao;
    private final ConfigurationStepDao mConfigurationStepDao;
    private final AppExecutor mExecutors;

    private RunMyWayRepository(ConfigurationDao configurationDao,
                              RunSessionDao runSessionDao,
                              AppExecutor appExecutor,
                              ConfigurationStepDao configurationStepDao) {
        this.mConfigurationDao = configurationDao;
        this.mRunSessionDao = runSessionDao;
        this.mConfigurationStepDao = configurationStepDao;
        this.mExecutors = appExecutor;
    }

    public static RunMyWayRepository getInstance(ConfigurationDao configurationDao,
                                                 RunSessionDao runSessionDao,
                                                 AppExecutor appExecutor,
                                                 ConfigurationStepDao configurationStepDao) {
        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new RunMyWayRepository(configurationDao,
                        runSessionDao,
                        appExecutor,
                        configurationStepDao);
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    /**
     * Database related operations
     **/

    public LiveData<RunSession> getLastRunSession() {
        return mRunSessionDao.getLastRunSession();
    }

    public LiveData<List<ConfigurationStep>> getConfigurationStepsByConfigurationId(int configurationId) {
        return mConfigurationStepDao.listAllConfigurationSteps(configurationId);
    }

    public void insertNewConfiguration(final Configuration newConfiguration, final OnInsertEndedListener listener){
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                long id =  mConfigurationDao.insertConfiguration(newConfiguration);
                if(listener != null){
                    listener.OnInsertEnded(id);
                }
            }
        });

    }

    public void insertNewConfigurationStep(final List<ConfigurationStep> newConfigurationSteps){
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mConfigurationStepDao.insertAll(newConfigurationSteps);

            }
        });
    }

    public interface OnInsertEndedListener{
        void OnInsertEnded(long idInserted);
    }
/**
    public void insertNewConfiguration(Configuration configuration){
        return mConfigurationDao.insertConfiguration(configu);
    }


     * Deletes old weather data because we don't need to keep multiple days' data

    private void deleteOldData() {
        Date today = SunshineDateUtils.getNormalizedUtcDateForToday();
        mWeatherDao.deleteOldWeather(today);
    }
     */

}
