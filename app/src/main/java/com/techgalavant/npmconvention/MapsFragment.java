package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * The purpose of this fragment is to display maps of the convention center and maps to the different locations.
 * The maps are really just downloaded images.
 *
 * Google Maps Help - https://developers.google.com/maps/documentation/urls/android-intents
 *
 * * Spinner help from Ravi Govarthanan - thank you, man!
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Intent.ACTION_VIEW;


public class MapsFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = MapsFragment.class.getSimpleName();

    // an Array list of a hashmap with the maps in it
    ArrayList<HashMap<String, String>> mapsList;
    ArrayList<HashMap<String, String>> onsiteList;
    ArrayList<HashMap<String, String>> offsiteList;

    // Listview to show the maps from the res/raw/ folder
    ListView roomList;

    // Spinner for site selection
    Spinner sitesSpinner;

    public Integer choice=0;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            Log.e(TAG, "MapsFragment tab selected.");

            // Inflate the maps_frag layout
            final View rootView = inflater.inflate(R.layout.maps_frag, container, false);

            // populate the JSON file into an arraylist
            mapsList = new ArrayList<>(); // when the app uses onCreate, it should create a new Arraylist, other wise, it will retain the array list when user swipes from tab to tab
            onsiteList= new ArrayList<>();
            offsiteList = new ArrayList<>();
            // drop-down for onsite, off site or both
            sitesSpinner = (Spinner) rootView.findViewById(R.id.spin_maps);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext().getApplicationContext(),
                    R.array.map_locations, R.layout.spinner_item);

            // apply custom adaptor view, this can be found in the layouts xml
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

            // Apply the adapter to the spinner
            sitesSpinner.setAdapter(adapter);

            sitesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                    String option = parent.getItemAtPosition(pos).toString();

                    if(pos==1){
                        Log.e(TAG, "User selected " + option + " on Maps fragment");
                        ListAdapter adapter = new SimpleAdapter(
                                getActivity().getApplicationContext(), onsiteList,
                                R.layout.listmapitem,
                                new String[]{"mapname"},
                                new int[]{R.id.map_name});

                        // Update adapter and listview based on choice selected

                        roomList.setAdapter(adapter);
                    }

                    if(pos==2){
                        Log.e(TAG, "User selected " + option + " on Maps fragment");
                        ListAdapter adapter = new SimpleAdapter(
                                getActivity().getApplicationContext(), offsiteList,
                                R.layout.listmapitem,
                                new String[]{"mapname"},
                                new int[]{R.id.map_name});

                        // Update adapter and listview based on choice selected

                        roomList.setAdapter(adapter);

                    }
                    if (pos==0) {
                        Log.e(TAG, "User selected " + option + " on Maps fragment");
                        ListAdapter adapter = new SimpleAdapter(
                                getActivity().getApplicationContext(), mapsList,
                                R.layout.listmapitem,
                                new String[]{"mapname"},
                                new int[]{R.id.map_name});

                        // Update adapter and listview based on choice selected

                        roomList.setAdapter(adapter);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    choice = 0;

                }
            });


            // Used to show the events in a listview
            roomList = (ListView) rootView.findViewById(R.id.maplist1);

            // Display the maps in the JSON file
            displayFile();

            return rootView;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return getView();
    }

    // Load JSON file into string
    public String loadJSONFromFile() {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.maps);
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

    // and then use this to display the chapters in a listview
    private void displayFile() {

        // The method getJSONObject returns the JSON object.
        // The method getString returns the string value of the specified key.

        try {
            // Load JSON file into JSON object
            JSONObject jsonObj = new JSONObject(loadJSONFromFile());

            // Getting JSON Array node
            JSONArray maplist = jsonObj.getJSONArray("maps"); // "list" is the node in the JSON file

            // populate the array nodes into a hashmap
            for (int i = 0; i < maplist.length(); i++) {

                JSONObject m = maplist.getJSONObject(i);
                String seq = m.getString("sequence");
                String mapname = m.getString("name");
                String mapfile = "";
                String mapCoord = "";

                // the "name" is not present on every JSON object, so do this if it exists
                if (m.has("imageName")) {
                    mapfile = m.getString("imageName");
                } else {
                    mapCoord = ("geo:" + m.getString("latitude") + "," + m.getString("longitude") + "?q=" + m.getString("latitude") + "," + m.getString("longitude") + "(" + m.getString("name") + ")");
                }

                // the hash map for single map info
                HashMap<String, String> mapinfo = new HashMap<>();

                // add each child node to HashMap key => value
                mapinfo.put("seq", seq);
                mapinfo.put("mapname", mapname);
                mapinfo.put("mapfile", mapfile);
                mapinfo.put("mapCoord", mapCoord);

                // add the map to the map list
                mapsList.add(mapinfo);

                // if the sequence number includes long and lat, then it's offsite, otherwise it's onsite
                // convert the seq number to string 'myNum'
                int myNum = 0; // initialize first!

                try {
                    myNum = Integer.parseInt(seq);
                } catch(NumberFormatException nfe) {
                   Log.e(TAG, "Number Format Exception " + nfe);
                }

                // the maps.json in the res/raw folder contains a 'seq' number and that is a string converted to int 'myNum'
                // to separate the onsite maps from the offsite maps so that the sitesSpinner works properly
                if (myNum<=6){
                    onsiteList.add(mapinfo);
                } else {
                    offsiteList.add(mapinfo);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSONException: " + e.getMessage());

        }

        // set on click listener
        roomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // Take the chapter strings from listview position and populate them in ChapterDetails.java
                Intent intent = new Intent(getActivity(), MapsExpandActivity.class);
                intent.putExtra("mapname", ((HashMap<String, String>) roomList.getAdapter().getItem((int)id)).get("mapname"));
                intent.putExtra("mapfile", ((HashMap<String, String>) roomList.getAdapter().getItem((int)id)).get("mapfile"));
                intent.putExtra("mapCoord", ((HashMap<String, String>) roomList.getAdapter().getItem((int)id)).get("mapCoord"));

                // Create a URI from String
                Uri gmmIntentUri = Uri.parse(intent.getStringExtra("mapCoord"));

                // If it contains latitude and longitude, then launch it in Google Maps
                if (intent.getStringExtra("mapfile").isEmpty()){

                    // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                    Intent mapIntent = new Intent(ACTION_VIEW, gmmIntentUri);
                    // Make the Intent explicit by setting the Google Maps package
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                } else {

                    // Take the chapter strings from listview position and populate them in ChapterDetails.java
                    startActivity(intent);
                }
            }

        });


    }


    @Override
    public void onClick(View view) {
        // Download file to sdcard

    }

}
