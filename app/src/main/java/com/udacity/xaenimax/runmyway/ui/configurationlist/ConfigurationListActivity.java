package com.udacity.xaenimax.runmyway.ui.configurationlist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.model.entity.Configuration;
import com.udacity.xaenimax.runmyway.viewmodel.ConfigurationListViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfigurationListActivity extends AppCompatActivity {
    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";

    @BindView(R.id.configuration_list_rv)
    RecyclerView configurationList;

    private RecyclerView.LayoutManager mLayoutManager;
    private Parcelable mLayoutParcelable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_step_list);
        ButterKnife.bind(this);

        mLayoutManager = new LinearLayoutManager(this);
        if(savedInstanceState != null && savedInstanceState.containsKey(RECYCLER_VIEW_STATE)){
            mLayoutParcelable = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
            mLayoutManager.onRestoreInstanceState(mLayoutParcelable);
        }

        configurationList.setLayoutManager(mLayoutManager);

        setupViewModel();
    }

    private void setupViewModel() {
        ConfigurationListViewModel configurationListViewModel = ViewModelProviders.of(this).get(ConfigurationListViewModel.class);
        configurationListViewModel.getConfigurationList().observe(this, new Observer<List<Configuration>>() {
            @Override
            public void onChanged(@Nullable List<Configuration> configurations) {
                updateUI(configurations);
            }
        });
    }

    private void updateUI(List<Configuration> configurations) {
        ConfigurationListAdapter adapter = new ConfigurationListAdapter(configurations);
        configurationList.swapAdapter(adapter, true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mLayoutParcelable = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(RECYCLER_VIEW_STATE, mLayoutParcelable);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey(RECYCLER_VIEW_STATE)) {
            mLayoutParcelable = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
            mLayoutManager.onRestoreInstanceState(mLayoutParcelable);
        }
    }
}
