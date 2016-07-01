package com.example.android.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    String FORECASTFRAGMENT_TAG = "F_TAG";
    String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();
        }

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        mLocation = Utility.getPreferredLocation(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
       if(!mLocation.equals(Utility.getPreferredLocation(this))){
           ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
           ff.onLocationChanged();
           mLocation = Utility.getPreferredLocation(this);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_map){
           showPreferredLocationOnMap();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPreferredLocationOnMap(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String location = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_location_key),
                        getString(R.string.pref_location_default));
        Uri locationUri = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q",location )
                .build();
        intent.setData(locationUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        else {
            Toast.makeText(MainActivity.this, R.string.no_map_app_found, Toast.LENGTH_SHORT).show();
        }
    }

}
