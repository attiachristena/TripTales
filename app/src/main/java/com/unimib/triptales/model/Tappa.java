package com.unimib.triptales.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "tappe")
public class Tappa {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "nome")
    public String nome;

    @ColumnInfo(name = "data")
    public String data;

    @ColumnInfo(name = "immagine_uri")
    public String immagineUri; // Percorso dell'immagine come stringa

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "tappa_isSelected")
    public boolean tappa_isSelected;

    public Tappa(String nome, String data, String immagineUri, double latitude, double longitude) {
        this.nome = nome;
        this.data = data;
        this.immagineUri = immagineUri;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getImmagineUri() {
        return immagineUri;
    }

    public void setImmagineUri(String immagineUri) {
        this.immagineUri = immagineUri;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isTappa_isSelected() {
        return tappa_isSelected;
    }

    public void setTappa_isSelected(boolean tappa_isSelected) {
        this.tappa_isSelected = tappa_isSelected;
    }
}
