package com.unimib.triptales.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;
import java.util.UUID;

@Entity/*(foreignKeys = @ForeignKey(entity = Diary.class, parentColumns = "id", childColumns = "diaryId", onDelete = CASCADE))*/
public class Task {

    @PrimaryKey
    @NonNull
    private String id;

    public Task(){}

    public Task(String name, boolean task_isSelected, boolean task_isChecked) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.task_isSelected = task_isSelected;
        this.task_isChecked = task_isChecked;
    }

    //public int diaryId; // ID del diario

    @ColumnInfo(name = "task_name")
    private String name;

    @ColumnInfo(name = "task_isSelected")
    private boolean task_isSelected;

    @ColumnInfo(name = "task_isChecked")
    private boolean task_isChecked;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTask_isSelected() {
        return task_isSelected;
    }

    public void setTask_isSelected(boolean task_isSelected) {
        this.task_isSelected = task_isSelected;
    }

    public boolean isTask_isChecked() {
        return task_isChecked;
    }

    public void setTask_isChecked(boolean task_isChecked) {
        this.task_isChecked = task_isChecked;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
