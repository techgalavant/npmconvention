package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * The purpose of this class is to show some information about the creator of this app.
 */

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class About extends AppCompatActivity {
    private static final String TAG = About.class.getSimpleName();

    TextView tvTitle, tvAbout, tvContact;
    ImageView ivPhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        final String title = getResources().getString(R.string.about);  // title
        final String info = getResources().getString(R.string.info);  // info about Tech Galavant
        final String contact = getResources().getString(R.string.contact);  // info to contact
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set to false as there is already a back button on the user's device
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // The collapsing toolbar shows a random image in it as well as the event title from the list event
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(title);

        // loads a random image from EventImages with the 'getAboutImages' class
        loadBackdrop();

        // populate the textviews and imageviews with the specific event list items
        ivPhoto = (ImageView) findViewById(R.id.photo);
        tvTitle = (TextView) findViewById(R.id.about_title);
        tvAbout = (TextView) findViewById(R.id.about);
        tvContact = (TextView) findViewById(R.id.contact);

        ivPhoto.setImageResource(R.drawable.img_3);
        tvTitle.setText(title);
        tvAbout.setText(info);
        tvContact.setText(contact);
    }


    // Takes random images from EventImages class and uses it for the backdrop on the top
    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(EventImages.getAboutImage()).centerCrop().into(imageView);
    }

    // Possibly setup a menu button so that users can view their favorites?
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }


}
