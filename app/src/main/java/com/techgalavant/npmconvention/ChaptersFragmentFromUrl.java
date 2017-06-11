package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * The purpose of this class is to display JSON from URL into the chapters fragment.
 * If Internet is unavailable, then check to see if the file has been downloaded.
 * If it has been downloaded, display that file.
 * If it hasn't been downloaded, then prompt user to download file.
 *
 * Credits
 *  - JSON Parsing Tutorial - http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
 */

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

// TODO this doesn't seem to be working properly
public class ChaptersFragmentFromUrl extends Fragment implements View.OnClickListener {

    private String TAG = ChaptersFragmentFromUrl.class.getSimpleName();

    private ProgressDialog pDialog;

    // Listview to show the chapters
    private ListView lv;

    //Buttons
    private Button btnDownload;
    private Button btnView;

    // A download manager is used to download a file from a URL onto the device
    DownloadManager downloadManager;
    private String jsonDir = "/NPM"; // the name of the directory to store the file
    private String jsonFile = "Chapters_NPM.json"; // the name of the actual file
    File localFile = new File(Environment.getExternalStorageDirectory()+jsonDir, jsonFile);

    ArrayList<HashMap<String, String>> chapterList = new ArrayList<>();

    // URL to get the chapters JSON
    private static String url = "https://www.brockmann.com/apps/npmconvention/2017/objects/chapters.json";

    public ChaptersFragmentFromUrl() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {

        // If the network connection exists, and internet is available, then parse the file from URL
        // Else if the network connection does not exist, then parse the file from the download folder.
        // If the file hasn't been downloaded, though, show buttons to download the files.

        try {
            if (isInternetWorking()) {
                Log.e(TAG, "Internet is available, attempting to parse Chapters.json from URL.");

                // Display the chapters if the chapters JSON file has already been downloaded
                View rootView = inflater.inflate(R.layout.chapters_frag, container, false);

                // populate the JSON file into an arraylist
                chapterList = new ArrayList<>();

                // Used to show the chapters in a listview
                lv = (ListView) rootView.findViewById(R.id.list);

                // do the Async tasks to download the JSON from URL
                new GetContacts().execute();

                return rootView;

            } else if (localFile.exists()) {  // when internet is unavailable then look to see if the file has been downloaded

                Log.e(TAG, "Internet not available. Found " + jsonFile + " in " + jsonDir + ".");

                // Display the chapters if the chapters JSON file has already been downloaded
                View rootView = inflater.inflate(R.layout.chapters_frag, container, false);

                // populate the JSON file into an arraylist
                chapterList = new ArrayList<>();

                // Used to show the chapters in a listview
                lv = (ListView) rootView.findViewById(R.id.list);

                // Display the list of chapters from local download folder
                displayFile();

                return rootView;

            } else {
                // show screen with download button when Internet unavailable and JSON file not found
                Log.e(TAG, "Internet unavailable and " + jsonFile + " not found at " + jsonDir);

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


    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            Log.e(TAG, "Attempting onPreExecute tasks");
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity().getApplicationContext());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Log.e(TAG, "Attempting doInBackground tasks");

            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    //JSONObject jsonObj = new JSONObject(loadJSONFromFile());

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
                } catch (final JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "JSONException: " + e.getMessage());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext().getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext().getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Log.e(TAG, "Attempting onPostExecute tasks");

            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

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

    }

    // simple way to check if the internet is available, not just whether network connection exists
    public boolean isInternetAvailable() {
        try {
            final InetAddress address = InetAddress.getByName("www.google.com");
            Log.e(TAG,"Internet " + address + "found.");
            //return !address.equals("");
            return true;
        } catch (UnknownHostException e) {
            // Log error
            Log.e(TAG, "Internet not found");
        }
        return false;
    }

    public boolean isInternetWorking() {
        boolean success = false;
        try {
            URL url = new URL("https://google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1600);
            connection.connect();
            success = connection.getResponseCode() == 200;
            Log.e(TAG,"Internet connection can access " + url);
        } catch (IOException e) {
            e.printStackTrace();
            // Log error
            Log.e(TAG, "Internet not found");
        }
        return success;
    }

    @Override
    public void onClick(View view) {
        // Download file to sdcard
        if (view == btnDownload) {
            downloadManager = (DownloadManager)getContext().getSystemService(Context.DOWNLOAD_SERVICE);

            // Location of the file to be downloaded
            Uri uri = Uri.parse(url);

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

    // Load JSON from local file into string
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

    public void displayFile(){
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
}