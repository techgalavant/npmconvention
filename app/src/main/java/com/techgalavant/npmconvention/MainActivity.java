package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * The Main Activity will display tabs to users. It is integrated with Firebase remote config.
 * If users shake their device, it has an onShakeListener so that the users can post / send a message to Digital Hermosa.
 * The same messaging capability will also allow Digital Hermosa to post a message to users in the TextView.
 *
 * Credits to:
 * - Android Hive - Working with tabs - http://www.androidhive.info/2015/09/android-material-design-working-with-tabs/
 * - Device Shaking Detection - http://jasonmcreynolds.com/?p=388
 * - Android Hive - Working with alertdialog - http://www.androidhive.info/2011/09/how-to-show-alert-dialog-in-android/
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
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
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    // The following are related to the Firebase Remote Config
    private FirebaseRemoteConfig mRemoteConfig;
    private static final String exhibits_tab = "is_exhibits_on";
    private static final String maps_tab = "is_maps_on";

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    // Used SharedPreferences to detect if app is on first launch
    // Upon first launch, you want the app to run LaunchPermissions
    SharedPreferences prefs = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


            setContentView(R.layout.activity_main);

            // Toolbar shows the appName and the appIcon
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.npmlogo32);

            // Access the shared preferences to detect if this is the first time the app has launched
            prefs = getSharedPreferences("com.techgalavant.npmconvention", MODE_PRIVATE);

            // better implementation example seen here https://github.com/saulmm/CoordinatorExamples
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            setupViewPager(viewPager);

            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);

            // The user can also shake the device to send a message
            // Device Shake Listener credit to http://jasonmcreynolds.com/?p=388
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mAccelerometer = mSensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mShakeDetector = new ShakeDetector();
            mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

                @Override
                public void onShake(int count) {

                    // Launch an AlertDialog box so that users can post a message

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    Log.e(TAG, "ShakeListener detected movement so it displayed the alert dialog box.");

                    // Set Dialog box title
                    alertDialog.setTitle("App Feedback");

                    // Set Dialog message
                    alertDialog.setMessage("Send feedback to app developer?");

                    // Sets icon in the AlertDialog window
                    alertDialog.setIcon(R.drawable.ic_mesg);

                    // Set operation for when user selects "YES" on AlertDialog
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            // Launch feedback form - SendFeedback
                            Intent intent = new Intent(MainActivity.this, SendFeedback.class);
                            startActivity(intent);
                            Log.e(TAG, "User selected YES to SendFeedback AlertDialog");
                        }
                    });

                    // Sets operation for when "NO" button is selected
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Close the AlertDialog box
                            Log.e(TAG, "User selected NO on SendFeedback AlertDialog");
                            dialog.cancel();
                        }
                    });

                    // Show AlertDialog box
                    alertDialog.show();

                }
            });

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
        adapter.addFragment(new ProgramFragment(), "PROGRAM");
        adapter.addFragment(new EventFragment(), "EVENTS");

        // display the tabs if it's enabled on Firebase remote configuration
        if (mRemoteConfig.getBoolean(maps_tab)) {
                adapter.addFragment(new MapsFragment(), "MAPS");
            }
        if (mRemoteConfig.getBoolean(exhibits_tab)) {
                adapter.addFragment(new ExhibitsFragment(), "EXHIBITS");
            }

        adapter.addFragment(new ChaptersFragment(), "CHAPTERS");
        //adapter.addFragment(new ChaptersFragmentFromUrl(), "CHAPTERS");

        // start displaying the different tabs in the viewPager
        viewPager.setAdapter(adapter);
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
                           /* Toast.makeText(MainActivity.this, "Successfully fetched remote configs",
                                    Toast.LENGTH_SHORT).show();*/

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mRemoteConfig.activateFetched();
                        } else {
                            Log.e(TAG,"fetchRemoteConfigs UNSUCCESSFUL");
                      /*      Toast.makeText(MainActivity.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();*/
                            // The app will use default parameters in case it can't reach the Firebase Remote Config service
                            mRemoteConfig.setDefaults(R.xml.remote_config_defaults);
                        }
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


    // Used for ShakerListener and detect if app is first run
    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

        // Detects if app is running for the first time, and users must download the files & set permissions
        if (prefs.getBoolean("firstrun", true)) {

            // If this is the first time that the app is running, user's must set their app permissions and download the files
            Intent intent = new Intent(MainActivity.this, LaunchPermissions.class);
            startActivity(intent);

            // commit () or apply () to Shared Preferences
            //prefs.edit().putBoolean("firstrun", false).apply();  // https://developer.android.com/reference/android/content/SharedPreferences.Editor.html#apply()
        }

    }

    // Used for ShakerListener
    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    // Possibly setup a menu button so that users can view their favorites?
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, About.class));
                return true;
            case R.id.settings:
                startActivity(new Intent(this, LaunchPermissions.class));
                return true;
            case R.id.feedback:
                startActivity(new Intent(this, SendFeedback.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
