package nanodegree.damian.runny.persistence.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import nanodegree.damian.runny.persistence.data.WorkoutSession;

@Dao
public interface WorkoutSessionDao {

    @Query("SELECT * FROM workout_session ORDER BY start_time DESC LIMIT 1")
    LiveData<WorkoutSession> getCurrentSessionLiveData();

    @Query("SELECT * FROM workout_session ORDER BY start_time DESC LIMIT 1")
    WorkoutSession getCurrentSession();

    @Query("SELECT * FROM workout_session ORDER BY start_time DESC")
    LiveData<List<WorkoutSession>> getAllSessionsLiveData();

    @Query("SELECT * FROM workout_session ORDER BY distance DESC LIMIT 1")
    WorkoutSession getLongestSession();

    @Insert
    long insertWorkoutSession(WorkoutSession session);

    @Delete
    void deleteWorkoutSession(WorkoutSession session);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateWorkoutSession(WorkoutSession session);
}
