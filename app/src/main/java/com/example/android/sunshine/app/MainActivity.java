package com.example.android.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    //private final String FORECASTFRAGMENT_TAG = "FFTAG";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;
    private String mLocation;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation(this);

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        ForecastFragment forecastFragment = ((ForecastFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(!mTwoPane);
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//        String location = sharedPrefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        String location = Utility.getPreferredLocation(this);

        // Using the URI scheme for showing a location found on a map.  This super-handy
        // intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
        }
    }


    @Override
    protected void onStart() {
        Log.v(LOG_TAG, "in onStart");
        super.onStart();
        // The activity is about to become visible.
    }


    @Override
    protected void onResume() {
        Log.v(LOG_TAG, "in onResume");
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        // update the location in our second pane using the fragment manager
        if (location != null && !location.equals(mLocation)) {
            //ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if (null != ff) {
                ff.onLocationChanged();
            }
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != df) {
                df.onLocationChanged(location);
            }
            mLocation = location;
        }
    }

    @Override
    protected void onPause() {
        Log.v(LOG_TAG, "in onPause");
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }


    @Override
    protected void onStop() {
        Log.v(LOG_TAG, "in onStop");
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }

    @Override
    protected void onDestroy() {
        Log.v(LOG_TAG, "in onDestroy");
        super.onDestroy();
        // The activity is about to be destroyed.
    }


    /**
     * DetailFragmentCallback for when an item has been selected.
     *
     * @param contentUri
     */
    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }

    }
}

