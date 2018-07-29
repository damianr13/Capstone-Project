package nanodegree.damian.runny.persistence.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Used to persist session info during a run (in case the application is killed the controller will
 * read this from the database)
 *
 * Created by robert_damian on 29.07.2018.
 */
@Entity(tableName = "workout_session")
public class WorkoutSession {

    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "start_time")
    private Calendar startTime;
    @ColumnInfo(name = "end_time")
    private Calendar endTime;
    private float distance;
    private List<Long> breakdown;
    @ColumnInfo(name = "last_known_location")
    private Location lastKnownLocation;

    @Ignore
    private boolean persisted;

    public WorkoutSession(long id, Calendar startTime, Calendar endTime, float distance,
                          List<Long> breakdown, Location lastKnownLocation) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.distance = distance;
        this.breakdown = breakdown;
        this.lastKnownLocation = lastKnownLocation;

        persisted = true;
    }

    @Ignore
    public WorkoutSession(Calendar startTime, Calendar endTime, float distance,
                          List<Long> breakdown, Location lastKnownLocation) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.distance = distance;
        this.breakdown = breakdown;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public List<Long> getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(List<Long> breakdown) {
        this.breakdown = breakdown;
    }

    public void update(float distance) {
        // add the time for the current kilometer if completed
        if (distance / 1000 > breakdown.size()) {
            breakdown.add(Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis());
        }

        this.distance = distance;
    }

    public boolean isPersisted() {
        return persisted;
    }

    public void setPersisted(boolean persisted) {
        this.persisted = persisted;
    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLastKnownLocation(Location lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }

    public String getFormattedTimeValue(boolean onGoing) {
        Calendar upperValue = endTime;
        if (onGoing) {
            upperValue = Calendar.getInstance();
        }

        if (upperValue == null) {
            return "N/A";
        }

        long timeCopy = upperValue.getTimeInMillis() - startTime.getTimeInMillis();
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
        formattedDistance.put("km", (long) distance / 1000);
        formattedDistance.put("m", (long) (distance - ((long) distance / 1000) * 1000));

        return buildFormattedString(formattedDistance);
    }
}
