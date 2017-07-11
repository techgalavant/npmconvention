package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * Used with SearchGetSet
 * This will load the Events JSON file in the NPM directory and then put it into an Array list
 *
 * Credit - http://manishkpr.webheavens.com/android-autocompletetextview-example-json/
 *
 * USED with EventFragmentB.java
 */

import android.os.Environment;
import android.util.Log;

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


public class JsonParse {
    public static final String TAG = JsonParse.class.getSimpleName();

    private String jsonDir = "/NPM"; // the name of the directory to store the file
    private String jsonFile = "Events_NPM.json"; // the name of the actual file
    File localFile = new File(Environment.getExternalStorageDirectory()+jsonDir, jsonFile);

    // Used to put all the events into an EventsList
    ArrayList<HashMap<String, String>> eventsList = new ArrayList<>();

    public JsonParse(){

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

        } catch (IOException ex) {
            ex.printStackTrace();
            Log.e(TAG, "IOException: " + ex.getMessage());
            return null;
        }
        return json;

    }

    public List<SearchGetSet> getParseJsonWCF(String sName) {

        List<SearchGetSet> ListData = new ArrayList<SearchGetSet>();

        try {
            // Load JSON file into JSON object
            JSONObject jsonObj = new JSONObject(loadJSONFromFile());

            // Getting JSON Array node
            JSONArray events = jsonObj.getJSONArray("events");

            // Convert the timestamp in the JSON to something more familiar
            SimpleDateFormat dtStart = new SimpleDateFormat("M/dd/yy h:mm a zzz", Locale.US);
            DateFormat dtDay = new SimpleDateFormat("M/dd/yy", Locale.US);
            DateFormat dtTime = new SimpleDateFormat("h:mm a", Locale.US);
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

                String map = c.getString("Map");

                // Add the JSON data to the SearchGetSet
                ListData.add(new SearchGetSet(evid,name,presented,description,room,day));

                /*
                // the hash map with all the data for an event
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
                event.put("map", map);
                event.put("start_mills", start);
                event.put("end_mills", end);

                // add the data for this event into the event list (a collection of events)
                eventsList.add(event);
                */

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSONException: " + e.getMessage());

        }


        return ListData;

    }

}