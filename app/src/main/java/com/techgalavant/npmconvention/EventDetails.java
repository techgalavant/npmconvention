package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * This class is used to display the event details from the EventFragment in a card-like layout.
 * Users can add the event to their own Google calendar.
 *
 * References
 * - https://developers.google.com/google-apps/calendar/v3/reference/calendars/insert
 *
 * Collapsible view & card layout credit to CheeseSquare - - https://github.com/chrisbanes/cheesesquare
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.techgalavant.npmconvention.R.id.edesc;
import static com.techgalavant.npmconvention.R.id.epresent;
import static com.techgalavant.npmconvention.R.id.etitle;


public class EventDetails extends AppCompatActivity {
    private static final String TAG = EventDetails.class.getSimpleName();

    TextView evtTitle, evtDesc, evtPresenter, evtTime, evtRoom;
    ImageView evtMap;
    long sTime; // event start time in milliseconds
    long eTime; // event end time in milliseconds


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);

        Intent intent = getIntent();

        // get the event list items from EventFragment
        String complete_event = (intent.getStringExtra("complete_event"));
        Log.e(TAG, "Event Detail for the list item - " + complete_event); // Log the event details to ensure it's displaying the correct information

        final String eventId = intent.getStringExtra("evid");
        final String eventTitle = intent.getStringExtra("name");
        final String eventDesc = intent.getStringExtra("description");
        String eventPresent = intent.getStringExtra("presented");
        String eventTime = (intent.getStringExtra("day")) + " " + (intent.getStringExtra("start")) + " to " + (intent.getStringExtra("finish")); // Used to display in the TextView
        String eventMap = intent.getStringExtra("map");
        final String eventRoom = "Room: " + (intent.getStringExtra("room"));
        final String event_start = intent.getStringExtra("start_mills"); // used to convert to milliseconds for adding to the Google Calendar
        final String event_end = intent.getStringExtra("end_mills"); // used to convert to milliseconds for adding to the Google Calendar

        // Use SimpleDateFormat to convert the strings for the event times to Date in milliseconds so that it can be added to the user's Google Calendar
        final SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yy h:mm a zzz", Locale.US);
        try {
            Date sDate = sdf.parse(event_start);
            sTime = sDate.getTime(); // convert sDate to milliseconds - sDate is actually the start day and time
            String starttime = sdf.format(sDate);
            Date eDate = sdf.parse(event_end);
            eTime = eDate.getTime(); // convert eDate to milliseconds - eDate is actually in the end date and time
            String endtime = sdf.format(eDate);
            Log.e(TAG, "Event Start Time in milliseconds: " + sTime + " Start Time as string: " + starttime);
            Log.e(TAG, "Event End Time in milliseconds" + eTime + " End Time as string: " + endtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Let the user use their own back button to return to EventFragment rather than showing a back arrow in the image.
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

        /*if (eventMap.contains("Convention")) {
            evtMap.setImageResource(R.drawable.sample_map2);
        } else if (eventMap.contains("Registration")) {
            evtMap.setImageResource(R.drawable.sample_map);
        } else if (eventMap.contains("Hilton Hotel")) {
            evtMap.setImageResource(R.drawable.sample_map3);
        } else {
            evtMap.setImageResource(R.drawable.sample_map4);
        }*/

        // Use a FAB to add the event to the user's favorites or to their Google calendar
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Log if calendar_add button was pressed
                Log.e(TAG, "Add calendar button pressed");


                // Insert event to user's calendar
                // Reference:
                // - https://code.tutsplus.com/tutorials/android-essentials-adding-events-to-the-users-calendar--mobile-8363
                // - http://stackoverflow.com/questions/20947075/setting-date-content-values-on-android-calendar
                // - http://www.grokkingandroid.com/androids-calendarcontract-provider/

                Intent calIntent = new Intent(Intent.ACTION_INSERT);
                calIntent.setData(CalendarContract.Events.CONTENT_URI);
                calIntent.putExtra(CalendarContract.Events.TITLE, "NPM Event - " + eventId);
                calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "NPM Event Session Title: " + eventTitle + ". " + eventDesc);
                calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, eventRoom);
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, sTime); // must be in milliseconds
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eTime); // time must be in milliseconds

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