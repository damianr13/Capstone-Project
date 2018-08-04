package nanodegree.damian.runny.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import nanodegree.damian.runny.persistence.data.WorkoutSession;
import nanodegree.damian.runny.persistence.database.AppDatabase;

/**
 * Created by robert_damian on 04.08.2018.
 */

public class WorkoutSessionWidgetService extends IntentService {

    public static final String ACTION_UPDATE_WIDGETS = "Update widgets";
    public static final String NAME_WORKOUT_SESSION_WIDGET_SERVICE =
            "Workout session widget service";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public WorkoutSessionWidgetService() {
        super(NAME_WORKOUT_SESSION_WIDGET_SERVICE);
    }


    public static void startActionUpdateWidgets(Context context) {
        Intent updateWidetsIntent = new Intent(context, WorkoutSessionWidgetService.class);
        updateWidetsIntent.setAction(ACTION_UPDATE_WIDGETS);
        context.startService(updateWidetsIntent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return ;
        }
        String action = intent.getAction();

        if (action == null) {
            return ;
        }

        switch (action) {
            case ACTION_UPDATE_WIDGETS:
                AsyncTask.execute(this::handleUpdateWidgets);
        }
    }

    @WorkerThread
    private void handleUpdateWidgets() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                WorkoutSessionWidgetProvider.class));

        WorkoutSession longestWorkoutSession = AppDatabase.getInstance(this)
                .workoutSessionDao().getLongestSession();
        WorkoutSessionWidgetProvider.updateWorkoutSessionWidget(this,
                appWidgetManager, appWidgetIds, longestWorkoutSession);

    }
}