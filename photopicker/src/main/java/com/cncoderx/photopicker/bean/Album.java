package com.cncoderx.photopicker.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author wujie
 */
public class Album implements Parcelable {
    private String id;
    private String name;
    private int photoCount;
    private String coverPath;

    public Album() {
    }

    public Album(Parcel in) {
        id = in.readString();
        name = in.readString();
        photoCount = in.readInt();
        coverPath = in.readString();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(photoCount);
        dest.writeString(coverPath);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.photoCount = photoCount;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }
}
