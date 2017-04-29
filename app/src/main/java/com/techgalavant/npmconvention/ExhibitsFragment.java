package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * The purpose of this fragment is to provide the users with links to the relevant exhibits.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ExhibitsFragment extends Fragment{

    public ExhibitsFragment() {
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
        return inflater.inflate(R.layout.exhibits_frag, container, false);
    }

}
