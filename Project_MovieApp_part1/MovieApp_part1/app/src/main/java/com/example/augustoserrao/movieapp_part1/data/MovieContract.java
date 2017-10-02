package com.example.augustoserrao.movieapp_part1.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by augusto.serrao on 26/09/2017.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.augustoserrao.movieapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID          = "movie_id";
        public static final String COLUMN_TITLE             = "title";
        public static final String COLUMN_SYNOPSIS          = "synopsis";
        public static final String COLUMN_USER_RATING       = "user_rating";
        public static final String COLUMN_RELEASE_DATE      = "release_date";
        public static final String COLUMN_POSTER            = "poster";
        public static final String COLUMN_POSTER_BYTE_ARRAY = "poster_byte_array";

        public static Uri buildMovieUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }
}
