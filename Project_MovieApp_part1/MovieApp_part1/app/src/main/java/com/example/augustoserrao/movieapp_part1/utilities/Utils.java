package com.example.augustoserrao.movieapp_part1.utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.augustoserrao.movieapp_part1.data.MovieContract;
import com.example.augustoserrao.movieapp_part1.data.MovieData;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by augusto.serrao on 14/09/2017.
 */

public class Utils {
    public final static String BASE_PICTURE_URL = "http://image.tmdb.org/t/p/w185/";

    public static ArrayList<MovieData> convertCursorIntoMovieDataArray(Cursor cursor) {

        ArrayList<MovieData> movieDataList = new ArrayList<MovieData>();

        while (cursor.moveToNext()) {
            MovieData movieData = new MovieData();

            int idIndex          = cursor.getColumnIndex(MovieContract.WeatherEntry.COLUMN_MOVIE_ID);
            int posterPathIndex  = cursor.getColumnIndex(MovieContract.WeatherEntry.COLUMN_POSTER);
            int releaseDateIndex = cursor.getColumnIndex(MovieContract.WeatherEntry.COLUMN_RELEASE_DATE);
            int synopsisIndex    = cursor.getColumnIndex(MovieContract.WeatherEntry.COLUMN_SYNOPSIS);
            int titleIndex       = cursor.getColumnIndex(MovieContract.WeatherEntry.COLUMN_TITLE);
            int userRatingIndex  = cursor.getColumnIndex(MovieContract.WeatherEntry.COLUMN_USER_RATING);
            int posterArrayIndex = cursor.getColumnIndex(MovieContract.WeatherEntry.COLUMN_POSTER_BYTE_ARRAY);

            movieData.id              = cursor.getLong(idIndex);
            movieData.poster_path     = cursor.getString(posterPathIndex);
            movieData.release_date    = cursor.getString(releaseDateIndex);
            movieData.overview        = cursor.getString(synopsisIndex);
            movieData.title           = cursor.getString(titleIndex);
            movieData.vote_average    = cursor.getDouble(userRatingIndex);
            movieData.posterByteArray = cursor.getBlob(posterArrayIndex);

            Log.d(TAG, "Current movie data: " + " ID = " + movieData.id +
                    " Poster path = " + movieData.poster_path +
                    " Release date = " + movieData.release_date +
                    " Synopsis = " + movieData.overview +
                    " Title = " + movieData.title +
                    " Votes = " + movieData.vote_average);

            movieDataList.add(movieData);
        }
        return movieDataList;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
