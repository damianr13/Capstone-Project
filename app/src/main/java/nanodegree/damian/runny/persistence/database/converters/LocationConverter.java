package nanodegree.damian.runny.persistence.database.converters;

import android.arch.persistence.room.TypeConverter;
import android.location.Location;

/**
 * Created by robert_damian on 29.07.2018.
 */

public class LocationConverter {
    @TypeConverter
    public static Location toLocation(String description) {
        if (description == null) {
            return null;
        }
        String[] values = description.split(";");

        Location result = new Location("");
        result.setLatitude(Double.valueOf(values[0].trim()));
        result.setLongitude(Double.valueOf(values[1].trim()));

        return result;
    }

    @TypeConverter
    public static String toDescriptor(Location location) {
        return location == null ? null : location.getLatitude() + ";" +
                location.getLongitude();
    }
}
