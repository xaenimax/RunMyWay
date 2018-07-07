package com.udacity.xaenimax.runmyway.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.model.RunSession;
import com.udacity.xaenimax.runmyway.model.dao.AppDatabase;
import com.udacity.xaenimax.runmyway.viewmodel.MainViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private AppDatabase mAppDatabase;

    @BindView(R.id.today_achievement_distance_tv)
    public TextView distanceTextView;
    @BindView(R.id.today_achievement_calory_tv)
    public TextView caloriesTextView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getRunSession().observe(this, new Observer<RunSession>() {
            @Override
            public void onChanged(@Nullable RunSession runSession) {
                //TODO setup UI
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppDatabase = AppDatabase.getInstance(getActivity());

    }

    public void setUserInfo(float distance, long Kcal) {
        caloriesTextView.setText(String.format(getString(R.string.calories), Kcal));
        distanceTextView.setText(String.format(getString(R.string.distance), distance));

    }
}
