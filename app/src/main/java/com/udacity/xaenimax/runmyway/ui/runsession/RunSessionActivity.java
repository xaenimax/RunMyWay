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

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(CONFIGURATION_ID_EXTRA)) {
            setupViewModel(intent.getLongExtra(CONFIGURATION_ID_EXTRA, 0));
        } else {
            //TODO start normal run:
            //TODO create a gpstracker manager
            //TODO see at link http://www.vogella.com/tutorials/AndroidLocationAPI/article.html
            //TODO see at link https://www.journaldev.com/13373/android-google-map-drawing-route-two-points
            //main aim is to get distance, not show it in map is it possible to get distance with location
            // https://stackoverflow.com/questions/27730902/calculating-distance-travelled-as-the-user-moves
        }
        
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkGPSPermission();
    }

    private void setupViewModel(long configId) {
        // Get the ViewModel from the factory
        RunSessionViewFactory factory = InjectorUtils.provideRunSessionViewModelFactory(this.getApplicationContext(), configId);
        mViewModel = ViewModelProviders.of(this, factory).get(RunSessionViewModel.class);

        // Observers changes
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

                }
            };
        };
    }

    private void checkGPSPermission() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            showAlert();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        else {

        }
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.attention);
        builder.setMessage(R.string.gps_info_message);
        builder.setPositiveButton(R.string.ok, null);
    }
}
