package nanodegree.damian.runny.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import nanodegree.damian.runny.persistence.data.WorkoutSession;

/**
 * Created by robert_damian on 29.07.2018.
 */

public class WorkoutHistoryViewModel extends AndroidViewModel {

    private LiveData<WorkoutSession> mWorkoutSessionsLiveData;

    public WorkoutHistoryViewModel(@NonNull Application application) {
        super(application);


    }
}
