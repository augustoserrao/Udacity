package com.example.augustoserrao.movieapp_part1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by augusto.serrao on 26/09/2017.
 */

// HELP FROM UDACITY!
public class MovieProvider extends ContentProvider {

    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", CODE_MOVIE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_MOVIE_WITH_ID:

                String uriId= uri.getLastPathSegment();
                String[] selectionArguments = new String[]{uriId};

                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.WeatherEntry.TABLE_NAME,
                        projection,
                        MovieContract.WeatherEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("getType not implemented.");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {

        Uri retUri;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                long id = mOpenHelper.getReadableDatabase().insert(
                        MovieContract.WeatherEntry.TABLE_NAME,
                        null,
                        contentValues);

                if (id > 0) {
                    retUri = ContentUris.withAppendedId(MovieContract.WeatherEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row!");
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return retUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int rowsDeleted;

        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.WeatherEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case CODE_MOVIE_WITH_ID:

                String uriId= uri.getLastPathSegment();
                String[] selectionArguments = new String[]{uriId};

                rowsDeleted = mOpenHelper.getReadableDatabase().delete(
                        MovieContract.WeatherEntry.TABLE_NAME,
                        MovieContract.WeatherEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE_WITH_ID:

                String uriId= uri.getLastPathSegment();
                String[] selectionArguments = new String[]{uriId};

                rowsUpdated = mOpenHelper.getReadableDatabase().update(
                        MovieContract.WeatherEntry.TABLE_NAME,
                        contentValues,
                        MovieContract.WeatherEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
