package com.unimib.triptales.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "CheckpointDiary",
        foreignKeys = @ForeignKey(
                entity = Diary.class,
                parentColumns = "id",
                childColumns = "diary_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "diary_id")}
)
public class CheckpointDiary {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "diary_id")
    private String diaryId; // Riferimento al Diary

    @ColumnInfo(name = "nome")
    public String nome;

    @ColumnInfo(name = "data")
    public String data;

    @ColumnInfo(name = "immagine_uri")
    public String immagineUri;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    public CheckpointDiary(String diaryId, String nome, String data, String immagineUri, double latitude, double longitude) {
        this.diaryId = diaryId;
        this.nome = nome;
        this.data = data;
        this.immagineUri = immagineUri;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getter e Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDiaryId() {
        return diaryId;
    }

//    public int getCheckpointId() {
//        return diaryId;
//    }


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

}