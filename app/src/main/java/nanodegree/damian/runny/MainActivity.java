package nanodegree.damian.runny;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;

import java.util.LinkedHashMap;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import nanodegree.damian.runny.firebase.FirebaseWriterSingleton;
import nanodegree.damian.runny.fragments.FriendsFragment;
import nanodegree.damian.runny.fragments.PersonalStatsFragment;
import nanodegree.damian.runny.services.WorkoutService;
import nanodegree.damian.runny.utils.Basics;

public class MainActivity extends AppCompatActivity implements
        FriendsFragment.OnFriendsFragmentInteractionListener{

		
		// COmment pentru demonstratie
    private static final String TAG = MainActivity.class.getName();

    public static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;

    public static final int REQUEST_GOOGLE_SIGN_IN_CODE = 0;

    @BindString(R.string.personal_tab) public String NAME_PERSONAL_STATS_TAB;
    @BindString(R.string.friends_tab) public String NAME_FRIENDS_TAB;

    @BindView(R.id.vp_tabs)
    ViewPager mTabsViewPager;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tl_tabs)
    TabLayout mTabLayout;

    @BindView(R.id.civ_profile_image)
    CircleImageView mProfileImageView;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mFirebaseAuth;

    private FriendsFragment mFriendsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // We do this because android handles fragment reconstruction on configuration change
        // but we take care of this manually below (and if we keep both it results in a conflict)
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getResources().getString(R.string.firebase_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_GOOGLE_SIGN_IN_CODE);

        setSupportActionBar(mToolbar);

        mFriendsFragment = new FriendsFragment();

        ViewPageAdapter tabFragmentAdapter = new ViewPageAdapter(getSupportFragmentManager());
        tabFragmentAdapter.addTab(NAME_PERSONAL_STATS_TAB, new PersonalStatsFragment());
        tabFragmentAdapter.addTab(NAME_FRIENDS_TAB, mFriendsFragment);
        mTabsViewPager.setAdapter(tabFragmentAdapter);

        mTabLayout.setupWithViewPager(mTabsViewPager);

        if (!Basics.hasAccessToLocation(this)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_SIGN_IN_CODE:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (!result.isSuccess() || result.getSignInAccount() == null) {
                    Toast.makeText(this, "Google sign in failed!", Toast.LENGTH_SHORT)
                            .show();
                    return ;
                }
                GoogleSignInAccount account = result.getSignInAccount();
                Log.d(TAG, account.getEmail());
                Picasso.get().load(account.getPhotoUrl()).into(mProfileImageView);

                firebaseAuthWithGoogle(account);

                break;
        }
    }

    @Override
    public void onAddFriendClicked() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "You have to be logged in for accessing this feature",
                    Toast.LENGTH_SHORT).show();
            return ;
        }

        onSearchRequested();
    }

    class ViewPageAdapter extends FragmentPagerAdapter {

        private LinkedHashMap<String, Fragment> mTabMap;

        ViewPageAdapter(FragmentManager fm) {
            super(fm);

            mTabMap = new LinkedHashMap<>();
        }

        void addTab(String name, Fragment fragment) {
            mTabMap.put(name, fragment);
        }

        @Override
        public Fragment getItem(int position) {
            // LinkedHashMap guarantees that the values are stored in the order they were introduced
            return mTabMap.values().toArray(new Fragment[mTabMap.size()])[position];
        }

        @Override
        public int getCount() {
            return mTabMap.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabMap.keySet().toArray(new String[mTabMap.size()])[position];
        }
    }

    @OnClick(R.id.fab_start)
    public void onStartRunning(View v){
        if (!Basics.hasAccessToLocation(this)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }

        if (!Basics.hasAccessToLocation(this)) {
            Toast.makeText(this, "This feature is only available if location is enabled",
                    Toast.LENGTH_LONG).show();
            return ;
        }

        Intent startRunnyService = new Intent(this, WorkoutService.class);
        startRunnyService.setAction(WorkoutService.ACTION_START);
        startService(startRunnyService);

        Intent startRunningIntent = new Intent(this, WorkoutActivity.class);
        startActivity(startRunningIntent);
    }

    /**
     * Taken from Firebase documentation:
     * https://firebase.google.com/docs/auth/android/google-signin
     * @param acct
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(authResult -> {
            FirebaseWriterSingleton.getInstance().writeUserInfo(acct);
            mFriendsFragment.loadFriendsWorkouts();
        });
    }
}
