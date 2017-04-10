package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * This tab will be used to display the list of events. The events are parsed from a JSON file.
 * The events are grouped using an expandablelistview adapter.
 * Future-State: users will be able to add the content to their own Google Calendar.
 *
 * Checkout - http://stackoverflow.com/questions/3721963/how-to-add-calendar-events-in-android
 * - http://stacktips.com/tutorials/android/how-to-add-event-to-calendar-in-android
 * - https://developers.google.com/google-apps/calendar/v3/reference/calendars/insert
 * - Expandable ListView Adaptor tutorial - http://www.vogella.com/tutorials/AndroidListView/article.html
 * - Expandable ListView Adaptor tutorial - http://www.journaldev.com/9942/android-expandablelistview-example-tutorial
 *
 * JSON Parsing Tutorial - http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
 *
 * If your JSON node starts with [, then we should use getJSONArray() method.
 * If the node starts with {, then we should use getJSONObject() method.
 */

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class ScheduleFragmentB extends Fragment implements View.OnClickListener{
    public static final String TAG = ScheduleFragmentB.class.getSimpleName();

    // Listview to show the contacts
    private ListView lv;

    //Buttons
    private Button btnDownload;
    private Button btnView;

    // A download manager is used to download a file from a URL onto the device
    DownloadManager downloadManager;
    private String jsonDir = "/NPM"; // the name of the directory to store the file
    private String jsonFile = "Events_NPM.json"; // the name of the actual file
    File localFile = new File(Environment.getExternalStorageDirectory()+jsonDir, jsonFile);

    ArrayList<HashMap<String, String>> eventList = new ArrayList<>();

    // Presented as an expandable list
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    // the title will refer to the date (day)
    List<String> expandableListTitle;
    // expandable list detail will show a brief description (topic, presenter, time start to finish).
    HashMap<String, List<String>> expandableListDetail;


    public ScheduleFragmentB() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // If the Program PDF file has been downloaded, then display it.
        // Otherwise show the buttons for users to download it.
        try {
            if (localFile.exists()) {
                Log.e(TAG, "Found " + jsonFile + " in " + jsonDir + ".");

                // Display the program without the download buttons
                View rootView = inflater.inflate(R.layout.schedulec_frag, container, false);
                expandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);

                // populate the JSON file into an arraylist
                eventList = new ArrayList<>();

                // Used to show the events in a listview
                //lv = (ListView) rootView.findViewById(R.id.list);

                // Display the list of events
                displayFile();

                return rootView;

            } else {
                // show screen with download button
                Log.e(TAG, jsonFile + " not found at " + jsonDir);

                // Display the fragment with the download buttons
                View rootView = inflater.inflate(R.layout.schedule_frag, container, false);

                //getting views from layout
                btnDownload = (Button) rootView.findViewById(R.id.btnDownload);
                btnView = (Button) rootView.findViewById(R.id.btnView);

                // Used to show the events in a listview
                lv = (ListView) rootView.findViewById(R.id.list);

                //attaching listener
                btnDownload.setOnClickListener(this);
                btnView.setOnClickListener(this);

                return rootView;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return getView();
    }

    // Load JSON file into string
    public String loadJSONFromFile() {
        String json = null;
        try {
            FileInputStream is = new FileInputStream(localFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Toast.makeText(getContext(),"JSON file loaded from asset", Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.e(TAG, "IOException: " + ex.getMessage());
            Toast.makeText(getContext(),"JSON file load error: " +ex.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;

    }
    // creates an expandable list
    private void expandList() {
        expandableListDetail = ExpandableListDataPump.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(getContext(), expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });


    }

    // CONSIDER: building a handler first in a separate class,
    // and then use this to display the events in a listview
    private void displayFile() {

        File localFile = new File(Environment.getExternalStorageDirectory() + jsonDir, jsonFile);

        // The method getJSONObject returns the JSON object.
        // The method getString returns the string value of the specified key.

        try {
            // Load JSON file into JSON object
            JSONObject jsonObj = new JSONObject(loadJSONFromFile());

            // Getting JSON Array node
            JSONArray events = jsonObj.getJSONArray("events");

            SimpleDateFormat dtStart = new SimpleDateFormat("M/dd/yy h:mm a zzz", Locale.US);
            DateFormat dtDay = new SimpleDateFormat("M/dd/yy", Locale.US);
            DateFormat dtTime = new SimpleDateFormat("h:mm a", Locale.US);
            Date dateStart = null;
            Date dateEnd = null;

            // populate the array nodes into hashmap
            for (int i = 0; i < events.length(); i++) {

                JSONObject c = events.getJSONObject(i);
                String id = c.getString("Event Number");
                String name = c.getString("Event Name");
                String presenter = c.getString("Presenter");
                String description = c.getString("Description");
                String room = c.getString("Room #");
                String start = c.getString("Start");
                String end = c.getString("End");

                // convert the date strings so that it can be used in the array
                try {
                    dateStart = dtStart.parse(start);
                    dateEnd = dtStart.parse(end);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // date / time formats: http://javatechniques.com/blog/dateformat-and-simpledateformat-examples/
                // convert date back to string so that it can go into the array
                String day = dtDay.format(dateStart);

                // convert time back to string so that it can be used in the array
                String starttime = dtTime.format(dateStart);

                // convert end date back to string for the array
                String endtime = dtTime.format(dateEnd);

                String map = c.getString("Map");

                // the hash map for single event
                HashMap<String, String> event = new HashMap<>();
                HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

                // add each child node to HashMap key => value
                event.put("id", id);
                event.put("name", name);
                event.put("presenter", presenter);
                event.put("description", description);
                event.put("room", room);
                event.put("day", day);
                event.put("start", starttime);
                event.put("finish", endtime);
                event.put("map", map);

                // Create a list for each day of the week and
                // add the event if it falls on that day
                if (day.equals("SUN")) {
                    List<String> sunday = new ArrayList<String>();
                    eventList.add(event);
                } else if (day.equals("MON")){
                    List<String> monday = new ArrayList<String>();
                    eventList.add(event);
                } else if (day.equals("TUE")){
                    List<String> tuesday = new ArrayList<String>();
                    eventList.add(event);
                } else if (day.equals("WED")){
                    List<String> wedday = new ArrayList<String>();
                    eventList.add(event);
                } else if (day.equals("THU")) {
                    List<String> thurday = new ArrayList<String>();
                    eventList.add(event);
                } else if (day.equals("FRI")) {
                    List<String> friday = new ArrayList<String>();
                    eventList.add(event);
                } else if (day.equals("SAT")) {
                    List<String> satday = new ArrayList<String>();
                    eventList.add(event);
                }
                // add the event into the event list
                //eventList.add(event);

                // REFER TO - http://www.journaldev.com/9942/android-expandablelistview-example-tutorial
                // the expandableListDetail object is used to map the group header strings to their respective children using an ArrayList of Strings.
                /*expandableListDetail.put("SUN", sunday);
                expandableListDetail.put("MON", monday);
                expandableListDetail.put("TUE", tueday);
                expandableListDetail.put("WED", wedday);
                expandableListDetail.put("THU", thurday);
                expandableListDetail.put("FRI", friday);
                expandableListDetail.put("SAT", satday);*/


            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSONException: " + e.getMessage());

        }


        // show the eventlist in a listview adaptor
        ListAdapter adapter = new SimpleAdapter(
                getActivity().getApplicationContext(), eventList,
                R.layout.listeventitem, new String[]{"id", "name", "description", "day",
                "start", "finish"}, new int[]{R.id.eid, R.id.ename,
                R.id.edesc, R.id.estart, R.id.efinish, R.id.emap});

        lv.setAdapter(adapter);

    }


    @Override
    public void onClick(View view) {
        // Download file to sdcard
        if (view == btnDownload) {
            downloadManager = (DownloadManager)getContext().getSystemService(Context.DOWNLOAD_SERVICE);

            // Location of the file to be downloaded
            Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/npm-convention.appspot.com/o/Events_NPM.json?alt=media&token=65a29d0a-5f78-487a-9d76-41c6c31762e0");

            DownloadManager.Request request = new DownloadManager.Request(uri);

            request.setDestinationInExternalPublicDir(jsonDir,jsonFile);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle(jsonFile);
            Long reference = downloadManager.enqueue(request);
        }
        // View the downloaded file which has already been stored locally on the user's device
        else if (view == btnView) {
            if (localFile.exists()){
                displayFile();
            } else {
                Toast.makeText(getContext(),"Please download the file to view it.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
