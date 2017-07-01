package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * This tab will be used to display the list of events. The events are parsed from a JSON file.
 * Users will be able to click on an event to see more details and to save it to their favorites.
 *
 * Credits
 *  - JSON Parsing Tutorial - http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
 *
 * If your JSON node starts with [, then we should use getJSONArray() method.
 * If the node starts with {, then we should use getJSONObject() method.
 */

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
import java.util.Locale;


public class EventFragment extends Fragment implements View.OnClickListener{
    public static final String TAG = EventFragment.class.getSimpleName();

    // Listview to show the events
    private ListView lv;

    //Buttons
    private Button btnDownload;
    private Button btnView;

    // A download manager is used to download a file from a URL onto the device
    DownloadManager downloadManager;
    private String jsonDir = "/NPM"; // the name of the directory to store the file
    private String jsonFile = "Events_NPM2017.json"; // the name of the actual file
    File localFile = new File(Environment.getExternalStorageDirectory()+jsonDir, jsonFile);

    ArrayList<HashMap<String, String>> eventList = new ArrayList<>();
    ArrayList<HashMap<String, String>> sunList = new ArrayList<>();
    ArrayList<HashMap<String, String>> monList = new ArrayList<>();
    ArrayList<HashMap<String, String>> tueList = new ArrayList<>();
    ArrayList<HashMap<String, String>> wedList = new ArrayList<>();
    ArrayList<HashMap<String, String>> thuList = new ArrayList<>();
    ArrayList<HashMap<String, String>> friList = new ArrayList<>();
    ArrayList<HashMap<String, String>> satList = new ArrayList<>();


    public EventFragment() {
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

                // Display the events if the events JSON file has already been downloaded
                View rootView = inflater.inflate(R.layout.events_frag, container, false);

                // populate the JSON file into an arraylist
                eventList = new ArrayList<>();

                // Used to show the events in a listview
                lv = (ListView) rootView.findViewById(R.id.list);

                // Display the list of events
                displayFile();

                return rootView;

            } else {
                // show screen with download button
                Log.e(TAG, jsonFile + " not found at " + jsonDir);

                // Display the fragment with the download buttons
                View rootView = inflater.inflate(R.layout.event_frag, container, false);

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
            //Toast.makeText(getContext(),"JSON file loaded from asset", Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.e(TAG, "IOException: " + ex.getMessage());
            //Toast.makeText(getContext(),"JSON file load error: " +ex.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;

    }

    // CONSIDER: building a handler first in a separate class,
    // and then use this to display the events in a listview
    private void displayFile() {

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
            DateFormat dtDayOfWeek = new SimpleDateFormat("EEE", Locale.US);
            Date dateStart = null;
            Date dateEnd = null;

            // populate the array nodes into a hashmap
            for (int i = 0; i < events.length(); i++) {

                JSONObject c = events.getJSONObject(i);
                String evid = c.getString("Event Number");
                String name = c.getString("Event Name");
                String presented = c.getString("Presenter");
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

                // convert date to day of the week for the array
                String dayofweek = dtDayOfWeek.format(dateStart);

                String map = c.getString("Map");

                // the hash map for single event
                HashMap<String, String> event = new HashMap<>();

                // add each child node to HashMap key => value
                event.put("evid", evid);
                event.put("name", name);
                event.put("presented", presented);
                event.put("description", description);
                event.put("room", room);
                event.put("day", day);
                event.put("start", starttime);
                event.put("finish", endtime);
                event.put("weekday", dayofweek);
                event.put("map", map);
                event.put("start_mills", start);
                event.put("end_mills", end);


                // add the event into the event list
                eventList.add(event);

                // adds the event to the appropriate day of the week
                // TODO add the event to the expandableListDetail
                switch (dayofweek){
                    case "Sun":
                        sunList.add(event);
                        break;
                    case "Mon":
                        monList.add(event);
                        break;
                    case "Tue":
                        tueList.add(event);
                        break;
                    case "Wed":
                        wedList.add(event);
                        break;
                    case "Thu":
                        thuList.add(event);
                        break;
                    case "Fri":
                        friList.add(event);
                        break;
                    case "Sat":
                        satList.add(event);
                        break;
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSONException: " + e.getMessage());

        }



        // use this adaptor to show the event list item on listeventitem.xml
        ListAdapter adapter = new SimpleAdapter(
                getActivity().getApplicationContext(), eventList,
                R.layout.listeventitem,
                new String[]{"evid", "name", "description", "weekday", "day", "start", "finish"},
                new int[]{R.id.eid, R.id.ename, R.id.edesc, R.id.eweekday, R.id.eday, R.id.estart, R.id.efinish});

        // List event adaptor
        lv.setAdapter(adapter);

        // set on click listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // Take the event strings from listview position and populate them in EventDetails.java
                Intent intent = new Intent(getActivity(), EventDetails.class);
                intent.putExtra("complete_event", parent.getItemAtPosition(position).toString()); // Pass the entire event info so I can check it in EventDetails
                intent.putExtra("evid", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("evid"));
                intent.putExtra("start", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("start"));
                intent.putExtra("map", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("map"));
                intent.putExtra("description", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("description"));
                intent.putExtra("day", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("day"));
                intent.putExtra("weekday", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("weekday"));
                intent.putExtra("name", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("name"));
                intent.putExtra("presented", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("presented"));
                intent.putExtra("finish", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("finish"));
                intent.putExtra("room", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("room"));
                intent.putExtra("start_mills", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("start_mills")); // need time in mills for adding to user's Google calendar
                intent.putExtra("end_mills", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("end_mills"));

                startActivity(intent);
                }

        });

    }

    @Override
    public void onClick(View view) {
        // Download file to sdcard
        if (view == btnDownload) {
            downloadManager = (DownloadManager)getContext().getSystemService(Context.DOWNLOAD_SERVICE);

            // Location of the file to be downloaded
            Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/npm-convention.appspot.com/o/2017Events.json?alt=media&token=2bc91c65-c17f-418d-b12a-a1e9e8ee7cdd");

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
