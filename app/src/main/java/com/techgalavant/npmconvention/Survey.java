package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon on 2/15/2017.
 *
 * This is a constructor used for sending feedback from the EventDetails.java
 * The feedback is stored in the Firebase DB.
 */

public class Survey {
    public String EventTitle;
    public String Feedback;

    // This is the default constructor for DataSnapshot.getValue(Survey.class)
    public Survey(){

        }

    public Survey(String EventTitle, String Feedback) {
        this.EventTitle = EventTitle;
        this.Feedback = Feedback;

        }

}
