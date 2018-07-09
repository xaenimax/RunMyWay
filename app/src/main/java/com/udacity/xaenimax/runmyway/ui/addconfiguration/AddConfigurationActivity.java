package com.udacity.xaenimax.runmyway.ui.addconfiguration;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.udacity.xaenimax.runmyway.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddConfigurationActivity extends AppCompatActivity {
    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";

    @BindView(R.id.run_step_rv)
    RecyclerView configurationStepRecyclerView;

    private RecyclerView.LayoutManager mLayoutManager;
    private Parcelable mLayoutParcelable;

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
