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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.techgalavant.npmconvention.R.id.edesc;
import static com.techgalavant.npmconvention.R.id.epresent;
import static com.techgalavant.npmconvention.R.id.etitle;


public class EventDetails extends AppCompatActivity {
    private static final String TAG = EventDetails.class.getSimpleName();

    TextView evtTitle, evtDesc, evtPresenter, evtWeekday, evtTime, evtRoom;
    ImageView evtMap;
    long sTime; // event start time in milliseconds
    long eTime; // event end time in milliseconds

    // used for sending event feedback
    private Button mashIt_btn, clearIt_btn;
    private EditText inWord1, inWord2;
    private DatabaseReference myFeedback;

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

        // some events don't have presenters listed, so friendly message to display that they are not listed
        if (eventPresent.isEmpty()){
            eventPresent = "Not Available";
        }

        String eventWeekDay = intent.getStringExtra("weekday");


        String eventTime = (intent.getStringExtra("day")) + " " + (intent.getStringExtra("start")) + " to " + (intent.getStringExtra("finish")); // Used to display in the TextView
        final String eventMap = intent.getStringExtra("map");
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
        assert getSupportActionBar() != null;

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
        evtWeekday = (TextView) findViewById(R.id.event_weekday);
        evtTime = (TextView) findViewById(R.id.event_time);
        evtRoom = (TextView) findViewById(R.id.room);
        evtMap = (ImageView) findViewById(R.id.map_location);
        inWord1 = (EditText) findViewById(R.id.survey1);

        mashIt_btn = (Button) findViewById(R.id.mashIt);
        clearIt_btn = (Button) findViewById(R.id.clearIt);

        evtTitle.setText(eventTitle);
        evtDesc.setText(eventDesc);
        evtPresenter.setText(eventPresent);
        evtWeekday.setText(eventWeekDay);
        evtTime.setText(eventTime);
        evtRoom.setText(eventRoom);


        String mapFile = eventMap;  // redundant, but copied this if - then - else - if below from MapsExpandActivity.java

        if (mapFile.contains("level1")) {
            evtMap.setImageResource(R.drawable.cincinnati_level1_sml);
            Log.e(TAG, mapFile + " - Mapped to cinn_level_1");
        } else if (mapFile.contains("level2")) {
            evtMap.setImageResource(R.drawable.cincinnati_level2_sml);
            Log.e(TAG, mapFile + " - Mapped to cinn_level_2");
        } else if (mapFile.contains("level3")) {
            evtMap.setImageResource(R.drawable.cincinnati_level3_sml);
            Log.e(TAG, mapFile + " - Mapped to cinn_level_3");
        }  else if (mapFile.contains("cin2")) {
            evtMap.setImageResource(R.drawable.millennium_cin2_sml);
            Log.e(TAG, mapFile + " - Mapped to millennium_cin2");
        } else {
            evtMap.setImageResource(R.drawable.sample_map);
            Log.e(TAG, mapFile + " - Mapped to sample_map");
        }

        evtMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Log.e(TAG, "User clicked on map image in EventDetails");

                // first check to make sure that the map image field is not null
                if (eventMap.isEmpty()){
                    // if no map is provided
                    // Toast.makeText(getApplicationContext(), "No map provided for this event", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "No map indicated in eventMap.");
                } else {
                    // launch intent to show map in new activity so that user can pinch/zoom to view it
                    Intent intent = new Intent(getApplication().getApplicationContext(), MapsExpandActivity.class);
                    intent.putExtra("mapname", eventRoom);
                    intent.putExtra("mapfile", eventMap);
                    startActivity(intent);

                    Log.e(TAG, "Launched " + eventMap + " in new intent");
                }

            }
        });


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

        // Store the feedback on the event in Firebase
        mashIt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Feedback = inWord1.getText().toString();
                myFeedback = MyFirebaseUtil.getDatabase().getReference(eventId);

                Log.e(TAG, "Firebase reference is set to " + myFeedback);

                // Create a new list of items to be stored.
                createList(eventTitle, Feedback);

                // Launch an AlertDialog box to post a confirmation message
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EventDetails.this);

                // Set Dialog box title
                alertDialog.setTitle("Thank You!");

                // Set Dialog message
                alertDialog.setMessage("Your feedback has been captured.");

                // Sets icon in the AlertDialog window
                alertDialog.setIcon(R.drawable.ic_mesg);

                // Sets operation for when "Close" button is selected
                alertDialog.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the AlertDialog box and return to MainActivity
                        Log.e(TAG, "User selected CLOSE button on AlertDialog in EventDetails.java");
                        dialog.cancel();
                        inWord1.setText(getResources().getString(R.string.feedback_thanks));
                        Log.e(TAG, "Event feedback captured and field was changed to thank user for their feedback.");
                    }
                });

                // Show AlertDialog box
                alertDialog.show();

            }

        });

        // Clear the feedback form
        clearIt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inWord1.setText("");
                Log.e(TAG, "clearIt_btn was used to reset entry");
            }
        });


    }
        // Creates a new list of feedback words in the Firebase Database
        private void createList(String EventTitle, String Feedback) {

            Survey survey = new Survey(EventTitle, Feedback);

            myFeedback.setValue(survey);

            addWordChangeListener();
        }

        // Updates the list of feedback words in the Firebase Database
        private void updateList(String EventTitle, String Feedback) {

            if (!TextUtils.isEmpty(EventTitle))
                myFeedback.child("EventTitle").setValue(EventTitle);
            if (!TextUtils.isEmpty(Feedback))
                myFeedback.child("Feedback").setValue(Feedback);
        }

        private void addWordChangeListener() {
            // User data change listener
            myFeedback.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Survey survey = dataSnapshot.getValue(Survey.class);

                    // Check for null
                    if (survey == null) {
                        Log.e(TAG, "No feedback has been found.");
                        return;
                    } else {

                        Log.e(TAG, "Feedback was updated.");

                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.e(TAG, "Database error. See onCancelled", error.toException());
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



