package com.example.augustoserrao.movieapp_part1.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by augusto.serrao on 13/09/2017.
 */

// Composition of Json movie data
public class MovieData implements Parcelable {
    public long vote_count = 0;
    public long id = 0;
    public boolean video = false;
    public double vote_average = 0;
    public String title = null;
    public double popularity = 0;
    public String poster_path = null;
    public String original_language = null;
    public String original_title = null;
    public int[] genre_ids = null;
    public String backdrop_path = null;
    public boolean adult = false;
    public String overview = null;  // Synopsis
    public String release_date = null;
    public byte[] posterByteArray = null;

    private MovieData(Parcel in) {
        id = in.readLong();
        title = in.readString();
        overview = in.readString();
        vote_average = in.readDouble();
        release_date = in.readString();
        poster_path = in.readString();
    }

    public MovieData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeDouble(vote_average);
        parcel.writeString(release_date);
        parcel.writeString(poster_path);
    }

    public static final Parcelable.Creator<MovieData> CREATOR
            = new Parcelable.Creator<MovieData>() {

        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }
        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
}
