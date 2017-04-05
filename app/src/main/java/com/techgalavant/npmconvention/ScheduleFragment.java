package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * This tab will be used to display the list of events. The events are parsed from a JSON file.
 * Future-State: users will be able to add the content to their own Google Calendar.
 *
 * Checkout - http://stackoverflow.com/questions/3721963/how-to-add-calendar-events-in-android
 * - http://stacktips.com/tutorials/android/how-to-add-event-to-calendar-in-android
 * - https://developers.google.com/google-apps/calendar/v3/reference/calendars/insert
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
import java.util.ArrayList;
import java.util.HashMap;



public class ScheduleFragment extends Fragment implements View.OnClickListener{
    public static final String TAG = ScheduleFragment.class.getSimpleName();

    // Listview to show the contacts
    private ListView lv;

    //Buttons
    private Button btnDownload;
    private Button btnView;

    // A download manager is used to download a file from a URL onto the device
    DownloadManager downloadManager;
    private String jsonDir = "/NPM"; // the name of the directory to store the file
    private String jsonFile = "contacts.json"; // the name of the downloaded test file
    //private String jsonFile = "Events_NPM.json"; // the name of the actual file
    File localFile = new File(Environment.getExternalStorageDirectory()+jsonDir, jsonFile);

    ArrayList<HashMap<String, String>> contactList;


    public ScheduleFragment() {
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
                View rootView = inflater.inflate(R.layout.scheduleb_frag, container, false);

                // populate the JSON file into an arraylist
                contactList = new ArrayList<>();

                // Used to show the events in a listview
                lv = (ListView) rootView.findViewById(R.id.list);

                // Display the list of contacts
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
            return null;
        }
        return json;

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
            JSONArray contacts = jsonObj.getJSONArray("contacts");

            // populate the array nodes into hashmap
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);

                String id = c.getString("id");
                String name = c.getString("name");
                String email = c.getString("email");
                String address = c.getString("address");
                String gender = c.getString("gender");

                // Phone node is JSON Object
                JSONObject phone = c.getJSONObject("phone");
                String mobile = phone.getString("mobile");
                String home = phone.getString("home");
                String office = phone.getString("office");

                // the hash map for single contact
                HashMap<String, String> contact = new HashMap<>();

                // add each child node to HashMap key => value
                contact.put("id", id);
                contact.put("name", name);
                contact.put("email", email);
                contact.put("mobile", mobile);
                contact.put("home", home);

                // add the contact into contact list
                contactList.add(contact);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSONException: " + e.getMessage());
        }

        // show the contactlist in a listview adaptor
        ListAdapter adapter = new SimpleAdapter(
                getActivity().getApplicationContext(), contactList,
                R.layout.listeventitem, new String[]{"name", "email",
                "mobile", "home"}, new int[]{R.id.ename,
                R.id.edesc, R.id.estart, R.id.efinish});

        lv.setAdapter(adapter);

    }


    @Override
    public void onClick(View view) {
        // Download file to sdcard
        if (view == btnDownload) {
            downloadManager = (DownloadManager)getContext().getSystemService(Context.DOWNLOAD_SERVICE);

            // Test file contacts.json
            Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/npm-convention.appspot.com/o/contacts.json?alt=media&token=815fddb6-fb72-4ebc-94f7-75ea6341b7a9");

            // Real file points here
            // Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/npm-convention.appspot.com/o/Events_NPM.json?alt=media&token=9f0df114-a1ec-41e4-a154-815e682a17a7");

            DownloadManager.Request request = new DownloadManager.Request(uri);

            // TO-DO set the destination directory in res.assets folder
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
