package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * This is the search adapter to be used in the EventsFragment
 * It's related to SearchGetSet and JsonParse as well
 *
 * Credits to - http://manishkpr.webheavens.com/android-autocompletetextview-example-json/
 * Ref also - http://stackoverflow.com/questions/24545345/how-to-bind-autocompletetextview-from-hashmap
 *
 */

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SearchAdapter extends ArrayAdapter<String> {

    protected static final String TAG = "SearchAdapter";
    private List<String> suggestions;
    ArrayList<HashMap<String,String>> autoCompleteList; // use this to build a hashmap of autocompletelist items (similar to eventlist??)


    public SearchAdapter(Activity context, String nameFilter) {
        super(context,R.layout.list_item_black_text);
        suggestions = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public String getItem(int index) {
        return suggestions.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                JsonParse jp=new JsonParse();
                if (constraint != null) {

                    // See also for below - http://stackoverflow.com/questions/23073627/json-array-to-auto-complete-text-view-in-android

                    // SearchGetSet is an ArrayList of Events parsed by the JsonParse.java
                    // this should return an arraylist
                    List<SearchGetSet> new_suggestions = jp.getParseJsonWCF(constraint.toString());
                    suggestions.clear();
                    for (int i = 0; i<new_suggestions.size(); i++) {

                        suggestions.add(new_suggestions.get(i).getName());
                    }

                    // Now assign the values and count to the FilterResults
                    // object
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint,
                                          FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return myFilter;
    }

}
