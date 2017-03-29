package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 * ProgramFragment
 *
 * This is a screen to download the convention program from Firebase.
 * Credit to -
 * https://www.simplifiedcoding.net/firebase-storage-tutorial-android/
 * https://firebase.google.com/docs/storage/android/download-files#create_a_reference
 * Displaying it use - http://androidsrc.net/create-and-display-pdf-within-android-application/
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


public class ProgramFragment extends Fragment implements View.OnClickListener{
    public static final String TAG = ProgramFragment.class.getSimpleName();

    //a constant to track the file chooser intent
    private static final int PICK_IMAGE_REQUEST = 234;

    //Buttons
    private Button btnDownload;
    private Button btnUpload;

    //ImageView
    private ImageView imageView;

    //a Uri object to store file path
    private Uri filePath;

    //firebase storage reference
    private StorageReference storageReference;

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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.program_frag, container, false);

        //getting views from layout
        btnDownload = (Button) rootView.findViewById(R.id.btnDownload);
        btnUpload = (Button) rootView.findViewById(R.id.btnUpload);

        imageView = (ImageView) rootView.findViewById(R.id.imageView);

        //attaching listener
        btnDownload.setOnClickListener(this);
        btnUpload.setOnClickListener(this);

        //getting firebase storage reference
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://npm-convention.appspot.com/2017convention.pdf");

        return rootView;

    }

    private void showFileChooser() {
        File localFile = new File(Environment.getExternalStorageDirectory(), "2017convention.pdf");
        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Successfully downloaded data to local file
                Log.e(TAG,"Successfully downloaded file and stored it on local device.");
                Toast.makeText(getContext(), "SUCCESS!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                Log.e(TAG,"Unsuccessful at downloading and storing file locally.");
                Toast.makeText(getContext(), "Unsuccessful at download...", Toast.LENGTH_LONG).show();
            }
        });


        /*// this code below picks a local file but not from Firebase!!
        Intent intent = new Intent();
        // setting the intent to only show images to pick from
        intent.setType("image*//*");
        // intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);*/
    }

    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        //if the clicked button is choose
        if (view == btnDownload) {
            showFileChooser();
        }
        //if the clicked button is upload
        else if (view == btnUpload) {

        }
    }

}

