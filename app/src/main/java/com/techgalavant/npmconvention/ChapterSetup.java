package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * This class is used to display the chapter setup information from the chaptersInfoList array in the ChaptersFragment in a card-like layout.
 * Users should be able to see information on setting up a chapter, etc.
 *
 * Credit to -
 * Collapsible view & card layout credit to CheeseSquare - - https://github.com/chrisbanes/cheesesquare
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class ChapterSetup extends AppCompatActivity {
    private static final String TAG = ChapterSetup.class.getSimpleName();

    TextView ch1, ch2, ch3, ch4, ch5;
    String chap1, chap2, chap3, chap4, chap5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chapter_detail);

        Intent intent = getIntent();

        // get the chapterInfoList items from ChaptersFragment
        String complete_chapter_info = (intent.getStringExtra("complete_chapter_info"));
        Log.e(TAG, "Chapter setup details - " + complete_chapter_info); // Log the correct information

        chap1 = intent.getStringExtra("chap1");
        chap2 = intent.getStringExtra("chap2");
        chap3 = intent.getStringExtra("chap3");
        chap4 = intent.getStringExtra("chap4");
        chap5 = intent.getStringExtra("chap5");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Let the user use their own back button to return to previous ChaptersFragment rather than showing a back arrow in the image.
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // The collapsing toolbar shows a random image in it as well as the Chapter's name
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Chapter Info");

        // load the top random image from EventImages
        loadBackdrop();

        // populate the textviews with the specific chapter details
        ch1 = (TextView) findViewById(R.id.ch_1_info);
        ch2 = (TextView) findViewById(R.id.ch_2_info);
        ch3 = (TextView) findViewById(R.id.ch_3_info);
        ch4 = (TextView) findViewById(R.id.ch_4_info);
        ch5 = (TextView) findViewById(R.id.ch_5_info);

        // Fill in the setup details for each item
        ch1.setText(chap1);
        ch2.setText(chap2);
        ch3.setText(chap3);
        ch4.setText(chap4);
        ch5.setText(chap5);
    }

    // Takes random images from EventImages class and uses it for the backdrop on the top
    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(EventImages.getRandomImage()).centerCrop().into(imageView);
    }

}