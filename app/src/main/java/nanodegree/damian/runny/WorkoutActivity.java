package nanodegree.damian.runny;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

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
import nanodegree.damian.runny.controller.WorkoutUpdateBundle;
import nanodegree.damian.runny.utils.Basics;

public class WorkoutActivity extends AppCompatActivity implements OnMapReadyCallback,
        Observer {

    public static final String TAG = WorkoutActivity.class.getName();
    public static final float DEFAULT_ZOOM = 15f;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tv_value_distance)
    TextView mDistanceTextView;

    @BindView(R.id.tv_value_time)
    TextView mTimeTextView;

    private GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        connectToController(getResources().getInteger(R.integer.workout_controller_id));
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

        if (Basics.hasAccessToLocation(this)) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location center = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            animateMapTo(center);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        WorkoutUpdateBundle bundle;
        try {
            bundle = (WorkoutUpdateBundle) arg;
        } catch (ClassCastException ex) {
            Log.e(TAG, "Expected WorkoutUpdateBundle argument:" + ex);
            return ;
        }

        runOnUiThread(() -> updateViews(bundle));
    }

    private void updateViews(WorkoutUpdateBundle bundle) {
        animateMapTo(bundle.getLocation());

        mTimeTextView.setText(bundle.getFormattedTimeValue());
        mDistanceTextView.setText(bundle.getFormattedDistanceValue());
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
