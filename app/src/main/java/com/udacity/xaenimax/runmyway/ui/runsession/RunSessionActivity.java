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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;
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
import com.udacity.xaenimax.runmyway.managers.GoogleFitService;
import com.udacity.xaenimax.runmyway.model.RunMyWayRepository;
import com.udacity.xaenimax.runmyway.model.entity.ConfigurationStep;
import com.udacity.xaenimax.runmyway.model.entity.RunSession;
import com.udacity.xaenimax.runmyway.ui.completedsession.CompletedSessionActivity;
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
    public static final double ONE_MINUTE_IN_MILLIS = (long) (1000 * 60.0);
    public static final int AVG_CALORIES_BURNED_PER_MINUTE = 6;

    private GoogleFitService.GoogleFitListener mListener;

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

    private Double totalDistance = 0d, lastLongitude = null, lastLatitude = null;

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
        if (savedInstanceState.keySet().contains(STARTED_TIMER)) {
            mTimerStarted = savedInstanceState.getBoolean(STARTED_TIMER);
        }
        if (savedInstanceState.keySet().contains(BASE_TIMER)) {
            mStartBase = savedInstanceState.getLong(BASE_TIMER);
        }
        if (savedInstanceState.keySet().contains(TOTAL_TIME)) {
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
        Log.d(LOG_TAG, "Stop location updates");
        if(mTimerStarted){
            stopTimer();
        }
        stopRequestingUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            Log.d(LOG_TAG, "Resume running");
            startRequestingUpdates();
        }
        if (mTimerStarted) {
            startTimer();
        }
    }

    private void stopTimer() {
        mStartBase = timerChronometer.getBase() - SystemClock.elapsedRealtime();
        timerChronometer.stop();
        GoogleFitService.unregisterGoogleFitnessData(this, mListener);
    }

    private void startTimer() {
        timerChronometer.setBase(SystemClock.elapsedRealtime() + mStartBase);
        timerChronometer.start();
        //it also starts fitness recording if possibile
        GoogleFitService.registerGoogleFitnessData(this, mListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_LOCATION_REQUEST_CODE && resultCode != RESULT_OK) {
            Log.d(LOG_TAG, "Couldn't ask for location services");
            mRequestingLocationUpdates = false;
        } else {
            startRequestingUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GOOGLE_LOCATION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Location permissions granted");
            startRequestingUpdates();
        } else {
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
        if (configurationSteps.size() > 0) {
            currentRunStep.setText(configurationSteps.get(0).stepType);

            mConfigurationSteps = configurationSteps;
            for (ConfigurationStep step : configurationSteps) {
                mTotalTime += step.duration * 1000 * 60;
            }
        } else {
            currentRunStep.setText(R.string.run);
        }
    }


    //region Location Service

    private double GetDistanceFromLatLonInKm(Double previousLat, Double previousLon, Double newLat, Double newLon) {
        double distance = 0;
        if(previousLat != null || previousLon != null){
            double theta = previousLon - newLon;
            distance = Math.sin(deg2rad(previousLat)) * Math.sin(deg2rad(newLat)) + Math.cos(deg2rad(previousLat)) * Math.cos(deg2rad(newLat)) * Math.cos(deg2rad(theta));
            distance = Math.acos(distance);
            distance = rad2deg(distance);
            distance = distance * 60 * 1.1515;
            distance = distance * 1.609344;

        }
        return distance;

    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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
        mListener = new GoogleFitService.GoogleFitListener() {
            @Override
            public void onDailyActivitiesListener(float distance, long Kcal) {
                //nothing to do
            }

            @Override
            public void onFailureListener(final String errorMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(startStopImageButton, errorMessage, Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        };
        timerChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                timerChronometer = chronometer;
                if (mConfigurationSteps != null && mConfigurationSteps.size() > 0) {
                    long elapsedMillis = SystemClock.elapsedRealtime() - timerChronometer.getBase();
                    //if time finished we leave activity
                    if (elapsedMillis >= mTotalTime) {
                        stopRegisteringSession();

                    } else {
                        //else we check wich runstep we're doing
                        long currentMinute = (long) (elapsedMillis / ONE_MINUTE_IN_MILLIS);
                        Log.d(LOG_TAG, "Elapsed time in minutes " + currentMinute);
                        long totalMinute = 0;
                        int index = -1;
                        while (totalMinute <= currentMinute && index < mConfigurationSteps.size() - 1) {
                            index++;
                            totalMinute += mConfigurationSteps.get(index).duration;
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
            }

            ;
        };
        startStopImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimerStarted) {
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
                stopRegisteringSession();
                return false;
            }
        });
    }

    private void stopRegisteringSession() {
        stopRequestingUpdates();
        stopTimer();
        saveCurrentSession();
        goToCompletedSession();
    }

    private void saveCurrentSession() {
        int calories = calculateCalories();
        RunSession currentSession = new RunSession(Math.abs(mStartBase), totalDistance, calories, new Date());
        RunMyWayRepository repository = InjectorUtils.provideRepository(this);
        repository.insertNewRunSession(currentSession);
    }

    private int calculateCalories() {
        //at the moment it is just a rough calories calculus
        double calories = AVG_CALORIES_BURNED_PER_MINUTE * Math.abs(mStartBase)/ ONE_MINUTE_IN_MILLIS;
        return (int) calories;
    }

    private void goToCompletedSession() {
        Intent intent = new Intent(RunSessionActivity.this, CompletedSessionActivity.class);
        startActivity(intent);
    }

    //endregion

}
