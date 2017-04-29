package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * The purpose of this fragment is to display contact information for the chapters across the country.
 *
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ChaptersFragment extends Fragment{

    public ChaptersFragment() {
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
        return inflater.inflate(R.layout.chapters_frag, container, false);
    }

}
