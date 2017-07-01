package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * The purpose of this fragment is to display contact information for the chapters across the country.
 * Users will click on the chapter info and then be brought to a chapter details page.
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
import android.webkit.MimeTypeMap;
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


public class ChaptersFragment extends Fragment implements View.OnClickListener{
    public static final String TAG = ChaptersFragment.class.getSimpleName();

    // Listview to show the chapters
    private ListView lv;

    //Buttons
    private Button btnDownload;
    private Button btnView;
    private Button btnChapManual;


    // A download manager is used to download a file from a URL onto the device
    DownloadManager downloadManager;
    private String jsonDir = "/NPM"; // the name of the directory to store the file
    private String jsonChapFile = "Chapters_NPM2017.json"; // the name of the actual file
    File localFile = new File(Environment.getExternalStorageDirectory()+jsonDir, jsonChapFile);

    // A separate file is the Chapters Manual which is a PDF file
    private String pdfChapFile = "NPMChapterManual_2016.pdf"; // the name of the file
    File localPDFFile = new File(Environment.getExternalStorageDirectory()+jsonDir,pdfChapFile);
    //TODO logic for remote configuration to check if a new file exists

    ArrayList<HashMap<String, String>> chapterList = new ArrayList<>();

    public ChaptersFragment() {
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
                Log.e(TAG, "Found " + jsonChapFile + " in " + jsonDir + ".");

                // Display the chapters if the chapters JSON file has already been downloaded
                View rootView = inflater.inflate(R.layout.chapters_frag, container, false);

                // populate the JSON file into an arraylist
                chapterList = new ArrayList<>();

                // Used to show the chapters in a listview
                lv = (ListView) rootView.findViewById(R.id.list);

                // Have a button to allow the user to view the national chapter manual
                btnChapManual = (Button) rootView.findViewById(R.id.chapter_manual_btn);

                // needs a listener
                btnChapManual.setOnClickListener(this);

                // Display the list of chapters
                displayFile();

                return rootView;

            } else {
                // show screen with download button
                Log.e(TAG, jsonChapFile + " not found at " + jsonDir);

                // Display the fragment with the download buttons
                View rootView = inflater.inflate(R.layout.chapter_frag, container, false);

                //getting views from layout
                btnDownload = (Button) rootView.findViewById(R.id.btnDownload);
                btnView = (Button) rootView.findViewById(R.id.btnView);
                btnChapManual = (Button) rootView.findViewById(R.id.chapter_manual_btn);

                // Used to show the chapters in a listview
                lv = (ListView) rootView.findViewById(R.id.list);

                //attaching listeners
                btnDownload.setOnClickListener(this);
                btnView.setOnClickListener(this);
                btnChapManual.setOnClickListener(this);

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

        // List chapter adaptor
        lv.setAdapter(adapter);

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

    }



    @Override
    public void onClick(View view) {
        // Download file to sdcard
        if (view == btnDownload) {
            downloadManager = (DownloadManager)getContext().getSystemService(Context.DOWNLOAD_SERVICE);

            // Location of the file to be downloaded
            Uri uri = Uri.parse("https://www.brockmann.com/apps/npmconvention/2017/objects/chapters.json");

            DownloadManager.Request request = new DownloadManager.Request(uri);

            request.setDestinationInExternalPublicDir(jsonDir,jsonChapFile);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle(jsonChapFile);
            Long reference = downloadManager.enqueue(request);
            Log.e(TAG, "Attempted to download the the Chapters JSON file.");
        }
        // View the downloaded file which has already been stored locally on the user's device
        else if (view == btnView) {
            if (localFile.exists()){
                displayFile();
            } else {
                Toast.makeText(getContext(),"Please download the file to view the chapters info.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Attempted to view the Chapters JSON file.");
            }
        }
        // FAB will allow the user to view the national chapters file
        else if (view == btnChapManual) {
            if (localPDFFile.exists()){
                // launch intent to view the manual which is a PDF file
                String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(".PDF");

                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(localPDFFile), mime);
                startActivityForResult(intent, 10);

                Log.e(TAG, "Attempt to launch intent to view the Chapters Manual PDF file.");

                //Snackbar.make(view, "Button worked!", Snackbar.LENGTH_LONG).show();

            } else {
                Toast.makeText(getContext(),"Please try downloading the file again.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Attempted to view the Chapters Manual PDF file.");
            }

        }
    }

}


