package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * The Welcome fragment will show a message to users. It is integrated with the Firebase Database.
 * If users shake their device, it has an onShakeListener so that the users can post / send a message to Digital Hermosa.
 * The same messaging capability will also allow Digital Hermosa to post a message to users in the TextView.
 *
 * Credits to:
 * - Android Hive - http://www.androidhive.info/2015/09/android-material-design-working-with-tabs/
 * - Device Shaking Detection - http://jasonmcreynolds.com/?p=388
 * - Firebase DB tutorial - https://www.simplifiedcoding.net/firebase-realtime-database-example-android-application/
 * - Crazy Madlibs App on GitHub (my other app!) - https://github.com/techgalavant/madlibs
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class WelcomeFragment extends Fragment{

    private static final String TAG = WelcomeFragment.class.getSimpleName();

    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.welcome_frag, container, false);


    }



}
