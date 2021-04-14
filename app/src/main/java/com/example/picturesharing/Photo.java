package com.example.picturesharing;

import android.net.Uri;

import com.google.firebase.Timestamp;

public class Photo {
    String photoRef, photoUri;
    Uri imageUri;
    Timestamp createdAt;

    public Photo(String photoRef, String photoUri, Timestamp createdAt) {
        this.photoRef = photoRef;
        this.photoUri = photoUri;
        this.createdAt = createdAt;
    }

    public Photo() {
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getPhotoRef() {
        return photoRef;
    }

    public void setPhotoRef(String photoRef) {
        this.photoRef = photoRef;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "createdAt='" + createdAt + '\'' +
                ", photoRef='" + photoRef + '\'' +
                ", photoUri='" + photoUri + '\'' +
                ", imageUri=" + imageUri +
                '}';
    }
}
