package com.example.augustoserrao.movieapp_part1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.augustoserrao.movieapp_part1.data.MovieContract;
import com.example.augustoserrao.movieapp_part1.data.MovieData;
import com.example.augustoserrao.movieapp_part1.utilities.NetworkUtils;
import com.example.augustoserrao.movieapp_part1.utilities.OpenMoviesJsonUtils;
import com.example.augustoserrao.movieapp_part1.utilities.Utils;

import java.net.URL;
import java.util.ArrayList;

import static com.example.augustoserrao.movieapp_part1.data.MoviesIntents.EXTRA_MOVIE;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private final static String POPULAR_URL_STRING = "/popular";
    private final static String TOPRATED_URL_STRING = "/top_rated";
    private final static String FAVORITES_URL_STRING = "/favorites";

    private static final String RADIO_BUTTON_ID_EXTRA = "radio_button_id";
    private static final String LAYOUT_EXTRA = "layout_id";
    private static final String POSITION_EXTRA = "position_id";
    private static final String SEARCH_QUERY_URL_EXTRA = "query";

    private static final int MOVIE_DATA_SEARCH_LOADER = 22;
    private static final int MOVIE_DATA_CONTENT_PROVIDER_LOADER = 32;

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessageTextView;
    private ProgressBar mProgressBar;
    private RadioButton mPopularRadioButton;
    private RadioButton mTopRatedRadioButton;
    private RadioButton mFavoritesRadioButton;
    private int currentRadioButtonId;

    GridLayoutManager gridLayoutManager;
    private Parcelable mRecyclerViewParcelable;
    private int mScrollPosition = -1;

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mPopularRadioButton = (RadioButton) findViewById(R.id.popular_radio_button);
        mTopRatedRadioButton = (RadioButton) findViewById(R.id.toprated_radio_button);
        mFavoritesRadioButton = (RadioButton) findViewById(R.id.favorites_radio_button);

        String URL_STRING;

        if (savedInstanceState != null) {
            int radioButtonActivated = savedInstanceState.getInt(RADIO_BUTTON_ID_EXTRA);

            switch(radioButtonActivated) {
                case R.id.popular_radio_button:
                    URL_STRING = POPULAR_URL_STRING;
                    mPopularRadioButton.setChecked(true);
                    break;
                case R.id.toprated_radio_button:
                    URL_STRING = TOPRATED_URL_STRING;
                    mTopRatedRadioButton.setChecked(true);
                    break;
                case R.id.favorites_radio_button:
                    URL_STRING = FAVORITES_URL_STRING;
                    mFavoritesRadioButton.setChecked(true);
                    break;
                default:
                    URL_STRING = POPULAR_URL_STRING;
                    mPopularRadioButton.setChecked(true);
                    break;
            }

            mRecyclerViewParcelable = savedInstanceState.getParcelable(LAYOUT_EXTRA);
            mScrollPosition = savedInstanceState.getInt(POSITION_EXTRA);

            currentRadioButtonId = radioButtonActivated;
        } else {
            URL_STRING = POPULAR_URL_STRING;
            mPopularRadioButton.setChecked(true);
            currentRadioButtonId = R.id.popular_radio_button;
        }

        gridLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.grid_size));

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        loadMoviesData(URL_STRING);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String URL_STRING;

        switch(currentRadioButtonId) {
            case R.id.popular_radio_button:
                URL_STRING = POPULAR_URL_STRING;
                break;
            case R.id.toprated_radio_button:
                URL_STRING = TOPRATED_URL_STRING;
                break;
            case R.id.favorites_radio_button:
                URL_STRING = FAVORITES_URL_STRING;
                break;
            default:
                URL_STRING = POPULAR_URL_STRING;
                break;
        }
        loadMoviesData(URL_STRING);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int scrollPosition = ((GridLayoutManager)
                mRecyclerView.getLayoutManager())
                .findFirstCompletelyVisibleItemPosition();
        mRecyclerViewParcelable = gridLayoutManager.onSaveInstanceState();
        outState.putParcelable(LAYOUT_EXTRA, mRecyclerViewParcelable);
        outState.putInt(POSITION_EXTRA, scrollPosition);

        Log.v("Main activity", "Saving instance = " + currentRadioButtonId);
        outState.putInt(RADIO_BUTTON_ID_EXTRA, currentRadioButtonId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void loadMoviesData(String finalUrlString) {
        // Check string lenght
        if (finalUrlString.length() == 0) {
            return;
        }

        showProgressBar();

        LoaderManager loaderManager = getSupportLoaderManager();

        // If url string is favorites, it is going to get information from Content Provider
        if (finalUrlString.equals(FAVORITES_URL_STRING)) {
            Loader<String> movieDataSearchLoader = loaderManager.getLoader(MOVIE_DATA_CONTENT_PROVIDER_LOADER);

            if (movieDataSearchLoader == null) {
                loaderManager.initLoader(MOVIE_DATA_CONTENT_PROVIDER_LOADER, null, movieDataContentProviderLoader).forceLoad();
            } else {
                loaderManager.restartLoader(MOVIE_DATA_CONTENT_PROVIDER_LOADER, null, movieDataContentProviderLoader).forceLoad();
            }
        } else if (Utils.isNetworkAvailable(this)) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString(SEARCH_QUERY_URL_EXTRA, finalUrlString);

            Loader<String> movieDataSearchLoader = loaderManager.getLoader(MOVIE_DATA_SEARCH_LOADER);

            if (movieDataSearchLoader == null) {
                loaderManager.initLoader(MOVIE_DATA_SEARCH_LOADER, queryBundle, movieDataNetworkLoader).forceLoad();
            } else {
                loaderManager.restartLoader(MOVIE_DATA_SEARCH_LOADER, queryBundle, movieDataNetworkLoader).forceLoad();
            }
        } else {
            showErrorMessage();
        }
    }

    public void onRadioButtonClicked(View view) {
        // Check if button was really clicked
        if (((RadioButton) view).isChecked()) {
            switch(view.getId()) {
                case R.id.popular_radio_button:
                    loadMoviesData(POPULAR_URL_STRING);
                    break;
                case R.id.toprated_radio_button:
                    loadMoviesData(TOPRATED_URL_STRING);
                    break;
                case R.id.favorites_radio_button:
                    loadMoviesData(FAVORITES_URL_STRING);
                    break;
            }

            currentRadioButtonId = view.getId();
        }
    }

    public void showProgressBar() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void showRecyclerView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.scrollToPosition(mScrollPosition);
    }

    public void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(MovieData movieData) {
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToStartMovieDetailActivity = new Intent(context, destinationClass);
        intentToStartMovieDetailActivity.putExtra(EXTRA_MOVIE, movieData);
        startActivity(intentToStartMovieDetailActivity);
    }



    /**************************   LOADERS ************************/

    private LoaderManager.LoaderCallbacks<ArrayList<MovieData>> movieDataNetworkLoader =
            new LoaderManager.LoaderCallbacks<ArrayList<MovieData>>() {

        @Override
        public Loader<ArrayList<MovieData>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<ArrayList<MovieData>>(mContext) {
                @Override
                protected void onStartLoading() {
                    if (args == null) {
                        return;
                    }
                }

                @Override
                public ArrayList<MovieData> loadInBackground() {
                    String finalUrlString = args.getString(SEARCH_QUERY_URL_EXTRA);
                    URL url = NetworkUtils.buildMovieUrl(finalUrlString, mContext);

                    if (url == null) {
                        return null;
                    }

                    // Author: Udacity
                    try {
                        String jsonMoviesResponse = NetworkUtils
                                .getResponseFromHttpUrl(url);

                        ArrayList<MovieData> JsonMoviesData = OpenMoviesJsonUtils
                                .getMoviesDataFromJson(mContext, jsonMoviesResponse);

                        return JsonMoviesData;

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<MovieData>> loader, ArrayList<MovieData> data) {
            if (data != null) {
                showRecyclerView();
                mMovieAdapter.setMovieDataList(data);
                getLoaderManager().destroyLoader(loader.getId());
            } else {
                showErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<MovieData>> loader) {
            mMovieAdapter.setMovieDataList(null);
        }
    };


    private LoaderManager.LoaderCallbacks<Cursor> movieDataContentProviderLoader =
            new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

            return new AsyncTaskLoader<Cursor>(mContext) {
                @Override
                public Cursor loadInBackground() {
                    try {
                        return getContentResolver().query(MovieContract.WeatherEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                MovieContract.WeatherEntry._ID);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (cursor != null) {
                showRecyclerView();
                mMovieAdapter.setMovieDataList(Utils.convertCursorIntoMovieDataArray(cursor));
                getLoaderManager().destroyLoader(loader.getId());
            } else {
                showErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mMovieAdapter.setMovieDataList(null);
        }

    };
}
