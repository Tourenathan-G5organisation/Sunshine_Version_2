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
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

public class DetailActivity extends ActionBarActivity {

    public static final String LOG_TAG = DetailActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);

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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

        public static final String LOG_TAG = DetailFragment.class.getSimpleName();
         static final int COL_WEATHER_ID = 0;
         static final int COL_WEATHER_DATE = 1;
         static final int COL_WEATHER_DESC = 2;
         static final int COL_WEATHER_MAX_TEMP = 3;
         static final int COL_WEATHER_MIN_TEMP = 4;
        private static final String[] FORECAST_COLUMNS = new String[]{
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
        };
        final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
        final int DETAIL_LOADER = 0;
        String mForecastString;
        ShareActionProvider mShareActionProvider;
        View mRootView;



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

            String dateString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
            String weatherDescription = data.getString(COL_WEATHER_DESC);
            boolean isMetric = Utility.isMetric(getContext());
            String high = Utility.formatTemperature(getContext(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
            String low = Utility.formatTemperature(getContext(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

            mForecastString = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);
            TextView detailTextView = (TextView) mRootView.findViewById(R.id.textview);
           detailTextView.setText(mForecastString);

            // If createOptionsMenu has already happened, we update the share Intent.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareIntent());
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}

