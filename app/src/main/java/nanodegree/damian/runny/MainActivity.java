package nanodegree.damian.runny;

import android.Manifest;
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
import android.view.View;

import java.util.LinkedHashMap;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nanodegree.damian.runny.fragments.FriendsFragment;
import nanodegree.damian.runny.fragments.PersonalStatsFragment;
import nanodegree.damian.runny.services.WorkoutService;
import nanodegree.damian.runny.utils.Basics;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    public static final int PERMISSIONS_REQUEST_LOGS_AND_STORAGE = 201;

    @BindString(R.string.personal_tab) public String NAME_PERSONAL_STATS_TAB;
    @BindString(R.string.friends_tab) public String NAME_FRIENDS_TAB;

    @BindView(R.id.vp_tabs)
    ViewPager mTabsViewPager;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tl_tabs)
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ViewPageAdapter tabFragmentAdapter = new ViewPageAdapter(getSupportFragmentManager());
        tabFragmentAdapter.addTab(NAME_PERSONAL_STATS_TAB, new PersonalStatsFragment());
        tabFragmentAdapter.addTab(NAME_FRIENDS_TAB, new FriendsFragment());
        mTabsViewPager.setAdapter(tabFragmentAdapter);

        mTabLayout.setupWithViewPager(mTabsViewPager);

        if (!Basics.hasAccessToLocation(this)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }
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
        if (!Basics.hasAccessToLogs(this) || !Basics.hasAccessToStorage(this)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_LOGS,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_LOGS_AND_STORAGE);
        }

        Intent startRunnyService = new Intent(this, WorkoutService.class);
        startRunnyService.setAction(WorkoutService.ACTION_START);
        startService(startRunnyService);

        Intent startRunningIntent = new Intent(this, WorkoutActivity.class);
        startActivity(startRunningIntent);
    }
}
