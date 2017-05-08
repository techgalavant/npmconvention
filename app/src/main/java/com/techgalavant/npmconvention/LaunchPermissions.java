package com.techgalavant.npmconvention;

/**
 * Created by Michael Fallon
 *
 * The purpose of Launch Permissions is to have the user enable permissions needed for the app to run successfully.
 * This is especially required for Android 6.0 or higher.
 *
 * Credits -
 * http://www.journaldev.com/10409/android-handling-runtime-permissions-example
 */

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class LaunchPermissions extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = LaunchPermissions.class.getSimpleName();

    private static final int PERMISSION_REQUEST_CODE = 200;
    private View view;
    private TextView txtPurpose;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button check_permission = (Button) findViewById(R.id.check_permission);
        Button request_permission = (Button) findViewById(R.id.request_permission);
        check_permission.setOnClickListener(this);
        request_permission.setOnClickListener(this);

        txtPurpose = (TextView) findViewById(R.id.purpose); // displays the purpose string

    }


    @Override
    public void onClick(View v) {

        view = v;

        int id = v.getId();
        switch (id) {
            case R.id.check_permission:
                if (checkPermission()) {

                    Snackbar.make(view, "Permissions already completed.", Snackbar.LENGTH_LONG).show();  // if permissions already completed
                    downloadFiles();

                } else {

                    Snackbar.make(view, "Please complete permissions.", Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.request_permission:
                if (!checkPermission()) {

                    requestPermission();

                } else {

                    Snackbar.make(view, "Permissions already completed.", Snackbar.LENGTH_LONG).show();
                    downloadFiles();
                }
                break;
        }

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
        new AlertDialog.Builder(LaunchPermissions.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    // Download the files needed for the app to perform it's operations
    private void downloadFiles(){

        if (PDFFile.exists()){

            Log.e(TAG, "PDF Programs file exists already.");

        } else {

            // download the PDF file
            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            // Location of the PDF file to be downloaded
            Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/npm-convention.appspot.com/o/ConventionBrochure.pdf?alt=media&token=bf07296e-cc61-4429-b27e-ad628c6eb486");
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDestinationInExternalPublicDir(pdfDir,pdfFile);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle(pdfFile);
            Long reference = downloadManager.enqueue(request);

            Log.e(TAG, "Downloading Program PDF file.");

            Snackbar.make(view, "Downloading files started", Snackbar.LENGTH_LONG).show();
        }

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

        if (PDFFile.exists() && JSONFile.exists()){
            Log.e(TAG, "All downloaded files are present.");

            // Launch an alert dialog with a confirmation that allows the user to return to WelcomeFragment (MainActivity)
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LaunchPermissions.this);

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
                    Intent intent = new Intent(LaunchPermissions.this, MainActivity.class);
                    startActivity(intent);
                    Log.e(TAG, "User selected OK on AlertDialog");
                    finish(); // closes this activity so that it's not accessible in the back button
                }
            });

            // Show AlertDialog box
            alertDialog.show();


        }

    }


}