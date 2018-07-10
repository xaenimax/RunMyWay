package com.udacity.xaenimax.runmyway.utils;

import android.content.Context;

import com.udacity.xaenimax.runmyway.model.RunMyWayRepository;
import com.udacity.xaenimax.runmyway.model.dao.AppDatabase;
import com.udacity.xaenimax.runmyway.model.dao.AppExecutor;
import com.udacity.xaenimax.runmyway.viewmodel.MainViewModel;

/**
 * Provides static methods to inject the various classes needed for Sunshine
 */
public class InjectorUtils {

    public static RunMyWayRepository provideRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context.getApplicationContext());
        AppExecutor executors = AppExecutor.getInstance();

        return RunMyWayRepository.getInstance(database.configurationDao(),
                database.runSessionDao(),
                executors,
                database.configurationStepDao());
    }

    public static AddConfigurationViewFactory provideAddConfigurationViewModelFactory(Context context, int configurationId) {
        RunMyWayRepository repository = provideRepository(context.getApplicationContext());
        return new AddConfigurationViewFactory(repository, configurationId);
    }

    public static MainViewModel provideMainActivityViewModel(Context context) {
        SunshineRepository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository);
    }

}
