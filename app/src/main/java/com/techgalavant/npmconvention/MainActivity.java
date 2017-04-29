package com.techgalavant.npmconvention;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.List;

// TODO Setup a permissions interface so that users can set permissions to download and read the JSON and PDF files
// Refer to - http://www.journaldev.com/10409/android-handling-runtime-permissions-example

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseRemoteConfig mRemoteConfig;
    private static final String welcome_tab = "is_welcome_on";
    private static final String schedule_tab = "is_events_on";
    private static final String program_tab = "is_program_on";
    private static final String chapters_tab = "is_chapters_on";
    private static final String exhibits_tab = "is_exhibits_on";
    private static final String sponsors_tab = "is_sponsors_on";
    private static final String maps_tab = "is_maps_on";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar shows the appName and the appIcon
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.npmlogo32);

        // better implementation example seen here https://github.com/saulmm/CoordinatorExamples
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Use Firebase Remote Config to display the tabs when they are ready
        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        // [START enable_dev_mode]
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();
        mRemoteConfig.setConfigSettings(remoteConfigSettings);
        // [END enable_dev_mode]

        // fetch the remote configs and determine if these are different from local R.xml.remote_config_defaults file
        fetchRemoteConfigs();

        // Welcome tab should be displayed regardless
        adapter.addFragment(new WelcomeFragment(), "WELCOME");

        // Firebase remote management will not work on Kindle Fire, so display all tabs by default
        if (isKindleFire()) {
            adapter.addFragment(new ProgramFragment(), "PROGRAM");
            adapter.addFragment(new ScheduleFragment(), "EVENTS");
            adapter.addFragment(new MapsFragment(), "MAPS");
            adapter.addFragment(new ChaptersFragment(), "CHAPTERS");
            adapter.addFragment(new ExhibitsFragment(), "EXHIBITS");
        }

        // display the tabs if it's enabled on Firebase remote configuration
        else {
            if (mRemoteConfig.getBoolean(program_tab)) {
                adapter.addFragment(new ProgramFragment(), "PROGRAM");
            }
            if (mRemoteConfig.getBoolean(schedule_tab)) {
                adapter.addFragment(new ScheduleFragment(), "SCHEDULE");
            }
            if (mRemoteConfig.getBoolean(maps_tab)) {
                adapter.addFragment(new MapsFragment(), "MAPS");
            }
            if (mRemoteConfig.getBoolean(chapters_tab)) {
                adapter.addFragment(new ChaptersFragment(), "CHAPTERS");
            }
            if (mRemoteConfig.getBoolean(exhibits_tab)) {
                adapter.addFragment(new ExhibitsFragment(), "EXHIBITS");
            }

            // start displaying the different tabs in the viewPager
            viewPager.setAdapter(adapter);
        }
    }

    // Used to retrieve the remote configs and compare against local remote config files
    private void fetchRemoteConfigs() {

        // START retrieving from Firebase service
        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices - https://github.com/firebase/quickstart-android/blob/master/README.md#firebase-quickstarts-for-android
        mRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG,"fetchRemoteConfigs SUCCESSFUL");
                            Toast.makeText(MainActivity.this, "Successfully fetched remote configs",
                                    Toast.LENGTH_SHORT).show();

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mRemoteConfig.activateFetched();
                        } else {
                            Log.e(TAG,"fetchRemoteConfigs UNSUCCESSFUL");
                            Toast.makeText(MainActivity.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                            // The app will use default parameters in case it can't reach the Firebase Remote Config service
                            mRemoteConfig.setDefaults(R.xml.remote_config_defaults);
                        }
                        //return;
                    }
                });
        // [END fetch_config_with_callback]
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    // The app should run on the Kindle Fire which has an older version of Android.
    // Firebase remote config will not run on these older models.
    // This is a check to determine if the app is on a Kindle Fire
    public static boolean isKindleFire() {
        return android.os.Build.MANUFACTURER.equals("Amazon")
                && (android.os.Build.MODEL.equals("Kindle Fire")
                || android.os.Build.MODEL.startsWith("KF"));
    }
}
