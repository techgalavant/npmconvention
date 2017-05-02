package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * This class is used to display the event details from the ScheduleFragment in a card-like layout.
 * Users should also be able to add the event to their own favorites list or ideally onto their own Google calendar.
 * Checkout - http://stackoverflow.com/questions/3721963/how-to-add-calendar-events-in-android
 * - http://stacktips.com/tutorials/android/how-to-add-event-to-calendar-in-android
 * - https://developers.google.com/google-apps/calendar/v3/reference/calendars/insert
 *
 * Credit to CheeseSquare - - https://github.com/chrisbanes/cheesesquare
 */

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import static com.techgalavant.npmconvention.R.id.edesc;
import static com.techgalavant.npmconvention.R.id.epresent;
import static com.techgalavant.npmconvention.R.id.etitle;


public class EventDetails extends AppCompatActivity {
    private static final String TAG = EventDetails.class.getSimpleName();

    TextView evtTitle, evtDesc, evtPresenter, evtTime, evtRoom;
    ImageView evtMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);

        Intent intent = getIntent();

        // get the event list items from ScheduleFragment
        String complete_event = (intent.getStringExtra("complete_event"));
        Log.e(TAG, "Event Detail for the list item - " + complete_event); // Log the event details to ensure it's displaying the correct information

        final String eventId = intent.getStringExtra("evid");
        final String eventTitle = intent.getStringExtra("name");
        final String eventDesc = intent.getStringExtra("description");
        String eventPresent = intent.getStringExtra("presented");
        String eventTime = (intent.getStringExtra("day")) + " " + (intent.getStringExtra("start")) + " to " + (intent.getStringExtra("finish"));
        String eventMap = intent.getStringExtra("map");
        final String eventRoom = "Room: " + (intent.getStringExtra("room"));
        final String eventStart = intent.getStringExtra("start");
        final String eventEnd = intent.getStringExtra("finish");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Let the user use the back button to return to ScheduleFragment rather than showing a back arrow in the image.
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // The collapsing toolbar shows a random image in it as well as the event title from the list event
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(eventId);

        // load the top random image from EventImages
        loadBackdrop();

        // populate the textviews and imageviews with the specific event list items
        evtTitle = (TextView) findViewById(etitle);
        evtDesc = (TextView) findViewById(edesc);
        evtPresenter = (TextView) findViewById(epresent);
        evtTime = (TextView) findViewById(R.id.event_time);
        evtRoom = (TextView) findViewById(R.id.room);
        evtMap = (ImageView) findViewById(R.id.map_location);

        evtTitle.setText(eventTitle);
        evtDesc.setText(eventDesc);
        evtPresenter.setText(eventPresent);
        evtTime.setText(eventTime);
        evtRoom.setText(eventRoom);

        // TODO locate map from string
        // Use a sample map for the time being
        evtMap.setImageResource(R.drawable.sample_map);


        // Use a FAB to add the event to the user's favorites or to their Google calendar
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Log if calendar_add button was pressed
                Log.e(TAG, "Add calendar button pressed");

                // Reference: https://code.tutsplus.com/tutorials/android-essentials-adding-events-to-the-users-calendar--mobile-8363
                // See also:
                // - http://stackoverflow.com/questions/20947075/setting-date-content-values-on-android-calendar
                // - http://www.grokkingandroid.com/androids-calendarcontract-provider/

                Intent calIntent = new Intent(Intent.ACTION_INSERT);
                calIntent.setData(CalendarContract.Events.CONTENT_URI);
                calIntent.putExtra(CalendarContract.Events.TITLE, "NPM - " + eventId);
                calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "Title: " + eventTitle + " Description: " + eventDesc + ". " + eventRoom);
                calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, eventRoom);
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventStart); // TODO fix start time with inMills
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eventEnd); // TODO fix end time with inMills
                // TODO add map for this as an attachment
                startActivity(calIntent);
            }
        });
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


}