package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * The purpose of this activity is to display the map image from the MapsFragment
 * The user may also launch this activity from the EventDetails when they click on a map image
 *
 * This class uses the TouchImageView.java library to detect pinch/zoom gesture.
 *
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;


public class MapsExpandActivity extends AppCompatActivity {

    private static final String TAG = MapsExpandActivity.class.getSimpleName();

    TextView mapTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_image_zoom);

        Intent intent = getIntent();

        // get the intents from MapsFragment
        //final String seqId = intent.getStringExtra("seq");
        final String mapName = intent.getStringExtra("mapname");
        final String mapFile = intent.getStringExtra("mapfile");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the return arrow?
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // populate the textview and custom imageview with the map
        mapTitle = (TextView) findViewById(R.id.map_title);
        TouchImageView evtMap = (TouchImageView) findViewById(R.id.map_image);


        mapTitle.setText(mapName);

        if (mapFile.contains("level1")) {
            evtMap.setImageResource(R.drawable.cinn_level_1);
            Log.e(TAG, mapFile + " - Mapped to cinn_level_1");
        } else if (mapFile.contains("level2")) {
            evtMap.setImageResource(R.drawable.cinn_level_2);
            Log.e(TAG, mapFile + " - Mapped to cinn_level_2");
        } else if (mapFile.contains("level3")) {
            evtMap.setImageResource(R.drawable.cinn_level_3);
            Log.e(TAG, mapFile + " - Mapped to cinn_level_3");
        }  else if (mapFile.contains("cin2")) {
            evtMap.setImageResource(R.drawable.millennium_cin2);
            Log.e(TAG, mapFile + " - Mapped to millennium_cin2");
        } else {
            evtMap.setImageResource(R.drawable.sample_map);
            Log.e(TAG, mapFile + " - Mapped to sample_map");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }
}
