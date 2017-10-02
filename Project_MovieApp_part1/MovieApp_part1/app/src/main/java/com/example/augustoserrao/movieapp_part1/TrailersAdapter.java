package com.example.augustoserrao.movieapp_part1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.augustoserrao.movieapp_part1.data.MovieTrailer;

import java.util.ArrayList;

/**
 * Created by augusto.serrao on 29/09/2017.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {

    private ArrayList<MovieTrailer> mMovieTrailerList = null;

    private Context context;
    private final TrailersAdapterOnClickHandler mClickHandler;

    public TrailersAdapter(TrailersAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public interface TrailersAdapterOnClickHandler {
        void onClick(MovieTrailer movieTrailer);
    }

    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new TrailersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailersAdapterViewHolder holder, int position) {
        // If it is last position don't show separator
        if (position == mMovieTrailerList.size() - 1) {
            holder.trailerSeparator.setVisibility(View.INVISIBLE);
        }

        holder.trailerDescription.setText(mMovieTrailerList.get(position).description);
    }

    @Override
    public int getItemCount() {
        if (mMovieTrailerList == null) {
            return 0;
        }
        return mMovieTrailerList.size();
    }

    public void setTrailerDataList(ArrayList<MovieTrailer> movieTrailerList) {
        mMovieTrailerList = movieTrailerList;
        notifyDataSetChanged();
    }

    public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final Button playButton;
        public final TextView trailerDescription;
        public final View trailerSeparator;

        public TrailersAdapterViewHolder(View itemView) {
            super(itemView);

            playButton = itemView.findViewById(R.id.play_button);
            trailerDescription = itemView.findViewById(R.id.trailer_desc_textview);
            trailerSeparator = itemView.findViewById(R.id.trailer_item_separator);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            MovieTrailer clickMovieTrailer = mMovieTrailerList.get(position);
            mClickHandler.onClick(clickMovieTrailer);
        }
    }
}
