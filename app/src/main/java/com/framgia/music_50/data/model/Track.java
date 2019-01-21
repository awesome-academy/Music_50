package com.framgia.music_50.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Track implements Parcelable {
    private long mId;
    private String mTitle;
    private String mArtworkUrl;
    private String mArtistName;
    private String mAvatarUrl;
    private int mDuration;
    private boolean mDownloadable;
    private String mDownloadUrl;
    private String mStreamUrl;

    private Track(TrackBuilder builder) {
        mId = builder.mId;
        mTitle = builder.mTitle;
        mArtworkUrl = builder.mArtworkUrl;
        mArtistName = builder.mArtistName;
        mAvatarUrl = builder.mAvatarUrl;
        mDuration = builder.mDuration;
        mDownloadable = builder.mDownloadable;
        mDownloadUrl = builder.mDownloadUrl;
        mStreamUrl = builder.mStreamUrl;
    }

    private Track(Parcel in) {
        mId = in.readLong();
        mTitle = in.readString();
        mArtworkUrl = in.readString();
        mArtistName = in.readString();
        mAvatarUrl = in.readString();
        mDuration = in.readInt();
        mDownloadable = in.readByte() != 0;
        mDownloadUrl = in.readString();
        mStreamUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mTitle);
        dest.writeString(mArtworkUrl);
        dest.writeString(mArtistName);
        dest.writeString(mAvatarUrl);
        dest.writeInt(mDuration);
        dest.writeByte((byte) (mDownloadable ? 1 : 0));
        dest.writeString(mDownloadUrl);
        dest.writeString(mStreamUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getArtworkUrl() {
        return mArtworkUrl;
    }

    public void setArtworkUrl(String artworkUrl) {
        mArtworkUrl = artworkUrl;
    }

    public String getArtistName() {
        return mArtistName;
    }

    public void setArtistName(String artistName) {
        mArtistName = artistName;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        mAvatarUrl = avatarUrl;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public boolean isDownloadable() {
        return mDownloadable;
    }

    public void setDownloadable(boolean downloadable) {
        mDownloadable = downloadable;
    }

    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        mDownloadUrl = downloadUrl;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getStreamUrl() {
        return mStreamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        mStreamUrl = streamUrl;
    }

    public static class TrackBuilder {
        private long mId;
        private String mTitle;
        private String mArtworkUrl;
        private String mArtistName;
        private String mAvatarUrl;
        private int mDuration;
        private boolean mDownloadable;
        private String mDownloadUrl;
        private String mStreamUrl;

        public TrackBuilder() {
        }

        public TrackBuilder setId(long id) {
            mId = id;
            return this;
        }

        public TrackBuilder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public TrackBuilder setArtworkUrl(String artworkUrl) {
            mArtworkUrl = artworkUrl;
            return this;
        }

        public TrackBuilder setArtistName(String artistName) {
            mArtistName = artistName;
            return this;
        }

        public TrackBuilder setAvatarUrl(String avatarUrl) {
            mAvatarUrl = avatarUrl;
            return this;
        }

        public TrackBuilder setDuration(int duration) {
            mDuration = duration;
            return this;
        }

        public TrackBuilder setDownloadable(boolean downloadable) {
            mDownloadable = downloadable;
            return this;
        }

        public TrackBuilder setDownloadUrl(String downloadUrl) {
            mDownloadUrl = downloadUrl;
            return this;
        }

        public TrackBuilder setStreamUrl(String streamUrl) {
            mStreamUrl = streamUrl;
            return this;
        }

        public Track build() {
            return new Track(this);
        }
    }

    public final class TrackEntry {
        public static final String ID = "id";
        public static final String TRACK = "track";
        public static final String TITLE = "title";
        public static final String ARTWORK_URL = "artwork_url";
        public static final String AVATAR_URL = "avatar_url";
        public static final String ARTIST_NAME = "username";
        public static final String DOWNLOADABLE = "downloadable";
        public static final String DOWNLOAD_URL = "download_url";
        public static final String DURATION = "duration";
    }
}
