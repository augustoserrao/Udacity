package com.example.augustoserrao.movieapp_part1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.augustoserrao.movieapp_part1.data.MovieData;
import com.example.augustoserrao.movieapp_part1.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Help from Udacity.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private ArrayList<MovieData> movieDataList = null;

    private Context context;
    private final MovieAdapterOnClickHandler mClickHandler;

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(MovieData movieData);
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {

        // Set item's picture
        if (Utils.isNetworkAvailable(context)) {
            Picasso.with(context).load(Utils.BASE_PICTURE_URL + movieDataList.get(position).poster_path).into(holder.thumbnailImageView);
        } else if (movieDataList.get(position).posterByteArray != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(
                    movieDataList.get(position).posterByteArray,
                    0,
                    movieDataList.get(position).posterByteArray.length);

            holder.thumbnailImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        if (movieDataList == null) {
            return 0;
        }
        return movieDataList.size();
    }

    public void setMovieDataList(ArrayList<MovieData> movieData) {
        movieDataList = movieData;
        notifyDataSetChanged();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView thumbnailImageView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);

            thumbnailImageView = itemView.findViewById(R.id.iv_movie_thumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            MovieData clickMovieData = movieDataList.get(position);
            mClickHandler.onClick(clickMovieData);
        }
    }
}
