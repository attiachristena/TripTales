package com.unimib.triptales.model;

import java.util.Date;

public class CardItem {
    private String title;
    private String description;
    private String date;
    private String imageUri;

    public CardItem(String title, String date, String description, String imageUri) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.imageUri = imageUri;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}

