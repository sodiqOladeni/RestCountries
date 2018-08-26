package com.sodiqoladeni.restcountries;

import android.app.LoaderManager.LoaderCallbacks;
import android.app.LoaderManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
                implements LoaderCallbacks<List<CountryListModel>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    /** URL for earthquake data from the Rest Countries dataset */
    private static final String REST_COUNTRIES_REQUEST_URL =
            "https://restcountries.eu/rest/v2/all";

    /**
     * Constant value for the countries loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int COUNTRIES_LOADER_ID = 1;

    /** Adapter for the list of countries */
    private ListItemAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = findViewById(R.id.list);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new ListItemAdapter(this, new ArrayList<CountryListModel>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            getLoaderManager().initLoader(COUNTRIES_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<CountryListModel>> onCreateLoader(int id, Bundle args) {

        return new CountriesLoader(this, REST_COUNTRIES_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<CountryListModel>> loader, List<CountryListModel> countryData) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No countryData found."
       mEmptyStateTextView.setText(R.string.no_country_data);

        mAdapter.clear();

        // If there is a valid list of {@link Country}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (countryData != null && !countryData.isEmpty()) {
            mAdapter.addAll(countryData);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<CountryListModel>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}
