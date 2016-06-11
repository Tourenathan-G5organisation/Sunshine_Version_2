package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A ForecastFragment fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    public static String LOG_TAG = ForecastFragment.class.getSimpleName();

    ArrayAdapter<String> mForecastAdapter;
    public ForecastFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This indicate that this fragment want to handle menu options
        setHasOptionsMenu(true);
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

        mForecastAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.list_item_forecast, // ID of list item layout
                R.id.list_item_forecast_textview, //ID of textview to populate
                forecastList);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), mForecastAdapter.getItem(position), Toast.LENGTH_LONG).show();
            }
        });


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh){

            new FetchWeatherTAsk().execute("94043");
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchWeatherTAsk extends AsyncTask<String, Void, String[]>{

        public final String LOG_TAG = FetchWeatherTAsk.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String unit = "metric";
            int numDays = 7;
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String uri = Uri.parse(getString(R.string.base_url)).buildUpon()
                        .appendQueryParameter(getString(R.string.forecast_query_param), params[0])
                        .appendQueryParameter(getString(R.string.forecast_mode_param), format)
                        .appendQueryParameter(getString(R.string.forecast_unit_param), unit)
                        .appendQueryParameter(getString(R.string.forecast_day_param), String.valueOf(numDays))
                        .appendQueryParameter(getString(R.string.forecast_APPID_param), getString(R.string.app_weathermap_id))
                        .build().toString();
                Log.d(LOG_TAG, uri);
                URL url = new URL(uri);
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&APPID=172cf43b195b37d3429651823f0ac2dd");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            Log.d(LOG_TAG, "Data: " + forecastJsonStr);
            try {
                return WeatherDataParser.getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] weekForecast) {
            if (weekForecast != null){
                mForecastAdapter.clear();
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                   mForecastAdapter.addAll(Arrays.asList(weekForecast));
                else {
                   for (String dayForecast: weekForecast){
                       mForecastAdapter.add(dayForecast);
                   }
               }

                Log.d(LOG_TAG, String.valueOf(weekForecast));
            }

        }
    }
}
