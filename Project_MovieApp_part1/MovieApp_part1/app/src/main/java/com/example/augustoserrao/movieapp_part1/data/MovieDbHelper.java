package com.example.augustoserrao.movieapp_part1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by augusto.serrao on 26/09/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "movies.db";

    public static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + MovieContract.WeatherEntry.TABLE_NAME + " (" +

                        MovieContract.WeatherEntry._ID                      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.WeatherEntry.COLUMN_MOVIE_ID          + " INTEGER NOT NULL, "                  +
                        MovieContract.WeatherEntry.COLUMN_TITLE             + " TEXT NOT NULL,"                      +
                        MovieContract.WeatherEntry.COLUMN_SYNOPSIS          + " TEXT NOT NULL,"                      +
                        MovieContract.WeatherEntry.COLUMN_USER_RATING       + " REAL NOT NULL, "                     +
                        MovieContract.WeatherEntry.COLUMN_RELEASE_DATE      + " TEXT NOT NULL, "                     +
                        MovieContract.WeatherEntry.COLUMN_POSTER            + " TEXT NOT NULL, "                     +
                        MovieContract.WeatherEntry.COLUMN_POSTER_BYTE_ARRAY + " TEXT NOT NULL, "                     +

                        " UNIQUE (" + MovieContract.WeatherEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
