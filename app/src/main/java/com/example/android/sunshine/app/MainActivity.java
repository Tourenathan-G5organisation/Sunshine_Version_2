package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // We will now create some dummy data for the Listview

            // Sample Weather information for a week: day - wether - high/low
            String[] weeklyWeather = new String[]{
                    "Today - Sunny - 88/63",
                    "Tomorrow - Fuggy - 70/46",
                    "Weds - Cloudy - 72/63",
                    "Thurs - Rainy - 64/51",
                    "Fri - Foggy - 70/46",
                    "Sat - Sunny - 70/68",
                    "Sun - Cloudy - 71/62"
            };

            ArrayList<String> forecastList = new ArrayList<>();
            forecastList.addAll(Arrays.asList(weeklyWeather));

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    R.layout.list_item_forecast, // ID of list item layout
                    R.id.list_item_forecast_textview, //ID of textview to populate
                    forecastList);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
            listView.setAdapter(adapter);
            return rootView;
        }
    }
}
