package com.unimib.triptales.model;

import static androidx.room.ForeignKey.CASCADE;

import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity
public class Diary {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userId; // ID dell'utente proprietario del diario

    @ColumnInfo(name = "diary_name")
    private String name;

    @ColumnInfo(name = "diary_start_date")
    private String startDate;

    @ColumnInfo(name = "diary_end_date")
    private String endDate;

    @ColumnInfo(name = "diary_cover_image_uri")
    private String coverImageUri;

    @ColumnInfo(name = "diary_budget")
    private String budget;

    private boolean isSelected;

    @ColumnInfo(name = "diary_country")
    private String country;

    // Costruttore completo
    public Diary(int id, String userId, String name, String startDate, String endDate, String coverImageUri, String budget, String country) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coverImageUri = coverImageUri;
        this.budget = budget;
        this.country = country;
        this.isSelected = false;
    }

    // Metodi per calcolare la durata del viaggio
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

    // Metodo per ottenere l'abbreviazione del mese di inizio
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

    // Getter e setter per i campi
    public int getId() {
        return (id);
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCoverImageUri() {
        return coverImageUri;
    }
    public void setCoverImageUri(String coverImageUri) {
        this.coverImageUri = coverImageUri;
    }


    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
