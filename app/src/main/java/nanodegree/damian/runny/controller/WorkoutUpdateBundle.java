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

    public String getFormattedTimeValue() {
        long timeCopy = mTime;
        Map<String, Long> formattedTime = new LinkedHashMap<>();
        long hours = TimeUnit.MILLISECONDS.toHours(timeCopy);
        timeCopy -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeCopy);
        timeCopy -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeCopy);

        formattedTime.put("h", hours);
        formattedTime.put("m", minutes);
        formattedTime.put("sec", seconds);

        return buildFormattedString(formattedTime);
    }

    private String buildFormattedString(Map<String, Long> formattedMap) {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, Long> timeEntry : formattedMap.entrySet()) {
            if (timeEntry.getValue() == 0) {
                continue;
            }

            result.append(timeEntry.getValue()).append(timeEntry.getKey()).append(" ");
        }

        if (result.length() == 0) {
            return "";
        }

        return result.substring(0, result.length() - 1);
    }

    public String getFormattedDistanceValue() {
        Map<String, Long> formattedDistance = new LinkedHashMap<>();
        formattedDistance.put("km", (long) mDistance / 1000);
        formattedDistance.put("m", (long) (mDistance - ((long) mDistance / 1000) * 1000));

        return buildFormattedString(formattedDistance);
    }
}
