package com.example.augustoserrao.movieapp_part1.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static android.content.ContentValues.TAG;


public class NetworkUtils {

    private final static String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie";
    private final static String API_KEY_PARAM = "api_key";
    private final static String API_KEY_FILE_NAME = "API_Key.txt";


    public static URL buildMovieUrl(String finalUrlString, Context context) {

        String apiKey = readApiKeyFromFile(context);

        Uri builtUri = Uri.parse(MOVIE_BASE_URL + finalUrlString).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     * @author Udacity
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private static String readApiKeyFromFile(Context context) {
        // Read API Key
        String apiKey = null;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(API_KEY_FILE_NAME)));
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
}
