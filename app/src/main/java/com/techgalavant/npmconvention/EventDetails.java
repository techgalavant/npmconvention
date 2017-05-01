package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * This class is used to display the event details from the ScheduleFragment in a card-like layout.
 * Credit to CheeseSquare - - https://github.com/chrisbanes/cheesesquare
 *
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class EventDetails extends AppCompatActivity {
    private static final String TAG = EventDetails.class.getSimpleName();

    TextView evtDesc, evtPresenter, evtTime;
    ImageView evtMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);

        Intent intent = getIntent();

            String eventTitle = intent.getStringExtra("Title");
            String eventDesc = intent.getStringExtra("Desc");
            String eventPresent = intent.getStringExtra("Presenter");
            String eventTime = intent.getStringExtra("eTime");
            String eventMap = intent.getStringExtra("Map");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // It should bring the user back to the ScheduleFragment
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(eventTitle);

        // load the top random image from EventImages
        loadBackdrop();

        evtDesc = (TextView) findViewById(R.id.edesc);
        evtPresenter = (TextView) findViewById(R.id.epresent);
        evtTime = (TextView) findViewById(R.id.event_time);
        evtMap = (ImageView) findViewById(R.id.map_location);

        evtDesc.setText(eventDesc);
        evtPresenter.setText(eventPresent);
        evtTime.setText(eventTime);
        evtMap.setImageResource(R.drawable.sample_map);
    }

    // Takes random images from EventImages class and uses it for the backdrop on the top
    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(EventImages.getRandomImage()).centerCrop().into(imageView);
    }


    // Possibly setup a menu button so that users can view their favorites?
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    // See reponse here: http://stackoverflow.com/questions/20947075/setting-date-content-values-on-android-calendar
    // REF also - http://www.grokkingandroid.com/androids-calendarcontract-provider/

    // Use to add the event -- look at FAB action button
     public class addEvent {

       /*  Intent calIntent = new Intent(Intent.ACTION_INSERT)
                 .setData(CalendarContract.CONTENT_URI)
                 .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, estart)
                 .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, efinish)
                 .putExtra(CalendarContract.Events.TITLE, eid + ": " + ename)
                 .putExtra(CalendarContract.Events.DESCRIPTION, edesc + " See location on " + emap)
                 .putExtra(CalendarContract.Events.EVENT_LOCATION, "See " + emap)
                 .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

         startActivity(calIntent);*/
     }


}