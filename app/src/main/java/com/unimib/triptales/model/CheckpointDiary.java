package com.unimib.triptales.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "CheckpointDiary",
        foreignKeys = @ForeignKey(
                entity = Checkpoint.class,
                parentColumns = "id",
                childColumns = "checkpoint_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "checkpoint_id")}
)
public class CheckpointDiary {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "checkpoint_id")
    private int checkpointId; // Riferimento al Checkpoint

    public CheckpointDiary(int checkpointId) {
        this.checkpointId = checkpointId;
    }

    // Getter e Setter
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

}