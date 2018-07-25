package com.udacity.xaenimax.runmyway.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.managers.GoogleFitService;
import com.udacity.xaenimax.runmyway.ui.addconfiguration.AddConfigurationActivity;
import com.udacity.xaenimax.runmyway.ui.configurationlist.ConfigurationListActivity;
import com.udacity.xaenimax.runmyway.viewmodel.MainViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.xaenimax.runmyway.managers.GoogleFitService.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE;
import static com.udacity.xaenimax.runmyway.managers.GoogleFitService.accessHistoryData;
import static com.udacity.xaenimax.runmyway.managers.GoogleFitService.getFitnessOptions;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = AppCompatActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.fab)
    public FloatingActionButton mFloatingActionButton;

    private GoogleFitService.GoogleFitListener mGoogleFitListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddConfigurationActivity.class));
            }
        });

        setUpListeners();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private void setUpListeners() {
        //Setup the GoogleFitService Listener, once it retrieve data from Fitness api
        mGoogleFitListener = new GoogleFitService.GoogleFitListener() {
            @Override
            public void onDailyActivitiesListener(float distance, long Kcal) {
                setFragmentsParameter(distance, Kcal);
            }

            @Override
            public void onFailureListener(String errorMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(mFloatingActionButton, getString(R.string.google_fit_failed), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        };
    }

    /**
     * Retrieve the main fragment and send parameters to show in UI
     * @param distance
     * @param Kcal
     */
    private void setFragmentsParameter(float distance, long Kcal) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainActivityFragment fragment = (MainActivityFragment) fragmentManager.findFragmentById(R.id.main_fragment);
        if(fragment != null){
            fragment.setUserInfo(distance, Kcal);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check if user has given permission to access Fitness data
        if (resultCode == Activity.RESULT_OK && requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            accessHistoryData(this, mGoogleFitListener);
        } else {
            setFragmentsParameter(0,0);
            Snackbar.make(mFloatingActionButton, getString(R.string.google_account_alert), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_configuration_list:
                goToActivity(ConfigurationListActivity.class);
                return true;
            case R.id.action_news:
                //TODO go to news
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToActivity(Class activityClass) {
        Intent intent = new Intent(MainActivity.this, activityClass);
        this.startActivity(intent);
    }


    public void checkPermission(){
        if(!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), getFitnessOptions())) {
            GoogleSignIn.requestPermissions(
                    this,
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    getFitnessOptions());
        }else {
            accessHistoryData(this, mGoogleFitListener);
        }
    }

}
