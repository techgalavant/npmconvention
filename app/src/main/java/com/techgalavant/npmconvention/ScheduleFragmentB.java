package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * This tab will be used to display the list of events. The events are parsed from a JSON file.
 * The events are grouped using an expandablelistview adapter.
 * Future-State: users will be able to add the content to their own Google Calendar.
 *
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ScheduleFragmentB extends Fragment implements View.OnClickListener{
    public static final String TAG = ScheduleFragmentB.class.getSimpleName();

    // Listview to show the contacts
    private ListView lv;

    //Buttons
    private Button btnDownload;
    private Button btnView;

    // A download manager is used to download a file from a URL onto the device
    DownloadManager downloadManager;
    String jsonDir = "/NPM"; // the name of the directory to store the file
    String jsonFile = "Events_NPM.json"; // the name of the actual file
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

                // show us the data using an expandlistview adapter
                expandList();

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

    // creates an expandable list
    private void expandList() {

        // Gets the data from the JSON file using ExpandableListDataPump class
        expandableListDetail = new ExpandableListDataPump().getData();
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

    // Displays the events in a listview
    private void displayFile() {

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