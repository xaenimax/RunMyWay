package com.udacity.xaenimax.runmyway.managers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.RecordingClient;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.udacity.xaenimax.runmyway.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.fitness.data.Field.FIELD_CALORIES;
import static com.google.android.gms.fitness.data.Field.FIELD_DISTANCE;

public class GoogleFitService {
    public static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 3457;
    private static final String LOG_TAG = "GOOGLE_FIT_MANAGER";
    private static boolean isRegisteredActivitySample = false;
    private static boolean isRegisteredCalories = false;
    private static boolean isRegisteredDistance = false;


    static FitnessOptions fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_WRITE)
            // .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
            // .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
            .build();

    public static FitnessOptions getFitnessOptions() {
        return fitnessOptions;
    }

    /**
     * Retrieve History in Google Fitness account and sends it back to the listener
     *
     * @param context
     * @param activitiesListener
     */
    public static void accessHistoryData(Context context, final GoogleFitListener activitiesListener) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        long startTime = cal.getTimeInMillis();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_DISTANCE_DELTA,
                        DataType.AGGREGATE_DISTANCE_DELTA)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED,
                        DataType.AGGREGATE_CALORIES_EXPENDED)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();


        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        Log.d(LOG_TAG, "onSuccess()");

                        DataSet distanceDataSet =
                                dataReadResponse.getDataSet(DataType.TYPE_DISTANCE_DELTA);
                        float totalDistance = distanceDataSet.isEmpty()
                                ? 0
                                : distanceDataSet.getDataPoints().get(0).getValue(FIELD_DISTANCE).asFloat();

                        DataSet caloriesDataSet =
                                dataReadResponse.getDataSet(DataType.TYPE_CALORIES_EXPENDED);
                        long totalkCal = caloriesDataSet.isEmpty()
                                ? 0
                                : caloriesDataSet.getDataPoints().get(0).getValue(FIELD_CALORIES).asInt();

                        activitiesListener.onDailyActivitiesListener(totalDistance, totalkCal);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(LOG_TAG, "onFailure()", e);
                        activitiesListener.onFailureListener(e.getMessage());
                    }
                });
    }


    public static void registerGoogleFitnessData(final Context context, final GoogleFitListener activitiesListener) {
        RecordingClient recordingClient = Fitness.getRecordingClient(context, Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(context)));
        if (!isRegisteredActivitySample) {
            recordingClient
                    .subscribe(DataType.TYPE_ACTIVITY_SAMPLES)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(LOG_TAG, "Successfully subscribed to Activity samples!");
                            isRegisteredActivitySample = true;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(LOG_TAG, "There was a problem subscribing, maybe the user has not logged in");
                            activitiesListener.onFailureListener(
                                    String.format(context.getString(R.string.subscribe_error), context.getString(R.string.activity)));
                        }
                    });
        }
        if (!isRegisteredCalories) {
            recordingClient.subscribe(DataType.TYPE_CALORIES_EXPENDED)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(LOG_TAG, "Successfully subscribed to Calories expended!");
                            isRegisteredCalories = true;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(LOG_TAG, "There was a problem subscribing, maybe the user has not logged in");
                            activitiesListener.onFailureListener(
                                    String.format(context.getString(R.string.subscribe_error), context.getString(R.string.calories)));
                        }
                    });
        }
        if (!isRegisteredDistance) {
            recordingClient.subscribe(DataType.TYPE_DISTANCE_DELTA)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(LOG_TAG, "Successfully subscribed to Distance delta!");
                            isRegisteredDistance = true;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(LOG_TAG, "There was a problem subscribing, maybe the user has not logged in");
                            activitiesListener.onFailureListener(
                                    String.format(context.getString(R.string.subscribe_error), context.getString(R.string.distance)));
                        }
                    });
        }
    }

    public static void unregisterGoogleFitnessData(final Context context, final GoogleFitListener activitiesListener) {
        RecordingClient recordingClient = Fitness.getRecordingClient(context, Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(context)));

        if (isRegisteredActivitySample) {
            recordingClient
                    .unsubscribe(DataType.TYPE_ACTIVITY_SAMPLES)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(LOG_TAG, "Successfully unsubscribed for data type: activity samples");
                            isRegisteredActivitySample = false;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Subscription not removed
                            Log.i(LOG_TAG, "Failed to unsubscribe for data type: activity samples");
                            activitiesListener.onFailureListener(
                                    String.format(context.getString(R.string.unsubscribe_error), context.getString(R.string.activity)));
                        }
                    });
        }
        if (isRegisteredCalories) {
            recordingClient
                    .unsubscribe(DataType.TYPE_CALORIES_EXPENDED)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(LOG_TAG, "Successfully unsubscribed for data type: activity samples");
                            isRegisteredCalories = false;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Subscription not removed
                            Log.i(LOG_TAG, "Failed to unsubscribe for data type: activity samples");
                            activitiesListener.onFailureListener(
                                    String.format(context.getString(R.string.unsubscribe_error), context.getString(R.string.calories)));

                        }
                    });
        }
        if (isRegisteredDistance) {
            recordingClient
                    .unsubscribe(DataType.TYPE_DISTANCE_DELTA)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(LOG_TAG, "Successfully unsubscribed for data type: activity samples");
                            isRegisteredDistance = false;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Subscription not removed
                            Log.i(LOG_TAG, "Failed to unsubscribe for data type: activity samples");
                            activitiesListener.onFailureListener(
                                    String.format(context.getString(R.string.unsubscribe_error), context.getString(R.string.distance)));
                        }
                    });
        }
    }


    public interface GoogleFitListener {
        void onDailyActivitiesListener(float distance, long Kcal);

        void onFailureListener(String errorMessage);
    }


}

