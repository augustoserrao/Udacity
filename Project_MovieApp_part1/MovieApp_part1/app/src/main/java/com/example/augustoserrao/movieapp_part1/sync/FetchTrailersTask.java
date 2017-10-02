package com.example.augustoserrao.movieapp_part1.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.example.augustoserrao.movieapp_part1.data.MovieTrailer;
import com.example.augustoserrao.movieapp_part1.utilities.NetworkUtils;
import com.example.augustoserrao.movieapp_part1.utilities.OpenMoviesJsonUtils;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by augusto.serrao on 26/09/2017.
 */

public class FetchTrailersTask extends AsyncTask<String, Void, ArrayList<MovieTrailer>> {

    private Context mContext;
    private OnFetchTrailerTaskCompleted listener;

    private final static String TRAILERS_URL_STRING = "/videos";

    public interface OnFetchTrailerTaskCompleted{
        void OnFetchTrailerTaskCompleted(ArrayList<MovieTrailer> movieTrailer);
    }

    public FetchTrailersTask(Context context, OnFetchTrailerTaskCompleted handler) {
        mContext = context;
        listener = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<MovieTrailer> doInBackground(String... id) {
        // Check url is valid
        if (id[0] == null) {
            return null;
        }

        // Author: Udacity
        try {
            URL urlTrailers = NetworkUtils.buildMovieUrl("/" + id[0] + TRAILERS_URL_STRING, mContext);
            String jsonMovieTrailersResponse = NetworkUtils.getResponseFromHttpUrl(urlTrailers);

            return OpenMoviesJsonUtils.getMovieTrailersFromJson(mContext, jsonMovieTrailersResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<MovieTrailer> movieTrailer) {
        listener.OnFetchTrailerTaskCompleted(movieTrailer);
    }
}
