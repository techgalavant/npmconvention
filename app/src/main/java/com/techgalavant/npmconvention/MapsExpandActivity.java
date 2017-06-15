package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * The purpose of this activity is to display the map image from the MapsFragment
 *
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;


public class MapsExpandActivity extends AppCompatActivity {

    private static final String TAG = MapsExpandActivity.class.getSimpleName();

    TextView mapTitle;
    ImageView evtMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_image);

        Intent intent = getIntent();

        // get the intents from MapsFragment
        final String seqId = intent.getStringExtra("seq");
        final String mapName = intent.getStringExtra("mapname");
        final String mapFile = intent.getStringExtra("mapfile");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the return arrow?
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // populate the textview and imageview with the map
        mapTitle = (TextView) findViewById(R.id.map_title);
        evtMap = (ImageView) findViewById(R.id.map_image);

        mapTitle.setText(mapName);

        if (mapFile.contains("level1")) {
            evtMap.setImageResource(R.drawable.cincinnati_level1);
            Log.e(TAG, mapFile + " - Mapped to cincinnati_level1");
        } else if (mapFile.contains("level2")) {
            evtMap.setImageResource(R.drawable.cincinnati_level2);
            Log.e(TAG, mapFile + " - Mapped to cincinnati_level2");
        } else if (mapFile.contains("level3")) {
            evtMap.setImageResource(R.drawable.cincinnati_level3);
            Log.e(TAG, mapFile + " - Mapped to cincinnati_level3");
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
