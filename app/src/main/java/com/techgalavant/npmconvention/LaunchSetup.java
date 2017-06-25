package com.techgalavant.npmconvention;

/**
 * Created by Michael Fallon
 *
 * The purpose of Launch Setup is to have the user enable permissions needed for the app to run successfully,
 * to download the necessary files, or to be used again in case the files were accidentally deleted by the user.
 * This is especially required for Android 6.0 or higher.
 *
 * Credits -
 * http://www.journaldev.com/10409/android-handling-runtime-permissions-example
 */

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.READ_CALENDAR;
import static android.Manifest.permission.WRITE_CALENDAR;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LaunchSetup extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = LaunchSetup.class.getSimpleName();

    private static final int PERMISSION_REQUEST_CODE = 200;
    private View view;
    private TextView txtPurpose;
    int viewInt;

    SharedPreferences prefs; // used to answer whether this is the first-time the app is being run

    private String togglebtn; // use to display text on conditions
    private Button request_permission;  // use to kick off app permission process

    // A download manager is used to download a file from a URL onto the device
    // Download the Program PDF file
    private DownloadManager downloadManager;
    private String pdfDir = "/NPM"; // the name of the directory to store the PDF files
    private String pdfFile = "ConventionBrochure.pdf"; // the name of the PDF file
    File PDFFile = new File(Environment.getExternalStorageDirectory()+pdfDir, pdfFile);

    // Download the Events JSON file
    private DownloadManager dm;
    private String jsonDir = "/NPM"; // the name of the directory to store the file
    private String jsonFile = "Events_NPM.json"; // the name of the actual file
    File JSONFile = new File(Environment.getExternalStorageDirectory()+jsonDir, jsonFile);

    // Download the Chapters JSON file
    private DownloadManager dmc;
    private String jsonChapFile = "Chapters_NPM.json"; // the name of the actual file
    File JSONChapFile = new File(Environment.getExternalStorageDirectory()+jsonDir, jsonChapFile);

    // Download the Chapters Manual PDF file
    private DownloadManager dmcm;
    private String pdfChapFile = "NPMChapterManual_2016.pdf"; // the name of the actual file
    File PDFChapFile = new File(Environment.getExternalStorageDirectory()+pdfDir, pdfChapFile);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Access the shared preferences to detect if this is the first time the app has launched
        prefs = getSharedPreferences("com.techgalavant.npmconvention", MODE_PRIVATE);
        viewInt = prefs.getInt("viewInt", 0);

        request_permission = (Button) findViewById(R.id.request_permission);

        request_permission.setOnClickListener(this);

        txtPurpose = (TextView) findViewById(R.id.purpose); // displays the purpose string 1 - 5

        toggleButton(); // conditional text on the button if the files have already been downloaded

    }


    @Override
    public void onClick(View v) {

        //view = v;

        if (viewInt==4) {

            if (checkPermission()) {

                Snackbar.make(view, "Device permissions completed.", Snackbar.LENGTH_LONG).show();  // if permissions already completed
                downloadFiles();

            } else {

                requestPermission();

            }
        } else {

            viewInt = viewInt+1;
            Log.e(TAG, "viewInt = " +viewInt);
            prefs.edit().putInt("viewInt", viewInt).commit();
            onRestart();
        }

    }

    @Override
    protected void onRestart() {

        super.onRestart();
        Intent i = new Intent(LaunchSetup.this, LaunchSetup.class);  // Restart the same screen
        startActivity(i);

        // viewInt = viewInt+1;
        prefs.getInt("viewInt", viewInt);
        Log.e(TAG, "viewInt from prefs = " + prefs.getInt("viewInt", viewInt));
        int vInt = prefs.getInt("viewInt",viewInt);

        // prefs.edit().putInt("viewInt", viewInt).commit();

        Log.e(TAG, "viewInt = " +viewInt);

        switch(vInt) {
            case 0:
                txtPurpose.setText(R.string.purpose1);
                break;
            case 1:
                txtPurpose.setText(R.string.purpose2);
                break;
            case 2:
                txtPurpose.setText(R.string.purpose3);
                break;
            case 3:
                txtPurpose.setText(R.string.purpose4);
                break;
            case 4:
                txtPurpose.setText(R.string.purpose5);
                break;
            default:
                txtPurpose.setText(R.string.purpose);
        }

        //this.finish();

    }

    // check if permission has been completed
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CALENDAR);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_CALENDAR);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), GET_ACCOUNTS);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
    }

    // prompt user to complete the permissions
    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_CALENDAR, WRITE_CALENDAR, GET_ACCOUNTS}, PERMISSION_REQUEST_CODE);

        }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean writestorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readcalAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writecalAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean getacctsAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if (writestorageAccepted && readcalAccepted  && writecalAccepted  && getacctsAccepted) {
                        Snackbar.make(view, "All permissions granted.", Snackbar.LENGTH_LONG).show();
                        downloadFiles();
                    }
                    else {

                        Snackbar.make(view, "Not all permissions have been granted.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to all these permissions.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_CALENDAR, WRITE_CALENDAR, GET_ACCOUNTS,},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }

    // Alert dialog displays permission settings to user
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LaunchSetup.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    // Download the files needed for the app to perform it's operations
    private void downloadFiles(){

        // Main Program file
        if (PDFFile.exists()){

            Log.e(TAG, "PDF Programs file exists already.");

        } else {

            // download the PDF file
            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            // Location of the PDF file to be downloaded
            //Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/npm-convention.appspot.com/o/ConventionBrochure.pdf?alt=media&token=bf07296e-cc61-4429-b27e-ad628c6eb486");
            Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/npm-convention.appspot.com/o/NPM-Convention-2016-lowRes.pdf?alt=media&token=a7aea853-37ff-409e-8a38-7f1b3308bc5a");
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDestinationInExternalPublicDir(pdfDir,pdfFile);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle(pdfFile);
            Long reference = downloadManager.enqueue(request);

            Log.e(TAG, "Downloading Program PDF file.");

            Snackbar.make(view, "Downloading files started", Snackbar.LENGTH_LONG).show();
        }

        // Events JSON file
        if (JSONFile.exists()){

            Log.e(TAG, "Events JSON file exists already.");

        } else {
            // download the JSON file
            dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            // Location of the  JSON file to be downloaded
            Uri uri2 = Uri.parse("https://firebasestorage.googleapis.com/v0/b/npm-convention.appspot.com/o/Events_NPM.json?alt=media&token=65a29d0a-5f78-487a-9d76-41c6c31762e0");
            DownloadManager.Request request2 = new DownloadManager.Request(uri2);
            request2.setDestinationInExternalPublicDir(jsonDir,jsonFile);
            request2.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request2.setTitle(jsonFile);
            Long reference2 = dm.enqueue(request2);

            Log.e(TAG, "Downloading Events JSON file.");

            Snackbar.make(view, "Downloading files started", Snackbar.LENGTH_LONG).show();
        }

        // Chapter JSON file
        if (JSONChapFile.exists()){

            Log.e(TAG, "Chapters JSON file exists already.");

        } else {
            // download the JSON file
            dmc = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            // Location of the  JSON file to be downloaded
            Uri uri3 = Uri.parse("https://www.brockmann.com/apps/npmconvention/2017/objects/chapters.json");
            DownloadManager.Request request3 = new DownloadManager.Request(uri3);
            request3.setDestinationInExternalPublicDir(jsonDir,jsonChapFile);
            request3.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request3.setTitle(jsonChapFile);
            Long reference3 = dmc.enqueue(request3);

            Log.e(TAG, "Downloading Chapters JSON file.");

            Snackbar.make(view, "Downloading files started", Snackbar.LENGTH_LONG).show();
        }

        // Chapter Manual
        if (PDFChapFile.exists()){

            Log.e(TAG, "NPM Chapter Manual PDF file exists already.");

        } else {
            // download manager for the the Chapter Manual PDF file
            dmcm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            // Location of the  Chapter Manual PDF to be downloaded
            Uri uri4 = Uri.parse("https://firebasestorage.googleapis.com/v0/b/npm-convention.appspot.com/o/NPMChapterManual.pdf?alt=media&token=7a9fb9ff-cb92-442e-9075-8a1c777782ac");
            DownloadManager.Request request4 = new DownloadManager.Request(uri4);
            request4.setDestinationInExternalPublicDir(pdfDir,pdfChapFile);
            request4.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request4.setTitle(pdfChapFile);
            Long reference4 = dmcm.enqueue(request4);

            Log.e(TAG, "Downloading NPM Chapter Manual PDF file.");

            Snackbar.make(view, "Downloading files started", Snackbar.LENGTH_LONG).show();
        }

        if (PDFFile.exists() && JSONFile.exists() && JSONChapFile.exists() && PDFChapFile.exists()){
            Log.e(TAG, "All downloaded files are present.");

            viewInt=5; // setting this integer value to 5 will show the Launch Convention App button

            // if they have all the downloaded files then you can set this to false
            prefs.edit().putBoolean("firstrun", false).commit();  // https://developer.android.com/reference/android/content/SharedPreferences.Editor.html#apply()

            // Launch an alert dialog with a confirmation that allows the user to return to WelcomeFragment (MainActivity)
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LaunchSetup.this);

            // Set Dialog box title
            alertDialog.setTitle("SUCCESS!");

            // Set Dialog message
            alertDialog.setMessage("Your initial app setup appears to be completed.");

            // Sets icon in the AlertDialog window
            alertDialog.setIcon(R.drawable.ic_check);

            // Set operation for when user selects "YES" on AlertDialog
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {

                    // Launch feedback form - SendFeedback
                    Intent intent = new Intent(LaunchSetup.this, MainActivity.class);
                    startActivity(intent);
                    Log.e(TAG, "User selected OK on AlertDialog");
                    finish(); // closes this activity so that it's not accessible in the back button
                }
            });

            // Show AlertDialog box
            alertDialog.show();

        } else {
            // if the files can't be found then set firstrun condition to true
            prefs.edit().putBoolean("firstrun", true).commit();  // https://developer.android.com/reference/android/content/SharedPreferences.Editor.html#apply()
        }

    }

    // Changing button text
    private void toggleButton() {

        Boolean firstRun = prefs.getBoolean("firstrun", true);

        if (firstRun){

            request_permission.setText("NEXT");
            Log.e(TAG, "firstRun is true. viewInt = " + viewInt); // this is the first time the user has initiated setup

        } else if (JSONFile.exists() && PDFFile.exists() && JSONChapFile.exists() && PDFChapFile.exists()) {
                request_permission.setText("LAUNCH CONVENTION APP");
            viewInt = 5; // should show the last text on the TextView
            Log.e(TAG, "firstRun is false and user has completed downloading files. viewInt = " + viewInt); // the user has previously completed the initial setup

        } else {
            request_permission.setText("CONTINUE");

            Log.e(TAG, "firstRun is false and user needs to finish setup. viewInt = " + viewInt); // the user has previously started but incompleted the initial setup
        }
    }


}