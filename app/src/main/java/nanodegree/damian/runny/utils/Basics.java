package nanodegree.damian.runny.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by robert_damian on 28.07.2018.
 */

public class Basics {

    public static boolean hasAccessToLocation(Context context) {
        return ActivityCompat
                .checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }
}
