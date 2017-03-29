package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * Credit to Android Hive - http://www.androidhive.info/2015/09/android-material-design-working-with-tabs/
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MapsFragment extends Fragment{

    public MapsFragment() {
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
        return inflater.inflate(R.layout.maps_frag, container, false);
    }

}
