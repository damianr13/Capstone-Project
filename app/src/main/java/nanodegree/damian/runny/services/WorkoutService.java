package nanodegree.damian.runny.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import nanodegree.damian.runny.R;
import nanodegree.damian.runny.WorkoutActivity;
import nanodegree.damian.runny.controller.WorkoutController;
import nanodegree.damian.runny.persistence.data.WorkoutSession;
import nanodegree.damian.runny.persistence.database.AppDatabase;

/**
 * A foreground service that stays active and tracks the workout even after the app was killed
 * Created by robert_damian on 28.07.2018.
 */
public class WorkoutService extends Service {

    public static final String TAG = WorkoutService.class.getName();

    public static final String ACTION_START = "Start";
    public static final String ACTION_STOP = "Stop";

    public static final String CHANNEL_NAME = "Runny App";
    public static final String CHANNEL_DESCRIPTION = "Channel destined for notification displayed " +
            "by the Runny App!";
    public static final String CHANNEL_ID = "Runny";

    public static final int NOTIFICATION_ID = 13;

    private WorkoutController mWorkoutController;

    public WorkoutService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind got called");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Called on start");
        if (intent == null) {
            // this happens when the app was killed so we recreate the session controller
            if (mWorkoutController == null) {
                Log.d(TAG, "Controller was null");
                new GetCurrentSessionAsyncTask(AppDatabase.getInstance(this), session ->
                    mWorkoutController = new WorkoutController(this, session)
                ).execute();
            }
            return START_NOT_STICKY;
        }

        String action = intent.getAction();
        if (action == null) {
            Log.d(TAG, "Action is null");
            return START_NOT_STICKY;
        }

        switch (action) {
            case ACTION_START:
                startForegroundService();
                break;
            case ACTION_STOP:
                stopForegroundService();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void stopForegroundService() {
        Log.v(TAG, "Workout Service stopped");

        if (mWorkoutController != null) {
            mWorkoutController.stop();
        }
        else {
            Log.d(TAG, "WorkoutController was null!");
        }

        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
        stopForeground(true);
        stopSelf();
    }

    private void initLogsWriting() {
        String appLogsDirectoryName = Environment.getExternalStorageDirectory() + "/RunnyLogs";
        File currentLogFile = new File(appLogsDirectoryName +
                "/log" +
                System.currentTimeMillis() +
                ".txt");

        currentLogFile.getParentFile().mkdirs();
        try {
            Runtime.getRuntime().exec("logcat -c");
            Runtime.getRuntime().exec("logcat -f " + currentLogFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startForegroundService() {
        initLogsWriting();

        Log.d(TAG, "Start foreground service method");
        mWorkoutController = new WorkoutController(this);

        Intent stopRunningIntent = new Intent(this, WorkoutService.class);
        stopRunningIntent.setAction(ACTION_STOP);
        PendingIntent stopRunningPendingIntent =
                PendingIntent.getService(this, 0, stopRunningIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

        createNotificationChannel();

        Intent launchActivityIntent = new Intent(this, WorkoutActivity.class);
        PendingIntent launchActivityPendingIntent = PendingIntent.getActivity(this,
                0, launchActivityIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_runner)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(launchActivityPendingIntent)
                .setOngoing(true)
                .addAction(R.drawable.ic_stopwatch, getString(R.string.stop),
                        stopRunningPendingIntent);

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, mBuilder.build());
        mWorkoutController.start();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            if (notificationManager == null) {
                return ;
            }
            notificationManager.createNotificationChannel(channel);
        }
    }

    static class GetCurrentSessionAsyncTask extends AsyncTask<Void, Void, WorkoutSession> {

        private AppDatabase mDb;
        private OnCurrentSessionRetrievedCallback mCallback;

        GetCurrentSessionAsyncTask(AppDatabase database, OnCurrentSessionRetrievedCallback callback) {
            mDb = database;
            mCallback = callback;
        }

        @Override
        protected WorkoutSession doInBackground(Void... voids) {
            return mDb.workoutSessionDao().getCurrentSession();
        }

        @Override
        protected void onPostExecute(WorkoutSession session) {
            super.onPostExecute(session);
            mCallback.currentSessionRetrieved(session);
        }
    }

    interface OnCurrentSessionRetrievedCallback {
        void currentSessionRetrieved(WorkoutSession session);
    }
}
