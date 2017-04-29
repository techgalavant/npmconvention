package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * The purpose of this fragment is to display maps of the convention center and maps to the different locations.
 * The maps are really just downloaded images.
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
