package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon on 2/15/2017.
 *
 * This is a constructor used for SendFeedback.
 * The Words are stored in the Firebase DB.
 */

public class Words {
    public String Sender;
    public String Contact;
    public String Feedback;


    // This is the default constructor for DataSnapshot.getValue(Words.class)
    public Words(){

        }

    public Words(String Sender, String Contact, String Feedback) {
            this.Sender = Sender;
            this.Contact = Contact;
            this.Feedback = Feedback;
        }

}
