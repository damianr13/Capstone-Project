package nanodegree.damian.runny.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import nanodegree.damian.runny.firebase.data.FirebaseRegisteredUser;

import static nanodegree.damian.runny.firebase.FirebaseConstants.KEY_NAME;
import static nanodegree.damian.runny.firebase.FirebaseConstants.KEY_PHOTO_URL;

/**
 * Created by robert_damian on 28.07.2018.
 */

public class Basics {

    private static final String TIME_FORMAT = "dd/MMM/yyyy";

    public static boolean hasAccessToLocation(Context context) {
        return ActivityCompat
                .checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasAccessToLogs(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_LOGS) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasAccessToStorage(Context context) {
        return ActivityCompat
                .checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat
                .checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;

    }

    public static String formatCalendar(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH);
        return formatter.format(calendar.getTime());
    }
}
