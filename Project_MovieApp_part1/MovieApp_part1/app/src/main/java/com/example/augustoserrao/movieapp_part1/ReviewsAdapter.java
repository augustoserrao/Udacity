package com.example.augustoserrao.movieapp_part1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.augustoserrao.movieapp_part1.data.MovieReview;

import java.util.ArrayList;

/**
 * Created by augusto.serrao on 29/09/2017.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private ArrayList<MovieReview> mMovieReviewList = null;

    private Context context;

    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder holder, int position) {
        // If it is last position don't show separator
        if (position == mMovieReviewList.size() - 1) {
            holder.tvReviewSeparator.setVisibility(View.INVISIBLE);
        }

        holder.tvReviewAuthor.setText(mMovieReviewList.get(position).author + ":");
        holder.tvReviewBody.setText(mMovieReviewList.get(position).content);
    }

    @Override
    public int getItemCount() {
        if (mMovieReviewList == null) {
            return 0;
        }
        return mMovieReviewList.size();
    }

    public void setMovieReviewList(ArrayList<MovieReview> movieReviewList) {
        mMovieReviewList = movieReviewList;
        notifyDataSetChanged();
    }

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvReviewAuthor;
        public final TextView tvReviewBody;
        public final View tvReviewSeparator;

        public ReviewsAdapterViewHolder(View itemView) {
            super(itemView);

            tvReviewAuthor = itemView.findViewById(R.id.review_author_textview);
            tvReviewBody = itemView.findViewById(R.id.review_body_textview);
            tvReviewSeparator = itemView.findViewById(R.id.review_item_separator);
        }
    }
}
