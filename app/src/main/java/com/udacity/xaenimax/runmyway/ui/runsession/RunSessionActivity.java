package com.udacity.xaenimax.runmyway.ui.runsession;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.model.dao.AppExecutor;
import com.udacity.xaenimax.runmyway.model.entity.ConfigurationStep;
import com.udacity.xaenimax.runmyway.utils.InjectorUtils;
import com.udacity.xaenimax.runmyway.viewmodel.RunSessionViewFactory;
import com.udacity.xaenimax.runmyway.viewmodel.RunSessionViewModel;

import java.util.List;

import butterknife.ButterKnife;

public class RunSessionActivity extends AppCompatActivity implements LocationListener {

    public static final String CONFIGURATION_ID_EXTRA = "configuration_id_extra";
    private static final int GOOGLE_LOCATION_REQUEST_CODE = 6590;
    private LocationCallback mLocationCallback;

    private RunSessionViewModel mViewModel;
    private LocationManager locationManager;
    private String provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_session);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(CONFIGURATION_ID_EXTRA)) {
            setupViewModel(intent.getLongExtra(CONFIGURATION_ID_EXTRA, 0));
        } else {
            //TODO start normal run:
            //TODO create a gpstracker manager
            //TODO see at link http://www.vogella.com/tutorials/AndroidLocationAPI/article.html
            //TODO see at link https://www.journaldev.com/13373/android-google-map-drawing-route-two-points
            //main aim is to get distance, not show it in map is it possible to get distance with location
            // https://stackoverflow.com/questions/27730902/calculating-distance-travelled-as-the-user-moves
            startRunning();
        }

        setupListeners();
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkGPSPermission();
    }

    private void startRunning() {
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, GOOGLE_LOCATION_REQUEST_CODE);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
          //TODO location not available
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == GOOGLE_LOCATION_REQUEST_CODE && grantResults[0] == RESULT_OK && grantResults[1] == RESULT_OK){
            startRunning();
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
        LocationManager service = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(service != null && !service.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showAlert();
        }
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.attention);
        builder.setMessage(R.string.gps_info_message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AppExecutor.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

            }
        });
    }

    //region Location Listener
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    //endregion
}
