package nanodegree.damian.runny.controller;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import nanodegree.damian.runny.firebase.FirebaseWriterSingleton;
import nanodegree.damian.runny.persistence.data.WorkoutSession;
import nanodegree.damian.runny.persistence.database.AppDatabase;
import nanodegree.damian.runny.utils.Basics;
import nanodegree.damian.runny.widget.WorkoutSessionWidgetProvider;
import nanodegree.damian.runny.widget.WorkoutSessionWidgetService;

/**
 * Controller of the workout session. Keeps track of distance and time, and informs the observers
 * every second about the new state
 *
 * Created by robert_damian on 28.07.2018.
 */
public class WorkoutController extends Observable implements LocationListener{

    public static final String TAG = WorkoutController.class.getName();

    private static final int MAX_ACCURACY_MARGIN = 50;

    private Context mContext;
    private LocationManager mLocationManager;
    private Location mLastKnownLocation;
    private float mDistance;
    private boolean mStarted;

    private WorkoutSession mSession;

    private Timer mInformingTimer;
    private OutputStream mLogStream;

    public WorkoutController(Context context) {
        this.mContext = context;

        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        registerLocationListener();

        initLogs();
    }

    public WorkoutController(Context context, WorkoutSession session) {
        this(context);
        Log.d(TAG, "Called secondary constructor");

        mSession = session;
        mStarted = true;
        startInformingListeners();
        mDistance = session.getDistance();
    }

    public void start() {
        mStarted = true;
        Calendar mStartTime = Calendar.getInstance();

        mSession = new WorkoutSession(mStartTime, null, 0f,
                new ArrayList<>(), mLastKnownLocation);
        new InsertSessionAsyncTask(AppDatabase.getInstance(mContext), id -> {
            mSession.setId(id);
            mSession.setPersisted(true);
        }).execute(mSession);

        startInformingListeners();
    }

    private void startInformingListeners() {
        mInformingTimer = new Timer();
        mInformingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                informObservers();
            }
        }, 0, TimeUnit.SECONDS.toMillis(1));
    }

    private void informObservers() {
        new UpdateSessionAsyncTask(AppDatabase.getInstance(mContext)).execute(mSession);
    }

    public void stop() {
        if (mLogStream != null) {
            try {
                mLogStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mInformingTimer.cancel();
        mInformingTimer.purge();

        mSession.setEndTime(Calendar.getInstance());
        new UpdateSessionAsyncTask(AppDatabase.getInstance(mContext)).execute(mSession);
        FirebaseWriterSingleton.getInstance().writeWorkoutSession(mSession);

        mLocationManager.removeUpdates(this);
        WorkoutSessionWidgetService.startActionUpdateWidgets(mContext);
    }

    /**
     * Tries to register this activity as a location listener
     */
    private void registerLocationListener() {
        if (!Basics.hasAccessToLocation(mContext)) {
            return;
        }
        Log.v(TAG, "Location Listener Submitted!");
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            return;
        }
        String log = "Location changed! Lat: " + location.getLatitude() +
                ", Lng: " + location.getLongitude() +
                ", Accuracy " + location.getAccuracy();
        if (mLogStream != null) {
            try {
                mLogStream.write(log.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.v(TAG, log);

        if (mStarted && mLastKnownLocation != null &&
                allLocationsAreAccurate(mLastKnownLocation, location)) {
            mDistance += mLastKnownLocation.distanceTo(location);
            mSession.setDistance(mDistance);
        }

        Log.v(TAG, "Current distance: " + mDistance +
                "; Location accuracy: " + location.getAccuracy());
        mLastKnownLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private boolean allLocationsAreAccurate(Location... locations) {
        for (Location location : locations) {
            if (location.getAccuracy() > MAX_ACCURACY_MARGIN) {
                return false;
            }
        }

        return true;
    }

    static class InsertSessionAsyncTask extends AsyncTask<WorkoutSession, Void, Long> {

        private AppDatabase mDb;
        private OnInsertCallback mCallback;

        InsertSessionAsyncTask(AppDatabase database, OnInsertCallback callback) {
            mDb = database;
            mCallback = callback;
        }

        @Override
        protected Long doInBackground(WorkoutSession... workoutSessions) {
            if (workoutSessions.length < 1) {
                return null;
            }

            return mDb.workoutSessionDao().insertWorkoutSession(workoutSessions[0]);
        }

        @Override
        protected void onPostExecute(Long id) {
            super.onPostExecute(id);
            mCallback.insertOperationCompleted(id);
        }
    }

    private void initLogs() {
        String appLogsDirectoryName = Environment.getExternalStorageDirectory() + "/RunnyLogs";
        File currentLogFile = new File(appLogsDirectoryName +
                "/log" +
                System.currentTimeMillis() +
                ".txt");

        currentLogFile.getParentFile().mkdirs();

        try {
            if(currentLogFile.createNewFile()) {
                mLogStream = new FileOutputStream(currentLogFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static class UpdateSessionAsyncTask extends AsyncTask<WorkoutSession, Void, Void> {

        private AppDatabase mDb;

        UpdateSessionAsyncTask(AppDatabase database) {
            mDb = database;
        }

        @Override
        protected Void doInBackground(WorkoutSession... workoutSessions) {
            if (workoutSessions.length < 1) {
                return null;
            }

            mDb.workoutSessionDao().updateWorkoutSession(workoutSessions[0]);
            return null;
        }
    }

    private interface OnInsertCallback {
        void insertOperationCompleted(Long id);
    }
}
