package nanodegree.damian.runny.persistence.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import nanodegree.damian.runny.persistence.data.WorkoutSession;
import nanodegree.damian.runny.persistence.database.converters.CalendarConverter;
import nanodegree.damian.runny.persistence.database.converters.LongListConverter;
import nanodegree.damian.runny.persistence.database.converters.LocationConverter;

/**
 * Created by robert_damian on 29.07.2018.
 */

@Database(entities = {WorkoutSession.class}, version = 1, exportSchema = false)
@TypeConverters({CalendarConverter.class, LocationConverter.class, LongListConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "runny";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract WorkoutSessionDao workoutSessionDao();
}
