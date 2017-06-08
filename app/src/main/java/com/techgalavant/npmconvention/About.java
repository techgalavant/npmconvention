package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * The purpose of this class is to show some information about the creator of this app.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class About extends AppCompatActivity {
    private static final String TAG = About.class.getSimpleName();

    TextView tvAbout, tvContact, tvWeb, tvPrivacy;
    ImageView ivPhoto, ivWeb, ivDH;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Set to false as there is already a back button on the user's device
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // populate the textviews and imageviews with the specific event list items
        ivPhoto = (ImageView) findViewById(R.id.photo);
        ivWeb = (ImageView) findViewById(R.id.blog);
        ivDH = (ImageView) findViewById(R.id.digital);
        tvAbout = (TextView) findViewById(R.id.info);
        tvContact = (TextView) findViewById(R.id.contact);
        tvWeb = (TextView) findViewById(R.id.website);
        tvPrivacy = (TextView) findViewById(R.id.privacy);

        final String title = getResources().getString(R.string.about);  // title
        final String info = getResources().getString(R.string.info);  // info about Tech Galavant
        final String contact = getResources().getString(R.string.contact);  // info to contact
        final String website = getResources().getString(R.string.website); // techgalavant blog
        final String privacy = getResources().getString(R.string.privacy); // title for Privacy Policy section

        // The collapsing toolbar shows a random image in it as well as the event title from the list event
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(title);

        // loads a random image from EventImages with the 'getAboutImages' class
        loadBackdrop();

        ivPhoto.setImageResource(R.drawable.devteam);  // photo of Peter and me!
        tvAbout.setText(info);
        tvContact.setText(contact);
        tvWeb.setText(website);
        tvPrivacy.setText(privacy);

        // if the user clicks on the logo, then launch web browser and bring them to my blog
        ivWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.techgalavant.com"));
                startActivity(browserIntent);
            }
        });

        // if the user selects the Privacy Policy title, then launch the web browser and bring them to the policy online
        tvPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bit.ly/2qU6DKL"));
                startActivity(browserIntent);
            }
        });

        // if the user selects Digital Hermosa image, then launch web browser and bring the user to the DH blog
        ivDH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.brockmann.com/digitalhermosa/"));
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
