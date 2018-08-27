package com.sodiqoladeni.restcountries;

import android.app.LoaderManager.LoaderCallbacks;
import android.app.LoaderManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
                implements LoaderCallbacks<List<CountryListModel>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    /** URL for countries data from the Rest Countries dataset */
    private static final String REST_COUNTRIES_REQUEST_URL =
            "https://restcountries.eu/rest/v2/all";

    /**
     * Constant value for the countries loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int COUNTRIES_LOADER_ID = 1;

    /** Adapter for the list of countries */
    private ListCountryAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    /**RecyclerView to repeat each country model  */
    private RecyclerView countriesListView;

    /** Loading indicator keep the user waiting before fetching the data */
    private View loadingIndicator;

    private ColorDrawable background = new ColorDrawable();
    private int backgroundColor = Color.parseColor("#800080");
    private Paint clearPaint;
    private Drawable deleteIcon;
    private int intrinsicWidth;
    private int intrinsicHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      //  clearPaint = new Paint().ap { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_icon);
        intrinsicWidth = deleteIcon.getIntrinsicWidth();
        intrinsicHeight = deleteIcon.getIntrinsicHeight();

        // Find a reference to the {@link ListView} in the layout
         countriesListView = findViewById(R.id.list);

        countriesListView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        countriesListView.setItemAnimator(new DefaultItemAnimator());

        //Specify Layout manager for the recyclerView
        countriesListView.setLayoutManager(new LinearLayoutManager(this));

        mEmptyStateTextView = findViewById(R.id.empty_view);

        loadingIndicator = findViewById(R.id.loading_indicator);

        // Create a new adapter that takes context as input, and new ArrayList
        mAdapter = new ListCountryAdapter(this, new ArrayList<CountryListModel>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        countriesListView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder v, int i) {
                mAdapter.removeItem(v.getAdapterPosition());
            }

            @Override
            public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                                                    int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;
                int itemHeight = itemView.getBottom() - itemView.getTop();

                background.setColor(backgroundColor);
                background.setBounds( itemView.getRight() + ((int)dX), itemView.getTop(),
                        itemView.getRight(), itemView.getBottom());
                background.draw(canvas);

                // Calculate position of delete icon
                int iconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int iconMargin = (itemHeight - intrinsicHeight) / 2;
                int iconLeft = itemView.getRight() - iconMargin - intrinsicWidth;
                int iconRight = itemView.getRight() - iconMargin;
                int iconBottom = iconTop + intrinsicHeight;

                // Draw the delete icon
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                deleteIcon.draw(canvas);

                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(countriesListView);

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
        loadingIndicator.setVisibility(View.GONE);

        mAdapter.clear();

        // If there is a valid list of {@link Country}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (countryData != null && !countryData.isEmpty()) {
            mAdapter.addAll(countryData);
        }else {

            //Set EmptyView TextView to be Visible
            mEmptyStateTextView.setVisibility(View.VISIBLE);

            //Set RecyclerView Visibility to GONE since there is no data to recycle
            countriesListView.setVisibility(View.GONE);

            //Set the progress loading bar to GONE
            loadingIndicator.setVisibility(View.GONE);

            // Set empty state text to display "No countryData found."
            mEmptyStateTextView.setText(R.string.no_country_data);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<CountryListModel>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}
