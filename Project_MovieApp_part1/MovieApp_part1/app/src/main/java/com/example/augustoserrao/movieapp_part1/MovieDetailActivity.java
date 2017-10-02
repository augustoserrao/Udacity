package com.example.augustoserrao.movieapp_part1;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.augustoserrao.movieapp_part1.data.MovieContract;
import com.example.augustoserrao.movieapp_part1.data.MovieData;
import com.example.augustoserrao.movieapp_part1.data.MovieReview;
import com.example.augustoserrao.movieapp_part1.data.MovieTrailer;
import com.example.augustoserrao.movieapp_part1.utilities.NetworkUtils;
import com.example.augustoserrao.movieapp_part1.utilities.OpenMoviesJsonUtils;
import com.example.augustoserrao.movieapp_part1.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;

import static com.example.augustoserrao.movieapp_part1.data.MovieContract.WeatherEntry.buildMovieUriWithId;
import static com.example.augustoserrao.movieapp_part1.data.MoviesIntents.EXTRA_MOVIE;

public class MovieDetailActivity extends AppCompatActivity implements TrailersAdapter.TrailersAdapterOnClickHandler {

    private String YOUTUBE_BASE_URI = "http://www.youtube.com/watch?v=";

    private ImageView moviePosterImageView;
    private TextView  movieTitleTextView;
    private TextView  movieReleaseDateTextView;
    private TextView  movieVoteAverageTextView;
    private TextView  movieSynopsisTextView;

    private RecyclerView mTrailerRecyclerView;
    private TrailersAdapter mTrailerAdapter;

    private RecyclerView mReviewRecyclerView;
    private ReviewsAdapter mReviewAdapter;

    private MovieData mMovieData;

    private boolean mIsFavorite;

    private Context mContext;
    private Button mFavoriteButton;

    private Toast mToast;

    /********** Definition for Loaders ***************/
    private static final String SEARCH_REVIEWS_URL_EXTRA = "reviews";
    private static final int MOVIE_REVIEWS_SEARCH_LOADER = 25;
    private final static String REVIEWS_URL_STRING = "/reviews";

    private static final String SEARCH_TRAILERS_URL_EXTRA = "trailers";
    private static final int MOVIE_TRAILERS_SEARCH_LOADER = 26;
    private final static String TRAILERS_URL_STRING = "/videos";


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mContext = this;

        mTrailerRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_trailers);
        mReviewRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviews);

        moviePosterImageView = (ImageView) findViewById(R.id.movie_detail_image);
        movieTitleTextView = (TextView) findViewById(R.id.movie_detail_title);
        movieReleaseDateTextView = (TextView) findViewById(R.id.movie_detail_release_date);
        movieVoteAverageTextView = (TextView) findViewById(R.id.movie_detail_vote_average);
        movieSynopsisTextView = (TextView) findViewById(R.id.movie_detail_synopsis);

        mFavoriteButton = (Button) findViewById(R.id.favoriteButton);

        // Get intent and check its parameters
        Intent startActivityIntent = getIntent();
        if (startActivityIntent != null) {
            if (startActivityIntent.hasExtra(EXTRA_MOVIE)) {
                mMovieData = startActivityIntent.getParcelableExtra(EXTRA_MOVIE);

                Picasso.with(this).load(Utils.BASE_PICTURE_URL + mMovieData.poster_path).into(moviePosterImageView);

                movieTitleTextView.setText(mMovieData.title);

                String sReleaseDate = mMovieData.release_date;
                String[] stringDateArray = sReleaseDate.split("-");
                movieReleaseDateTextView.setText(stringDateArray[0]);

                double dVoteAverage = mMovieData.vote_average;
                movieVoteAverageTextView.setText(dVoteAverage + "/10");

                movieSynopsisTextView.setText(Html.fromHtml("<b>" + getString(R.string.synopsis) + ": " + "</b>" + mMovieData.overview));

                String movieId = String.valueOf(mMovieData.id);

                LinearLayoutManager trailerLayoutManager =
                        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                mTrailerRecyclerView.setLayoutManager(trailerLayoutManager);
                mTrailerRecyclerView.setHasFixedSize(true);
                mTrailerAdapter = new TrailersAdapter(this);
                mTrailerRecyclerView.setAdapter(mTrailerAdapter);

                LinearLayoutManager reviewLayoutManager =
                        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                mReviewRecyclerView.setLayoutManager(reviewLayoutManager);
                mReviewRecyclerView.setHasFixedSize(true);
                mReviewAdapter = new ReviewsAdapter();
                mReviewRecyclerView.setAdapter(mReviewAdapter);

                Bundle reviewBundle = new Bundle();
                reviewBundle.putString(SEARCH_REVIEWS_URL_EXTRA, movieId);
                getSupportLoaderManager().initLoader(MOVIE_REVIEWS_SEARCH_LOADER, reviewBundle, movieReviewLoader);

                Bundle trailerBundle = new Bundle();
                trailerBundle.putString(SEARCH_TRAILERS_URL_EXTRA, movieId);
                getSupportLoaderManager().initLoader(MOVIE_TRAILERS_SEARCH_LOADER, reviewBundle, movieTrailerLoader);

                mIsFavorite = checkMovieDataIsFavorite();
                if (mIsFavorite) {
                    mFavoriteButton.setBackground(getDrawable(R.drawable.gold_star));
                } else {
                    mFavoriteButton.setBackground(getDrawable(R.drawable.empty_star));
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onClickAddRemoveFavorite(View view) {

        if (mIsFavorite) {
            int deleteCount = deleteMovieDataFromContentProvider();
            if(deleteCount > 0) {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(getBaseContext(), getString(R.string.remove_favorite_toast_message), Toast.LENGTH_LONG);
                mToast.show();
                mFavoriteButton.setBackground(getDrawable(R.drawable.empty_star));
                mIsFavorite = false;
            }
        } else {
            Uri uri = insertMovieDataIntoContentProvider();
            if(uri != null) {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(getBaseContext(), getString(R.string.add_favorite_toast_message), Toast.LENGTH_LONG);
                mToast.show();
                mFavoriteButton.setBackground(getDrawable(R.drawable.gold_star));
                mIsFavorite = true;
            }
        }
    }

    private Uri insertMovieDataIntoContentProvider() {
        ContentValues contentValues = new ContentValues();
        // Put the task description and selected mPriority into the ContentValues
        contentValues.put(MovieContract.WeatherEntry.COLUMN_MOVIE_ID, mMovieData.id);
        contentValues.put(MovieContract.WeatherEntry.COLUMN_POSTER, mMovieData.poster_path);
        contentValues.put(MovieContract.WeatherEntry.COLUMN_RELEASE_DATE, mMovieData.release_date);
        contentValues.put(MovieContract.WeatherEntry.COLUMN_SYNOPSIS, mMovieData.overview);
        contentValues.put(MovieContract.WeatherEntry.COLUMN_TITLE, mMovieData.title);
        contentValues.put(MovieContract.WeatherEntry.COLUMN_USER_RATING, mMovieData.vote_average);

        Bitmap bitmap = ((BitmapDrawable) moviePosterImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInByte = baos.toByteArray();

        contentValues.put(MovieContract.WeatherEntry.COLUMN_POSTER_BYTE_ARRAY, imageInByte);

        return getContentResolver().insert(MovieContract.WeatherEntry.CONTENT_URI, contentValues);
    }

    private boolean checkMovieDataIsFavorite() {
        Uri uri = buildMovieUriWithId(mMovieData.id);

        Cursor cursor = getContentResolver().query(uri,
                null,
                null,
                null,
                null);

        // If cursor is null or has count = 0, it means this movie is not favorite yet
        if (cursor != null && cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private int deleteMovieDataFromContentProvider() {
        Uri uri = buildMovieUriWithId(mMovieData.id);

        return getContentResolver().delete(uri,
                null,
                null);
    }


    @Override
    public void onClick(MovieTrailer movieTrailer) {

        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(YOUTUBE_BASE_URI + movieTrailer.key)));
    }


    /**************    LOADERS ************/

    private LoaderManager.LoaderCallbacks<ArrayList<MovieReview>> movieReviewLoader =
                    new LoaderManager.LoaderCallbacks<ArrayList<MovieReview>>() {

        @Override
        public Loader<ArrayList<MovieReview>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<ArrayList<MovieReview>>(mContext) {
                @Override
                protected void onStartLoading() {
                    if (args == null) {
                        return;
                    }

                    forceLoad();
                }

                @Override
                public ArrayList<MovieReview> loadInBackground() {
                    String finalIdString = args.getString(SEARCH_REVIEWS_URL_EXTRA);
                    URL urlReviews = NetworkUtils.buildMovieUrl("/" + finalIdString + REVIEWS_URL_STRING, mContext);

                    if (urlReviews == null) {
                        return null;
                    }

                    // Author: Udacity
                    try {
                        String jsonMovieReviewsResponse = NetworkUtils.getResponseFromHttpUrl(urlReviews);

                        return OpenMoviesJsonUtils.getMovieReviewsFromJson(mContext, jsonMovieReviewsResponse);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<MovieReview>> loader, ArrayList<MovieReview> movieReview) {
            if (movieReview != null && movieReview.size() > 0) {
                mReviewAdapter.setMovieReviewList(movieReview);
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<MovieReview>> loader) {

        }
    };


    private LoaderManager.LoaderCallbacks<ArrayList<MovieTrailer>> movieTrailerLoader =
            new LoaderManager.LoaderCallbacks<ArrayList<MovieTrailer>>() {

        @Override
        public Loader<ArrayList<MovieTrailer>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<ArrayList<MovieTrailer>>(mContext) {
                @Override
                protected void onStartLoading() {
                    if (args == null) {
                        return;
                    }

                    forceLoad();
                }

                @Override
                public ArrayList<MovieTrailer> loadInBackground() {
                    String finalIdString = args.getString(SEARCH_REVIEWS_URL_EXTRA);
                    URL urlTrailers = NetworkUtils.buildMovieUrl("/" + finalIdString + TRAILERS_URL_STRING, mContext);

                    if (urlTrailers == null) {
                        return null;
                    }

                    // Author: Udacity
                    try {
                        String jsonMovieTrailersResponse = NetworkUtils.getResponseFromHttpUrl(urlTrailers);

                        return OpenMoviesJsonUtils.getMovieTrailersFromJson(mContext, jsonMovieTrailersResponse);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<MovieTrailer>> loader, ArrayList<MovieTrailer> movieTrailer) {
            if (movieTrailer != null && movieTrailer.size() > 0) {
                for (int i = 0; i < movieTrailer.size(); i++) {
                    movieTrailer.get(i).description = getString(R.string.trailer) + " " + String.valueOf(i + 1);
                }
                mTrailerAdapter.setTrailerDataList(movieTrailer);
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<MovieTrailer>> loader) {

        }
    };
}
