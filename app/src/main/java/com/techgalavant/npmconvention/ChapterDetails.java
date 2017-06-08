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
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import static com.techgalavant.npmconvention.R.id.dir_adr;
import static com.techgalavant.npmconvention.R.id.dir_name;


public class ChapterDetails extends AppCompatActivity {
    private static final String TAG = ChapterDetails.class.getSimpleName();

    TextView chapName, chapDir, chapAddr;
    ImageView chapPhone, chapEmail, chapWebsite, chapFace;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chapter_detail);

        Intent intent = getIntent();

        // get the event list items from ChaptersFragment
        String complete_chapter = (intent.getStringExtra("complete_chapter"));
        Log.e(TAG, "Chapter-specific detail for the list item - " + complete_chapter); // Log the chapter details to ensure it's displaying the correct information

        final String chapadr = intent.getStringExtra("chapadr"); // chapter address
        final String chapname = intent.getStringExtra("chapname"); // chapter name
        final String chapdir = intent.getStringExtra("chapdir"); // chapter director's name
        final String chaptel = "Tel: " + (intent.getStringExtra("chaptel")); // chapter telephone
        final String chapem = "Email: " + (intent.getStringExtra("chapem")); // chapter email
        final String chapweb = intent.getStringExtra("chapweb"); // chapter website
        final String chapfb = intent.getStringExtra("chapfb"); // chapter FaceBook

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
        //chapName = (TextView) findViewById(chap_name);
        chapDir = (TextView) findViewById(dir_name);
        chapAddr = (TextView) findViewById(dir_adr);

        //chapName.setText(chapname);
        chapAddr.setText(chapadr);
        chapDir.setText(chapdir);

        chapPhone = (ImageView) findViewById(R.id.chap_ph);
        chapEmail = (ImageView) findViewById(R.id.chap_em);
        chapWebsite = (ImageView) findViewById(R.id.chap_web);
        chapFace = (ImageView) findViewById(R.id.chap_fb);

        // if the user clicks on the phone icon, then open phone to dial
        chapPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(chaptel));
                startActivity(browserIntent);
            }
        });

        // if the user selects the email icon, then launch email application with email address populated
        chapEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bit.ly/2qU6DKL"));
                startActivity(browserIntent);

                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/html");
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Message to " + chapname);
                sendIntent.putExtra(Intent.EXTRA_EMAIL, chapem);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Dear " + chapdir + ",");
                startActivity(Intent.createChooser(sendIntent, "Select:"));
            }
        });

        // if the user selects the website icon, then launch web browser and bring the user to the chapter's website
        chapWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chapweb));
                startActivity(browserIntent);
            }
        });

        // if the user selects the FaceBook icon, then launch web browser and bring the user to their Facebook page
        chapFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chapfb));
                startActivity(browserIntent);
            }
        });

    }

    // Takes random images from EventImages class and uses it for the backdrop on the top
    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(EventImages.getRandomImage()).centerCrop().into(imageView);
    }


}