package com.unimib.triptales.adapters;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Diary {
    private String diaryName;
    private String startDate;
    private String endDate;
    private Uri coverImageUri;

    public Diary(String diaryName, String startDate, String endDate, Uri coverImageUri) {
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

    // Method to calculate the number of days between start and end dates
    public int getTravelDuration() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            // If dates are valid and start date is before end date
            if (start != null && end != null && start.before(end)) {
                long diffInMillis = end.getTime() - start.getTime();
                long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);  // Convert milliseconds to days
                return (int) diffInDays;
            } else {
                return 0; // Return 0 if the dates are invalid or if start date is after end date
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;  // Return 0 if there's an error parsing the dates
        }
    }

    // Method to get the abbreviated month (e.g., "JAN", "FEB") from the start date
    public String getStartMonthAbbreviation() {
        return getMonthAbbreviation(startDate);
    }
    // Helper method to extract the abbreviated month (e.g., "JAN", "FEB") from a date string
    private String getMonthAbbreviation(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(dateString);
            if (date != null) {
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault()); // "MMM" gives abbreviated month
                return monthFormat.format(date).toUpperCase(); // Return the abbreviated month name in uppercase (e.g., "JAN")
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";  // Return an empty string if there's an error
    }

}
