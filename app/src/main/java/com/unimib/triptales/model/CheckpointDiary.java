package com.unimib.triptales.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(
        tableName = "checkpoint_diary",
        foreignKeys = @ForeignKey(
                entity = Checkpoint.class,
                parentColumns = "id",
                childColumns = "checkpoint_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "checkpoint_id")}
)
public class CheckpointDiary {
    @PrimaryKey(autoGenerate = true) // Aggiungi un ID univoco per ogni record
    private int id; // ID univoco per ogni diario di checkpoint
    private int checkpointId; // Riferimento al checkpoint
    private String nomeTappa;
    private String dataTappa;
    private String descrizione;
    private String immagineTappaUri;
    private List<CardItem> immaginiDiario;

    public CheckpointDiary(int checkpointId, String nomeTappa, String dataTappa, String descrizione, String immagineTappaUri) {
        this.checkpointId = checkpointId;  // Riferimento al checkpoint
        this.nomeTappa = nomeTappa;
        this.dataTappa = dataTappa;
        this.descrizione = descrizione;
        this.immagineTappaUri = immagineTappaUri;
        this.immaginiDiario = new ArrayList<>();
    }

    // Getter e setter per l'ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCheckpointId() {
        return checkpointId;
    }

    public void setCheckpointId(int checkpointId) {
        this.checkpointId = checkpointId;
    }

    public String getNomeTappa() {
        return nomeTappa;
    }

    public void setNomeTappa(String nomeTappa) {
        this.nomeTappa = nomeTappa;
    }

    public String getDataTappa() {
        return dataTappa;
    }

    public void setDataTappa(String dataTappa) {
        this.dataTappa = dataTappa;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getImmagineTappaUri() {
        return immagineTappaUri;
    }

    public void setImmagineTappaUri(String immagineTappaUri) {
        this.immagineTappaUri = immagineTappaUri;
    }

    public List<CardItem> getImmaginiDiario() {
        return immaginiDiario;
    }

    public void setImmaginiDiario(List<CardItem> immaginiDiario) {
        this.immaginiDiario = immaginiDiario;
    }

    // Metodo per aggiungere un'immagine al diario
    public void aggiungiImmagineDiario(CardItem immagine) {
        this.immaginiDiario.add(immagine);
    }
}
