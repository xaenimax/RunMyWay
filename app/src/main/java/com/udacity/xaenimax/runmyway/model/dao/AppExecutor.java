package com.udacity.xaenimax.runmyway.model.dao;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutor {
    private static final Object LOCK = new Object();
    private static AppExecutor mAppExecutor ;
    private final Executor diskIO;
    private final Executor mainThread;
    private final Executor networkIO;

    public AppExecutor(Executor diskIO, Executor mainThread, Executor networkIO) {
        this.diskIO = diskIO;
        this.mainThread = mainThread;
        this.networkIO = networkIO;
    }

    public static AppExecutor getInstance(){
        if(mAppExecutor == null){
            synchronized (LOCK){
                mAppExecutor = new AppExecutor(Executors.newSingleThreadExecutor(),
                        new MainThreadExecutor(),
                        Executors.newFixedThreadPool(3));
            }
        }
        return mAppExecutor;
    }

    public Executor diskIO() {
        return this.diskIO;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
