package com.unimib.triptales.model;

import android.net.Uri;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;


import androidx.room.TypeConverter;


@Entity(tableName = "tappe")
public class Tappa {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "nome")
    public String nome;

    @ColumnInfo(name = "data")
    public String data;

    @ColumnInfo(name = "immagine_uri")
    public Uri immagineUri;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    // Costruttore
    public Tappa(String nome, String data, Uri immagineUri, double latitude, double longitude) {
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

    public Uri getImmagineUri() {
        return immagineUri;
    }

    public void setImmagineUri(Uri immagineUri) {
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
}
