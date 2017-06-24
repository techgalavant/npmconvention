package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * This is a utility for configuration of the Firebase database.
 * Reference:  https://github.com/firebase/quickstart-android/issues/15
 *
 */

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MyFirebaseUtil {

    private static FirebaseDatabase mDatabase;

    private FirebaseRemoteConfig mRemoteConfig;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance(); // get instance of Firebase
            mDatabase.setPersistenceEnabled(true); // set to true so that it will store data if user is not connected online
        }

        return mDatabase;
    }

    public FirebaseRemoteConfig getmRemoteConfig() {

            // Use Firebase Remote Config to display the tabs when they are ready
            mRemoteConfig = FirebaseRemoteConfig.getInstance();
            // [START enable_dev_mode]
            FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(true)
                    .build();
            mRemoteConfig.setConfigSettings(remoteConfigSettings);
            // [END enable_dev_mode]

        return mRemoteConfig;

    }


}