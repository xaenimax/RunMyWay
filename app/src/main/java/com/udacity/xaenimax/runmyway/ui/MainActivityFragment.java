package com.udacity.xaenimax.runmyway.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.model.Configuration;
import com.udacity.xaenimax.runmyway.model.dao.AppDatabase;
import com.udacity.xaenimax.runmyway.model.dao.AppExecutor;

import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private AppDatabase mAppDatabase;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppDatabase = AppDatabase.getInstance(getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();

        LiveData<List<Configuration>> configurations = mAppDatabase.configurationDao().listAllConfigurations();
        configurations.observe(this, new Observer<List<Configuration>>() {
            @Override
            public void onChanged(@Nullable List<Configuration> configurations) {

            }
        });
    }
}
