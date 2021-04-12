package com.example.picturesharing;

public class Photo {
    String createdAt, photoRef;

    public Photo(String createdAt, String photoRef) {
        this.createdAt = createdAt;
        this.photoRef = photoRef;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPhotoRef() {
        return photoRef;
    }

    public void setPhotoRef(String photoRef) {
        this.photoRef = photoRef;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "createdAt='" + createdAt + '\'' +
                ", photoRef='" + photoRef + '\'' +
                '}';
    }
}
