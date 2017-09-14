package com.example.augustoserrao.movieapp_part1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.augustoserrao.movieapp_part1.data.MoviesIntents;
import com.example.augustoserrao.movieapp_part1.utilities.Utils;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView moviePosterImageView;
    private TextView  movieTitleTextView;
    private TextView  movieReleaseDateTextView;
    private TextView  movieVoteAverageTextView;
    private TextView  movieSynopsisTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        moviePosterImageView = (ImageView) findViewById(R.id.movie_detail_image);
        movieTitleTextView = (TextView) findViewById(R.id.movie_detail_title);
        movieReleaseDateTextView = (TextView) findViewById(R.id.movie_detail_release_date);
        movieVoteAverageTextView = (TextView) findViewById(R.id.movie_detail_vote_average);
        movieSynopsisTextView = (TextView) findViewById(R.id.movie_detail_synopsis);

        // Get intent and check its parameters
        Intent startActivityIntent = getIntent();
        if (startActivityIntent != null) {
            // Chech if it has IMAGE
            if (startActivityIntent.hasExtra(MoviesIntents.EXTRA_MOVIE_POSTER)) {
                String posterPath = startActivityIntent.getStringExtra(MoviesIntents.EXTRA_MOVIE_POSTER);
                Picasso.with(this).load(Utils.BASE_PICTURE_URL + posterPath).into(moviePosterImageView);
            }

            // Chech if it has TITLE
            if (startActivityIntent.hasExtra(MoviesIntents.EXTRA_MOVIE_TITLE)) {
                movieTitleTextView.setText(startActivityIntent.getStringExtra(MoviesIntents.EXTRA_MOVIE_TITLE));
            }

            // Chech if it has RELEASE DATE
            if (startActivityIntent.hasExtra(MoviesIntents.EXTRA_MOVIE_RELEASE_DATE)) {
                String sReleaseDate = startActivityIntent.getStringExtra(MoviesIntents.EXTRA_MOVIE_RELEASE_DATE);
                sReleaseDate = sReleaseDate.replace("-", "/");
                movieReleaseDateTextView.setText(Html.fromHtml("<b>" + getString(R.string.release_date) + ": " + "</b>" + sReleaseDate));
            }

            // Chech if it has VOTE AVERAGE
            if (startActivityIntent.hasExtra(MoviesIntents.EXTRA_MOVIE_VOTE_AVERAGE)) {
                double dVoteAverage = startActivityIntent.getDoubleExtra(MoviesIntents.EXTRA_MOVIE_VOTE_AVERAGE, 0);
                movieVoteAverageTextView.setText(Html.fromHtml("<b>" + getString(R.string.vote_average) + ": " + "</b>" + dVoteAverage));
            }

            // Chech if it has SYNOPSIS
            if (startActivityIntent.hasExtra(MoviesIntents.EXTRA_MOVIE_SYNOPSIS)) {
                String sSynopsis = startActivityIntent.getStringExtra(MoviesIntents.EXTRA_MOVIE_SYNOPSIS);
                movieSynopsisTextView.setText(Html.fromHtml("<b>" + getString(R.string.synopsis) + ": " + "</b>" + sSynopsis));
            }
        }
    }
}
