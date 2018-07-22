package com.udacity.xaenimax.runmyway.ui.runsession;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

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
import com.udacity.xaenimax.runmyway.model.entity.ConfigurationStep;
import com.udacity.xaenimax.runmyway.utils.InjectorUtils;
import com.udacity.xaenimax.runmyway.viewmodel.RunSessionViewFactory;
import com.udacity.xaenimax.runmyway.viewmodel.RunSessionViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RunSessionActivity extends AppCompatActivity {
    private static final String LOG_TAG = RunSessionActivity.class.getSimpleName();
    public static final String CONFIGURATION_ID_EXTRA = "configuration_id_extra";
    public static final String REQUEST_LOCATION_UPDATE = "request_location update";
    private boolean mRequestingLocationUpdates = false;

    private static final int GOOGLE_LOCATION_REQUEST_CODE = 6590;
    public static final int LOCATION_TIME_INTERVAL = 10000;
    public static final int LOCATION_FASTEST_TIME_INTERVAL = 5000;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;



    private RunSessionViewModel mViewModel;
    private long mConfigId;

    @BindView(R.id.start_stop_button)
    public ImageButton startStopImageButton;


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

        //main aim is to get distance, not show it in map is it possible to get distance with location
        // https://stackoverflow.com/questions/27730902/calculating-distance-travelled-as-the-user-moves
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

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUEST_LOCATION_UPDATE, mRequestingLocationUpdates);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "Stop running");
        stopRequestingUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            Log.d(LOG_TAG, "Resume running");
            startRequestingUpdates();
        }
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
        if(requestCode == GOOGLE_LOCATION_REQUEST_CODE && grantResults[0] == RESULT_OK && grantResults[1] == RESULT_OK){
            Log.d(LOG_TAG, "Location permissions granted");
            startRequestingUpdates();
        }
        else {
            Log.d(LOG_TAG, "Permissions denied");
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
                if (configurationSteps != null) {
                    updateUI(configurationSteps);
                }
            }
        });

    }

    private void updateUI(List<ConfigurationStep> configurationSteps) {


    }


    //region Location Listener

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
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                }
            };
        };
    }

    //endregion

}
