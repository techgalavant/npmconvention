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
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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

        String eventId = intent.getStringExtra("evid");
        String eventTitle = intent.getStringExtra("name");
        String eventDesc = intent.getStringExtra("description");
        String eventPresent = intent.getStringExtra("presented");
        String eventTime = (intent.getStringExtra("day")) + " " + (intent.getStringExtra("start")) + " to " + (intent.getStringExtra("finish"));
        String eventMap = intent.getStringExtra("map");
        String eventRoom = "Room: " + (intent.getStringExtra("room"));

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