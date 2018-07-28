package nanodegree.damian.runny;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import nanodegree.damian.runny.controller.WorkoutController;
import nanodegree.damian.runny.utils.Basics;

public class WorkoutActivity extends AppCompatActivity implements OnMapReadyCallback,
        Observer {

    public static final String TAG = WorkoutActivity.class.getName();
    public static final float DEFAULT_ZOOM = 15f;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private GoogleMap mGoogleMap;
    private Location mLastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        connectToController(getResources().getInteger(R.integer.workout_controller_id));
    }

    private void connectToController(final int id) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                WorkoutController result = WorkoutController.getInstance(id);
                // if the controller is not available try again after some time
                if (result == null) {
                    connectToController(id);
                    return ;
                }

                result.addObserver(WorkoutActivity.this);
            }
        }, 1000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (Basics.hasAccessToLocation(this)) {
            mGoogleMap.setMyLocationEnabled(true);
        }

        Location center = mLastKnownLocation;
        animateMapTo(center);
    }

    @Override
    public void update(Observable o, Object arg) {
        Location location;
        try {
            location = (Location) arg;
        } catch (ClassCastException ex) {
            Log.e(TAG, "Expected Location argument:" + ex);
            return ;
        }

        mLastKnownLocation = location;
        animateMapTo(location);
    }

    private void animateMapTo(Location location) {
        if (location == null) {
            return ;
        }

        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        if (mGoogleMap != null) {
            mGoogleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(currentPosition, DEFAULT_ZOOM));
        }
    }
}
