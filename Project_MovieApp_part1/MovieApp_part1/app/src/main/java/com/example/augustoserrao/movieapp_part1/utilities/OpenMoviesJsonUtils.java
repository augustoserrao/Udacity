package com.example.augustoserrao.movieapp_part1.utilities;


import android.content.Context;
import android.util.Log;

import com.example.augustoserrao.movieapp_part1.data.MovieData;
import com.example.augustoserrao.movieapp_part1.data.MovieReview;
import com.example.augustoserrao.movieapp_part1.data.MovieTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;


// Help from Udacity!!!!
public class OpenMoviesJsonUtils {
    public static ArrayList<MovieData> getMoviesDataFromJson(Context context, String moviesJsonStr)
            throws JSONException {

        // Json strings to be parsed
        final String OWM_RESULTS = "results";
        final String OWM_VOTE_COUNT = "vote_count";
        final String OWM_ID = "id";
        final String OWM_VIDEO = "video";
        final String OWM_VOTE_AVERAGE = "vote_average";
        final String OWM_TITLE = "title";
        final String OWM_POPULARITY = "popularity";
        final String OWM_POSTER_PATH = "poster_path";
        final String OWM_ORIGINAL_LANGUAGE = "original_language";
        final String OWM_ORIGINAL_TITLE = "original_title";
        final String OWM_GENRE_IDS = "genre_ids";
        final String OWM_BACKDROP_PATH = "backdrop_path";
        final String OWM_ADULT = "adult";
        final String OWM_OVERVIEW = "overview";
        final String OWM_RELEASE_DATE = "release_date";

        final String OWM_MESSAGE_CODE = "cod";


        /* MovieData array to hold each movie's data */
        ArrayList<MovieData> moviesDataList = null;

        JSONObject moviesJson = new JSONObject(moviesJsonStr);

        /* Is there an error? */
        if (moviesJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = moviesJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray moviesArray = moviesJson.getJSONArray(OWM_RESULTS);

        moviesDataList = new ArrayList<MovieData>();
        for (int i = 0; i < moviesArray.length(); i++) {

            JSONObject currentMovieData = moviesArray.getJSONObject(i);

            MovieData parsedMoviesData = new MovieData();

            // Parses each data

            parsedMoviesData.vote_count        = currentMovieData.getLong(OWM_VOTE_COUNT);
            parsedMoviesData.id                = currentMovieData.getLong(OWM_ID);
            parsedMoviesData.video             = currentMovieData.getBoolean(OWM_VIDEO);
            parsedMoviesData.vote_average      = currentMovieData.getDouble(OWM_VOTE_AVERAGE);
            parsedMoviesData.title             = currentMovieData.getString(OWM_TITLE);
            parsedMoviesData.popularity        = currentMovieData.getDouble(OWM_POPULARITY);
            parsedMoviesData.poster_path       = currentMovieData.getString(OWM_POSTER_PATH);
            parsedMoviesData.original_language = currentMovieData.getString(OWM_ORIGINAL_LANGUAGE);
            parsedMoviesData.original_title    = currentMovieData.getString(OWM_ORIGINAL_TITLE);
            parsedMoviesData.backdrop_path     = currentMovieData.getString(OWM_BACKDROP_PATH);
            parsedMoviesData.adult             = currentMovieData.getBoolean(OWM_ADULT);
            parsedMoviesData.overview          = currentMovieData.getString(OWM_OVERVIEW);
            parsedMoviesData.release_date      = currentMovieData.getString(OWM_RELEASE_DATE);

            // As genre_ids is an array of INTs, it is necessary to get the Json array first
            JSONArray genreIdsArray = currentMovieData.getJSONArray(OWM_GENRE_IDS);
            parsedMoviesData.genre_ids = new int[genreIdsArray.length()];
            for (int j = 0; j < genreIdsArray.length(); j++) {
                parsedMoviesData.genre_ids[j] = genreIdsArray.getInt(j);
            }

            moviesDataList.add(parsedMoviesData);

            Log.v(TAG, "Current movie data: " +
                    OWM_VOTE_COUNT + ": " + parsedMoviesData.vote_count + " " +
                    OWM_ID + ": " + parsedMoviesData.id + " " +
                    OWM_VIDEO + ": " + parsedMoviesData.video + " " +
                    OWM_VOTE_AVERAGE + ": " + parsedMoviesData.vote_average + " " +
                    OWM_TITLE + ": " + parsedMoviesData.title + " " +
                    OWM_POPULARITY + ": " + parsedMoviesData.popularity + " " +
                    OWM_POSTER_PATH + ": " + parsedMoviesData.poster_path + " " +
                    OWM_ORIGINAL_LANGUAGE + ": " + parsedMoviesData.original_language + " " +
                    OWM_ORIGINAL_TITLE + ": " + parsedMoviesData.original_title + " " +
                    OWM_GENRE_IDS + ": " + parsedMoviesData.genre_ids + " " +
                    OWM_BACKDROP_PATH + ": " + parsedMoviesData.backdrop_path + " " +
                    OWM_ADULT + ": " + parsedMoviesData.adult + " " +
                    OWM_OVERVIEW + ": " + parsedMoviesData.overview + " " +
                    OWM_RELEASE_DATE + ": " + parsedMoviesData.release_date + " ");
        }

        return moviesDataList;
    }

    public static ArrayList<MovieTrailer> getMovieTrailersFromJson(Context context, String movieTrailersJsonStr)
        throws JSONException {

        // Json strings to be parsed
        final String OWM_RESULTS = "results";
        final String OWM_ID = "id";
        final String OWM_ISO_639_1 = "iso_639_1";
        final String OWM_ISO_3166_1 = "iso_3166_1";
        final String OWM_KEY = "key";
        final String OWM_NAME = "name";
        final String OWM_SITE = "site";
        final String OWM_SIZE = "size";
        final String OWM_TYPE = "type";

        final String OWM_MESSAGE_CODE = "cod";

    /* MovieData array to hold each movie's data */
        ArrayList<MovieTrailer> movieTrailerList = null;

        JSONObject movieTrailerJson = new JSONObject(movieTrailersJsonStr);

    /* Is there an error? */
        if (movieTrailerJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieTrailerJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                /* Location invalid */
                    return null;
                default:
                /* Server probably down */
                    return null;
            }
        }

        JSONArray movieTrailersArray = movieTrailerJson.getJSONArray(OWM_RESULTS);

        movieTrailerList = new ArrayList<MovieTrailer>();
        for (int i = 0; i < movieTrailersArray.length(); i++) {

            JSONObject currentMovieTrailer = movieTrailersArray.getJSONObject(i);

            MovieTrailer parsedMoviesTrailer = new MovieTrailer();

            // Parses each data

            parsedMoviesTrailer.id         = currentMovieTrailer.getString(OWM_ID);
            parsedMoviesTrailer.iso_639_1  = currentMovieTrailer.getString(OWM_ISO_639_1);
            parsedMoviesTrailer.iso_3166_1 = currentMovieTrailer.getString(OWM_ISO_3166_1);
            parsedMoviesTrailer.key        = currentMovieTrailer.getString(OWM_KEY);
            parsedMoviesTrailer.name       = currentMovieTrailer.getString(OWM_NAME);
            parsedMoviesTrailer.site       = currentMovieTrailer.getString(OWM_SITE);
            parsedMoviesTrailer.size       = currentMovieTrailer.getInt(OWM_SIZE);
            parsedMoviesTrailer.type       = currentMovieTrailer.getString(OWM_TYPE);

            movieTrailerList.add(parsedMoviesTrailer);

            Log.v(TAG, "Current movie trailer: " +
                    OWM_ID         + ": " + parsedMoviesTrailer.id         + " " +
                    OWM_ISO_639_1  + ": " + parsedMoviesTrailer.iso_639_1  + " " +
                    OWM_ISO_3166_1 + ": " + parsedMoviesTrailer.iso_3166_1 + " " +
                    OWM_KEY        + ": " + parsedMoviesTrailer.key        + " " +
                    OWM_NAME       + ": " + parsedMoviesTrailer.name       + " " +
                    OWM_SITE       + ": " + parsedMoviesTrailer.site       + " " +
                    OWM_SIZE       + ": " + parsedMoviesTrailer.size       + " " +
                    OWM_TYPE       + ": " + parsedMoviesTrailer.type       + " ");
        }

        return movieTrailerList;
    }

    public static ArrayList<MovieReview> getMovieReviewsFromJson(Context context, String movieReviewsJsonStr)
            throws JSONException {

        // Json strings to be parsed
        final String OWM_RESULTS = "results";
        final String OWM_ID = "id";
        final String OWM_AUTHOR = "author";
        final String OWM_CONTENT = "content";
        final String OWM_URL = "url";

        final String OWM_MESSAGE_CODE = "cod";


    /* MovieData array to hold each movie's data */
        ArrayList<MovieReview> movieReviewsList = null;

        JSONObject movieReviewJson = new JSONObject(movieReviewsJsonStr);

    /* Is there an error? */
        if (movieReviewJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieReviewJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                /* Location invalid */
                    return null;
                default:
                /* Server probably down */
                    return null;
            }
        }

        JSONArray movieReviewsArray = movieReviewJson.getJSONArray(OWM_RESULTS);

        movieReviewsList = new ArrayList<MovieReview>();
        for (int i = 0; i < movieReviewsArray.length(); i++) {

            JSONObject currentMovieReview = movieReviewsArray.getJSONObject(i);

            MovieReview parsedMovieReview = new MovieReview();

            // Parses each data
            parsedMovieReview.id      = currentMovieReview.getString(OWM_ID);
            parsedMovieReview.author  = currentMovieReview.getString(OWM_AUTHOR);
            parsedMovieReview.content = currentMovieReview.getString(OWM_CONTENT);
            parsedMovieReview.url     = currentMovieReview.getString(OWM_URL);

            movieReviewsList.add(parsedMovieReview);

            Log.v(TAG, "Current movie review: " +
                    OWM_ID      + ": " + parsedMovieReview.id      + " " +
                    OWM_AUTHOR  + ": " + parsedMovieReview.author  + " " +
                    OWM_CONTENT + ": " + parsedMovieReview.content + " " +
                    OWM_URL     + ": " + parsedMovieReview.url     + " ");
        }

        return movieReviewsList;
    }
}
