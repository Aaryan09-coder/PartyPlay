package com.vibey.PartPlay.Entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ProfilePhoto {

    private String id;

    private long userId;

    private byte[] imageData;

    private String contentType;

    // getters and setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
