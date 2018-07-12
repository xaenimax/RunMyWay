package com.udacity.xaenimax.runmyway.ui.addconfiguration;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.model.entity.Configuration;
import com.udacity.xaenimax.runmyway.model.entity.ConfigurationStep;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddConfigurationActivity extends AppCompatActivity {
    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";
    //default id for new configuration
    private static final int DEFAULT_CONFIGURATION_ID = -1;
    //handle rotation
    private static final String CONFIGURATION_OBJECT = "configuration_object";
    private static final String CONFIGURATION_STEP = "configuration_step";

    @BindView(R.id.add_button)
    Button addButton;
    @BindView(R.id.run_step_rv)
    RecyclerView configurationStepRecyclerView;
    @BindView(R.id.minutes_et)
    EditText minuteValueEditText;
    @BindView(R.id.step_spinner)
    Spinner stepSpinner;


    private RecyclerView.LayoutManager mLayoutManager;
    private Parcelable mLayoutParcelable;
    private Configuration mConfiguration;
    private ArrayList<ConfigurationStep> mConfigurationSteps = new ArrayList<>();
    private ConfigurationStepAdapter mConfigurationStepAdapter;

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

        if (savedInstanceState != null && savedInstanceState.containsKey(CONFIGURATION_OBJECT)) {
            mConfiguration = savedInstanceState.getParcelable(CONFIGURATION_OBJECT);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(CONFIGURATION_OBJECT)) {
            mConfigurationSteps = savedInstanceState.getParcelableArrayList(CONFIGURATION_STEP);
        }
        mConfigurationStepAdapter = new ConfigurationStepAdapter(mConfigurationSteps);
        configurationStepRecyclerView.setAdapter(mConfigurationStepAdapter);

        setupListeners();
    }

    private void setupListeners() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable text = minuteValueEditText.getText();
                    if(text != null && !text.toString().equals("")){
                    String stepType = (String) stepSpinner.getSelectedItem();
                    int minutes = Integer.parseInt(minuteValueEditText.getText().toString());
                    ConfigurationStep step = new ConfigurationStep(minutes, stepType);
                    mConfigurationSteps.add(step);
                    minuteValueEditText.setText("");
                    mConfigurationStepAdapter = new ConfigurationStepAdapter(mConfigurationSteps);
                    configurationStepRecyclerView.swapAdapter(mConfigurationStepAdapter, true);

                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mLayoutParcelable = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(RECYCLER_VIEW_STATE, mLayoutParcelable);
        outState.putParcelable(CONFIGURATION_OBJECT, mConfiguration);
        outState.putParcelableArrayList(CONFIGURATION_OBJECT, mConfigurationSteps);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey(RECYCLER_VIEW_STATE)) {
            mLayoutParcelable = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
            mLayoutManager.onRestoreInstanceState(mLayoutParcelable);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(CONFIGURATION_OBJECT)) {
            mConfiguration = savedInstanceState.getParcelable(CONFIGURATION_OBJECT);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(CONFIGURATION_OBJECT)) {
            mConfigurationSteps = savedInstanceState.getParcelableArrayList(CONFIGURATION_STEP);
        }

    }
}
