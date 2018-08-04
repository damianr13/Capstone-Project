package nanodegree.damian.runny.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import nanodegree.damian.runny.MainActivity;
import nanodegree.damian.runny.R;
import nanodegree.damian.runny.persistence.data.WorkoutSession;
import nanodegree.damian.runny.utils.Basics;

/**
 * Created by robert_damian on 04.08.2018.
 */

public class WorkoutSessionWidgetProvider extends AppWidgetProvider {

    public static void updateWorkoutSessionWidget(final Context context, AppWidgetManager appWidgetManager,
                                                  int[] appWidgetIds, WorkoutSession session) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_personal_best);

        Intent launchAppIntent = new Intent(context, MainActivity.class);
        PendingIntent launchAppPendingIntent = PendingIntent.getActivity(context, 0,
                launchAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.lt_best_session, launchAppPendingIntent);

        remoteViews.setTextViewText(R.id.tv_entry_title, context.getResources()
                .getString(R.string.run_label, Basics.formatCalendar(session.getStartTime())));
        remoteViews.setTextViewText(R.id.tv_value_distance, session.getFormattedDistanceValue());
        remoteViews.setTextViewText(R.id.tv_value_time, session.getFormattedTimeValue(false));

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WorkoutSessionWidgetService.startActionUpdateWidgets(context);
    }
}
