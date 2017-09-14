package com.example.augustoserrao.movieapp_part1;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.augustoserrao.movieapp_part1.data.MovieData;
import com.example.augustoserrao.movieapp_part1.data.MoviesIntents;
import com.example.augustoserrao.movieapp_part1.utilities.NetworkUtils;
import com.example.augustoserrao.movieapp_part1.utilities.OpenMoviesJsonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private final static String POPULAR_URL_STRING = "/popular";
    private final static String TOPRATED_URL_STRING = "/top_rated";

    private final static String API_KEY_FILE_NAME = "API_Key.txt";

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessageTextView;
    private ProgressBar mProgressBar;
    private RadioButton mPopularRadioBuutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mPopularRadioBuutton = (RadioButton) findViewById(R.id.popular_radio_button);

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        // Get popular movies when launching
        mPopularRadioBuutton.setChecked(true);
        loadMoviesData(POPULAR_URL_STRING);
    }

    public void loadMoviesData(String finalUrlString) {
        // Check string lenght
        if (finalUrlString.length() == 0) {
            return;
        }

        showProgressBar();

        String sApiKey = readApiKeyFromFile();
        URL url = NetworkUtils.buildMovieUrl(finalUrlString, sApiKey);

        // Creates new thread to execute network task
        new FetchMovieTask().execute(url);
    }

    private String readApiKeyFromFile() {
        // Read API Key
        String apiKey = null;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open(API_KEY_FILE_NAME)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            apiKey = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("FILE", "API_Key: " + apiKey);

        return apiKey;
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
            }
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
        intentToStartMovieDetailActivity.putExtra(MoviesIntents.EXTRA_MOVIE_TITLE, movieData.title);
        intentToStartMovieDetailActivity.putExtra(MoviesIntents.EXTRA_MOVIE_RELEASE_DATE, movieData.release_date);
        intentToStartMovieDetailActivity.putExtra(MoviesIntents.EXTRA_MOVIE_POSTER, movieData.poster_path);
        intentToStartMovieDetailActivity.putExtra(MoviesIntents.EXTRA_MOVIE_VOTE_AVERAGE, movieData.vote_average);
        intentToStartMovieDetailActivity.putExtra(MoviesIntents.EXTRA_MOVIE_SYNOPSIS, movieData.overview);
        startActivity(intentToStartMovieDetailActivity);
    }

    public class FetchMovieTask extends AsyncTask<URL, Void, ArrayList<MovieData>> {

        @Override
        protected ArrayList<MovieData> doInBackground(URL... url) {
            // Check url is valid
            if (url[0] == null) {
                return null;
            }

            // Author: Udacity
            try {
                String jsonMoviesResponse = NetworkUtils
                        .getResponseFromHttpUrl(url[0]);

                ArrayList<MovieData> JsonMoviesData = OpenMoviesJsonUtils
                        .getMoviesDataFromJson(MainActivity.this, jsonMoviesResponse);

                return JsonMoviesData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MovieData> movieDatas) {
            if (movieDatas != null) {
                showRecyclerView();
                mMovieAdapter.setMovieDataList(movieDatas);
            } else {
                showErrorMessage();
            }
        }
    }
}
