package com.example.augustoserrao.movieapp_part1.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.example.augustoserrao.movieapp_part1.data.MovieReview;
import com.example.augustoserrao.movieapp_part1.utilities.NetworkUtils;
import com.example.augustoserrao.movieapp_part1.utilities.OpenMoviesJsonUtils;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by augusto.serrao on 26/09/2017.
 */

public class FetchReviewsTask extends AsyncTask<String, Void, ArrayList<MovieReview>> {
    private Context mContext;
    private OnFetchReviewTaskCompleted listener;

    private final static String REVIEWS_URL_STRING = "/reviews";

    public interface OnFetchReviewTaskCompleted{
        void OnFetchReviewTaskCompleted(ArrayList<MovieReview> movieReview);
    }

    public FetchReviewsTask(Context context, OnFetchReviewTaskCompleted handler) {
        mContext = context;
        listener = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<MovieReview> doInBackground(String... id) {
        // Check url is valid
        if (id[0] == null) {
            return null;
        }

        // Author: Udacity
        try {
            URL urlReviews = NetworkUtils.buildMovieUrl("/" + id[0] + REVIEWS_URL_STRING, mContext);
            String jsonMovieReviewsResponse = NetworkUtils.getResponseFromHttpUrl(urlReviews);

            return OpenMoviesJsonUtils.getMovieReviewsFromJson(mContext, jsonMovieReviewsResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<MovieReview> movieReview) {
        listener.OnFetchReviewTaskCompleted(movieReview);
    }
}
