package nanodegree.damian.runny.controller;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import nanodegree.damian.runny.utils.Basics;

/**
 * Controller of the workout session. Keeps track of distance and time, and informs the observers
 * every second about the new state
 *
 * Created by robert_damian on 28.07.2018.
 */
public class WorkoutController extends Observable implements LocationListener{

    private static final String TAG = WorkoutController.class.getName();

    private static final int MAX_ACCURACY_MARGIN = 30;

    private Calendar mStartTime;
    private Context mContext;
    private LocationManager mLocationManager;
    private Location mLastKnownLocation;
    private float mDistance;
    private boolean mStarted;
    private int mId;

    private Timer mInformingTimer;

    private static SparseArray<WorkoutController> instances = new SparseArray<>();

    public static WorkoutController getInstance(int id) {
        return instances.get(id);
    }

    public WorkoutController(Context context, int id) {
        this.mContext = context;
        this.mId = id;

        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        registerLocationListener();

        WorkoutController previouslyStoredInstance = instances.get(id);
        if (previouslyStoredInstance != null) {
            previouslyStoredInstance.stop();
        }
        instances.append(id, this);
    }

    public void start() {
        mStarted = true;
        mStartTime = Calendar.getInstance();

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
        WorkoutUpdateBundle bundle = new WorkoutUpdateBundle(mLastKnownLocation, mDistance,
                Calendar.getInstance().getTimeInMillis() - mStartTime.getTimeInMillis());

        setChanged();
        notifyObservers(bundle);

        Log.v(TAG, "Observers updated from WorkoutController");
    }

    public void stop() {
        mInformingTimer.cancel();
        mInformingTimer.purge();

        instances.remove(mId);
        //TODO: store info about the current session in database
        mLocationManager.removeUpdates(this);
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

        Log.v(TAG, "Location changed! Lat: " + location.getLatitude() +
                ", Lng: " + location.getLongitude());

        if (mStarted && mLastKnownLocation != null &&
                allLocationsAreAccurate(mLastKnownLocation, location)) {
            mDistance += mLastKnownLocation.distanceTo(location);
            Toast.makeText(mContext, "Distance updated", Toast.LENGTH_SHORT).show();
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
}
