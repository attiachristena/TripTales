package com.unimib.triptales.adapters;

import android.net.Uri;

public class Diary {
    private String diaryName;
    private String startDate;
    private String endDate;
    private Uri coverImageUri;

    public Diary(String diaryName, String startDate, Uri coverImageUri) {
        this.diaryName = diaryName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coverImageUri = coverImageUri;
    }

    public String getName() {
        return diaryName;
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
