package com.techgalavant.npmconvention;

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

class ExpandableListDataPump {
    private static final String TAG = ExpandableListDataPump.class.getSimpleName();

    // Refer to the Events JSON file which has been downloaded by the user
    private String jsonDir = "/NPM"; // the name of the directory to store the file
    private String jsonFile = "Events_NPM.json"; // the name of the actual file
    private File localFile = new File(Environment.getExternalStorageDirectory()+jsonDir, jsonFile);

    // Load JSON file into string
    private String loadJSONFromFile() {
        String json = null;
        try {
            FileInputStream is = new FileInputStream(localFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Log.e(TAG, "loadJSONFromFile SUCCESSFUL");
            //Toast.makeText(getContext(),"JSON file loaded from asset", Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.e(TAG, "IOException: " + ex.getMessage());
            //Toast.makeText(getContext(),"JSON file load error: " +ex.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;

    }

    public HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        // The method getJSONObject returns the JSON object.
        // The method getString returns the string value of the specified key.

        try {
            // Load JSON file into JSON object
            JSONObject jsonObj = new JSONObject(loadJSONFromFile());

            // Getting JSON Array node
            JSONArray events = jsonObj.getJSONArray("events");

            // Group events by Date
            JSONArray groupJsonByDay = new JSONArray();

            SimpleDateFormat dtStart = new SimpleDateFormat("M/dd/yy h:mm a zzz", Locale.US);
            DateFormat dtDay = new SimpleDateFormat("M/dd/yy", Locale.US);
            DateFormat dtTime = new SimpleDateFormat("h:mm a", Locale.US);
            Date dateStart = null;
            Date dateEnd = null;

            // Events for each day of the week
            List<String> sunday = new ArrayList<String>();
            List<String> monday = new ArrayList<String>();
            List<String> tuesday = new ArrayList<String>();
            List<String> wedday = new ArrayList<String>();
            List<String> thurday = new ArrayList<String>();
            List<String> friday = new ArrayList<String>();
            List<String> satday = new ArrayList<String>();

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
                //HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

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

                // With help from http://stackoverflow.com/questions/39911460/how-to-group-json-array-data-if-exits-the-same-value-android-studio
                // this will put all the JSON items with the same sub-name of Day
                // REFER TO - http://www.journaldev.com/9942/android-expandablelistview-example-tutorial/*
                // the expandableListDetail object is used to map the group header strings to their respective children using an ArrayList of Strings.
                // groupJsonByDay --> http://stackoverflow.com/questions/39911460/how-to-group-json-array-data-if-exits-the-same-value-android-studio

                // Create a list for each day of the week and
                // add the event if it falls on that day
                switch (day){
                    case "SUN":
                        sunday.add(id);  // adds just the event id to the Sunday list
                        groupJsonByDay.put(c);  // adds the JSON object to an array groupJsonByDay
                        //eventList.add(event);  // adds the event into an event list array --> maybe I should make a separate list for each day?
                        expandableListDetail.put("SUN", sunday);  //  puts the event into an expandable list detail for "SUN"
                        break;
                    case "MON":
                        monday.add(id);  // adds the event id to the Sunday list
                        groupJsonByDay.put(c);  // adds the JSON object to an array groupJsonByDay
                        //eventList.add(event);  // adds the event into an event list array --> maybe I should make a separate list for each day?
                        expandableListDetail.put("MON", monday);  //  puts the event into an expandable list detail for "SUN"
                        break;
                    case "TUE":
                        tuesday.add(id);  // adds the event id to the Sunday list
                        groupJsonByDay.put(c);  // adds the JSON object to an array groupJsonByDay
                        //eventList.add(event);  // adds the event into an event list array --> maybe I should make a separate list for each day?
                        expandableListDetail.put("TUE", tuesday);  //  puts the event into an expandable list detail for "SUN"
                        break;
                    case "WED":
                        wedday.add(id);  // adds the event id to the Sunday list
                        groupJsonByDay.put(c);  // adds the JSON object to an array groupJsonByDay
                        //eventList.add(event);  // adds the event into an event list array --> maybe I should make a separate list for each day?
                        expandableListDetail.put("WED", wedday);  //  puts the event into an expandable list detail for "SUN"
                        break;
                    case "THU":
                        thurday.add(id);  // adds the event id to the Sunday list
                        groupJsonByDay.put(c);  // adds the JSON object to an array groupJsonByDay
                        //eventList.add(event);  // adds the event into an event list array --> maybe I should make a separate list for each day?
                        expandableListDetail.put("THU", thurday);  //  puts the event into an expandable list detail for "SUN"
                        break;
                    case "FRI":
                        friday.add(id);  // adds the event id to the Sunday list
                        groupJsonByDay.put(c);  // adds the JSON object to an array groupJsonByDay
                        //eventList.add(event);  // adds the event into an event list array --> maybe I should make a separate list for each day?
                        expandableListDetail.put("FRI", friday);  //  puts the event into an expandable list detail for "SUN"
                        break;
                    case "SAT":
                        satday.add(id);  // adds the event id to the Sunday list
                        groupJsonByDay.put(c);  // adds the JSON object to an array groupJsonByDay
                        //eventList.add(event);  // adds the event into an event list array --> maybe I should make a separate list for each day?
                        expandableListDetail.put("SAT", satday);  //  puts the event into an expandable list detail for "SUN"
                        break;
                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSONException: " + e.getMessage());

        }

        return expandableListDetail;

    }
}