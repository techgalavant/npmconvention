package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 * ProgramFragment
 *
 * This is a screen to download & display the convention program which is a PDF file.
 * Credits to -
 * Displaying it use - https://github.com/barteksc/AndroidPdfViewer
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
import android.widget.EditText;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.List;


public class ProgramFragment_Search extends Fragment implements View.OnClickListener,OnPageChangeListener,OnLoadCompleteListener {
    public static final String TAG = ProgramFragment_Search.class.getSimpleName();

    //Buttons
    private Button btnDownload;
    private Button btnView;
    private Button btnGoToPage;

    // A download manager is used to download a file from a URL onto the device
    DownloadManager downloadManager;

    // PDFView from https://github.com/barteksc/AndroidPdfViewer
    PDFView pdfView;
    Integer pageNumber = 0;
    private String pdfDir = "/NPM"; // the name of the directory to store the PDF files
    private String pdfFile = "ConventionBrochure.pdf"; // the name of the PDF file
    File localFile = new File(Environment.getExternalStorageDirectory()+pdfDir, pdfFile);

    // use this boolean to display pages as Toast
    boolean mUserVisibleHint = true;

    // go to page
    private EditText goToPage;

    public ProgramFragment_Search() {
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
                View rootView = inflater.inflate(R.layout.programb_search_frag, container, false);

                // Used to show the downloaded PDF
                pdfView = (PDFView) rootView.findViewById(R.id.pdfView);

                displayFile();

                return rootView;

            } else {
                // show screen with download button
                Log.e(TAG, pdfFile + " not found at " + pdfDir);

                // Display the fragment with the download buttons
                View rootView = inflater.inflate(R.layout.program_search_frag, container, false);

                //getting views from layout
                btnDownload = (Button) rootView.findViewById(R.id.btnDownload);
                btnView = (Button) rootView.findViewById(R.id.btnView);
                btnGoToPage = (Button) rootView.findViewById(R.id.btnGoToPage);

                goToPage = (EditText) rootView.findViewById(R.id.goto_page);

                // Used to show the downloaded PDFs
                pdfView = (PDFView) rootView.findViewById(R.id.pdfView);

                //attaching listener
                btnDownload.setOnClickListener(this);
                btnView.setOnClickListener(this);
                btnGoToPage.setOnClickListener(this);



                return rootView;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return getView();
    }

    private void displayFile(){

        File localFile = new File(Environment.getExternalStorageDirectory()+pdfDir, pdfFile);

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

    // Need a mechanism for going to a specific page in the PDF
    private void goToPage(int page){
        pdfView.jumpTo(page);

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


    @Override
    public void onClick(View view) {
        // Download file to sdcard
        if (view == btnDownload) {
           downloadManager = (DownloadManager)getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/npm-convention.appspot.com/o/ConventionBrochure.pdf?alt=media&token=bf07296e-cc61-4429-b27e-ad628c6eb486");
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDestinationInExternalPublicDir(pdfDir,pdfFile);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle(pdfFile);
            Long reference = downloadManager.enqueue(request);
        }
        // View the PDF stored locally in the app
        else if (view == btnView) {
            if (localFile.exists()){
                displayFile();
            } else {
                Toast.makeText(getContext(),"Please download the file to view it.", Toast.LENGTH_LONG).show();
            }

            // allow user to go to a specific page
        } else if (view == btnGoToPage){
            if (goToPage!=null){ // if string is entered in edittext
                int pagenumber;
                try { // try parsing the string to a number (int)
                    pagenumber = Integer.parseInt(goToPage.getText().toString());
                    Log.e(TAG, "btnGoToPage was pressed. User tried to go to page " + pagenumber +".");
                    pdfView.jumpTo(pagenumber);
                }catch(NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                    Toast.makeText(getContext(),"Use a correct number format.", Toast.LENGTH_LONG).show();  // toast if they haven't entered it as a number
                }
            } else {
                Toast.makeText(getContext(), "You haven't entered a page number.", Toast.LENGTH_LONG).show();  // toast that they need to enter a number
            }
        }
    }



}

