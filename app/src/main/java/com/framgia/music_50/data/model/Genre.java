package com.framgia.music_50.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Genre implements Parcelable {
    private String mTitle;
    private int mImage;
    private String mGenreType;

    public Genre(String title, int image, String genreType) {
        mTitle = title;
        mImage = image;
        mGenreType = genreType;
    }

    private Genre(Parcel in) {
        mTitle = in.readString();
        mImage = in.readInt();
        mGenreType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeInt(mImage);
        dest.writeString(mGenreType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Genre> CREATOR = new Creator<Genre>() {
        @Override
        public Genre createFromParcel(Parcel in) {
            return new Genre(in);
        }

        @Override
        public Genre[] newArray(int size) {
            return new Genre[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getImage() {
        return mImage;
    }

    public void setImage(int image) {
        mImage = image;
    }

    public String getGenreType() {
        return mGenreType;
    }

    public void setGenreType(String genreType) {
        mGenreType = genreType;
    }
}
