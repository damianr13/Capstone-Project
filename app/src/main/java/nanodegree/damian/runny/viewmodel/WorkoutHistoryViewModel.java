package nanodegree.damian.runny.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import nanodegree.damian.runny.persistence.data.WorkoutSession;
import nanodegree.damian.runny.persistence.database.AppDatabase;

/**
 * ViewModel that takes care of keeping the history of the sessions in sync
 *
 * Created by robert_damian on 29.07.2018.
 */
public class WorkoutHistoryViewModel extends AndroidViewModel {

    private LiveData<List<WorkoutSession>> mWorkoutSessionsLiveData;

    public WorkoutHistoryViewModel(@NonNull Application application) {
        super(application);

        mWorkoutSessionsLiveData = AppDatabase.getInstance(getApplication())
                .workoutSessionDao().getAllSessionsLiveData();
    }

    public LiveData<List<WorkoutSession>> getSessionsLiveData() {
        return mWorkoutSessionsLiveData;
    }
}
