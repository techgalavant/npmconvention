package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 * ProgramFragment
 *
 * This is a screen to download the convention program from Firebase.
 * Credit to -
 * https://www.simplifiedcoding.net/firebase-storage-tutorial-android/
 * https://firebase.google.com/docs/storage/android/download-files#create_a_reference
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        storageReference = FirebaseStorage.getInstance().getReference();

        return rootView;

    }
    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
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

