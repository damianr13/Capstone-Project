package nanodegree.damian.runny.controller;

import android.location.Location;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Transportation vessel for the updates created by the session controller
 * Created by robert_damian on 29.07.2018.
 */
public class WorkoutUpdateBundle {
    private Location mLocation;
    private float mDistance;
    private long mTime;

    WorkoutUpdateBundle(Location location, float distance, long time) {
        this.mLocation = location;
        this.mDistance = distance;
        this.mTime = time;
    }

    public Location getLocation() {
        return mLocation;
    }
}
