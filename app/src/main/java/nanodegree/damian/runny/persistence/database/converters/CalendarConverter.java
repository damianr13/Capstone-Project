package nanodegree.damian.runny.persistence.database.converters;

import android.arch.persistence.room.TypeConverter;

import java.util.Calendar;

/**
 * Created by robert_damian on 29.07.2018.
 */

public class CalendarConverter {
    @TypeConverter
    public static Calendar toCalendar(Long timestamp) {
        if (timestamp == null) {
            return null;
        }

        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(timestamp);

        return result;
    }

    @TypeConverter
    public static Long toTimestamp(Calendar calendar) {
        return calendar == null ? null : calendar.getTimeInMillis();
    }
}
