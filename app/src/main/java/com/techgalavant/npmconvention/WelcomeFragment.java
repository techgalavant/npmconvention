package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * The Welcome fragment will show a message to users.
 * It is integrated with the Firebase Database.
 *
 * Credits to:
 * - Android Hive tutorial - http://www.androidhive.info/2016/10/android-working-with-firebase-realtime-database/
 * - Crazy Madlibs App on GitHub (my other app!) - https://github.com/techgalavant/madlibs
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.net.HttpURLConnection;
import java.net.URL;


public class WelcomeFragment extends Fragment{

    private static final String TAG = WelcomeFragment.class.getSimpleName();

    private TextView txtWelcom, txtMsg, tvDate;

    private String hermosa; // used to display messages on WelcomeFragment

    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO check URL if news exists, then have a FAB to open it

        String customURL = "https://www.brockmann.com/apps/npmconvention/2017/objects/NEWS_Tuesday.pdf";  // to be used for the daily news file

        MyTask task = new MyTask(); // conducts an async check to see if a file exists on a URL
        task.execute(customURL); // for checking if a file exists at a URL - really if a URL exists

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Set view to welcome page layout
        View rootView = inflater.inflate(R.layout.welcome_frag, container, false);


        // Used to retrieve welcome messages in the Firebase DB
        hermosa = getResources().getString(R.string.hermosa);
        DatabaseReference myRef = MyFirebaseUtil.getDatabase().getReference(hermosa); // see MyFirebaseUtil.class
        DatabaseReference myMesg = myRef.child("Feedback");
        DatabaseReference mesgTime = myRef.child("Contact");

        // Show a friendly message about the screen's purpose
        txtWelcom = (TextView) rootView.findViewById(R.id.welcominfo);

        // Show a friendly date and time to the user
        tvDate = (TextView) rootView.findViewById(R.id.appdate); // Shows the time for the most recent message

        txtMsg = (TextView) rootView.findViewById(R.id.quicktext); // Used to show the current message

        // Read from the Firebase database
/*

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String added = dataSnapshot.getChildren().toString();
                //String value = dataSnapshot.getChildren().getValue(String.class);

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.e(TAG,""+ childDataSnapshot.getKey()); //displays the key for the node
                    Log.e(TAG,""+ childDataSnapshot.child("Feedback").getValue());   //gives the value for given keyname
                    //String added = childDataSnapshot.child("Feedback").getValue(String.class);

                }

                txtMsg.setText(added);
                Toast.makeText(getContext(),"FB DB just added child " + value, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);
                //Toast.makeText(getContext(),"FB DB just changed child", Toast.LENGTH_LONG).show();

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.e(TAG,""+ childDataSnapshot.getKey()); //displays the key for the node
                    Log.e(TAG,""+ childDataSnapshot.child("Feedback").getValue());   //gives the value for given keyname
                    String changed = childDataSnapshot.child("Feedback").getValue(String.class);
                    txtMsg.setText(changed);
                }
                Toast.makeText(getContext(),"FB DB just changed child " + value, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", databaseError.toException());

            }
        });

*/

        // Read from the Firebase database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method will present an update on the Welcome Fragment
                // whenever data changes.
                String updatetime = dataSnapshot.child("Contact").getValue(String.class);  // "Contact" is actually the time that the update was provided - see SendFeedback if userName equals...
                String updatemesg = dataSnapshot.child("Feedback").getValue(String.class); // "Feedback" is converted to message if userName.equals ...

                tvDate.setText(updatetime); // provide the update time
                txtMsg.setText(updatemesg); // provide the update message

                Log.e(TAG, "Update time = " + updatetime + " and Update Mesg = " + updatemesg);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });


        // Inflate the layout for this fragment
        return rootView;


    }

    // Check to see if the file exists at a URL - this is to be used with the daily news PDF
    private class MyTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con =  (HttpURLConnection) new URL(params[0]).openConnection();
                con.setRequestMethod("HEAD");
                return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            boolean bResponse = result;
            if (bResponse)
            {
                //Toast.makeText(getContext(), "File exists!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "File exists at URL");
            }
            else
            {
                //Toast.makeText(getContext(), "File does not exist!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "File does not exist at URL");
            }
        }
    }


}
