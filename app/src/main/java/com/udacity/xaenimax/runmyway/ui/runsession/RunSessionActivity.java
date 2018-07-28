package com.udacity.xaenimax.runmyway.ui.runsession;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.model.RunMyWayRepository;
import com.udacity.xaenimax.runmyway.model.entity.ConfigurationStep;
import com.udacity.xaenimax.runmyway.model.entity.RunSession;
import com.udacity.xaenimax.runmyway.utils.InjectorUtils;
import com.udacity.xaenimax.runmyway.viewmodel.RunSessionViewFactory;
import com.udacity.xaenimax.runmyway.viewmodel.RunSessionViewModel;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RunSessionActivity extends AppCompatActivity {
    private static final String LOG_TAG = RunSessionActivity.class.getSimpleName();
    public static final String CONFIGURATION_ID_EXTRA = "configuration_id_extra";
    public static final String REQUEST_LOCATION_UPDATE = "request_location update";
    private static final String TOTAL_DISTANCE = "total_distance";
    private static final String LAST_LONGITUDE = "last_longitude";
    private static final String LAST_LATITUDE = "last_latitude";
    private static final String STARTED_TIMER = "started_timer";
    private static final String BASE_TIMER = "base_timer";
    private static final String TOTAL_TIME = "total_time";

    private boolean mRequestingLocationUpdates = false, mTimerStarted = true;

    private List<ConfigurationStep> mConfigurationSteps;

    private long mStartBase = 0;
    private long mTotalTime;


    private static final int GOOGLE_LOCATION_REQUEST_CODE = 6590;
    public static final int LOCATION_TIME_INTERVAL = 10000;
    public static final int LOCATION_FASTEST_TIME_INTERVAL = 5000;
    
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    private Double totalDistance = 0d, lastLongitude = 0d, lastLatitude = 0d;
    
    private RunSessionViewModel mViewModel;
    private long mConfigId;

    @BindView(R.id.start_stop_button)
    public ImageButton startStopImageButton;
    @BindView(R.id.timer_chronometer)
    public Chronometer timerChronometer;
    @BindView(R.id.current_step_tv)
    public TextView currentRunStep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_session);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(CONFIGURATION_ID_EXTRA)) {
            mConfigId = intent.getLongExtra(CONFIGURATION_ID_EXTRA, 0);
            setupViewModel(mConfigId);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        updateValuesFromBundle(savedInstanceState);
        setupListeners();
        createAndForwardLocationRequest();
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        if (savedInstanceState.keySet().contains(REQUEST_LOCATION_UPDATE)) {
            mRequestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUEST_LOCATION_UPDATE);
        }
        if (savedInstanceState.keySet().contains(TOTAL_DISTANCE)) {
            totalDistance = savedInstanceState.getDouble(TOTAL_DISTANCE);
        }
        if (savedInstanceState.keySet().contains(LAST_LONGITUDE)) {
            lastLongitude = savedInstanceState.getDouble(LAST_LONGITUDE);
        }
        if (savedInstanceState.keySet().contains(LAST_LATITUDE)) {
            lastLatitude = savedInstanceState.getDouble(LAST_LATITUDE);
        }
        if(savedInstanceState.keySet().contains(STARTED_TIMER)){
            mTimerStarted = savedInstanceState.getBoolean(STARTED_TIMER);
        }
        if(savedInstanceState.keySet().contains(BASE_TIMER)){
            mStartBase = savedInstanceState.getLong(BASE_TIMER);
        }
        if(savedInstanceState.keySet().contains(TOTAL_TIME)){
            mTotalTime = savedInstanceState.getLong(TOTAL_TIME);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUEST_LOCATION_UPDATE, mRequestingLocationUpdates);
        outState.putDouble(TOTAL_DISTANCE, totalDistance);
        outState.putDouble(LAST_LATITUDE, lastLatitude);
        outState.putDouble(LAST_LONGITUDE, lastLongitude);
        outState.putBoolean(STARTED_TIMER, mTimerStarted);
        outState.putLong(BASE_TIMER, mStartBase);
        outState.putLong(TOTAL_TIME, mTotalTime);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "Stop location updates " + mConfigurationSteps.size());
        stopRequestingUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            Log.d(LOG_TAG, "Resume running");
            startRequestingUpdates();
        }
        if(mTimerStarted){
            startTimer();
        }
    }

    private void stopTimer(){
        mStartBase = timerChronometer.getBase() - SystemClock.elapsedRealtime();
        timerChronometer.stop();
    }

    private void startTimer() {
        timerChronometer.setBase(SystemClock.elapsedRealtime() + mStartBase);
        timerChronometer.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_LOCATION_REQUEST_CODE && resultCode != RESULT_OK){
            Log.d(LOG_TAG, "Couldn't ask for location services");
            mRequestingLocationUpdates = false;
        }else {
            startRequestingUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == GOOGLE_LOCATION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            Log.d(LOG_TAG, "Location permissions granted");
            startRequestingUpdates();
        }
        else {
            Log.d(LOG_TAG, "Location Permissions denied");
            mRequestingLocationUpdates = false;
        }
    }

    private void setupViewModel(long configId) {
        // Get the ViewModel from the factory
        RunSessionViewFactory factory = InjectorUtils.provideRunSessionViewModelFactory(this.getApplicationContext(), configId);
        mViewModel = ViewModelProviders.of(this, factory).get(RunSessionViewModel.class);

        // Observers changes
        mViewModel.getConfigurationSteps().observe(this, new Observer<List<ConfigurationStep>>() {
            @Override
            public void onChanged(@Nullable List<ConfigurationStep> configurationSteps) {
                Log.d(LOG_TAG, "Fetched data, " + configurationSteps.size() + " items found");
                updateUIAndSetData(configurationSteps);

            }
        });

    }


    private void updateUIAndSetData(List<ConfigurationStep> configurationSteps) {
        if(configurationSteps.size() > 0) {
            currentRunStep.setText(configurationSteps.get(0).stepType);

            mConfigurationSteps = configurationSteps;
            for (ConfigurationStep step : configurationSteps) {
                mTotalTime += step.duration * 1000 * 60;
            }
        }else {
            currentRunStep.setText(R.string.run);
        }
    }


    //region Location Service

    private double GetDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2)
    {
        final int R = 6371;
        // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);
        // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        // Distance in km
        return d;
    }
    private double deg2rad(double deg)
    {
        return deg * (Math.PI / 180);
    }

    private void stopRequestingUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void startRequestingUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, GOOGLE_LOCATION_REQUEST_CODE);
            return;
        }
        Log.d(LOG_TAG, "Requesting location updates");
        mRequestingLocationUpdates = true;
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);

    }

    private void createAndForwardLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_TIME_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_FASTEST_TIME_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                Log.d(LOG_TAG, "All location settings are satisfied");
                startRequestingUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    Log.d(LOG_TAG, "Location settings are not satisfied, send a message to the user");
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(RunSessionActivity.this, GOOGLE_LOCATION_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private void setupListeners() {
        timerChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                timerChronometer = chronometer;
                if(mConfigurationSteps != null && mConfigurationSteps.size() > 0){
                    long elapsedMillis = SystemClock.elapsedRealtime() - timerChronometer.getBase();
                    //if time finished we leave activity
                    if(elapsedMillis >= mTotalTime){
                        goToCompletedSession();

                    }else {
                        //else we check wich runstep we're doing
                        long currentMinute = (long) (elapsedMillis/(1000*60.0));
                        Log.d(LOG_TAG, "Elapsed time in minutes " + currentMinute);
                        long totalMinute = 0;
                        int index = -1;
                        while(totalMinute <= currentMinute && index < mConfigurationSteps.size() - 1){
                            index++;
                            totalMinute +=  mConfigurationSteps.get(index).duration;
                        }

                        final ConfigurationStep step = mConfigurationSteps.get(index);
                        Log.d(LOG_TAG, "Current step " + index + " " + step.stepType + " " + step.duration);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                currentRunStep.setText(step.stepType);
                            }
                        });
                    }
                }
            }
        });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update distance with location data
                    totalDistance += GetDistanceFromLatLonInKm(lastLatitude, lastLongitude, location.getLatitude(), location.getLongitude());
                    lastLatitude = location.getLatitude();
                    lastLongitude = location.getLongitude();
                }
            };
        };
        startStopImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTimerStarted){
                    stopTimer();
                    mTimerStarted = false;
                } else {
                    mTimerStarted = true;
                    startTimer();
                }
            }
        });

        startStopImageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                saveCurrentSession();
                //TODO finire qui
                return false;
            }
        });
    }

    private void saveCurrentSession() {
        int calories = calculateCalories();
        RunSession currentSession = new RunSession(mTotalTime, totalDistance, calories, new Date());
        RunMyWayRepository repository =  InjectorUtils.provideRepository(this);
        repository.insertNewRunSession(currentSession);
    }

    private int calculateCalories() {
        double calories = 6 * mTotalTime;
        return (int)calories;
    }

    private void goToCompletedSession() {
        //TODO navigate to CompletedSessionActivity
    }

    //endregion

}
