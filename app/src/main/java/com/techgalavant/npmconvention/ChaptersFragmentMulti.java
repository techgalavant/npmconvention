package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * The purpose of this fragment is to display contact information for the chapters across the country.
 * Users will click on the chapter info and then be brought to a chapter details page.
 * The second listview is supposed to represent how to setup a chapter.
 *
 * Credits
 *  - JSON Parsing Tutorial - http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
 *
 * Note:
 * If your JSON node starts with [, then we should use getJSONArray() method.
 * If the node starts with {, then we should use getJSONObject() method.
 *
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
import java.util.ArrayList;
import java.util.HashMap;

import static com.techgalavant.npmconvention.R.id.list2;


public class ChaptersFragmentMulti extends Fragment implements View.OnClickListener{
    public static final String TAG = ChaptersFragmentMulti.class.getSimpleName();

    // Listview to show the chapters
    private ListView lv;
    private ListView lv2;

    //Buttons
    private Button btnDownload;
    private Button btnView;

    // A download manager is used to download a file from a URL onto the device
    DownloadManager downloadManager;
    private String jsonDir = "/NPM"; // the name of the directory to store the file
    private String jsonFile = "Chapters_NPM.json"; // the name of the actual file
    File localFile = new File(Environment.getExternalStorageDirectory()+jsonDir, jsonFile);

    ArrayList<HashMap<String, String>> chapterList = new ArrayList<>();
    ArrayList<HashMap<String, String>> chapterInfoList = new ArrayList<>();

    public ChaptersFragmentMulti() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // If the network connection exists, and internet is available, then parse the file from URL
        // Else if the network connection does not exist, then parse the file from the download folder.
        // If the file hasn't been downloaded, though, show buttons to download the files.

        try {
            if (localFile.exists()) {
                Log.e(TAG, "Found " + jsonFile + " in " + jsonDir + ".");

                // Display the chapters if the chapters JSON file has already been downloaded
                View rootView = inflater.inflate(R.layout.chapters_multi_frag, container, false);

                // populate the JSON file into an arraylist
                chapterList = new ArrayList<>();
                chapterInfoList = new ArrayList<>();

                // Used to show the chapters in a listview
                lv = (ListView) rootView.findViewById(R.id.list);
                lv2 = (ListView) rootView.findViewById(list2);

                // Display the list of chapters
                displayFile();

                return rootView;

            } else {
                // show screen with download button
                Log.e(TAG, jsonFile + " not found at " + jsonDir);

                // Display the fragment with the download buttons
                View rootView = inflater.inflate(R.layout.chapter_frag, container, false);

                //getting views from layout
                btnDownload = (Button) rootView.findViewById(R.id.btnDownload);
                btnView = (Button) rootView.findViewById(R.id.btnView);

                // Used to show the chapters in a listview
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
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.e(TAG, "IOException: " + ex.getMessage());
            return null;
        }
        return json;

    }

    // CONSIDER: building a handler first in a separate class,
    // and then use this to display the chapters in a listview
    private void displayFile() {

        // The method getJSONObject returns the JSON object.
        // The method getString returns the string value of the specified key.

        try {
            // Load JSON file into JSON object
            JSONObject jsonObj = new JSONObject(loadJSONFromFile());

            // Getting JSON Array node
            JSONArray chapters = jsonObj.getJSONArray("list"); // "list" is the node in the JSON file
            JSONArray chaptersinfo = jsonObj.getJSONArray("headers"); // "list" is the node in the JSON file

            // populate the array nodes into a hashmap
            for (int i = 0; i < chapters.length(); i++) {

                JSONObject c = chapters.getJSONObject(i);
                String chapadr = c.getString("chapterDirectorAddress");
                String chapweb = c.getString("chapterWebsite");
                String chaptel = c.getString("chapterDirectorTelephone");
                String chapname = c.getString("chapter");
                String chapfb = c.getString("chapterFacebook");
                String chapdir = c.getString("chapterDirector");
                String chapem = c.getString("chapterDirectorEmail");

                // the hash map for single chapter
                HashMap<String, String> chapter = new HashMap<>();

                // add each child node to HashMap key => value
                chapter.put("chapadr", chapadr);
                chapter.put("chapweb", chapweb);
                chapter.put("chaptel", chaptel);
                chapter.put("chapname", chapname);
                chapter.put("chapfb", chapfb);
                chapter.put("chapdir", chapdir);
                chapter.put("chapem", chapem);

                // add the chapter into the chapter list
                chapterList.add(chapter);
            }
            // populate the 2nd array nodes into a hashmap
            for (int j = 0; j < chaptersinfo.length(); j++) {

                JSONObject ci = chaptersinfo.getJSONObject(j);

                String chap1 = ci.getString("Nat'l Committee on Chapters.");
                String chap2 = ci.getString("Chapters @ Convention.");
                String chap3 = ci.getString("How to Form a Chapter.");
                String chap4 = ci.getString("Become a Chapter Member.");
                String chap5 = ci.getString("Purpose of NPM Chapters.");

                // the hash map for single chapter
                HashMap<String, String> chapterinfo = new HashMap<>();

                // add each child node to HashMap key => value
                chapterinfo.put("chap1", chap1);
                chapterinfo.put("chap2", chap2);
                chapterinfo.put("chap3", chap3);
                chapterinfo.put("chap4", chap4);
                chapterinfo.put("chap5", chap5);

                // add the chapter into the chapter list
                chapterInfoList.add(chapterinfo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSONException: " + e.getMessage());

        }

        // use this adaptor to show the chapter list item on listchaptritem.xml
        ListAdapter adapter = new SimpleAdapter(
                getActivity().getApplicationContext(), chapterList,
                R.layout.listchaptritem,
                new String[]{"chapname"},
                new int[]{R.id.chap_name});

        // use this adaptor to show the chapter setup instructions list item on listchaptrsetupitem.xml
        ListAdapter adapter2 = new SimpleAdapter(
                getActivity().getApplicationContext(), chapterInfoList,
                R.layout.listchaptrsetupitem,
                new String[]{"chap5"},
                new int[]{R.id.chap_instruct});

        // List chapter adaptor
        lv.setAdapter(adapter);
        lv2.setAdapter(adapter2);

        // set on click listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // Take the chapter strings from listview position and populate them in ChapterDetails.java
                Intent intent = new Intent(getActivity(), ChapterDetails.class);
                intent.putExtra("complete_chapter", parent.getItemAtPosition(position).toString()); // Pass the entire chapter info so I can check it in ChapterDetails
                intent.putExtra("chapadr", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("chapadr"));
                intent.putExtra("chapname", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("chapname"));
                intent.putExtra("chapweb", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("chapweb"));
                intent.putExtra("chapfb", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("chapfb"));
                intent.putExtra("chapem", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("chapem"));
                intent.putExtra("chaptel", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("chaptel"));
                intent.putExtra("chapdir", ((HashMap<String, String>) lv.getAdapter().getItem((int)id)).get("chapdir"));
                startActivity(intent);
            }

        });

        // set on click listener
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // Take the chapter strings from listview position and populate them in ChapterDetails.java
                Intent intent = new Intent(getActivity(), ChapterSetup.class);
                intent.putExtra("complete_chapter_info", parent.getItemAtPosition(position).toString()); // Pass the entire chapter info so I can check it in ChapterDetails
                intent.putExtra("chap1", ((HashMap<String, String>) lv2.getAdapter().getItem((int)id)).get("chap1"));
                intent.putExtra("chap2", ((HashMap<String, String>) lv2.getAdapter().getItem((int)id)).get("chap2"));
                intent.putExtra("chap3", ((HashMap<String, String>) lv2.getAdapter().getItem((int)id)).get("chap3"));
                intent.putExtra("chap4", ((HashMap<String, String>) lv2.getAdapter().getItem((int)id)).get("chap4"));
                intent.putExtra("chap5", ((HashMap<String, String>) lv2.getAdapter().getItem((int)id)).get("chap5"));
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
            Uri uri = Uri.parse("https://www.brockmann.com/apps/npmconvention/2017/objects/chapters.json");

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
                Toast.makeText(getContext(),"Please download the file to view the chapters info.", Toast.LENGTH_LONG).show();
            }
        }
    }


}


