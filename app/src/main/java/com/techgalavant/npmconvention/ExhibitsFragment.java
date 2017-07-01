package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * The purpose of this fragment is to provide the users with links to the relevant exhibits.
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;



public class ExhibitsFragment extends Fragment implements View.OnClickListener  {

    public static final String TAG = ExhibitsFragment.class.getSimpleName();

    // an Array list of a hashmap with the exhibitors in it
    ArrayList<HashMap<String, String>> exhibitsList = new ArrayList<>();

    // Listview to show the exhibitors from the res/raw/ folder
    ListView exhibitList;

    public ExhibitsFragment() {
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

            // Inflate the layout
            View rootView = inflater.inflate(R.layout.exhibits_frag, container, false);

            // populate the JSON file into an arraylist
            exhibitsList = new ArrayList<>();

            // Instantiate the Exhibitors Listview
            exhibitList = (ListView) rootView.findViewById(R.id.exhibitlist);

            // Parse and display the Exhibitors in the JSON file
            displayFile();

            return rootView;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return getView();
    }

    // Load JSON file into string from raw/resource
    public String loadJSONFromFile() {
        String json = null;
        try {

            InputStream is = getResources().openRawResource(R.raw.exhibits);
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


    // used to display the contents of the JSON file in local raw/res folder
    private void displayFile() {

        // The method getJSONObject returns the JSON object.
        // The method getString returns the string value of the specified key.

        try {
            // Load JSON file into JSON object
            JSONObject jsonObj = new JSONObject(loadJSONFromFile());

            // Getting JSON Array node
            JSONArray exhibitlist = jsonObj.getJSONArray("exhibitors"); // "list" is the node in the JSON file

            // populate the array nodes into a hashmap
            for (int i = 0; i < exhibitlist.length(); i++) {

                JSONObject m = exhibitlist.getJSONObject(i);
                String booth = ("Booth: " + m.getString("booth"));
                String exhibitor = m.getString("exhibitor");
                String web = "";
                String prose = "";
                String contact = "";
                String phone = "";
                String email = "";
                String facebook = "";

                if (booth.contains("216")){
                    web = m.getString("web");
                    prose = m.getString("prose");
                    contact = m.getString("contact");
                    phone = m.getString("phone");
                    email = m.getString("email");
                    facebook = m.getString("facebook");
                }

                // the hash map for a single exhbitor info
                HashMap<String, String> exhibitinfo = new HashMap<>();

                // add each child node to HashMap key => value
                exhibitinfo.put("booth", booth);
                exhibitinfo.put("exhibitor", exhibitor);
                exhibitinfo.put("web", web);
                exhibitinfo.put("prose", prose);
                exhibitinfo.put("contact", contact);
                exhibitinfo.put("phone", phone);
                exhibitinfo.put("email", email);
                exhibitinfo.put("facebook", facebook);

                // add the map to the map list
                exhibitsList.add(exhibitinfo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSONException: " + e.getMessage());

        }

        // use this adaptor to show the exhibitors on the list_exhibit_item.xml rows
        ListAdapter adapter = new SimpleAdapter(
                getActivity().getApplicationContext(), exhibitsList,
                R.layout.list_exhibit_item,
                new String[]{"booth", "exhibitor"},
                new int[]{R.id.exhibit_booth, R.id.exhibit_name});

        // Set adaptor for Exhibits
        exhibitList.setAdapter(adapter);

        // set on click listener for each row
        exhibitList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // Take the exhibits strings from listview position and populate them in ExhibitsExpandActivity.java
                Intent intent = new Intent(getActivity(), ExhibitsExpandActivity.class);
                intent.putExtra("booth", ((HashMap<String, String>) exhibitList.getAdapter().getItem((int) id)).get("booth"));
                intent.putExtra("exhibitor", ((HashMap<String, String>) exhibitList.getAdapter().getItem((int) id)).get("exhibitor"));
                intent.putExtra("web", ((HashMap<String, String>) exhibitList.getAdapter().getItem((int) id)).get("web"));
                intent.putExtra("prose", ((HashMap<String, String>) exhibitList.getAdapter().getItem((int) id)).get("prose"));
                intent.putExtra("contact", ((HashMap<String, String>) exhibitList.getAdapter().getItem((int) id)).get("contact"));
                intent.putExtra("phone", ((HashMap<String, String>) exhibitList.getAdapter().getItem((int) id)).get("phone"));
                intent.putExtra("email", ((HashMap<String, String>) exhibitList.getAdapter().getItem((int) id)).get("email"));
                intent.putExtra("facebook", ((HashMap<String, String>) exhibitList.getAdapter().getItem((int) id)).get("facebook"));
                startActivity(intent);

            }

        });

    }

    @Override
    public void onClick(View view) {
        // Download file to sdcard

    }
}
