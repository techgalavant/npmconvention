package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 * ProgramFragment
 *
 * This is a screen to download the convention program from a Firebase URL.
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
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.List;


public class ProgramFragment extends Fragment implements View.OnClickListener,OnPageChangeListener,OnLoadCompleteListener{
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
    private String pdfFile = "2017Convention.pdf"; // the name of the PDF file
    File localFile = new File(Environment.getExternalStorageDirectory()+pdfDir, pdfFile);

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
        Toast.makeText(getContext(),"Page " + pageNumber + " of " + pageCount, Toast.LENGTH_SHORT).show();
        //request.setTitle(String.format("%s %s / %s", pdfFile, page + 1, pageCount));
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
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
            Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/npm-convention.appspot.com/o/2017convention.pdf?alt=media&token=b9db1a12-3370-47f0-b422-9fa44e4c23df");
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
        }
    }

}

