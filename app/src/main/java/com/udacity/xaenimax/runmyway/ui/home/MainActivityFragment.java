package com.udacity.xaenimax.runmyway.ui.home;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.model.RunSession;
import com.udacity.xaenimax.runmyway.viewmodel.MainViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    @BindView(R.id.today_achievement_distance_tv)
    public TextView distanceTextView;
    @BindView(R.id.today_achievement_calory_tv)
    public TextView caloriesTextView;
    @BindView(R.id.last_session_recap_tv)
    public TextView lastSessionTextView;

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

        setupViewModel();
    }

    private void setupViewModel() {
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getRunSession().observe(this, new Observer<RunSession>() {
            @Override
            public void onChanged(@Nullable RunSession runSession) {
                Log.d(LOG_TAG, "Updating last run session from LiveData in View Model");
                if(runSession == null){
                    lastSessionTextView.setText(getString(R.string.no_run_session));
                }else {
                    //TODO finish setup ui
                    lastSessionTextView.setText(""+runSession.sessionDate + ": km cak e distance");
                }
            }
        });
    }

    public void setUserInfo(float distance, long Kcal) {
        caloriesTextView.setText(String.format(getString(R.string.calories), Kcal));
        distanceTextView.setText(String.format(getString(R.string.distance), distance));
    }
}
