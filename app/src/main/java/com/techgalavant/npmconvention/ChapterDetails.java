package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * This class is used to display the chapter details from the ChaptersFragment in a card-like layout.
 * Users should be able to call, email, visit FaceBook, or Chapter website
 *
 * Credit to -
 * Collapsible view & card layout credit to CheeseSquare - - https://github.com/chrisbanes/cheesesquare
 */

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import static com.techgalavant.npmconvention.R.id.ch_name;
import static com.techgalavant.npmconvention.R.id.dir_adr;
import static com.techgalavant.npmconvention.R.id.dir_name;


public class ChapterDetails extends AppCompatActivity {
    private static final String TAG = ChapterDetails.class.getSimpleName();

    TextView chapName, chapDir, chapAddr;
    ImageView chapPhone, chapEmail, chapWebsite, chapFace;
    String chapadr, chapname, chapdir, chaptel, chapem, chapweb, chapfb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chapter_detail);

        Intent intent = getIntent();

        // get the chapter list items from ChaptersFragment
        String complete_chapter = (intent.getStringExtra("complete_chapter"));
        Log.e(TAG, "Chapter-specific detail for the list item - " + complete_chapter); // Log the chapter details to ensure it's displaying the correct information

        chapadr = intent.getStringExtra("chapadr"); // chapter address
        chapname = intent.getStringExtra("chapname"); // chapter name
        chapdir = intent.getStringExtra("chapdir"); // chapter director's name
        chaptel = intent.getStringExtra("chaptel"); // chapter telephone
        chapem = intent.getStringExtra("chapem"); // chapter email
        chapweb = intent.getStringExtra("chapweb"); // chapter website
        chapfb = intent.getStringExtra("chapfb"); // chapter FaceBook

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Let the user use their own back button to return to previous ChaptersFragment rather than showing a back arrow in the image.
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // The collapsing toolbar shows a random image in it as well as the Chapter's name
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(chapname);

        // load the top random image from EventImages
        loadBackdrop();

        // populate the textviews and imageviews with the specific chapter details
        chapName = (TextView) findViewById(ch_name);
        chapDir = (TextView) findViewById(dir_name);
        chapAddr = (TextView) findViewById(dir_adr);

        chapName.setText(chapname);
        chapAddr.setText(chapadr);
        chapDir.setText("Director: " + chapdir);

        chapPhone = (ImageView) findViewById(R.id.chap_ph);
        chapEmail = (ImageView) findViewById(R.id.chap_em);
        chapWebsite = (ImageView) findViewById(R.id.chap_web);
        chapFace = (ImageView) findViewById(R.id.chap_fb);

        // if the user clicks on the phone icon, then open phone to dial
        chapPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "Action: callPhone selected.");

                PackageManager pm = getPackageManager();  // need to check to see if device has phone capability

                if (chaptel.isEmpty()){
                    // Toast to the user that no phone number provided
                    Toast.makeText(getApplicationContext(), "No phone number provided for this chapter", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "No phone number for this chapter provided.");
                } else if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)){
                    // Launch if the device has phone capability
                    Intent browserIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + chaptel));
                    startActivity(browserIntent);
                    Log.e(TAG, "Device has phone capability and launching with tel:" + chaptel);
                } else {
                    // show the telephone number
                    Toast.makeText(getApplicationContext(), "Tel: " + chaptel, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Device doesn't have phone capability.");
                }

            }
        });

        // if the user selects the email icon, then launch email application with email address populated
        chapEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "Action: sendEmail selected.");

                // first check to make sure that the email field is not null
                if (chapem.isEmpty()){
                    // if no email provided then Toast to the user
                    Toast.makeText(getApplicationContext(), "No email provided by this chapter", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "No email for this chapter provided.");

                } else {
                    // launch email with some fields pre-filled
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("text/html");
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Message to " + chapname);
                    sendIntent.putExtra(Intent.EXTRA_EMAIL, chapem);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Dear " + chapdir + ",");
                    startActivity(Intent.createChooser(sendIntent, "Select:")); // user should be able to pick which email option

                    Log.e(TAG, "Launched email application to send email to " + chapem);
                }

            }
        });

        // if the user selects the website icon, then launch web browser and bring the user to the chapter's website
        chapWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "Action: visitWeb selected.");

                // first check to make sure that the chapter's website field is not null
                if (chapweb.isEmpty()){
                    // if no chapter website, just Toast message to inform the user
                    Toast.makeText(getApplicationContext(), "No website provided by this chapter", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "No website for this chapter provided.");
                } else {
                    // launch user's web browser with the website
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chapweb));
                    startActivity(browserIntent);

                    Log.e(TAG, "Launched web browser for chapter website at " + chapweb);
                }

            }
        });

        // if the user selects the FaceBook icon, then launch web browser and bring the user to their Facebook page
        chapFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Action: visitFacebook selected.");

                if (chapfb.isEmpty()){

                    // if no chapter Facebook page, just Toast message to inform the user
                    Toast.makeText(getApplicationContext(), "No FaceBook link provided by this chapter", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "No FaceBook link for this chapter provided.");

                } else {

                    // launch user's web browser with the facebook website
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chapfb));
                    startActivity(browserIntent);

                    Log.e(TAG, "Launched web browser for chapter's Facebook page at " + chapfb);
                }

            }
        });

    }

    // Takes random images from EventImages class and uses it for the backdrop on the top
    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(EventImages.getRandomImage()).centerCrop().into(imageView);
    }

}