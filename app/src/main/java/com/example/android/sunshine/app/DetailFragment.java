package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = DetailFragment.class.getSimpleName();
     public static final int COL_WEATHER_ID = 0;
     public static final int COL_WEATHER_DATE = 1;
     public static final int COL_WEATHER_DESC = 2;
     public static final int COL_WEATHER_MAX_TEMP = 3;
     public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;

    private static final String[] FORECAST_COLUMNS = new String[]{
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            // This works because the WeatherProvider returns location data joined with
            // weather data, even though they're stored in two different tables.
             WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
    final int DETAIL_LOADER = 0;
    String mForecastString;
    ShareActionProvider mShareActionProvider;
    View mRootView;

    TextView  mDayTextview;
    TextView mDateTextview;
    TextView mHghTextview;
    TextView mLowTextview;
    ImageView mImage;
    TextView mDescriptionTextview;
    TextView mHumidityTextview;
    TextView mWindTextview;
    TextView mPressureTextview;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);
       mDayTextview = (TextView) mRootView.findViewById(R.id.detail_day_textview);
       mDateTextview = (TextView) mRootView.findViewById(R.id.detail_date_textview);
       mHghTextview = (TextView) mRootView.findViewById(R.id.detail_high_textview);
       mLowTextview = (TextView) mRootView.findViewById(R.id.detail_low_textview);
       mImage = (ImageView) mRootView.findViewById(R.id.detail_imageview);
       mDescriptionTextview = (TextView) mRootView.findViewById(R.id.detail_forecast_textview);
       mHumidityTextview = (TextView) mRootView.findViewById(R.id.detail_humidity_textview);
       mWindTextview = (TextView) mRootView.findViewById(R.id.detail_wind_textview);
       mPressureTextview = (TextView) mRootView.findViewById(R.id.detail_pressure_textview);
        return mRootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // If onLoadFinished happen before this, we can go ahead and set the share Intent
        if (mForecastString != null){
            mShareActionProvider.setShareIntent(createShareIntent());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareIntent(){
        Intent shareIntent = new Intent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastString + " " + FORECAST_SHARE_HASHTAG);
        shareIntent.setType("text/plain");
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        return new CursorLoader(getContext(),
                intent.getData(),
                FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, "onLoadeFinished");

        if (!data.moveToFirst()){return;}
        // Read date from cursor and update views for day of week and date
        long date = data.getLong(COL_WEATHER_DATE);
        String friendlyDateText = Utility.getDayName(getActivity(), date);
        String dateText = Utility.getFormattedMonthDay(getActivity(), date);
        mDayTextview.setText(friendlyDateText);
        mDateTextview.setText(dateText);

        int weather_condition_id = data.getInt(COL_WEATHER_CONDITION_ID);
        mImage.setImageResource(Utility.getArtResourceForWeatherCondition(weather_condition_id));

        boolean isMetric = Utility.isMetric(getContext());

        // Read description from cursor and update view
        String description = data.getString(COL_WEATHER_DESC);
        mDescriptionTextview.setText(description);

        // Read high temperature from cursor and update view

        double high = data.getDouble(COL_WEATHER_MAX_TEMP);
        String highString = Utility.formatTemperature(getActivity(), high, isMetric);
        mHghTextview.setText(highString);

        // Read low temperature from cursor and update view
        double low = data.getDouble(COL_WEATHER_MIN_TEMP);
        String lowString = Utility.formatTemperature(getActivity(), low, isMetric);
        mLowTextview.setText(lowString);

        // Read humidity from cursor and update view
      float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
      mHumidityTextview.setText(getActivity().getString(R.string.format_humidity, humidity));

      // Read wind speed and direction from cursor and update view
      float windSpeedStr = data.getFloat(COL_WEATHER_WIND_SPEED);
       float windDirStr = data.getFloat(COL_WEATHER_DEGREES);
       mWindTextview.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

       // Read pressure from cursor and update view
       float pressure = data.getFloat(COL_WEATHER_PRESSURE);
       mPressureTextview.setText(getActivity().getString(R.string.format_pressure, pressure));

       // We still need this for the share intent
        mForecastString = String.format("%s - %s - %s/%s", dateText, description, high, low);

        // If createOptionsMenu has already happened, we update the share Intent.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
