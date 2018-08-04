package nanodegree.damian.runny;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nanodegree.damian.runny.persistence.data.WorkoutSession;
import nanodegree.damian.runny.persistence.database.AppDatabase;
import nanodegree.damian.runny.services.WorkoutService;
import nanodegree.damian.runny.utils.Basics;

public class WorkoutActivity extends AppCompatActivity implements OnMapReadyCallback{

    public static final float DEFAULT_ZOOM = 15f;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tv_value_distance)
    TextView mDistanceTextView;

    @BindView(R.id.tv_value_time)
    TextView mTimeTextView;

    private GoogleMap mGoogleMap;
    private LatLng mTrackLineEnding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        new GetSessionAsyncTask(AppDatabase.getInstance(this), workoutSessionLiveData ->
                workoutSessionLiveData.observe(WorkoutActivity.this, session -> {
                    runOnUiThread(() -> updateViews(session));
        })).execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (Basics.hasAccessToLocation(this)) {
            mGoogleMap.setMyLocationEnabled(true);
        }

        if (Basics.hasAccessToLocation(this)) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location centerLocation = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng center = new LatLng(centerLocation.getLatitude(), centerLocation.getLongitude());
            animateMapTo(center);
            mTrackLineEnding = center;
        }
    }

    private void updateViews(WorkoutSession session) {
        Location lastKnownLocation = session.getLastKnownLocation();
        if (lastKnownLocation != null) {
            LatLng currentPosition = new LatLng(lastKnownLocation.getLatitude(),
                    lastKnownLocation.getLongitude());
            animateMapTo(currentPosition);

            if (mTrackLineEnding != null) {
                mGoogleMap.addPolyline(new PolylineOptions()
                        .add(mTrackLineEnding, currentPosition)
                        .width(5)
                        .color(Color.RED));
            }
            mTrackLineEnding = currentPosition;
        }

        mTimeTextView.setText(session.getFormattedTimeValue(true));
        mDistanceTextView.setText(session.getFormattedDistanceValue());
    }

    private void animateMapTo(LatLng currentPosition) {
        if (mGoogleMap != null) {
            mGoogleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(currentPosition, DEFAULT_ZOOM));
        }
    }

    @OnClick(R.id.fab_stop)
    public void onStopClick(View v) {
        Intent stopRunningIntent = new Intent(this, WorkoutService.class);
        stopRunningIntent.setAction(WorkoutService.ACTION_STOP);
        startService(stopRunningIntent);
    }

    static class GetSessionAsyncTask extends AsyncTask<Void, Void, LiveData<WorkoutSession>> {

        private AppDatabase mDb;
        private OnSessionRetrievedCallback mCallback;

        GetSessionAsyncTask(AppDatabase database, OnSessionRetrievedCallback callback) {
            mDb = database;
            mCallback = callback;
        }

        @Override
        protected LiveData<WorkoutSession> doInBackground(Void... voids) {
            return mDb.workoutSessionDao().getCurrentSessionLiveData();
        }

        @Override
        protected void onPostExecute(LiveData<WorkoutSession> workoutSessionLiveData) {
            super.onPostExecute(workoutSessionLiveData);
            mCallback.sessionRetrieved(workoutSessionLiveData);
        }
    }

    interface OnSessionRetrievedCallback {
        void sessionRetrieved(LiveData<WorkoutSession> workoutSessionLiveData);
    }
}
