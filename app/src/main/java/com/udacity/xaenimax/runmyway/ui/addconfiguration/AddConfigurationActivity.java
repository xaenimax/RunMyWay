package com.udacity.xaenimax.runmyway.ui.addconfiguration;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.utils.InjectorUtils;
import com.udacity.xaenimax.runmyway.viewmodel.AddConfigurationViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddConfigurationActivity extends AppCompatActivity {
    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";
    //default id for new configuration
    private static final int DEFAULT_CONFIGURATION_ID = -1;
    //handle rotation
    private static final String CONFIGURATION_ID = "extraTaskId";


    @BindView(R.id.run_step_rv)
    RecyclerView configurationStepRecyclerView;

    private RecyclerView.LayoutManager mLayoutManager;
    private Parcelable mLayoutParcelable;
    private int mConfigurationId = DEFAULT_CONFIGURATION_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_configuration);
        ButterKnife.bind(this);

        mLayoutManager = new LinearLayoutManager(this);
        if(savedInstanceState != null && savedInstanceState.containsKey(RECYCLER_VIEW_STATE)){
            mLayoutParcelable = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
            mLayoutManager.onRestoreInstanceState(mLayoutParcelable);
        }

        configurationStepRecyclerView.setLayoutManager(mLayoutManager);

        if (savedInstanceState != null && savedInstanceState.containsKey(CONFIGURATION_ID)) {
            mConfigurationId = savedInstanceState.getInt(CONFIGURATION_ID, DEFAULT_CONFIGURATION_ID);
        }
        if (mConfigurationId != DEFAULT_CONFIGURATION_ID) {
            // Retrieve data

            final AddConfigurationViewModel viewModel
                    = ViewModelProviders.of(this).get(AddConfigurationViewModel.class);

            // COMPLETED (12) Observe the LiveData object in the ViewModel. Use it also when removing the observer
            viewModel.getTask().observe(this, new Observer<TaskEntry>() {
                @Override
                public void onChanged(@Nullable TaskEntry taskEntry) {
                    viewModel.getTask().removeObserver(this);
                    populateUI(taskEntry);
                }
            });
        }


        setupViewModel();
    }

    private void setupViewModel() {

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
