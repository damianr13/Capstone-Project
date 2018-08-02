package nanodegree.damian.runny.firebase.data;

import java.util.Calendar;

import nanodegree.damian.runny.persistence.database.converters.CalendarConverter;

/**
 * Created by robert_damian on 02.08.2018.
 */
public class FriendWorkoutSession {
    public final FirebaseRegisteredUser friend;
    public final String time;
    public final String distance;
    public final Calendar startTime;

    public FriendWorkoutSession(FirebaseRegisteredUser friend, String time, String distance,
                                long startTimestamp) {
        this.friend = friend;
        this.time = time;
        this.distance = distance;
        this.startTime = CalendarConverter.toCalendar(startTimestamp);
    }
}
