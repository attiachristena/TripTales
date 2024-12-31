package com.unimib.triptales.adapters;

import android.net.Uri;

public class Diary {
    private String name;
    private String startDate;
    private String endDate;
    private Uri coverImageUri;

    public Diary(String name, String startDate, String endDate, Uri coverImageUri) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coverImageUri = coverImageUri;
    }

    public String getName() {
        return name;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public Uri getCoverImageUri() {
        return coverImageUri;
    }
}
