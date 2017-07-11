package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 * ProgramFragment
 *
 * This is a screen to download & display the convention program which is a PDF file.
 * Credits to -
 * Displaying it use - https://github.com/barteksc/AndroidPdfViewer
 */

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.List;

import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.READ_CALENDAR;
import static android.Manifest.permission.WRITE_CALENDAR;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class ProgramFragment extends Fragment implements View.OnClickListener,OnPageChangeListener,OnLoadCompleteListener {
    public static final String TAG = ProgramFragment.class.getSimpleName();

    //Buttons
    private Button btnDownload;
    private Button btnView;

    // A download manager is used to download a file from a URL onto the device
    DownloadManager downloadManager;

    // PDFView from https://github.com/barteksc/AndroidPdfViewer
    PDFView pdfView;
    Integer pageNumber = 0;
    private String pdfDir = "/NPM"; // the name of the directory to store the PDF files
    private String pdfFile = "ConventionBrochure2017.pdf"; // the name of the PDF file
    File localFile = new File(Environment.getExternalStorageDirectory()+pdfDir, pdfFile);

    // use this boolean to display pages as Toast
    boolean mUserVisibleHint = true;


    public ProgramFragment() {
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
                Log.e(TAG, "Found " + pdfFile + " in " + pdfDir + ".");

                // Display the program without the download buttons
                View rootView = inflater.inflate(R.layout.programb_frag, container, false);

                // Used to show the downloaded PDF
                pdfView = (PDFView) rootView.findViewById(R.id.pdfView);

                displayFile();

                return rootView;

            } else {
                // show screen with download button
                Log.e(TAG, pdfFile + " not found at " + pdfDir);

                // Display the fragment with the download buttons
                View rootView = inflater.inflate(R.layout.program_frag, container, false);

                //getting views from layout
                btnDownload = (Button) rootView.findViewById(R.id.btnDownload);
                btnView = (Button) rootView.findViewById(R.id.btnView);

                // Used to show the downloaded PDFs
                pdfView = (PDFView) rootView.findViewById(R.id.pdfView);

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

    private void displayFile(){

        //File localFile = new File(Environment.getExternalStorageDirectory()+pdfDir, pdfFile);

        // Credit to https://github.com/barteksc/AndroidPdfViewer
        pdfView.fromFile(localFile)
                .defaultPage(pageNumber)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page+1;
        pdfView.getCurrentPage();
        if (mUserVisibleHint){

            //Toast.makeText(getContext(),"Page " + pageNumber + " of " + pageCount, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    // check if permissions has been completed
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getContext(), READ_CALENDAR);
        int result2 = ContextCompat.checkSelfPermission(getContext(), WRITE_CALENDAR);
        int result3 = ContextCompat.checkSelfPermission(getContext(), GET_ACCOUNTS);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onClick(View view) {
        // Download file to sdcard
        if (view == btnDownload) {

            // check first if user has granted permissions
            if (checkPermission()) {

                // display the file if it was downloaded already
                if (localFile.exists()) {
                    displayFile();

                } else {

                    downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

                    // Location of the file to be downloaded
                    downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse("https://www.brockmann.com/apps/npmconvention/2017/objects/NPM-Convention-2017High.pdf");
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setDestinationInExternalPublicDir(pdfDir, pdfFile);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setTitle(pdfFile);
                    Long reference = downloadManager.enqueue(request);

                }

            } else {

                // Launch an AlertDialog box so that users can elect to run permissions check
                Log.e(TAG, "Android permissions weren't set on ProgramFragment, so prompted user.");

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProgramFragment.this.getActivity());

                // Set Dialog box title
                alertDialog.setTitle("NOTICE");

                // Set Dialog message
                alertDialog.setMessage("Please enable app permissions so you can download the file.");

                // Sets icon in the AlertDialog window
                alertDialog.setIcon(R.drawable.ic_mesg);

                // Set operation for when user selects "OK" on AlertDialog
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // Launch intent to bring user back to Settings screen
                        Intent intent = new Intent(ProgramFragment.this.getActivity(), LaunchPermissions.class);
                        startActivity(intent);
                        Log.e(TAG, "User selected OK to LaunchPermissions.class on ProgramFragment AlertDialog");
                    }
                });

                // Sets operation for when "CANCEL" is selected
                alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the AlertDialog box
                        Log.e(TAG, "User selected to CANCEL the permissions prompt on ProgramFragment AlertDialog");
                        dialog.cancel();
                    }
                });

                // Show AlertDialog box
                alertDialog.show();

            }

        }

        // View the PDF stored locally in the app
        else if (view == btnView) {
            if (localFile.exists()){
                displayFile();
            } else {
                Toast.makeText(getContext(),"Please download the file to view it.", Toast.LENGTH_LONG).show();
            }


        }
    }


}

