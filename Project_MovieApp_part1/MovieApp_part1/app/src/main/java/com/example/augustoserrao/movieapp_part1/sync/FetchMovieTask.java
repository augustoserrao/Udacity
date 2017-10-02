package com.example.augustoserrao.movieapp_part1.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.example.augustoserrao.movieapp_part1.data.MovieData;
import com.example.augustoserrao.movieapp_part1.utilities.NetworkUtils;
import com.example.augustoserrao.movieapp_part1.utilities.OpenMoviesJsonUtils;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by augusto.serrao on 26/09/2017.
 */

public class FetchMovieTask extends AsyncTask<URL, Void, ArrayList<MovieData>> {

    private Context mContext;
    private OnFetchMovieTaskCompleted listener;

    public interface OnFetchMovieTaskCompleted{
        void onFetchMovieTaskCompleted(ArrayList<MovieData> movieDatas);
    }

    public FetchMovieTask(Context context, OnFetchMovieTaskCompleted handler) {
        mContext = context;
        listener = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

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
                    .getMoviesDataFromJson(mContext, jsonMoviesResponse);

            return JsonMoviesData;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<MovieData> movieDatas) {
        listener.onFetchMovieTaskCompleted(movieDatas);
    }
}
