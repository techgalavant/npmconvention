package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * The purpose of this activity is to display the Exhibitors info
 * Users should be able to call, email, visit FaceBook, or website of the Exhibitor
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


public class ExhibitsExpandActivity extends AppCompatActivity {

    private static final String TAG = ExhibitsExpandActivity.class.getSimpleName();

    TextView exhibitName, exhibitBooth, exhibitContact, exhibitProse;
    ImageView exhbtPhone, exhbtEmail, exhbtWebsite, exhbtFace, exhbtLogo;
    String exbooth, exname, exprose, extel, exemail, exweb, exfb, excontact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        String complete_exhibitor = (intent.getStringExtra("complete_exhbitor"));
        Log.e(TAG, "Exhibitor details for the list item - " + complete_exhibitor); // Log the details to ensure it's displaying the correct information

        exbooth = intent.getStringExtra("booth"); // booth number
        exname = intent.getStringExtra("exhibitor"); // exhibitor name
        exprose = intent.getStringExtra("prose"); // exhibitor info
        extel = intent.getStringExtra("phone"); // exhibitor telephone
        exemail = intent.getStringExtra("email"); // exhibitor email
        exweb = intent.getStringExtra("web"); // exhibitor website
        exfb = intent.getStringExtra("facebook"); // exhibitor FaceBook site
        excontact = intent.getStringExtra("contact"); // exhibitor's contact name


        if (exbooth.contains("216")) {
            setContentView(R.layout.exhibits_detail);

            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // Let the user use their own back button to return to previous ChaptersFragment rather than showing a back arrow in the image.
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            // The collapsing toolbar shows a random image in it as well as the Chapter's name
            CollapsingToolbarLayout collapsingToolbar =
                    (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(exname);

            // load the top random image from EventImages
            loadBackdrop();

            // populate the textviews and imageviews with the specific chapter details
            exhibitName = (TextView) findViewById(R.id.exhibit_name);
            exhibitBooth = (TextView) findViewById(R.id.exhibit_booth);
            exhibitContact = (TextView) findViewById(R.id.exhibit_contact);
            exhibitProse = (TextView) findViewById(R.id.exhibit_prose);

            exhibitName.setText(exname);
            exhibitBooth.setText(exbooth);
            exhibitContact.setText(excontact);
            exhibitProse.setText(exprose);

            exhbtPhone = (ImageView) findViewById(R.id.chap_ph);
            exhbtEmail = (ImageView) findViewById(R.id.chap_em);
            exhbtWebsite = (ImageView) findViewById(R.id.chap_web);
            exhbtFace = (ImageView) findViewById(R.id.chap_fb);
            exhbtLogo = (ImageView) findViewById(R.id.digital);

            // if the user clicks on the phone icon, then open phone to dial
            exhbtPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e(TAG, "Action: callPhone selected.");

                    PackageManager pm = getPackageManager();  // need to check to see if device has phone capability

                    if (extel.isEmpty()) {
                        // Toast to the user that no phone number provided
                        Toast.makeText(getApplicationContext(), "No phone number provided for this exhibitor", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "No phone number for this exhibitor provided.");
                    } else if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                        // Launch if the device has phone capability
                        Intent browserIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + extel));
                        startActivity(browserIntent);
                        Log.e(TAG, "Device has phone capability and launching with tel:" + extel);
                    } else {
                        // show the telephone number
                        Toast.makeText(getApplicationContext(), "Tel: " + extel, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Device doesn't have phone capability.");
                    }

                }
            });

            // if the user selects the email icon, then launch email application with email address populated
            exhbtEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e(TAG, "Action: sendEmail selected.");

                    // first check to make sure that the email field is not null
                    if (exemail.isEmpty()) {
                        // if no email provided then Toast to the user
                        Toast.makeText(getApplicationContext(), "No email provided by this exhibitor", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "No email for this exhibitor provided.");

                    } else {
                        // launch email with some fields pre-filled
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.setType("text/html");
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Message to " + exname);
                        sendIntent.putExtra(Intent.EXTRA_EMAIL, exemail);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Dear " + excontact + ",");
                        startActivity(Intent.createChooser(sendIntent, "Select:")); // user should be able to pick which email option

                        Log.e(TAG, "Launched email application to send email to " + exemail);
                    }

                }
            });

            // if the user selects the website icon, then launch web browser and bring the user to the chapter's website
            exhbtWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e(TAG, "Action: visitWeb selected.");

                    // first check to make sure that the chapter's website field is not null
                    if (exweb.isEmpty()) {
                        // if no chapter website, just Toast message to inform the user
                        Toast.makeText(getApplicationContext(), "No website provided by this exhibitor", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "No website for this exhibitor provided.");
                    } else {
                        // launch user's web browser with the website
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(exweb));
                        startActivity(browserIntent);

                        Log.e(TAG, "Launched web browser for exhibitor website at " + exweb);
                    }

                }
            });

            // if the user selects the FaceBook icon, then launch web browser and bring the user to their Facebook page
            exhbtFace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "Action: visitFacebook selected.");

                    if (exfb.isEmpty()) {

                        // if no chapter Facebook page, just Toast message to inform the user
                        Toast.makeText(getApplicationContext(), "No FaceBook link provided by this exhibitor", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "No FaceBook link for this exhibitor provided.");

                    } else {

                        // launch user's web browser with the facebook website
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(exfb));
                        startActivity(browserIntent);

                        Log.e(TAG, "Launched web browser for exhibitor's Facebook page at " + exfb);
                    }

                }
            });

        } else {
                setContentView(R.layout.exhibits_empty_detail);

                final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                // Let the user use their own back button to return to previous ChaptersFragment rather than showing a back arrow in the image.
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                // The collapsing toolbar shows a random image in it as well as the Chapter's name
                CollapsingToolbarLayout collapsingToolbar =
                        (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                collapsingToolbar.setTitle(exname);

                // load the top random image from EventImages
                loadBackdrop();

                // populate the textviews and imageviews with the specific chapter details
                exhibitName = (TextView) findViewById(R.id.exhibit_name);
                exhibitBooth = (TextView) findViewById(R.id.exhibit_booth);
                exhibitContact = (TextView) findViewById(R.id.exhibit_contact);
                exhibitProse = (TextView) findViewById(R.id.exhibit_prose);

                exhibitName.setText(exname);
                exhibitBooth.setText(exbooth);

                if (excontact.isEmpty()){
                    exhibitContact.setText(getResources().getString(R.string.contact_na));
                } else {
                    exhibitContact.setText("Contact: " + excontact);
                }

                if (exprose.isEmpty()){
                    exhibitProse.setText(getResources().getString(R.string.not_avail));
                } else {
                    exhibitProse.setText(exprose);
                }

                exhbtPhone = (ImageView) findViewById(R.id.chap_ph);
                exhbtEmail = (ImageView) findViewById(R.id.chap_em);
                exhbtWebsite = (ImageView) findViewById(R.id.chap_web);
                exhbtFace = (ImageView) findViewById(R.id.chap_fb);

                // if the user clicks on the phone icon, then open phone to dial
                exhbtPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.e(TAG, "Action: callPhone selected.");

                        PackageManager pm = getPackageManager();  // need to check to see if device has phone capability

                        if (extel.isEmpty()) {
                            // Toast to the user that no phone number provided
                            Toast.makeText(getApplicationContext(), "No phone number provided by this exhibitor", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "No phone number for this exhibitor provided.");
                        } else if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                            // Launch if the device has phone capability
                            Intent browserIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + extel));
                            startActivity(browserIntent);
                            Log.e(TAG, "Device has phone capability and launching with tel:" + extel);
                        } else {
                            // show the telephone number
                            Toast.makeText(getApplicationContext(), "Tel: " + extel, Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Device doesn't have phone capability.");
                        }

                    }
                });

                // if the user selects the email icon, then launch email application with email address populated
                exhbtEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.e(TAG, "Action: sendEmail selected.");

                        // first check to make sure that the email field is not null
                        if (exemail.isEmpty()) {
                            // if no email provided then Toast to the user
                            Toast.makeText(getApplicationContext(), "No email provided by this exhibitor", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "No email for this exhibitor provided.");

                        } else {
                            // launch email with some fields pre-filled
                            Intent sendIntent = new Intent(Intent.ACTION_SEND);
                            sendIntent.setType("text/html");
                            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Message to " + exname);
                            sendIntent.putExtra(Intent.EXTRA_EMAIL, exemail);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, "Dear " + excontact + ",");
                            startActivity(Intent.createChooser(sendIntent, "Select:")); // user should be able to pick which email option

                            Log.e(TAG, "Launched email application to send email to " + exemail);
                        }

                    }
                });

                // if the user selects the website icon, then launch web browser and bring the user to the chapter's website
                exhbtWebsite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.e(TAG, "Action: visitWeb selected.");

                        // first check to make sure that the chapter's website field is not null
                        if (exweb.isEmpty()) {
                            // if no chapter website, just Toast message to inform the user
                            Toast.makeText(getApplicationContext(), "No website provided by this exhibitor", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "No website for this exhibitor provided.");
                        } else {
                            // launch user's web browser with the website
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(exweb));
                            startActivity(browserIntent);

                            Log.e(TAG, "Launched web browser for exhibitor website at " + exweb);
                        }

                    }
                });

                // if the user selects the FaceBook icon, then launch web browser and bring the user to their Facebook page
                exhbtFace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "Action: visitFacebook selected.");

                        if (exfb.isEmpty()) {

                            // if no chapter Facebook page, just Toast message to inform the user
                            Toast.makeText(getApplicationContext(), "No FaceBook link provided by this exhibitor", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "No FaceBook link for this exhibitor provided.");

                        } else {

                            // launch user's web browser with the facebook website
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(exfb));
                            startActivity(browserIntent);

                            Log.e(TAG, "Launched web browser for exhibitor's Facebook page at " + exfb);
                        }

                    }
                });


            }
        }

    // Takes random images from EventImages class and uses it for the backdrop on the top
    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(EventImages.getRandomImage()).centerCrop().into(imageView);
    }

}