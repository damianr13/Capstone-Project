package nanodegree.damian.runny;

import android.arch.lifecycle.LiveData;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import nanodegree.damian.runny.persistence.data.WorkoutSession;
import nanodegree.damian.runny.persistence.database.AppDatabase;
import nanodegree.damian.runny.utils.Basics;

public class WorkoutActivity extends AppCompatActivity implements OnMapReadyCallback{

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

        new GetSessionAsyncTask(AppDatabase.getInstance(this), workoutSessionLiveData ->{
            workoutSessionLiveData.observe(WorkoutActivity.this, session -> {
                runOnUiThread(() -> updateViews(session));
            });
        }).execute();
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

    private void updateViews(WorkoutSession session) {
        animateMapTo(session.getLastKnownLocation());

        mTimeTextView.setText(session.getFormattedTimeValue());
        mDistanceTextView.setText(session.getFormattedDistanceValue());
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
