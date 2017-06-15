package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * The purpose of this fragment is to display maps of the convention center and maps to the different locations.
 * The maps are really just downloaded images.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


public class MapsFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = MapsFragment.class.getSimpleName();

    // an Array list of a hashmap with the maps in it
    ArrayList<HashMap<String, String>> mapsList = new ArrayList<>();

    // Listview to show the maps from the res/raw/ folder
    ListView roomList;

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
            View rootView = inflater.inflate(R.layout.maps_frag, container, false);

            // populate the JSON file into an arraylist
            mapsList = new ArrayList<>();

            // Used to show the chapters in a listview
            roomList = (ListView) rootView.findViewById(R.id.maplist1);

            // Display the maps in the JSON file
            displayFile();

            return rootView;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return getView();
    }


    public String getStringFromJson() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.maps)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    // Load JSON file into string
    public String loadJSONFromFile() {
        String json = null;
        try {
            //FileInputStream is = new FileInputStream(localFile);
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
                String mapfile = ""; // instantiate it

                // the "name" is not present on every JSON object, so do this if it exists
                if (m.has("imageName")) {
                    mapfile = m.getString("imageName");
                }

                // the hash map for single map info
                HashMap<String, String> mapinfo = new HashMap<>();

                // add each child node to HashMap key => value
                mapinfo.put("seq", seq);
                mapinfo.put("mapname", mapname);
                mapinfo.put("mapfile", mapfile);

                // add the map to the map list
                mapsList.add(mapinfo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSONException: " + e.getMessage());

        }

        // use this adaptor to show the map list item on listmapitem.xml
        ListAdapter adapter = new SimpleAdapter(
                getActivity().getApplicationContext(), mapsList,
                R.layout.listmapitem,
                new String[]{"mapname"},
                new int[]{R.id.map_name});

    // List chapter adaptor
        roomList.setAdapter(adapter);

        // set on click listener
        roomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // Take the chapter strings from listview position and populate them in ChapterDetails.java
                Intent intent = new Intent(getActivity(), MapsExpandActivity.class);
                intent.putExtra("seq", ((HashMap<String, String>) roomList.getAdapter().getItem((int)id)).get("seq"));
                intent.putExtra("mapname", ((HashMap<String, String>) roomList.getAdapter().getItem((int)id)).get("mapname"));
                intent.putExtra("mapfile", ((HashMap<String, String>) roomList.getAdapter().getItem((int)id)).get("mapfile"));

                if (intent.getStringExtra("mapfile").isEmpty()){
                    // don't launch an intent unless the file exists
                    Toast.makeText(getActivity().getApplicationContext(), "Map image is not yet available.", Toast.LENGTH_LONG).show();
                } else {
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
