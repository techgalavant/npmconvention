package com.techgalavant.npmconvention;

import java.util.Random;

/**
 * Created by Mike Fallon
 *
 * The purpose of this class is to present images as a backdrop on the EventDetails page
 * Credit to CheeseSquare - https://github.com/chrisbanes/cheesesquare
 *
 */

public class EventImages {

    private static final Random RANDOM = new Random();

    public static int getRandomImage() {
        switch (RANDOM.nextInt(11)) {
            default:
            case 0:
                return R.drawable.image3;
            case 1:
                return R.drawable.image9;
            case 2:
                return R.drawable.image3;
            case 3:
                return R.drawable.image4;
            case 4:
                return R.drawable.image10;
            case 5:
                return R.drawable.image6;
            case 6:
                return R.drawable.image7;
            case 7:
                return R.drawable.image8;
            case 8:
                return R.drawable.image9;
            case 9:
                return R.drawable.image10;
            case 10:
                return R.drawable.image11;
        }
    }
}
