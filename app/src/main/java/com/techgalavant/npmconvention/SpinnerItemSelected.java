package com.techgalavant.npmconvention;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Mike Fallon
 *
 * The purpose of this class is to be used with the relevent spinner drop-downs with the fragments.
 *
 */

public class SpinnerItemSelected implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            String choice = parent.getItemAtPosition(pos).toString();

            if (choice.contains("On")){
                Snackbar.make(view, "Selected: " + choice, Snackbar.LENGTH_LONG).show();



            } else if (choice.contains("Off")){

            } else {

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }