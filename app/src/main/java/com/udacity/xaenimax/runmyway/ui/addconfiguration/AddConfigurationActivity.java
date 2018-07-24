package com.udacity.xaenimax.runmyway.ui.addconfiguration;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.model.RunMyWayRepository;
import com.udacity.xaenimax.runmyway.model.entity.Configuration;
import com.udacity.xaenimax.runmyway.model.entity.ConfigurationStep;
import com.udacity.xaenimax.runmyway.model.entity.RunSession;
import com.udacity.xaenimax.runmyway.ui.runsession.RunSessionActivity;
import com.udacity.xaenimax.runmyway.utils.InjectorUtils;
import com.udacity.xaenimax.runmyway.viewmodel.AddConfigurationViewModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.xaenimax.runmyway.ui.runsession.RunSessionActivity.CONFIGURATION_ID_EXTRA;

public class AddConfigurationActivity extends AppCompatActivity implements SaveConfigurationDialogFragment.SaveConfigListener{
    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";
    //handle rotation
    private static final String CONFIGURATION_OBJECT = "configuration_object";
    private static final String CONFIGURATION_STEP = "configuration_step";
    private static final String TOTAL_TIME = "total_time";
    private static final String TOTAL_TIME_RUNNING = "total_time_running";
    private static final String TOTAL_TIME_WALKING = "total_time_walking";

    @BindView(R.id.add_button)
    Button addButton;
    @BindView(R.id.confirm_button)
    Button confirmButton;
    @BindView(R.id.run_step_rv)
    RecyclerView configurationStepRecyclerView;
    @BindView(R.id.minutes_et)
    EditText minuteValueEditText;
    @BindView(R.id.step_spinner)
    Spinner stepSpinner;
    @BindView(R.id.total_time_tv)
    TextView totalTimeTextView;
    @BindView(R.id.total_running_tv)
    TextView totalTimeRunningTextView;
    @BindView(R.id.total_walking_tv)
    TextView totalTimeWalkingTextView;


    private RecyclerView.LayoutManager mLayoutManager;
    private Parcelable mLayoutParcelable;
//    private Configuration mConfiguration;
    private ArrayList<ConfigurationStep> mConfigurationSteps = new ArrayList<>();
    private ConfigurationStepAdapter mConfigurationStepAdapter;
    private int totalTime = 0, totalTimeRunning = 0, totalTimeWalking = 0;
    private SaveConfigurationDialogFragment saveConfigurationDialogFragment;
    private String SAVE_NEW_CONFIG_FRAGMENT_TAG = "SaveConfigurationDialogFragment";

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
        /*
        if (savedInstanceState != null && savedInstanceState.containsKey(CONFIGURATION_OBJECT)) {
            mConfiguration = savedInstanceState.getParcelable(CONFIGURATION_OBJECT);
        }
        */
        if (savedInstanceState != null && savedInstanceState.containsKey(TOTAL_TIME)) {
            totalTime = savedInstanceState.getInt(TOTAL_TIME);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(TOTAL_TIME_WALKING)) {
            totalTimeWalking = savedInstanceState.getInt(TOTAL_TIME_WALKING);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(TOTAL_TIME_RUNNING)) {
            totalTimeRunning = savedInstanceState.getInt(TOTAL_TIME_RUNNING);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(CONFIGURATION_STEP)) {
            mConfigurationSteps = savedInstanceState.getParcelableArrayList(CONFIGURATION_STEP);
        }
        mConfigurationStepAdapter = new ConfigurationStepAdapter(mConfigurationSteps);
        configurationStepRecyclerView.setAdapter(mConfigurationStepAdapter);
        updateUI();

        setupListeners();
    }

    private void updateUI() {
        totalTimeTextView.setText(String.format(getString(R.string.total_time_value), totalTime));
        totalTimeRunningTextView.setText(String.format(getString(R.string.total_time_value), totalTimeRunning));
        totalTimeWalkingTextView.setText(String.format(getString(R.string.total_time_value), totalTimeWalking));
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
                    totalTime += step.duration;
                    if(step.stepType.equals(ConfigurationStep.STEP_TYPE_WALK)){
                        totalTimeWalking += step.duration;
                    }else {
                        totalTimeRunning += step.duration;
                    }
                    updateUI();
                    mConfigurationSteps.add(step);
                    minuteValueEditText.setText("");
                    mConfigurationStepAdapter = new ConfigurationStepAdapter(mConfigurationSteps);
                    configurationStepRecyclerView.swapAdapter(mConfigurationStepAdapter, true);

                }
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveConfigurationDialogFragment == null)
                    saveConfigurationDialogFragment = new SaveConfigurationDialogFragment();
                saveConfigurationDialogFragment.show(getSupportFragmentManager(), SAVE_NEW_CONFIG_FRAGMENT_TAG);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mLayoutParcelable = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(RECYCLER_VIEW_STATE, mLayoutParcelable);
        //outState.putParcelable(CONFIGURATION_OBJECT, mConfiguration);
        outState.putParcelableArrayList(CONFIGURATION_STEP, mConfigurationSteps);
        outState.putInt(TOTAL_TIME, totalTime);
        outState.putInt(TOTAL_TIME_RUNNING, totalTimeRunning);
        outState.putInt(TOTAL_TIME_WALKING, totalTimeWalking);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey(RECYCLER_VIEW_STATE)) {
            mLayoutParcelable = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
            mLayoutManager.onRestoreInstanceState(mLayoutParcelable);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(CONFIGURATION_STEP)) {
            mConfigurationSteps = savedInstanceState.getParcelableArrayList(CONFIGURATION_STEP);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(TOTAL_TIME)) {
            totalTime = savedInstanceState.getInt(TOTAL_TIME);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(TOTAL_TIME_WALKING)) {
            totalTimeWalking = savedInstanceState.getInt(TOTAL_TIME_WALKING);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(TOTAL_TIME_RUNNING)) {
            totalTimeRunning = savedInstanceState.getInt(TOTAL_TIME_RUNNING);
        }
    }

    @Override
    public void onSaveButtonPressed(String configName) {
        final RunMyWayRepository runMyWayRepository = InjectorUtils.provideRepository(this);
        final Configuration newConfiguration = new Configuration(configName, new Date());
        runMyWayRepository.insertNewConfiguration(newConfiguration, new RunMyWayRepository.OnInsertEndedListener() {
            @Override
            public void OnInsertEnded(final long idInserted) {
                for (int i = 0 ; i < mConfigurationSteps.size();  i++){
                    ConfigurationStep step = mConfigurationSteps.get(i);
                    step.configurationId = idInserted;
                    step.position = i;
                }
                runMyWayRepository.insertNewConfigurationStep(mConfigurationSteps);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(AddConfigurationActivity.this, RunSessionActivity.class);
                        intent.putExtra(CONFIGURATION_ID_EXTRA, idInserted);
                        startActivity(intent);
                    }
                });
            }
        });

    }

    @Override
    public void onCancelButtonPressed() {
        saveConfigurationDialogFragment = (SaveConfigurationDialogFragment) getSupportFragmentManager().findFragmentByTag(SAVE_NEW_CONFIG_FRAGMENT_TAG);
        saveConfigurationDialogFragment.dismiss();
    }
}
