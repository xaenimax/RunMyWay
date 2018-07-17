package com.udacity.xaenimax.runmyway.ui.runsession;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.model.entity.ConfigurationStep;
import com.udacity.xaenimax.runmyway.utils.InjectorUtils;
import com.udacity.xaenimax.runmyway.viewmodel.RunSessionViewFactory;
import com.udacity.xaenimax.runmyway.viewmodel.RunSessionViewModel;

import java.util.List;

import butterknife.ButterKnife;

public class RunSessionActivity extends AppCompatActivity {

    public static final String CONFIGURATION_ID_EXTRA = "configuration_id_extra";
    private LocationCallback mLocationCallback;

    private RunSessionViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_session);
        ButterKnife.bind(this);
        
        checkGPSPermission();

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(CONFIGURATION_ID_EXTRA)) {
            setupViewModel(intent.getLongExtra(CONFIGURATION_ID_EXTRA, 0));
        }
        
        setupListeners();
    }

    private void setupViewModel(long configId) {
        // Get the ViewModel from the factory
        RunSessionViewFactory factory = InjectorUtils.provideRunSessionViewModelFactory(this.getApplicationContext(), configId);
        mViewModel = ViewModelProviders.of(this, factory).get(RunSessionViewModel.class);

        // Observers changes in the WeatherEntry with the id mId
        mViewModel.getConfigurationSteps().observe(this, new Observer<List<ConfigurationStep>>() {
                    @Override
                    public void onChanged(@Nullable List<ConfigurationStep> configurationSteps) {
                        if (configurationSteps != null) {
                            updateUI(configurationSteps);
                        }
                    }
                });

    }

    private void updateUI(List<ConfigurationStep> configurationSteps) {
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

    private void checkGPSPermission() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            //TODO add alert dialog to inform user first
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }
}
