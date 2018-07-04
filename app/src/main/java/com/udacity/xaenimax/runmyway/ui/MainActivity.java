package com.udacity.xaenimax.runmyway.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.network.GoogleFitManager;

import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.xaenimax.runmyway.network.GoogleFitManager.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE;
import static com.udacity.xaenimax.runmyway.network.GoogleFitManager.accessGoogleFit;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = AppCompatActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.fab)
    public FloatingActionButton mFloatingActionButton;

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
                
            }
        });

        GoogleFitManager.checkPermission(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            accessGoogleFit(this);
        }else {
            Snackbar.make(mFloatingActionButton, getString(R.string.google_account_alert), Snackbar.LENGTH_LONG).show();
        }
    }
}
