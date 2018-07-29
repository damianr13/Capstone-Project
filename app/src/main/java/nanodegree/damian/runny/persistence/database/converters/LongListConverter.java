package nanodegree.damian.runny.persistence.database.converters;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robert_damian on 29.07.2018.
 */

public class LongListConverter {
    @TypeConverter
    public static List<Long> toList(String field) {
        if (field == null) {
            return null;
        }

        List<Long> result = new ArrayList<>();

        String[] values = field.split(",");
        for (String value : values) {
            result.add(Long.valueOf(value.trim()));
        }

        return result;
    }

    @TypeConverter
    public static String toField(List<Long> list) {
        if (list == null || list.size() == 0) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        for (Long l : list) {
            result.append(l).append(",");
        }

        if (result.length() == 0) {
            return "";
        }

        return result.substring(0, result.length() - 1);
    }
}
