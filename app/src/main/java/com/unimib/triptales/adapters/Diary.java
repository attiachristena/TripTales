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
    private boolean isSelected;
    private String country;


    public Diary(String diaryName, String startDate, String endDate, Uri coverImageUri, String country) {
        this.diaryName = diaryName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coverImageUri = coverImageUri;
        this.isSelected = false;
        this.country= country;
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

    // Metodo per calcolare la durata del viaggio
    public int getTravelDuration() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            if (start != null && end != null && start.before(end)) {
                long diffInMillis = end.getTime() - start.getTime();
                long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
                return (int) diffInDays;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Metodo per ottenere l'abbreviazione del mese
    public String getStartMonthAbbreviation() {
        return getMonthAbbreviation(startDate);
    }

    // Metodo per estrarre l'abbreviazione del mese
    private String getMonthAbbreviation(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(dateString);
            if (date != null) {
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
                return monthFormat.format(date).toUpperCase();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // Getter e Setter per isSelected
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    // Setter per i dati del diario
    public void setName(String name) {
        this.diaryName = name;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setCoverImageUri(Uri coverImageUri) {
        this.coverImageUri = coverImageUri;
    }

    // Getter e setter per il paese
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
